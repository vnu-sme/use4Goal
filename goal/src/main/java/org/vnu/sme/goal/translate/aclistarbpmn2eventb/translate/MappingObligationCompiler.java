package org.vnu.sme.goal.translate.aclistarbpmn2eventb.translate;

import static org.vnu.sme.goal.analysis.mapping.MappingVerificationPlan.GenerationStatus.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vnu.sme.goal.analysis.mapping.MappingVerificationPlan;
import org.vnu.sme.goal.analysis.mapping.SemanticMappingAnalysis;
import org.vnu.sme.goal.analysis.mapping.SemanticMappingAnalysis.CandidateStatus;
import org.vnu.sme.goal.analysis.mapping.SemanticMappingAnalysis.MappingKind;
import org.vnu.sme.goal.dsl.acl.mm.AclModel;
import org.vnu.sme.goal.dsl.bpmn.mm.BpmnModel;
import org.vnu.sme.goal.dsl.istar.mm.ContextResolution;
import org.vnu.sme.goal.dsl.istar.mm.GoalModel;
import org.vnu.sme.goal.dsl.istar.mm.Task;
import org.vnu.sme.goal.translate.aclistarbpmn2eventb.ir.EventBProject;
import org.vnu.sme.goal.translate.aclistarbpmn2eventb.ir.EventBProject.Assignment;
import org.vnu.sme.goal.translate.aclistarbpmn2eventb.ir.EventBProject.Event;
import org.vnu.sme.goal.translate.aclistarbpmn2eventb.ir.EventBProject.Machine;
import org.vnu.sme.goal.translate.aclistarbpmn2eventb.ir.EventBProject.Predicate;
import org.vnu.sme.goal.translate.aclistarbpmn2eventb.ir.EventBProject.Property;

/** Turns statically supported atomic mappings into Rodin theorem guards and ProB LTL checks. */
public final class MappingObligationCompiler {
    public record Result(EventBProject project, MappingVerificationPlan plan) {}
    private MappingObligationCompiler() {}

    public static Result compile(EventBProject project, SemanticMappingAnalysis analysis,
                                 AclModel acl, GoalModel goals, BpmnModel bpmn) {
        Map<String,String> symbols=AclEventBCompositionSupport.symbols(acl);
        ContextResolution contexts=ContextResolution.of(goals);
        Map<String,List<SemanticMappingAnalysis.MappingEntry>> byActivity=new LinkedHashMap<>();
        analysis.mappings().stream()
                .filter(x->x.kind()==MappingKind.REALIZES)
                .filter(x->x.candidateStatus()==CandidateStatus.STATICALLY_SUPPORTED)
                .forEach(x->byActivity.computeIfAbsent(x.activityIds().get(0),ignored->new ArrayList<>()).add(x));

        List<Event> events=new ArrayList<>();
        List<Property> properties=new ArrayList<>(project.properties());
        List<MappingVerificationPlan.Obligation> obligations=new ArrayList<>();
        for(Event event:project.machine().events()) {
            List<SemanticMappingAnalysis.MappingEntry> candidates=byActivity.getOrDefault(sourceActivity(event,bpmn),List.of());
            if(candidates.isEmpty()) { events.add(event); continue; }
            List<Predicate> guards=new ArrayList<>(event.guards());
            List<Predicate> assumptions=new ArrayList<>(event.guards());
            for(var mapping:candidates) {
                var built=build(mapping,event,assumptions,acl,goals,contexts,symbols);
                obligations.add(built.obligation());
                if(built.guard()!=null) {
                    guards.add(built.guard());
                    properties.add(built.property());
                }
            }
            events.add(new Event(event.label(),event.parameters(),guards,event.actions()));
        }
        // Entries not reached above (for example a nested subprocess whose generated Event label
        // cannot be resolved) remain visible in the report instead of silently disappearing.
        for(var mapping:analysis.mappings()) if(mapping.candidateStatus()==CandidateStatus.STATICALLY_SUPPORTED
                && obligations.stream().noneMatch(x->x.mappingId().equals(mapping.id())))
            obligations.add(new MappingVerificationPlan.Obligation(mapping.id(),UNSUPPORTED,"","","",
                    mapping.kind()==MappingKind.COMPOSITE_REALIZES
                            ? "Composite mappings require an Event-B refinement chain; no atomic theorem was generated."
                            : mapping.kind()==MappingKind.ENABLES
                            ? "ENABLES is diagnostic evidence; an executability/refinement obligation is required."
                            : "No generated BPMN Event could be resolved for this Activity."));

        Machine machine=new Machine(project.machine().name(),project.machine().contextName(),
                project.machine().variables(),project.machine().invariants(),events);
        return new Result(new EventBProject(project.name(),project.context(),machine,project.traces(),properties),
                new MappingVerificationPlan(obligations));
    }

    private record Built(Predicate guard,Property property,MappingVerificationPlan.Obligation obligation) {}

