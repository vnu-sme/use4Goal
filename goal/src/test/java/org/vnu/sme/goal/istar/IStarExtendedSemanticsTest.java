package org.vnu.sme.goal.istar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.vnu.sme.goal.dsl.acl.parser.AclCompiler;
import org.vnu.sme.goal.verify.conformance.semantics.GoalTaskStatus;
import org.vnu.sme.goal.dsl.istar.parser.IStarCompiler;
import org.vnu.sme.goal.dsl.istar.mm.GoalActivationGraph;
import org.vnu.sme.goal.trace.usetrace.IStarUseTraceCompiler;

class IStarExtendedSemanticsTest {

    @TempDir Path temporaryDirectory;

    @Test
    void nonLeafGoalCannotBypassChildrenWithItsOwnCondition() {
        var result = IStarCompiler.compile("""
                istar ParentCondition {
                  role Worker {
                    goal Root : Achieve condition {[ self.done ]}
                    goal Leaf : Achieve > Root condition {[ self.done ]}
                  }
                }
                """);
        assertFalse(result.ok());
        assertTrue(result.errors().stream().anyMatch(error ->
                error.contains("non-leaf goal 'Root' cannot declare a condition")));
    }

    @Test
    void incomingDependencyMakesAnActorLocalRootInheritDemand() throws Exception {
        var model = compile("""
                istar DependencyDemand {
                  role Requester {
                    goal RequestHandled : Achieve
                  }
                  role Provider {
                    goal WorkProvided : Achieve
                    task PerformWork
                  }
                  depend Requester.RequestHandled
                    -> task PerformWork
                    -> Provider.WorkProvided
                }
                """);
        GoalActivationGraph graph = GoalActivationGraph.of(model);
        assertEquals("RequestHandled", graph.parentOf("WorkProvided").orElseThrow());
        assertFalse(graph.isRoot("WorkProvided"));
    }

    @Test
    void taskDependencyAlsoSuppliesDemandToDependeeGoal() throws Exception {
        var model = compile("""
                istar TaskDependencyDemand {
                  role Requester {
                    goal RequestHandled : Achieve
                    task DelegateWork > RequestHandled
                    task RecordDelegation > RequestHandled
                  }
                  role Provider {
                    goal WorkProvided : Achieve
                  }
                  depend Requester.DelegateWork
                    -> goal DelegatedWork
                    -> Provider.WorkProvided
                }
                """);
        GoalActivationGraph graph = GoalActivationGraph.of(model);
        assertEquals("DelegateWork", graph.parentOf("WorkProvided").orElseThrow());
        assertFalse(graph.isRoot("WorkProvided"));
    }

    @Test
    void refinementParentCannotAlsoBeADependencyEndpoint() {
        var result = IStarCompiler.compile("""
                istar RefinedDependencyEndpoint {
                  role Requester {
                    goal Requested : Achieve
                    goal Delegated : Achieve > Requested
                    task Delegate > Requested
                  }
                  role Provider {
                    goal Service : Achieve
                    task FirstStep > Service
                    task SecondStep > Service
                  }
                  depend Requester.Requested
                    -> goal ServiceDelivery
                    -> Provider.Service
                }
                """);
        assertFalse(result.ok());
        assertTrue(result.errors().stream().anyMatch(error ->
                error.contains("dependencies may only attach to leaf intentional elements")));
    }

