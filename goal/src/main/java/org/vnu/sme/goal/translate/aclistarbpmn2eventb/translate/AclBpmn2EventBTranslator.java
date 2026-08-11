package org.vnu.sme.goal.translate.aclistarbpmn2eventb.translate;

import java.util.*;

import org.vnu.sme.goal.dsl.acl.mm.AclModel;
import org.vnu.sme.goal.dsl.bpmn.mm.*;
import org.vnu.sme.goal.dsl.bpmn.mm.Process;
import org.vnu.sme.goal.translate.acl2eventb.AclToEventBTranslator;
import org.vnu.sme.goal.translate.aclistarbpmn2eventb.ir.EventBProject;
import org.vnu.sme.goal.translate.aclistarbpmn2eventb.ir.EventBProject.*;

/** ACL/UML-B state extended with process identities, scoped BPMN lifecycle and token functions. */
public final class AclBpmn2EventBTranslator {
    private AclBpmn2EventBTranslator() {}

    private record ProcessState(String suffix,String declaration,String carrier,String extent,
                                String lifecycle,String scope,String groupType) {}

    public static EventBProject translate(String projectName,AclModel acl,BpmnModel bpmn,
                                          List<String> diagnostics) {
        EventBProject base=AclToEventBTranslator.translate(projectName,acl,diagnostics);
        Map<String,String> symbols=AclEventBCompositionSupport.symbols(acl);
        List<String> sets=new ArrayList<>(base.context().sets());
        List<Constant> constants=new ArrayList<>(base.context().constants());
        List<Predicate> axioms=new ArrayList<>(base.context().axioms());
        List<String> variables=new ArrayList<>(base.machine().variables());
        List<Predicate> invariants=new ArrayList<>(base.machine().invariants());
        List<Trace> traces=new ArrayList<>(base.traces());
        List<Assignment> init=new ArrayList<>(base.machine().events().get(0).actions());

        if(!bpmn.processes().isEmpty()) {
            sets.addAll(List.of("PROCESS_DECL","PROCESS_STATE","FLOW_DECL"));
            for(String state:List.of("created","active","completed")) constants.add(new Constant(state));
            axioms.add(predicate("axm_process_state","partition(PROCESS_STATE,{created},{active},{completed})"));
        }

        Map<Process,ProcessState> processStates=new IdentityHashMap<>();
        List<String> processDeclarations=new ArrayList<>();
        for(Process process:bpmn.processes()) {
            String suffix=id(process.id()),declaration="p_"+suffix,carrier="PI_"+suffix+"_ID";
            String extent="PI_"+suffix,lifecycle="processState_"+suffix,scope="processScope_"+suffix;
            String groupClass=resolveGroupClass(process,acl,diagnostics);
            String groupType=symbols.get("$type."+groupClass);
            if(groupType==null) fail(diagnostics,"BPMN process '"+process.id()+"' references unknown ACL Group '"+groupClass+"'");
            validateLanes(process,acl,symbols,groupClass,diagnostics);
            ProcessState state=new ProcessState(suffix,declaration,carrier,extent,lifecycle,scope,groupType);
            processStates.put(process,state); processDeclarations.add(declaration);
            sets.add(carrier); constants.add(new Constant(declaration));
            variables.addAll(List.of(extent,lifecycle,scope));
            invariants.add(predicate("inv_type_"+extent,extent+" ⊆ "+carrier));
            invariants.add(predicate("inv_type_"+lifecycle,lifecycle+" ∈ "+extent+" → PROCESS_STATE"));
            invariants.add(predicate("inv_type_"+scope,scope+" ∈ "+extent+" → "+groupType));
            init.add(new Assignment("act_init_"+extent,extent+" ≔ ∅"));
            init.add(new Assignment("act_init_"+lifecycle,lifecycle+" ≔ ∅"));
            init.add(new Assignment("act_init_"+scope,scope+" ≔ ∅"));
            traces.add(new Trace("BPMN","process "+process.id(),declaration+" / "+extent+" / "+scope));
        }
        if(!processDeclarations.isEmpty()) axioms.add(predicate("axm_process_decl",
                partition("PROCESS_DECL",processDeclarations)));

        Map<SequenceFlow,String> flowDeclarations=new IdentityHashMap<>(),tokens=new IdentityHashMap<>();
        List<String> declaredFlows=new ArrayList<>(); int flowNo=0;
        for(Process process:bpmn.processes()) for(SequenceFlow flow:process.sequenceFlows()) {
            String suffix=id(process.id())+"_"+id(flow.source().id())+"_"+id(flow.target().id())+"_"+(++flowNo);
            String declaration="f_"+suffix,token="tk_"+suffix;
            flowDeclarations.put(flow,declaration); tokens.put(flow,token); declaredFlows.add(declaration);
            constants.add(new Constant(declaration)); variables.add(token);
            invariants.add(predicate("inv_type_"+token,token+" ∈ "+processStates.get(process).extent()+" → ℕ"));
            init.add(new Assignment("act_init_"+token,token+" ≔ ∅"));
            traces.add(new Trace("BPMN",flow.source().id()+" -> "+flow.target().id(),declaration+" / "+token));
        }
        if(!declaredFlows.isEmpty()) axioms.add(predicate("axm_flow_decl",partition("FLOW_DECL",declaredFlows)));

        List<EventBProject.Event> events=new ArrayList<>();
        events.add(new EventBProject.Event("INITIALISATION",List.of(),List.of(),init));
        for(Process process:bpmn.processes()) for(FlowElement node:process.flowElements())
            addNodeEvents(process,node,processStates.get(process),tokens,symbols,acl,events,traces,diagnostics);
        if(!bpmn.messageFlows().isEmpty())
            fail(diagnostics,"BPMN message flows require an explicit message-buffer policy");

        Context context=new Context(base.context().name(),List.copyOf(new LinkedHashSet<>(sets)),constants,axioms);
        Machine machine=new Machine(base.machine().name(),context.name(),variables,invariants,events);
        return new EventBProject(base.name(),context,machine,traces,List.of());
    }