    private static Built build(SemanticMappingAnalysis.MappingEntry mapping,Event event,List<Predicate> assumptions,
                               AclModel acl,GoalModel goals,ContextResolution contexts,Map<String,String> symbols) {
        var element=goals.findElement(mapping.taskId()).orElse(null);
        if(!(element instanceof Task task)||task.postconditions().isEmpty()) return unsupported(mapping,"Task has no postcondition.");
        List<String> types=contexts.contextTypesOf(goals,task.id());
        if(types.size()!=1) return unsupported(mapping,
                "Task has a multi-occurrence context; generate an explicit group/atomicity refinement.");
        String type=types.get(0), variable;
        boolean performer=event.parameters().contains("performer")&&type.equals(mapping.laneRole());
        if(performer) variable="performer";
        else if(event.parameters().contains("self")&&symbols.get("$owner."+type)!=null) variable="mappedTask";
        else return unsupported(mapping,"Cannot bind the Task occurrence to this BPMN Event's ACL context.");

        List<String> translated=new ArrayList<>();
        for(var post:task.postconditions()) {
            String predicate=OclToEventB.translateContext(post.oclBody(),List.of(type),List.of(variable),symbols,acl);
            if(predicate==null) return unsupported(mapping,"Unsupported Task OCL in mapping obligation: "+oneLine(post.oclBody()));
            translated.add("("+predicate+")");
        }
        String post=String.join(" ∧ ",translated);
        if(!performer) post="(∀mappedTask·self ↦ mappedTask ∈ "+symbols.get("$owner."+type)+" ⇒ ("+post+"))";
        String wp=weakestPrecondition(post,event.actions());
        if(wp.equals(post)) return unsupported(mapping,
                "The generated Activity actions do not update any state referenced by the Task postcondition.");

        String label="map_"+safe(mapping.id())+"_sound";
        Predicate theorem=new Predicate(label,wp,true);
        String enabled=assumptions.isEmpty()?"TRUE = TRUE":assumptions.stream()
                .map(x->"("+x.formula()+")").reduce((a,b)->a+" ∧ "+b).orElse("TRUE = TRUE");
        String body="("+enabled+") ⇒ ("+wp+")";
        if(!event.parameters().isEmpty()) body="∀"+String.join(",",event.parameters())+"·"+body;
        String propertyId="LTL_"+safe(mapping.id())+"_SOUND";
        Property property=new Property(propertyId,"MAPPING_SOUNDNESS","G({"+body+"})",
                "candidate mapping "+String.join("+",mapping.activityIds())+" -> "+mapping.taskId());
        String po=event.label()+"/"+label+"/THM";
        var obligation=new MappingVerificationPlan.Obligation(mapping.id(),GENERATED,po,propertyId,wp,
                "Rodin proves the Activity's weakest precondition; ProB checks the same implication on reachable states.");
        return new Built(theorem,property,obligation);
    }

    private static Built unsupported(SemanticMappingAnalysis.MappingEntry mapping,String explanation) {
        return new Built(null,null,new MappingVerificationPlan.Obligation(mapping.id(),UNSUPPORTED,"","","",explanation));
    }

    /** Finds the source Activity whose sanitized id is the generated Event label. */
    private static String sourceActivity(Event event,BpmnModel bpmn) {
        for(var process:bpmn.processes()) for(var activity:activities(process.flowElements()))
            if(AclIStarBpmn2EventBTranslator.id(activity.id()).equals(event.label())) return activity.id();
        return null;
    }

    private static List<org.vnu.sme.goal.dsl.bpmn.mm.Activity> activities(
            List<org.vnu.sme.goal.dsl.bpmn.mm.FlowElement> elements) {
        List<org.vnu.sme.goal.dsl.bpmn.mm.Activity> result=new ArrayList<>();
        for(var element:elements) if(element instanceof org.vnu.sme.goal.dsl.bpmn.mm.Activity activity) {
            result.add(activity);
            if(activity instanceof org.vnu.sme.goal.dsl.bpmn.mm.SubProcess sub) result.addAll(activities(sub.flowElements()));
        }
        return result;
    }

    /** Simultaneous deterministic assignment substitution used for an Event theorem guard. */
    private static String weakestPrecondition(String predicate,List<Assignment> actions) {
        String result=predicate; List<String> replacements=new ArrayList<>(); int index=0;
        for(Assignment action:actions) {
            int split=action.formula().indexOf(" ≔ "); if(split<0) continue;
            String variable=action.formula().substring(0,split).trim();
            String expression=action.formula().substring(split+3).trim();
            String placeholder="__MAP_WP_"+(index++)+"__";
            String next=result.replaceAll("\\b"+Pattern.quote(variable)+"\\b",Matcher.quoteReplacement(placeholder));
            if(!next.equals(result)) replacements.add(placeholder+'\u0000'+expression);
            result=next;
        }
        for(String replacement:replacements) {
            int split=replacement.indexOf('\u0000');
            result=result.replace(replacement.substring(0,split),"("+replacement.substring(split+1)+")");
        }
        return result;
    }

    private static String safe(String value){return AclIStarBpmn2EventBTranslator.id(value);}
    private static String oneLine(String value){return value==null?"":value.replaceAll("\\s+"," ").trim();}
}
