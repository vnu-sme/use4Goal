package org.vnu.sme.goal.trace.istartrace.nativeacl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vnu.sme.goal.dsl.acl.mm.AclModel;
import org.vnu.sme.goal.dsl.acl.parser.AclCompiler;
import org.vnu.sme.goal.dsl.aol.mm.AolModel;
import org.vnu.sme.goal.trace.bpmn.BpmnAolTraceFile;
import org.vnu.sme.goal.verify.conformance.semantics.GoalMarking;
import org.vnu.sme.goal.verify.conformance.semantics.GoalTaskStatus;
import org.vnu.sme.goal.verify.conformance.semantics.IStarMarking;
import org.vnu.sme.goal.verify.conformance.semantics.IStarPropagation;
import org.vnu.sme.goal.verify.conformance.semantics.TaskMarking;
import org.vnu.sme.goal.dsl.istar.mm.Dependency;
import org.vnu.sme.goal.dsl.istar.mm.Goal;
import org.vnu.sme.goal.dsl.istar.mm.GoalActivationGraph;
import org.vnu.sme.goal.dsl.istar.mm.GoalModel;
import org.vnu.sme.goal.dsl.istar.mm.IntentionalElement;
import org.vnu.sme.goal.dsl.istar.mm.Task;
import org.vnu.sme.goal.dsl.istar.parser.IStarCompiler;
import org.vnu.sme.goal.dsl.iscn.goalmodelinstance.parser.GoalModelInstanceEvaluation;
import org.vnu.sme.goal.dsl.iscn.goalmodelinstance.parser.GoalModelInstanceTranslator;
import org.vnu.sme.goal.dsl.iscn.mm.IStarScenarioModel;
import org.vnu.sme.goal.dsl.iscn.mm.ScenarioInstance;

/** Verifies an already-generated BPMN trace against iStar directly over ACL/AOL snapshots. */
public final class BpmnTraceIStarMonitor {
    public record Frame(int index, String activityId, List<String> delta, AolModel snapshot,
                        GoalModel instanceModel, IStarMarking marking,
                        Map<String,String> actorLabels, Map<String,String> nodeLabels,
                        Map<String,GoalMarking> goalMarkings,
                        Map<String,TaskMarking> taskMarkings) {}
    public record ProcessResult(String processId, String selfObjectName, boolean ended,
                                List<Frame> frames) {}
    public record Result(AclModel acl, GoalModel istar, List<ProcessResult> processes,
                         List<String> errors) {
        public boolean ok(){return errors.isEmpty();}
    }

    private BpmnTraceIStarMonitor() {}

    public static Result run(Path aclFile, Path istarFile, Path traceFile) {
        List<String> errors=new ArrayList<>();
        try {
            var aclResult=AclCompiler.compile(aclFile); if(!aclResult.ok())return new Result(null,null,List.of(),aclResult.errors());
            var istarResult=IStarCompiler.compile(istarFile); if(!istarResult.ok())return new Result(aclResult.model(),null,List.of(),istarResult.errors());
            var trace=BpmnAolTraceFile.read(traceFile);
            List<ProcessResult> processes=new ArrayList<>();
            for(var process:trace.traces()) processes.add(runProcess(aclResult.model(),istarResult.model(),process,errors));
            return new Result(aclResult.model(),istarResult.model(),List.copyOf(processes),List.copyOf(errors));
        } catch(IOException|RuntimeException ex){errors.add(ex.getMessage()==null?ex.toString():ex.getMessage());return new Result(null,null,List.of(),errors);}
    }

