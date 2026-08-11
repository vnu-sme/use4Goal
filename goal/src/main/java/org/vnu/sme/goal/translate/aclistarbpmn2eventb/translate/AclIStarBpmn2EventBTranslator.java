package org.vnu.sme.goal.translate.aclistarbpmn2eventb.translate;

import java.text.Normalizer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vnu.sme.goal.dsl.acl.mm.AclModel;
import org.vnu.sme.goal.dsl.bpmn.mm.BpmnModel;
import org.vnu.sme.goal.dsl.bpmn.mm.EndEvent;
import org.vnu.sme.goal.dsl.istar.mm.GoalModel;
import org.vnu.sme.goal.translate.aclistarbpmn2eventb.ir.EventBProject;
import org.vnu.sme.goal.translate.aclistarbpmn2eventb.ir.EventBProject.*;

/** Composes the independently generated ACL+BPMN transition system and iStar evaluator. */
public final class AclIStarBpmn2EventBTranslator {
    private AclIStarBpmn2EventBTranslator() {}

    public static EventBProject translate(String projectName,AclModel acl,GoalModel goals,
                                          BpmnModel bpmn,List<String> diagnostics) {
        EventBProject process=AclBpmn2EventBTranslator.translate(projectName,acl,bpmn,diagnostics);
        var satisfaction=IStarSatisfactionCompiler.compileStandalone(
                goals,acl,AclEventBCompositionSupport.symbols(acl),diagnostics);

        List<String> sets=new ArrayList<>(process.context().sets());
        sets.addAll(satisfaction.contextSets()); sets.add("EVALUATION_PHASE");
        List<Constant> constants=new ArrayList<>(process.context().constants());
        constants.addAll(satisfaction.constants());
        constants.add(new Constant("READY")); constants.add(new Constant("DIRTY"));
        List<Predicate> axioms=new ArrayList<>(process.context().axioms());
        axioms.addAll(satisfaction.axioms());
        axioms.add(new Predicate("axm_evaluation_phase","partition(EVALUATION_PHASE,{READY},{DIRTY})",false));

        List<String> variables=new ArrayList<>(process.machine().variables());
        variables.add("intentionalPhase"); variables.addAll(satisfaction.state().variables());
        List<Predicate> invariants=new ArrayList<>(process.machine().invariants());
        invariants.add(new Predicate("inv_intentional_phase","intentionalPhase ∈ EVALUATION_PHASE",false));
        for(String formula:satisfaction.state().invariants())
            invariants.add(new Predicate("inv_istar_"+(invariants.size()+1),formula,false));
        for(String formula:satisfaction.maintainInvariants())
            invariants.add(new Predicate("inv_maintain_"+(invariants.size()+1),formula,false));

        Event oldInit=process.machine().events().get(0);
        List<Assignment> init=new ArrayList<>(oldInit.actions());
        init.add(new Assignment("act_phase","intentionalPhase ≔ READY"));
        for(var assignment:satisfaction.state().initialisation()) {
            String formula=assignment.formula();
            // ACL class extents start empty, so contextual occurrence sets also start empty.
            if(assignment.label().endsWith("_S"))
                formula=formula.substring(0,formula.indexOf(" ≔ "))+" ≔ ∅";
            init.add(new Assignment(assignment.label(),formula));
        }

        Set<String> endLabels=new LinkedHashSet<>();
        bpmn.processes().forEach(p->p.flowElements().stream().filter(EndEvent.class::isInstance)
                .forEach(x->endLabels.add(id(x.id()))));
        List<Event> events=new ArrayList<>();
        events.add(new Event("INITIALISATION",List.of(),List.of(),init));
        for(int i=1;i<process.machine().events().size();i++) {
            Event event=process.machine().events().get(i);
            List<Predicate> guards=new ArrayList<>(event.guards());
            guards.add(new Predicate("grd_goals_ready","intentionalPhase=READY",false));
            int maintainNo=0;
            for(String maintain:satisfaction.maintainInvariants()) {
                String alpha=alphaRenameSelf(maintain);
                String wp=weakestPrecondition(alpha,event.actions());
                if(!wp.equals(alpha))
                    guards.add(new Predicate("grd_maintain_post_"+(++maintainNo),wp,false));
            }
            if(endLabels.contains(event.label())) {
                int n=0;
                for(var root:satisfaction.rootContracts())
                    guards.add(new Predicate("grd_root_goal_"+(++n),
                            root.ownerRelation()+"[{self}] ⊆ "+root.progress(),false));
            }
            List<Assignment> actions=new ArrayList<>(event.actions());
            actions.add(new Assignment("act_goals_dirty","intentionalPhase ≔ DIRTY"));
            events.add(new Event(event.label(),event.parameters(),guards,actions));
        }
        List<Assignment> evaluate=new ArrayList<>();
        satisfaction.state().observation().forEach(x->evaluate.add(new Assignment(x.label(),x.formula())));
        evaluate.add(new Assignment("act_goals_ready","intentionalPhase ≔ READY"));
        events.add(new Event("EvaluateAllGoals",List.of(),
                List.of(new Predicate("grd_goals_dirty","intentionalPhase=DIRTY",false)),evaluate));

        List<Trace> traces=new ArrayList<>(process.traces()); traces.addAll(satisfaction.traces());
        traces.add(new Trace("Composition","BPMN step -> complete iStar reevaluation",
                "READY -> DIRTY -> EvaluateAllGoals -> READY"));
        Context context=new Context(process.context().name(),List.copyOf(new LinkedHashSet<>(sets)),constants,axioms);
        Machine machine=new Machine(process.machine().name(),context.name(),variables,invariants,events);
        return new EventBProject(process.name(),context,machine,traces,satisfaction.properties());
    }

    /** Substitutes all simultaneous deterministic assignments into a safety predicate. */
    private static String weakestPrecondition(String predicate,List<Assignment> actions) {
        String result=predicate;
        List<String> replacements=new ArrayList<>(); int index=0;
        for(Assignment action:actions) {
            int split=action.formula().indexOf(" ≔ ");
            if(split<0) continue;
            String variable=action.formula().substring(0,split).trim();
            String expression=action.formula().substring(split+3).trim();
            String placeholder="__EVENTB_WP_"+(index++)+"__";
            String next=result.replaceAll("\\b"+Pattern.quote(variable)+"\\b",Matcher.quoteReplacement(placeholder));
            if(!next.equals(result)) replacements.add(placeholder+"\u0000"+expression);
            result=next;
        }
        for(String replacement:replacements) {
            int split=replacement.indexOf('\u0000');
            result=result.replace(replacement.substring(0,split),"("+replacement.substring(split+1)+")");
        }
        return result;
    }

    /** Avoids capture by BPMN's Group-scoped event parameter named {@code self}. */
    private static String alphaRenameSelf(String predicate) {
        if(!predicate.contains("∀self·")&&!predicate.contains("∃self·")) return predicate;
        return predicate.replaceAll("\\bself\\b","goalSelf");
    }

    public static String id(String value) {
        String normalized=Normalizer.normalize(value==null?"model":value,Normalizer.Form.NFD)
                .replaceAll("\\p{M}+","").replaceAll("[^A-Za-z0-9_]","_")
                .replaceAll("_+","_").replaceAll("^_+|_+$","");
        if(normalized.isBlank()) normalized="model";
        if(Character.isDigit(normalized.charAt(0))) normalized="_"+normalized;
        return normalized;
    }
}