    private static void addNodeEvents(Process process,FlowElement node,ProcessState state,
            Map<SequenceFlow,String> tokens,Map<String,String> symbols,AclModel acl,
            List<EventBProject.Event> events,List<Trace> traces,List<String> diagnostics) {
        List<SequenceFlow> incoming=process.sequenceFlows().stream().filter(x->x.target()==node).toList();
        List<SequenceFlow> outgoing=process.sequenceFlows().stream().filter(x->x.source()==node).toList();
        if(node instanceof Gateway gateway && outgoing.size()>1 && gateway.kind()!=GatewayKind.AND) {
            if(gateway.kind()==GatewayKind.OR)
                fail(diagnostics,"Inclusive OR gateway '"+node.id()+"' needs an explicit subset-selection policy");
            for(SequenceFlow branch:outgoing)
                addEvent(process,node,incoming,List.of(branch),branchGuard(branch,outgoing,symbols,process,acl),
                        state,tokens,symbols,acl,events,traces,diagnostics,"_to_"+branch.target().id());
        } else if(incoming.size()>1 && (!(node instanceof Gateway gateway)||gateway.kind()!=GatewayKind.AND)) {
            for(SequenceFlow alternative:incoming)
                addEvent(process,node,List.of(alternative),outgoing,null,state,tokens,symbols,acl,
                        events,traces,diagnostics,"_from_"+alternative.source().id());
        } else addEvent(process,node,incoming,outgoing,null,state,tokens,symbols,acl,
                events,traces,diagnostics,"");
    }