    private static ProcessResult runProcess(AclModel acl, GoalModel source,
            BpmnAolTraceFile.InstanceTrace trace, List<String> errors) {
        List<Frame> frames=new ArrayList<>(); Map<String,GoalMarking> oldGoals=Map.of(); Map<String,TaskMarking> oldTasks=Map.of();
        for(var step:trace.steps()){
            AclSnapshot snapshot=AclSnapshot.of(acl,step.model());
            GoalModelInstanceEvaluation inst=instantiate(source,snapshot);
            Map<String,GoalMarking> goals=new LinkedHashMap<>(); Map<String,TaskMarking> tasks=new LinkedHashMap<>();
            IStarMarking marking=IStarMarking.initial(inst.instanceModel());
            GoalActivationGraph graph=GoalActivationGraph.of(inst.instanceModel());
            for(var entry:inst.goalModelInstance().elementInstanceOf().entrySet()){
                String occurrenceId=entry.getKey(); IntentionalElement sourceElement=entry.getValue();
                List<AclSnapshot.ObjectValue> context=contextOf(inst.nodeLabels().get(occurrenceId),snapshot);
                try {
                    if(sourceElement instanceof Goal goal){
                        // Structural goals have no state predicate of their own. Their status
                        // is produced exclusively by refinement/dependency propagation below.
                        if (goal.conditions().isEmpty()) continue;
                        boolean demanded=structurallyDemanded(
                                occurrenceId,graph,inst,snapshot);
                        boolean activation=goal.activations().isEmpty()||goal.activations().stream()
                                .allMatch(x->NativeOclEvaluator.evaluate(x.oclBody(),snapshot,context));
                        boolean predicate=goal.conditions().stream()
                                .allMatch(x->NativeOclEvaluator.evaluate(x.oclBody(),snapshot,context));
                        GoalMarking old=oldGoals.getOrDefault(occurrenceId,GoalMarking.initial(goal.goalType()));
                        GoalMarking current=old.update(demanded&&activation,predicate);
                        goals.put(occurrenceId,current);
                        if(current.status()!=GoalTaskStatus.UNKNOWN)
                            marking=IStarPropagation.assignGoalTask(inst.instanceModel(),marking,occurrenceId,current.status());
                    } else if(sourceElement instanceof Task task){
                        boolean demanded=structurallyDemanded(
                                occurrenceId,graph,inst,snapshot);
                        boolean pre=!task.preconditions().isEmpty()&&task.preconditions().stream()
                                .allMatch(x->NativeOclEvaluator.evaluate(x.oclBody(),snapshot,context));
                        boolean post=!task.postconditions().isEmpty()&&task.postconditions().stream()
                                .allMatch(x->NativeOclEvaluator.evaluate(x.oclBody(),snapshot,context));
                        TaskMarking current=oldTasks.getOrDefault(occurrenceId,TaskMarking.initial())
                                .update(demanded,pre,post);
                        tasks.put(occurrenceId,current);
                        if(current.status()!=GoalTaskStatus.UNKNOWN)
                            marking=IStarPropagation.assignGoalTask(inst.instanceModel(),marking,occurrenceId,current.status());
                    }
                } catch(RuntimeException ex){errors.add("process "+trace.processId()+", step "+step.index()+", "+sourceElement.id()+": "+ex.getMessage());}
            }
            marking=saturateDependencies(inst.instanceModel(),IStarPropagation.closePending(inst.instanceModel(),marking));
            frames.add(new Frame(step.index(),step.activityId(),step.delta(),step.model(),inst.instanceModel(),marking,
                    inst.actorLabels(),inst.nodeLabels(),Map.copyOf(goals),Map.copyOf(tasks)));
            oldGoals=goals;oldTasks=tasks;
        }
        return new ProcessResult(trace.processId(),trace.selfObjectName(),trace.ended(),List.copyOf(frames));
    }

    private static GoalModelInstanceEvaluation instantiate(GoalModel source,AclSnapshot snapshot){
        List<ScenarioInstance> instances=new ArrayList<>();
        for(var actor:source.getActors()) for(var object:snapshot.objectsOfType(actor.name()))
            if(object.kind()==AclSnapshot.Kind.ROLE) instances.add(new ScenarioInstance(object.id(),actor.name()));
        IStarScenarioModel scenario=new IStarScenarioModel("BpmnTraceSnapshot","",instances,List.of());
        return GoalModelInstanceTranslator.evaluate(source,scenario,(lt,li,rt,ri)->{
            var left=snapshot.object(li);
            var right=snapshot.object(ri);
            return left!=null&&right!=null&&java.util.Objects.equals(left.groupId(),right.groupId());
        });
    }

    private static List<AclSnapshot.ObjectValue> contextOf(String label,AclSnapshot snapshot){
        if(label==null)return List.of();int open=label.lastIndexOf('['),close=label.lastIndexOf(']');
        if(open<0||close<=open)return List.of();List<AclSnapshot.ObjectValue> out=new ArrayList<>();
        for(String id:label.substring(open+1,close).split(",")){var object=snapshot.object(id.strip());if(object!=null)out.add(object);}return out;
    }

    private static boolean structurallyDemanded(String id,GoalActivationGraph graph,
            GoalModelInstanceEvaluation evaluation,AclSnapshot snapshot){
        Set<String> seen=new LinkedHashSet<>();String current=id;
        while(seen.add(current)){
            var parent=graph.parentOf(current);if(parent.isEmpty())return true;current=parent.get();
            IntentionalElement source=evaluation.goalModelInstance().elementInstanceOf().get(current);
            if(source instanceof Goal goal&&!goal.activations().isEmpty()){
                List<AclSnapshot.ObjectValue> context=contextOf(evaluation.nodeLabels().get(current),snapshot);
                if(!goal.activations().stream().allMatch(x->NativeOclEvaluator.evaluate(x.oclBody(),snapshot,context)))return false;
            }
        }
        return false;
    }

    private static IStarMarking saturateDependencies(GoalModel model,IStarMarking input){
        IStarMarking current=input;boolean changed=true;int guard=0;
        while(changed&&guard++<10_000){changed=false;for(Dependency d:model.getDependencies()){
            if(d.dependerElmt()==null)continue;
            boolean direct=model.findElement(d.dependerElmt()).filter(Goal.class::isInstance).map(Goal.class::cast)
                    .map(g->!g.conditions().isEmpty()).orElse(false);
            if(direct||current.goalTaskStatus(d.dependerElmt())==GoalTaskStatus.FULFILLED)continue;
            GoalTaskStatus dependum=current.goalTaskStatus(d.dependum());
            if((dependum==GoalTaskStatus.FULFILLED||dependum==GoalTaskStatus.PENDING)
                    &&current.goalTaskStatus(d.dependerElmt())!=dependum){
                current=IStarPropagation.assignGoalTask(model,current,d.dependerElmt(),dependum);
                changed=true;
            }
        }}return IStarPropagation.closePending(model,current);
    }
}