    @Test
    void activationInheritanceRejectsTwoDifferentParents() {
        IStarCompiler.Result result = IStarCompiler.compile("""
                istar AmbiguousActivation {
                  role Worker {
                    goal ParentOne : Achieve
                    goal ParentTwo : Achieve
                    goal SharedChild : Achieve > or ParentOne > or ParentTwo
                  }
                }
                """);
        assertFalse(result.ok());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("activation inheritance requires")));
    }

    @Test
    void typedActivationRootDefaultsToTrueWhenActivationIsAbsent() throws Exception {
        compile("""
                istar MissingRootActivation {
                  role Worker {
                    goal Root : Achieve condition {[ true ]}
                  }
                }
                """);
    }

    @Test
    void taskRequiresPreInThePreviousStateAndPostInTheResultingState() throws Exception {
        Path istarFile = temporaryDirectory.resolve("task-transition.istar");
        Path useFile = temporaryDirectory.resolve("task-transition.use");
        Path soilFile = temporaryDirectory.resolve("task-transition.soil");
        Files.writeString(istarFile, """
                istar TaskTransition {
                  role Worker {
                    task CompleteWork
                    pre {[ self.ready ]}
                    post {[ self.done ]}
                  }
                }
                """);
        Files.writeString(useFile, """
                model TaskTransition
                class Worker
                attributes
                  ready : Boolean
                  done : Boolean
                end
                """);
        Files.writeString(soilFile, """
                !create worker : Worker
                !set worker.ready := true
                !set worker.done := false
                !set worker.done := true
                !set worker.done := false
                """);

        var result = IStarUseTraceCompiler.compile(istarFile, useFile, soilFile);
        assertTrue(result.ok(), () -> String.join("\n", result.errors()));
        var key = new IStarUseTraceCompiler.InstanceKey("Worker", "worker");
        assertEquals(GoalTaskStatus.PENDING,
                result.checkpoints().get(2).markings().get(key).goalTaskStatus("CompleteWork"));
        assertEquals(GoalTaskStatus.FULFILLED,
                result.checkpoints().get(3).markings().get(key).goalTaskStatus("CompleteWork"));
        assertEquals(GoalTaskStatus.FULFILLED,
                result.checkpoints().get(4).markings().get(key).goalTaskStatus("CompleteWork"),
                "a successful Task occurrence remains fulfilled like Achieve");
    }

    @Test
    void monitoringExampleProvidesARealAclContextForRecur() throws Exception {
        Path base = Path.of("src/main/resources/examples/monitoring");
        var acl = AclCompiler.compile(base.resolve("monitoring.acl"));
        var istar = IStarCompiler.compile(base.resolve("monitoring.istar"));
        assertTrue(acl.ok(), () -> String.join("\n", acl.errors()));
        assertTrue(istar.ok(), () -> String.join("\n", istar.errors()));
        assertEquals(org.vnu.sme.goal.dsl.istar.mm.GoalType.RECUR,
                ((org.vnu.sme.goal.dsl.istar.mm.Goal) istar.model()
                        .findElement("ServiceChecked").orElseThrow()).goalType());
    }

    @Test
    void supportsTheFourTemporalGoalTypes() throws Exception {
        var model = compile("""
                istar TemporalTypes {
                  role Controller {
                    goal EventuallyReady : Achieve
                      condition {[ true ]}
                    goal AlwaysSafe : Maintain
                      condition {[ true ]}
                    goal EventuallySustained : Sustain
                      condition {[ true ]}
                    goal PeriodicallyChecked : Recur
                      condition {[ true ]}
                  }
                }
                """);
        assertEquals(org.vnu.sme.goal.dsl.istar.mm.GoalType.ACHIEVE,
                ((org.vnu.sme.goal.dsl.istar.mm.Goal) model.findElement("EventuallyReady").orElseThrow()).goalType());
        assertEquals(org.vnu.sme.goal.dsl.istar.mm.GoalType.SUSTAIN,
                ((org.vnu.sme.goal.dsl.istar.mm.Goal) model.findElement("EventuallySustained").orElseThrow()).goalType());
        assertEquals(org.vnu.sme.goal.dsl.istar.mm.GoalType.RECUR,
                ((org.vnu.sme.goal.dsl.istar.mm.Goal) model.findElement("PeriodicallyChecked").orElseThrow()).goalType());
    }

    @Test
    void traceRuntimeDistinguishesTheFourTemporalStatuses() throws Exception {
        Path istarFile = temporaryDirectory.resolve("temporal-runtime.istar");
        Path useFile = temporaryDirectory.resolve("temporal-runtime.use");
        Path soilFile = temporaryDirectory.resolve("temporal-runtime.soil");
        Files.writeString(istarFile, """
                istar TemporalRuntime {
                  role Controller {
                    goal Achieved : Achieve condition {[ self.p ]}
                    goal Maintained : Maintain condition {[ self.p ]}
                    goal Sustained : Sustain condition {[ self.p ]}
                    goal Recurrent : Recur condition {[ self.p ]}
                  }
                }
                """);
        Files.writeString(useFile, """
                model TemporalRuntime
                class Controller
                attributes
                  p : Boolean
                end
                """);
        Files.writeString(soilFile, """
                !create controller : Controller
                !set controller.p := true
                !set controller.p := false
                """);

        var result = IStarUseTraceCompiler.compile(istarFile, useFile, soilFile);
        assertTrue(result.ok(), () -> String.join("\n", result.errors()));
        var key = new IStarUseTraceCompiler.InstanceKey("Controller", "controller");

        var pTrue = result.checkpoints().get(1).markings().get(key);
        assertEquals(GoalTaskStatus.FULFILLED, pTrue.goalTaskStatus("Achieved"));
        // Without an activation gate, a root goal is demanded from object creation (checkpoint
        // 0), where p still holds its Boolean default (false). Maintain's baseline is true, so
        // a false predicate at episode start is already one violation -- and a Maintain episode
        // cannot recover from a violation, so it stays VIOLATED even after p becomes true here.
        assertEquals(GoalTaskStatus.VIOLATED, pTrue.goalTaskStatus("Maintained"));
        assertEquals(GoalTaskStatus.FULFILLED, pTrue.goalTaskStatus("Sustained"));
        assertEquals(GoalTaskStatus.FULFILLED, pTrue.goalTaskStatus("Recurrent"));

        var pFalseAgain = result.checkpoints().get(2).markings().get(key);
        assertEquals(GoalTaskStatus.FULFILLED, pFalseAgain.goalTaskStatus("Achieved"),
                "Achieve latches once satisfied");
        assertEquals(GoalTaskStatus.VIOLATED, pFalseAgain.goalTaskStatus("Maintained"),
                "Maintain must hold continuously once active");
        assertEquals(GoalTaskStatus.VIOLATED, pFalseAgain.goalTaskStatus("Sustained"),
                "Sustain must not regress once first satisfied");
        assertEquals(GoalTaskStatus.PENDING, pFalseAgain.goalTaskStatus("Recurrent"),
                "Recur may fall back to pending between occurrences");
    }

    @Test
    void rejectsAchieveChildrenUnderAMaintainGoal() {
        IStarCompiler.Result result = IStarCompiler.compile("""
                istar InvalidTemporalRefinement {
                  role Controller {
                    goal Safe : Maintain
                    goal FirstStep : Achieve > Safe
                    goal SecondStep : Achieve > Safe
                  }
                }
                """);
        assertFalse(result.ok());
        assertTrue(result.errors().stream().anyMatch(e ->
                e.contains("ACHIEVE goal 'FirstStep' cannot refine MAINTAIN goal 'Safe'")));
    }

    @Test
    void permitsStrongerTemporalChildren() throws Exception {
        compile("""
                istar ValidTemporalRefinement {
                  role Controller {
                    goal EventuallySafe : Sustain
                    goal AlwaysClosed : Maintain > EventuallySafe
                    goal UsuallyLocked : Sustain > EventuallySafe
                  }
                }
                """);
    }

    @Test
    void obsoleteObstacleSyntaxIsRejected() throws Exception {
        Path file = temporaryDirectory.resolve("obsolete-obstacle.istar");
        Files.writeString(file, """
                istar ObsoleteObstacle {
                  role Scheduler {
                    goal MeetingScheduled : Achieve
                    obstacle NoCommonSlot : Prevention > obstructs MeetingScheduled
                  }
                }
                """);

        IStarCompiler.Result result = IStarCompiler.compile(file);
        assertFalse(result.ok(), "Obstacle must no longer be accepted by the iStar grammar");
    }

    @Test
    void releaseClauseIsNotPartOfTheGoalContractLanguage() {
        IStarCompiler.Result result = IStarCompiler.compile("""
                istar NoRelease {
                  role Worker {
                    goal Done : Achieve
                    release {[ self.cancelled ]}
                  }
                }
                """);
        assertFalse(result.ok());
        assertTrue(result.errors().stream().anyMatch(error -> error.contains("release")));
    }

    @Test
    void eachTypedOclContractSlotHasMultiplicityZeroOrOne() {
        IStarCompiler.Result result = IStarCompiler.compile("""
                istar DuplicateCondition {
                  role Worker {
                    goal Done : Achieve
                    condition {[ true ]}
                    condition {[ true ]}
                  }
                }
                """);
        assertFalse(result.ok());
        assertTrue(result.errors().stream().anyMatch(error ->
                error.contains("more than one 'condition' OCL contract")));
    }

    private org.vnu.sme.goal.dsl.istar.mm.GoalModel compile(String source) throws Exception {
        Path file = temporaryDirectory.resolve("model" + System.nanoTime() + ".istar");
        Files.writeString(file, source);
        IStarCompiler.Result result = IStarCompiler.compile(file);
        assertTrue(result.ok(), () -> String.join("\n", result.errors()));
        return result.model();
    }
}