    private static void addEvent(Process process,FlowElement node,List<SequenceFlow> incoming,
            List<SequenceFlow> outgoing,String branchGuard,ProcessState state,Map<SequenceFlow,String> tokens,
            Map<String,String> symbols,AclModel acl,List<EventBProject.Event> events,List<Trace> traces,
            List<String> diagnostics,String suffix) {
        boolean start=node instanceof StartEvent;
        List<String> parameters=new ArrayList<>(List.of("pid","self"));
        List<Predicate> guards=new ArrayList<>();
        if(start) {
            guards.add(predicate("grd_fresh_pid","pid ∈ "+state.carrier()+" ∖ "+state.extent()));
            guards.add(predicate("grd_self","self ∈ "+state.groupType()));
        } else {
            guards.add(predicate("grd_pid","pid ∈ "+state.extent()));
            guards.add(predicate("grd_active",state.lifecycle()+"(pid)=active"));
            guards.add(predicate("grd_self","self="+state.scope()+"(pid)"));
            int tokenNo=0;
            for(SequenceFlow flow:incoming)
                guards.add(predicate("grd_token_"+(++tokenNo),tokens.get(flow)+"(pid)>0"));
        }

        Lane lane=laneOf(process,node);
        if(lane!=null && !(node instanceof Gateway)) {
            String owner=symbols.get("$owner."+lane.id());
            if(owner==null) fail(diagnostics,"BPMN lane '"+lane.id()+"' is not owned by an ACL Group");
            parameters.add("performer");
            guards.add(predicate("grd_performer","self ↦ performer ∈ "+owner));
        }
        int preNo=0;
        for(ActivityConstraint condition:node.preconditions()) {
            String value=translateGuard(condition.oclBody(),symbols,process,acl);
            if(value==null) fail(diagnostics,"Unsupported BPMN OCL at "+node.id()+": "+oneLine(condition.oclBody()));
            guards.add(predicate("grd_pre_"+(++preNo),value));
        }
        if(branchGuard!=null) guards.add(predicate("grd_branch",branchGuard));

        List<Assignment> actions=new ArrayList<>();
        if(start) {
            String nextExtent=state.extent()+" ∪ {pid}";
            guards.add(predicate("grd_state_type","("+state.lifecycle()+" ∪ {pid ↦ active}) ∈ ("+nextExtent+") → PROCESS_STATE"));
            guards.add(predicate("grd_scope_type","("+state.scope()+" ∪ {pid ↦ self}) ∈ ("+nextExtent+") → "+state.groupType()));
            actions.add(new Assignment("act_create",state.extent()+" ≔ "+state.extent()+" ∪ {pid}"));
            actions.add(new Assignment("act_state",state.lifecycle()+" ≔ "+state.lifecycle()+" ∪ {pid ↦ active}"));
            actions.add(new Assignment("act_scope",state.scope()+" ≔ "+state.scope()+" ∪ {pid ↦ self}"));
            int tokenNo=0;
            Set<SequenceFlow> produced=Collections.newSetFromMap(new IdentityHashMap<>()); produced.addAll(outgoing);
            for(SequenceFlow flow:process.sequenceFlows()) {
                String value=produced.contains(flow)?"1":"0";
                guards.add(predicate("grd_token_type_"+(tokenNo+1),"("+tokens.get(flow)+" ∪ {pid ↦ "+value
                        +"}) ∈ ("+nextExtent+") → ℕ"));
                actions.add(new Assignment("act_token_"+(++tokenNo),tokens.get(flow)+" ≔ "+tokens.get(flow)
                        +" ∪ {pid ↦ "+value+"}"));
            }
        } else {
            int tokenNo=0;
            for(SequenceFlow flow:incoming) {
                String next=tokens.get(flow)+"  {pid ↦ "+tokens.get(flow)+"(pid)−1}";
                guards.add(predicate("grd_token_type_"+(tokenNo+1),"("+next+") ∈ "+state.extent()+" → ℕ"));
                actions.add(new Assignment("act_consume_"+(++tokenNo),tokens.get(flow)+" ≔ "+next));
            }
            for(SequenceFlow flow:outgoing) {
                String next=tokens.get(flow)+"  {pid ↦ "+tokens.get(flow)+"(pid)+1}";
                guards.add(predicate("grd_token_type_"+(tokenNo+1),"("+next+") ∈ "+state.extent()+" → ℕ"));
                actions.add(new Assignment("act_produce_"+(++tokenNo),tokens.get(flow)+" ≔ "+next));
            }
        }
        if(node instanceof Activity activity && !activity.postconditions().isEmpty()) {
            String post=activity.postconditions().get(0).oclBody();
            List<String> effects=OclToEventB.translateBpmnSelfPostEffect(post,symbols,process.groupClass(),acl);
            if(effects.isEmpty()) fail(diagnostics,"Unsupported BPMN effect at "+node.id()+": "+oneLine(post));
            int effectNo=0;
            for(String effect:effects) {
                int split=effect.indexOf(" ≔ "); String variable=split<0?null:effect.substring(0,split);
                String functionType=variable==null?null:symbols.get("$functionType."+variable);
                int n=++effectNo;
                if(functionType!=null) guards.add(predicate("grd_effect_type_"+n,
                        "("+effect.substring(split+3)+") ∈ "+functionType));
                actions.add(new Assignment("act_effect_"+n,effect));
            }
        }
        if(node instanceof EndEvent && !start) {
            guards.add(predicate("grd_complete_type","("+state.lifecycle()+"  {pid ↦ completed}) ∈ "
                    +state.extent()+" → PROCESS_STATE"));
            actions.add(new Assignment("act_complete",state.lifecycle()+" ≔ "+state.lifecycle()+"  {pid ↦ completed}"));
        }
        String label=id(node.id()+suffix);
        events.add(new EventBProject.Event(label,parameters,guards,actions));
        traces.add(new Trace("BPMN",node.id(),label+(lane==null?"":" / performer:R_"+id(lane.id()))));
    }

