package org.vnu.sme.goal.istar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.vnu.sme.goal.dsl.acl.parser.AclCompiler;
import org.vnu.sme.goal.verify.conformance.semantics.GoalTaskStatus;
import org.vnu.sme.goal.verify.conformance.semantics.IStarMarking;
import org.vnu.sme.goal.verify.conformance.semantics.IStarPropagation;
import org.vnu.sme.goal.verify.conformance.semantics.ObstacleStatus;
import org.vnu.sme.goal.dsl.istar.parser.IStarCompiler;
import org.vnu.sme.goal.dsl.istar.mm.GoalActivationGraph;
import org.vnu.sme.goal.trace.usetrace.IStarUseTraceCompiler;
import org.vnu.sme.goal.dsl.istar.mm.ContextResolution;
import org.vnu.sme.goal.dsl.iscn.goalmodelinstance.parser.GoalModelInstanceTranslator;
import org.vnu.sme.goal.dsl.iscn.mm.IStarScenarioModel;
import org.vnu.sme.goal.dsl.iscn.mm.ScenarioInstance;
import org.vnu.sme.goal.dsl.iscn.mm.ScenarioStmt;
import org.vnu.sme.goal.dsl.iscn.parser.IStarScenarioCompiler;

class IStarExtendedSemanticsTest {

    @TempDir Path temporaryDirectory;

    @Test
    void dependencyPreservesNestedContextStackForSelfOuter() throws Exception {
        var compiled = IStarCompiler.compile(Path.of("src/main/resources/examples/mtg/mtg.istar"));
        assertTrue(compiled.ok(), () -> String.join("\n", compiled.errors()));
        var model = compiled.model();
        var contexts = ContextResolution.of(model);
        assertEquals(List.of("Secretary", "Participant", "Organizer"),
                contexts.contextTypesOf(model, "CollectByPhone"));
        assertEquals("Secretary", contexts.actorTypeOf(model, "CollectByPhone"));
    }

    @Test
    void activationInheritanceIgnoresRefinementKind() throws Exception {
        var model = compile("""
                istar ActivationInheritance {
                  role Worker {
                    goal AndParent : Achieve
                    activation {[ true ]}
                    goal AndChildOne : Achieve > AndParent
                    goal AndChildTwo : Achieve > AndParent
                    goal OrParent : Achieve
                    activation {[ true ]}
                    goal OrChild : Achieve > or OrParent
                    goal ForallParent : Achieve
                    activation {[ true ]}
                    goal ForallChild : Achieve > forall Worker ForallParent
                    goal PickParent : Achieve
                    activation {[ true ]}
                    goal PickChild : Achieve > pick Worker PickParent
                  }
                }
                """);
        GoalActivationGraph graph = GoalActivationGraph.of(model);
        assertEquals("AndParent", graph.parentOf("AndChildOne").orElseThrow());
        assertEquals("OrParent", graph.parentOf("OrChild").orElseThrow());
        assertEquals("ForallParent", graph.parentOf("ForallChild").orElseThrow());
        assertEquals("PickParent", graph.parentOf("PickChild").orElseThrow());
    }

