package org.vnu.sme.goal.translate.aclistarbpmn2eventb.translate;

import java.util.ArrayList;
import java.util.List;

import org.vnu.sme.goal.dsl.acl.mm.AclModel;
import org.vnu.sme.goal.dsl.istar.mm.*;
import org.vnu.sme.goal.translate.acl2eventb.AclToEventBTranslator;
import org.vnu.sme.goal.translate.aclistarbpmn2eventb.ir.EventBProject;
import org.vnu.sme.goal.translate.aclistarbpmn2eventb.ir.EventBProject.*;

/** ACL UML-B state plus iStar intentional monitoring; no BPMN control state. */
public final class AclIStar2EventBTranslator {
    private AclIStar2EventBTranslator() {}

    public static EventBProject translate(String projectName,AclModel acl,GoalModel goals,
                                          List<String> diagnostics) {
        EventBProject base=AclToEventBTranslator.translate(projectName,acl,diagnostics);
        var satisfaction=IStarSatisfactionCompiler.compileStandalone(
                goals,acl,AclEventBCompositionSupport.symbols(acl),diagnostics);
        List<String> sets=new ArrayList<>(base.context().sets());
        sets.addAll(satisfaction.contextSets());
        List<Constant> constants=new ArrayList<>(base.context().constants());
        constants.addAll(satisfaction.constants());
        List<Predicate> axioms=new ArrayList<>(base.context().axioms());
        axioms.addAll(satisfaction.axioms());
        List<String> variables=new ArrayList<>(base.machine().variables());
        variables.addAll(satisfaction.state().variables());
        List<Predicate> invariants=new ArrayList<>(base.machine().invariants());
        for(String formula:satisfaction.state().invariants())
            invariants.add(new Predicate("inv_istar_"+(invariants.size()+1),formula,false));
        for(String formula:satisfaction.maintainInvariants())
            invariants.add(new Predicate("inv_maintain_"+(invariants.size()+1),formula,false));

        Event oldInit=base.machine().events().get(0);
        List<Assignment> initActions=new ArrayList<>(oldInit.actions());
        satisfaction.state().initialisation().forEach(x->{
            // ACL class extents/Owner relations are Machine variables and are initialised
            // simultaneously.  A Goal stable set therefore cannot read their before-state
            // in INITIALISATION; since every ACL extent starts empty, its correct initial
            // occurrence set is empty as well.
            String formula=x.label().endsWith("_S")
                    ?x.formula().substring(0,x.formula().indexOf(" ≔ "))+" ≔ ∅":x.formula();
            initActions.add(new Assignment(x.label(),formula));
        });
        List<Event> events=new ArrayList<>();
        events.add(new Event("INITIALISATION",List.of(),List.of(),initActions));
        List<Assignment> observation=new ArrayList<>();
        satisfaction.state().observation().forEach(x->
                observation.add(new Assignment(x.label(),x.formula())));
        events.add(new Event("EvaluateAllGoals",List.of(),List.of(),observation));

        List<Trace> traces=new ArrayList<>(base.traces());
        traces.addAll(satisfaction.traces());
        Context context=new Context(base.context().name(),List.copyOf(new java.util.LinkedHashSet<>(sets)),
                constants,axioms);
        Machine machine=new Machine(base.machine().name(),context.name(),variables,invariants,events);
        return new EventBProject(base.name(),context,machine,traces,satisfaction.properties());
    }
}