    private static String resolveGroupClass(Process process,AclModel acl,List<String> diagnostics) {
        if(process.groupClass()!=null) return process.groupClass();
        Set<String> candidates=new LinkedHashSet<>();
        for(Lane lane:process.lanes()) acl.owners().stream().filter(x->x.target().equals(lane.id()))
                .map(x->x.sourceGroup()).forEach(candidates::add);
        if(candidates.size()!=1) fail(diagnostics,"BPMN process '"+process.id()+"' must declare one ACL Group scope");
        return candidates.iterator().next();
    }

    private static void validateLanes(Process process,AclModel acl,Map<String,String> symbols,String group,
                                      List<String> diagnostics) {
        for(Lane lane:process.lanes()) {
            if(acl.findRole(lane.id()).isEmpty()) fail(diagnostics,"BPMN lane '"+lane.id()+"' does not resolve to an ACL Role");
            String ownerGroup=symbols.get("$ownerGroup."+lane.id());
            if(!Objects.equals(group,ownerGroup)) fail(diagnostics,"BPMN lane '"+lane.id()+"' is not owned by process Group '"+group+"'");
        }
    }

    private static Lane laneOf(Process process,FlowElement node) {
        return process.lanes().stream().filter(x->x.flowElements().contains(node)).findFirst().orElse(null);
    }

    private static String branchGuard(SequenceFlow branch,List<SequenceFlow> siblings,
            Map<String,String> symbols,Process process,AclModel acl) {
        if(!branch.isDefault()) return translateGuard(branch.guardSource(),symbols,process,acl);
        List<String> alternatives=siblings.stream().filter(x->x!=branch && x.guardSource()!=null)
                .map(x->translateGuard(x.guardSource(),symbols,process,acl)).filter(Objects::nonNull).toList();
        return alternatives.isEmpty()?"TRUE = TRUE":"¬("+String.join(" ∨ ",alternatives)+")";
    }

    private static String translateGuard(String source,Map<String,String> symbols,Process process,AclModel acl) {
        if(source==null) return null;
        return OclToEventB.translateBpmnSelfGuard(source,symbols,process.groupClass(),acl);
    }

    private static String partition(String set,List<String> values) {
        return "partition("+set+", "+String.join(", ",values.stream().map(x->"{"+x+"}").toList())+")";
    }
    private static Predicate predicate(String label,String formula) { return new Predicate(label,formula,false); }
    private static String oneLine(String value) { return value==null?"":value.replaceAll("\\s+"," ").trim(); }
    private static String id(String value) { return AclEventBCompositionSupport.id(value); }
    private static void fail(List<String> diagnostics,String message) {
        diagnostics.add(message); throw new IllegalArgumentException(message);
    }
}