    @Test
    void incomingDependencyMakesAnActorLocalRootInheritActivation() throws Exception {
        var model = compile("""
                istar DependencyActivation {
                  role Requester {
                    goal RequestHandled : Achieve
                    activation {[ true ]}
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
    void mandatoryChildrenCannotWeakenInheritedDemandWithActivation() {
        IStarCompiler.Result andResult = IStarCompiler.compile("""
                istar InvalidAndActivation {
                  role Worker {
                    goal Parent : Achieve
                    goal ChildOne : Achieve > Parent activation {[ true ]}
                    goal ChildTwo : Achieve > Parent
                  }
                }
                """);
        assertFalse(andResult.ok());
        assertTrue(andResult.errors().stream().anyMatch(e ->
                e.contains("inherits activation from AND parent")));

        IStarCompiler.Result forallResult = IStarCompiler.compile("""
                istar InvalidForallActivation {
                  role Owner {
                    goal Parent : Achieve
                    goal Child : Achieve > forall Worker Parent activation {[ true ]}
                  }
                  role Worker { }
                }
                """);
        assertFalse(forallResult.ok());
        assertTrue(forallResult.errors().stream().anyMatch(e ->
                e.contains("inherits activation from forall parent")));
    }

    @Test
    void optionalChildrenMayDeclareEligibilityActivation() throws Exception {
        compile("""
                istar OptionalActivation {
                  role Worker {
                    goal OrParent : Achieve
                    activation {[ true ]}
                    goal OrChild : Achieve > or OrParent activation {[ true ]}
                    goal PickParent : Achieve
                    activation {[ true ]}
                    goal PickChild : Achieve > pick Worker PickParent activation {[ true ]}
                  }
                }
                """);
    }

    @Test
    void typedActivationRootMustDeclareActivation() {
        IStarCompiler.Result result = IStarCompiler.compile("""
                istar MissingRootActivation {
                  role Worker {
                    goal Root : Achieve condition {[ true ]}
                  }
                }
                """);
        assertFalse(result.ok());
        assertTrue(result.errors().stream().anyMatch(e ->
                e.contains("typed activation-root goal 'Root'")));
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
                      activation {[ true ]}
                      condition {[ true ]}
                    goal AlwaysSafe : Maintain
                      activation {[ true ]}
                      condition {[ true ]}
                    goal EventuallySustained : Sustain
                      activation {[ true ]}
                      condition {[ true ]}
                    goal PeriodicallyChecked : Recur
                      activation {[ true ]}
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
                    goal Achieved : Achieve activation {[ self.enabled ]} condition {[ self.p ]}
                    goal Maintained : Maintain activation {[ self.enabled ]} condition {[ self.p ]}
                    goal Sustained : Sustain activation {[ self.enabled ]} condition {[ self.p ]}
                    goal Recurrent : Recur activation {[ self.enabled ]} condition {[ self.p ]}
                  }
                }
                """);
        Files.writeString(useFile, """
                model TemporalRuntime
                class Controller
                attributes
                  enabled : Boolean
                  p : Boolean
                end
                """);
        Files.writeString(soilFile, """
                !create controller : Controller
                !set controller.enabled := false
                !set controller.p := true
                !set controller.enabled := true
                !set controller.p := false
                """);

        var result = IStarUseTraceCompiler.compile(istarFile, useFile, soilFile);
        assertTrue(result.ok(), () -> String.join("\n", result.errors()));
        var key = new IStarUseTraceCompiler.InstanceKey("Controller", "controller");
        var fulfilled = result.checkpoints().get(3).markings().get(key);
        assertEquals(GoalTaskStatus.FULFILLED, fulfilled.goalTaskStatus("Achieved"));
        assertEquals(GoalTaskStatus.FULFILLED, fulfilled.goalTaskStatus("Maintained"));
        assertEquals(GoalTaskStatus.FULFILLED, fulfilled.goalTaskStatus("Sustained"));
        assertEquals(GoalTaskStatus.FULFILLED, fulfilled.goalTaskStatus("Recurrent"));

        var fallen = result.checkpoints().get(4).markings().get(key);
        assertEquals(GoalTaskStatus.FULFILLED, fallen.goalTaskStatus("Achieved"));
        assertEquals(GoalTaskStatus.VIOLATED, fallen.goalTaskStatus("Maintained"));
        assertEquals(GoalTaskStatus.VIOLATED, fallen.goalTaskStatus("Sustained"));
        assertEquals(GoalTaskStatus.PENDING, fallen.goalTaskStatus("Recurrent"));
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
                    activation {[ true ]}
                    goal AlwaysClosed : Maintain > EventuallySafe
                    goal UsuallyLocked : Sustain > EventuallySafe
                  }
                }
                """);
    }

    @Test
    void activeObstacleBlocksUntilADeclaredHandlerResolvesIt() throws Exception {
        var model = compile("""
                istar ObstacleHandling {
                  role Scheduler {
                    goal MeetingScheduled : Achieve
                    activation {[ true ]}
                    task PerformScheduling > or MeetingScheduled
                    obstacle NoCommonSlot : Prevention > obstructs MeetingScheduled
                    task RequestRescheduling > resolves NoCommonSlot
                  }
                }
                """);

        IStarMarking marking = IStarMarking.initial(model);
        marking = IStarPropagation.assignObstacle(
                model, marking, "NoCommonSlot", ObstacleStatus.ACTIVE);
        marking = IStarPropagation.fire(model, marking, "PerformScheduling");
        assertEquals(GoalTaskStatus.PENDING, marking.goalTaskStatus("MeetingScheduled"));
        assertEquals(ObstacleStatus.ACTIVE, marking.obstacleStatus("NoCommonSlot"));

        marking = IStarPropagation.fire(model, marking, "RequestRescheduling");
        assertEquals(ObstacleStatus.RESOLVED, marking.obstacleStatus("NoCommonSlot"));
        assertEquals(GoalTaskStatus.FULFILLED, marking.goalTaskStatus("MeetingScheduled"),
                "after resolution the existing fulfilled refinement may propagate again");
    }

    @Test
    void flatMarkingNeverPretendsToKnowForallOrPickUniverse() throws Exception {
        var model = compile("""
                istar QuantifiedRefinement {
                  role Initiator {
                    goal EveryParticipantConfirmed : Achieve
                    activation {[ true ]}
                    goal ParticipantConfirmed : Achieve
                      > forall Participant EveryParticipantConfirmed
                  }
                  role Participant { }
                }
                """);

        IStarMarking marking = IStarPropagation.fire(
                model, IStarMarking.initial(model), "ParticipantConfirmed");
        assertEquals(GoalTaskStatus.UNKNOWN, marking.goalTaskStatus("EveryParticipantConfirmed"),
                "only an instance-aware evaluator may aggregate a quantified refinement");
    }

    @Test
    void forallAggregatesEveryBoundInstanceAndPickNeedsOnlyOne() throws Exception {
        var model = compile("""
                istar QuantifiedInstances {
                  role Initiator {
                    goal EveryParticipantConfirmed : Achieve
                    activation {[ true ]}
                    goal ParticipantConfirmed : Achieve
                      > forall Participant EveryParticipantConfirmed
                    goal AnOrganizerCompleted : Achieve
                    activation {[ true ]}
                    goal OrganizerCompleted : Achieve
                      > pick Organizer AnOrganizerCompleted
                  }
                  role Participant { }
                  role Organizer { }
                }
                """);
        List<ScenarioInstance> instances = List.of(
                new ScenarioInstance("initiator", "Initiator"),
                new ScenarioInstance("participantOne", "Participant"),
                new ScenarioInstance("participantTwo", "Participant"),
                new ScenarioInstance("organizerOne", "Organizer"),
                new ScenarioInstance("organizerTwo", "Organizer"));

        var partial = GoalModelInstanceTranslator.evaluate(model, new IStarScenarioModel(
                "partial", "", instances, List.of(
                        new ScenarioStmt.Fire("initiator", "ParticipantConfirmed", "participantOne"),
                        new ScenarioStmt.Fire("initiator", "OrganizerCompleted", "organizerTwo"))));
        assertEquals(GoalTaskStatus.UNKNOWN,
                partial.instanceMarking().goalTaskStatus("EveryParticipantConfirmed__initiator"));
        assertEquals(GoalTaskStatus.FULFILLED,
                partial.instanceMarking().goalTaskStatus("AnOrganizerCompleted__initiator"));

        var complete = GoalModelInstanceTranslator.evaluate(model, new IStarScenarioModel(
                "complete", "", instances, List.of(
                        new ScenarioStmt.Fire("initiator", "ParticipantConfirmed", "participantOne"),
                        new ScenarioStmt.Fire("initiator", "ParticipantConfirmed", "participantTwo"))));
        assertEquals(GoalTaskStatus.FULFILLED,
                complete.instanceMarking().goalTaskStatus("EveryParticipantConfirmed__initiator"));
    }

    @Test
    void obstacleRelationsRemainEffectiveAfterInstanceExpansion() throws Exception {
        var model = compile("""
                istar InstanceObstacle {
                  role Scheduler {
                    goal MeetingScheduled : Achieve
                    activation {[ true ]}
                    obstacle NoCommonSlot : Prevention > obstructs MeetingScheduled
                    task RequestRescheduling > resolves NoCommonSlot
                  }
                }
                """);
        var evaluation = GoalModelInstanceTranslator.evaluate(model, new IStarScenarioModel(
                "obstacle", "", List.of(new ScenarioInstance("scheduler", "Scheduler")), List.of(
                        new ScenarioStmt.Assign("scheduler", "NoCommonSlot", "Active"),
                        new ScenarioStmt.Assign("scheduler", "MeetingScheduled", "Fulfilled"))));
        assertEquals(ObstacleStatus.ACTIVE,
                evaluation.instanceMarking().obstacleStatus("NoCommonSlot__scheduler"));
        assertEquals(GoalTaskStatus.PENDING,
                evaluation.instanceMarking().goalTaskStatus("MeetingScheduled__scheduler"));
    }

    @Test
    void scenarioLanguageCanActivateAndResolveObstacleOccurrences() throws Exception {
        Path modelFile = temporaryDirectory.resolve("scenario-obstacle.istar");
        Files.writeString(modelFile, """
                istar ScenarioObstacle {
                  role Scheduler {
                    goal MeetingScheduled : Achieve
                    activation {[ true ]}
                    obstacle NoCommonSlot : Prevention > obstructs MeetingScheduled
                    task RequestRescheduling > resolves NoCommonSlot
                  }
                }
                """);
        Path scenarioFile = temporaryDirectory.resolve("scenario-obstacle.iscn");
        Files.writeString(scenarioFile, """
                scenario ObstacleOccurrence for "scenario-obstacle.istar" {
                  instance scheduler : Scheduler;
                  assign scheduler.NoCommonSlot = Active;
                  fire scheduler.RequestRescheduling;
                }
                """);

        IStarScenarioCompiler.Result result = IStarScenarioCompiler.compile(scenarioFile);
        assertTrue(result.ok(), () -> String.join("\n", result.errors()));
        assertEquals(ObstacleStatus.RESOLVED,
                result.evaluation().instanceMarking().obstacleStatus("NoCommonSlot__scheduler"));
    }

    private org.vnu.sme.goal.dsl.istar.mm.GoalModel compile(String source) throws Exception {
        Path file = temporaryDirectory.resolve("model" + System.nanoTime() + ".istar");
        Files.writeString(file, source);
        IStarCompiler.Result result = IStarCompiler.compile(file);
        assertTrue(result.ok(), () -> String.join("\n", result.errors()));
        return result.model();
    }
}
