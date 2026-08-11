package org.vnu.sme.goal.translate.aclistarbpmn2eventb.translate;

import java.util.*;

import org.vnu.sme.goal.dsl.acl.mm.AclModel;
import org.vnu.sme.goal.dsl.istar.mm.*;
import org.vnu.sme.goal.translate.aclistarbpmn2eventb.ir.EventBProject.*;

/** Compiles iStar occurrences, G(A,P,S), T(Q,R), refinements and temporal contracts. */
final class IStarSatisfactionCompiler {
    record RootContract(String id,String roleType,String predicate,String progress,String ownerRelation) {}
    record AssignmentSpec(String label, String formula) {}
    record State(List<String> variables, List<String> invariants,
                 List<AssignmentSpec> initialisation, List<AssignmentSpec> observation,
                 List<String> completionInvariants) {}
    record Result(Map<String,String> predicates, List<String> roots, List<String> achieveRoots,
                  List<String> maintainInvariants, List<Property> properties, State state,
                  List<RootContract> rootContracts, List<String> contextSets,
                  List<Constant> constants, List<Predicate> axioms, List<Trace> traces) {}

    private record Occurrence(String id, boolean goal, boolean quality, List<String> types, List<String> vars,
                              String domain, String value, String type, String set,
                              String active, String progress, String stable,
                              String queued, String result) {}

    private final GoalModel model;
    private final AclModel acl;
    private final Map<String,String> attributes;
    private final List<String> diagnostics;
    private final ContextResolution contexts;
    private final GoalActivationGraph activationGraph;
    private final boolean processCoupled;
    private final Map<String,List<Refinement>> refinements = new LinkedHashMap<>();
    private final Map<String,List<Dependency>> dependenciesByRequester = new LinkedHashMap<>();
    private final Map<String,Occurrence> occurrences = new LinkedHashMap<>();
    private final Map<String,String> defaultPredicates = new LinkedHashMap<>();

    private IStarSatisfactionCompiler(GoalModel model, AclModel acl,
                                      Map<String,String> attributes, List<String> diagnostics,
                                      boolean processCoupled) {
        this.model=model; this.acl=acl; this.attributes=attributes; this.diagnostics=diagnostics;
        this.processCoupled=processCoupled;
        this.contexts=ContextResolution.of(model);
        this.activationGraph=GoalActivationGraph.of(model);
        model.getActors().forEach(a -> a.refinements()
                .forEach(r -> refinements.computeIfAbsent(r.parent(), x -> new ArrayList<>()).add(r)));
        model.getDependencies().stream().filter(d -> d.dependerElmt() != null && d.dependeeElmt() != null)
                .forEach(d -> dependenciesByRequester.computeIfAbsent(d.dependerElmt(), x -> new ArrayList<>()).add(d));
        model.allElements().values().stream()
                .filter(e -> e instanceof Goal || e instanceof Task || e instanceof Quality)
                .forEach(this::createOccurrence);
    }

    static Result compile(GoalModel model, AclModel acl, Map<String,String> attributes,
                          List<String> diagnostics) {
        return new IStarSatisfactionCompiler(model,acl,attributes,diagnostics,true).compile();
    }

    /** iStar-only monitoring has no BPMN Start event; existing ACL occurrences are
     * the default activation universe unless a Goal declares an activation clause. */
    static Result compileStandalone(GoalModel model, AclModel acl, Map<String,String> attributes,
                                    List<String> diagnostics) {
        return new IStarSatisfactionCompiler(model,acl,attributes,diagnostics,false).compile();
    }

    private Result compile() {
        List<String> contextSets=new ArrayList<>();
        List<Constant> constants=new ArrayList<>();
        List<Predicate> axioms=new ArrayList<>();
        List<Trace> traces=new ArrayList<>();
        buildDeclarations(contextSets,constants,axioms,traces);
        List<String> roots = model.allElements().values().stream().filter(Goal.class::isInstance)
                .map(IntentionalElement::id).filter(activationGraph::isRoot).toList();
        List<Property> properties = new ArrayList<>();
        List<String> achieveRoots = new ArrayList<>(), maintain = new ArrayList<>();
        List<RootContract> rootContracts=new ArrayList<>();

        for (Occurrence occurrence : occurrences.values()) {
            IntentionalElement element = model.findElement(occurrence.id()).orElseThrow();
            String sat = satisfaction(element, occurrence.types(), occurrence.vars(), new LinkedHashSet<>());
            if (sat == null) sat = "FALSE = TRUE";
            defaultPredicates.put(occurrence.id(), quantify(occurrence, sat, true));
            if (!(element instanceof Goal goal)) continue;
            String act = activation(goal, occurrence.types(), occurrence.vars());
            GoalType kind = goal.goalType() == null ? GoalType.ACHIEVE : goal.goalType();
            if(roots.contains(goal.id()) && occurrence.types().size()==1) {
                String owner=attributes.get("$owner."+occurrence.types().get(0));
                if(owner!=null) rootContracts.add(new RootContract(goal.id(),occurrence.types().get(0),
                        sat,occurrence.progress(),owner));
            }
            String allActiveSat = quantify(occurrence, "(" + act + ") ⇒ (" + sat + ")", true);
            switch (kind) {
                case ACHIEVE -> {
                    if (roots.contains(goal.id())) achieveRoots.add(goal.id());
                    properties.add(new Property("LTL_ACHIEVE_" + id(goal.id()), "LIVENESS",
                            "G({" + occurrence.active() + " ≠ ∅} => F {" + occurrence.progress()
                                    + " = " + occurrence.set() + "})", "iStar achieve goal " + goal.id()));
                }
                case MAINTAIN -> {
                    maintain.add(allActiveSat);
                    properties.add(new Property("LTL_MAINTAIN_" + id(goal.id()), "SAFETY",
                            "G({" + allActiveSat + "})", "iStar maintain goal " + goal.id()));
                }
                case SUSTAIN -> properties.add(new Property("LTL_SUSTAIN_" + id(goal.id()), "SUSTAINMENT",
                        "G({" + occurrence.active() + " ≠ ∅} => (! {" + occurrence.progress()
                                + " ≠ ∅} U G {" + occurrence.stable() + " = " + occurrence.set() + "}))",
                        "iStar sustain goal " + goal.id()));
                case RECUR -> properties.add(new Property("LTL_RECUR_" + id(goal.id()), "RECURRENCE",
                        "G({" + occurrence.active() + " ≠ ∅} => G F {" + occurrence.progress() + " ≠ ∅})",
                        "iStar recurrent goal " + goal.id()));
            }
        }

        for (Dependency d : model.getDependencies()) {
            Occurrence request = occurrences.get(d.dependerElmt());
            Occurrence provide = occurrences.get(d.dependeeElmt());
            if (request == null || provide == null) continue;
            String requested = request.goal()
                    ? request.active() + " ∖ " + request.progress() + " ≠ ∅"
                    : request.queued() + " ∖ " + request.result() + " ≠ ∅";
            String provided = completionSet(provide) + " ≠ ∅";
            properties.add(new Property("LTL_DEP_" + id(d.dependum()), "ASSUMPTION_GUARANTEE",
                    "G({" + requested + "} => F {" + provided + "})",
                    "iStar dependency " + d.dependerElmt() + " -> " + d.dependeeElmt()));
        }

        State state = buildState(roots);
        return new Result(Map.copyOf(defaultPredicates), roots, List.copyOf(achieveRoots),
                List.copyOf(maintain), List.copyOf(properties), state,List.copyOf(rootContracts),
                List.copyOf(contextSets),List.copyOf(constants),List.copyOf(axioms),List.copyOf(traces));
    }

    private void buildDeclarations(List<String> sets,List<Constant> constants,List<Predicate> axioms,
                                   List<Trace> traces) {
        for(Actor actor:model.getActors()) {
            if(acl.findRole(actor.name()).isEmpty())
                fail("iStar actor '"+actor.name()+"' does not resolve to an ACL Role");
            traces.add(new Trace("iStar","actor "+actor.name(),"R_"+id(actor.name())));
        }
        List<String> goals=new ArrayList<>(),tasks=new ArrayList<>(),qualities=new ArrayList<>();
        for(Actor actor:model.getActors()) for(IntentionalElement element:actor.elements()) {
            String declaration;
            if(element instanceof Goal goal) {
                declaration="g_"+id(element.id()); goals.add(declaration);
                traces.add(new Trace("iStar",actor.name()+"."+element.id(),declaration+" / G_"+id(element.id())+"_{A,P,S}"));
            } else if(element instanceof Task) {
                declaration="t_"+id(element.id()); tasks.add(declaration);
                traces.add(new Trace("iStar",actor.name()+"."+element.id(),declaration+" / T_"+id(element.id())+"_{Q,R}"));
            } else if(element instanceof Quality) {
                declaration="q_"+id(element.id()); qualities.add(declaration);
                traces.add(new Trace("iStar",actor.name()+"."+element.id(),declaration+" / Q_"+id(element.id())+"_{I,TRUE,FALSE}"));
            } else continue;
            constants.add(new Constant(declaration));
        }
        if(!goals.isEmpty()) {
            sets.add("GOAL_DECL"); sets.add("GOAL_KIND");
            for(String kind:List.of("ACHIEVE","MAINTAIN","SUSTAIN","RECUR")) constants.add(new Constant(kind));
            constants.add(new Constant("goalKind"));
            axioms.add(new Predicate("axm_goal_decl",partition("GOAL_DECL",goals),false));
            axioms.add(new Predicate("axm_goal_kind","partition(GOAL_KIND,{ACHIEVE},{MAINTAIN},{SUSTAIN},{RECUR})",false));
            axioms.add(new Predicate("axm_goal_kind_type","goalKind ∈ GOAL_DECL → GOAL_KIND",false));
            for(Actor actor:model.getActors()) for(IntentionalElement element:actor.elements()) if(element instanceof Goal goal) {
                GoalType kind=goal.goalType()==null?GoalType.ACHIEVE:goal.goalType();
                axioms.add(new Predicate("axm_goal_kind_"+id(goal.id()),"goalKind(g_"+id(goal.id())+")="+kind.name(),false));
            }
        }
        if(!tasks.isEmpty()) {
            sets.add("TASK_DECL");
            axioms.add(new Predicate("axm_task_decl",partition("TASK_DECL",tasks),false));
        }
        if(!qualities.isEmpty()) {
            sets.add("QUALITY_DECL");
            axioms.add(new Predicate("axm_quality_decl",partition("QUALITY_DECL",qualities),false));
        }
    }

    private static String partition(String set,List<String> values) {
        return "partition("+set+", "+String.join(", ",values.stream().map(x->"{"+x+"}").toList())+")";
    }

    private void createOccurrence(IntentionalElement element) {
        List<String> types = contexts.contextTypesOf(model, element.id());
        List<String> vars = new ArrayList<>();
        for (int i=0;i<types.size();i++) vars.add(i==0 ? "self" : "outer"+i);
        String domain = contextDomain(types, vars);
        String value = tuple(vars);
        String set = types.size()==1 ? typeSet(types.get(0))
                : "{" + String.join(",",vars) + "·" + domain + "∣" + value + "}";
        String occurrenceType=typeSet(types.get(types.size()-1));
        for(int i=types.size()-2;i>=0;i--) occurrenceType="("+occurrenceType+" × "+typeSet(types.get(i))+")";
        String base=id(element.id());
        boolean goal=element instanceof Goal,quality=element instanceof Quality;
        occurrences.put(element.id(), new Occurrence(element.id(),goal,quality,types,List.copyOf(vars),domain,value,occurrenceType,set,
                goal?"G_"+base+"_A":null, goal?"G_"+base+"_P":null, goal?"G_"+base+"_S":null,
                goal?null:quality?"Q_"+base+"_I":"T_"+base+"_Q",
                goal?null:quality?"Q_"+base+"_TRUE":"T_"+base+"_R"));
    }

    private State buildState(List<String> roots) {
        List<String> variables=new ArrayList<>(), invariants=new ArrayList<>();
        List<AssignmentSpec> init=new ArrayList<>(), observe=new ArrayList<>();
        List<String> completion=new ArrayList<>();
        for (Occurrence occurrence : occurrences.values()) {
            IntentionalElement element=model.findElement(occurrence.id()).orElseThrow();
            String sat=satisfaction(element,occurrence.types(),occurrence.vars(),new LinkedHashSet<>());
            if (sat==null) sat="FALSE = TRUE";
            if (occurrence.quality()) {
                String positive=qualityContribution(element.id(),occurrence,true);
                String negative=qualityContribution(element.id(),occurrence,false);
                String falseVariable="Q_"+id(element.id())+"_FALSE";
                variables.addAll(List.of(occurrence.queued(),occurrence.result(),falseVariable));
                invariants.add(occurrence.queued()+" ⊆ "+occurrence.type());
                invariants.add(occurrence.result()+" ⊆ "+occurrence.queued());
                invariants.add(falseVariable+" ⊆ "+occurrence.queued());
                invariants.add(occurrence.result()+" ∩ "+falseVariable+" = ∅");
                init.add(new AssignmentSpec("act_"+occurrence.queued(),occurrence.queued()+" ≔ ∅"));
                init.add(new AssignmentSpec("act_"+occurrence.result(),occurrence.result()+" ≔ ∅"));
                init.add(new AssignmentSpec("act_"+falseVariable,falseVariable+" ≔ ∅"));
                observe.add(new AssignmentSpec("obs_"+occurrence.queued(),subsetAssignment(
                        occurrence.queued(),occurrence.set(),occurrence.type())));
                observe.add(new AssignmentSpec("obs_"+occurrence.result(),subsetAssignment(
                        occurrence.result(),setWhere(occurrence,"("+positive+") ∧ ¬("+negative+")"),occurrence.type())));
                observe.add(new AssignmentSpec("obs_"+falseVariable,subsetAssignment(
                        falseVariable,setWhere(occurrence,"("+negative+") ∧ ¬("+positive+")"),occurrence.type())));
            } else if (occurrence.goal()) {
                String act=activation((Goal)element,occurrence.types(),occurrence.vars());
                variables.addAll(List.of(occurrence.active(),occurrence.progress(),occurrence.stable()));
                invariants.add(occurrence.active()+" ⊆ "+occurrence.type());
                invariants.add(occurrence.progress()+" ⊆ "+occurrence.type());
                invariants.add(occurrence.stable()+" ⊆ "+occurrence.type());
                init.add(new AssignmentSpec("act_"+occurrence.active(),occurrence.active()+" ≔ ∅"));
                init.add(new AssignmentSpec("act_"+occurrence.progress(),occurrence.progress()+" ≔ ∅"));
                init.add(new AssignmentSpec("act_"+occurrence.stable(),occurrence.stable()+" ≔ "+occurrence.set()));
                String activeSet=setWhere(occurrence,act);
                String satSet=setWhere(occurrence,sat);
                observe.add(new AssignmentSpec("obs_"+occurrence.active(),subsetAssignment(
                        occurrence.active(),activeSet,occurrence.type())));
                observe.add(new AssignmentSpec("obs_"+occurrence.progress(),subsetAssignment(
                        occurrence.progress(),activeSet+" ∩ ("+occurrence.progress()+" ∪ "+satSet+")",
                        occurrence.type())));
                String stablePredicate="¬("+act+") ∨ ("+occurrence.value()+"∈"+occurrence.stable()
                        +" ∧ ("+occurrence.value()+"∉"+occurrence.progress()+" ∨ ("+sat+")))";
                observe.add(new AssignmentSpec("obs_"+occurrence.stable(),subsetAssignment(
                        occurrence.stable(),setWhere(occurrence,stablePredicate),occurrence.type())));
            } else {
                Task task=(Task)element;
                String pre=constraints(task.preconditions(),occurrence.types(),occurrence.vars());
                if(pre==null) pre="TRUE = TRUE";
                variables.addAll(List.of(occurrence.queued(),occurrence.result()));
                invariants.add(occurrence.queued()+" ⊆ "+occurrence.type());
                invariants.add(occurrence.result()+" ⊆ "+occurrence.type());
                init.add(new AssignmentSpec("act_"+occurrence.queued(),occurrence.queued()+" ≔ ∅"));
                init.add(new AssignmentSpec("act_"+occurrence.result(),occurrence.result()+" ≔ ∅"));
                String preSet=setWhere(occurrence,pre), postSet=setWhere(occurrence,sat);
                observe.add(new AssignmentSpec("obs_"+occurrence.queued(),subsetAssignment(
                        occurrence.queued(),occurrence.queued()+" ∪ "+preSet,occurrence.type())));
                observe.add(new AssignmentSpec("obs_"+occurrence.result(),subsetAssignment(
                        occurrence.result(),occurrence.result()+" ∪ (("+occurrence.queued()+" ∪ "+preSet+") ∩ "+postSet+")",
                        occurrence.type())));
            }
        }
        for(Actor actor:model.getActors()) for(Refinement refinement:actor.refinements()) {
            if(!(refinement instanceof PickRefinement pick)) continue;
            Occurrence parent=occurrences.get(pick.parent());
            IntentionalElement child=model.findElement(pick.child()).orElse(null);
            if(parent==null||child==null) continue;
            String variable="Pick_"+id(pick.child())+"_candidates";
            String selected="selectedPick";
            List<String> childTypes=new ArrayList<>(); childTypes.add(pick.actorType()); childTypes.addAll(parent.types());
            List<String> childVars=new ArrayList<>(); childVars.add(selected); childVars.addAll(parent.vars());
            String childSat=satisfaction(child,childTypes,childVars,new LinkedHashSet<>());
            if(childSat==null) childSat="FALSE = TRUE";
            variables.add(variable);
            invariants.add(variable+" ⊆ "+parent.type()+" × "+typeSet(pick.actorType()));
            init.add(new AssignmentSpec("act_"+variable,variable+" ≔ ∅"));
            String domain=parent.domain()+" ∧ "+candidate(selected,pick.actorType(),parent.vars().get(0),parent.types().get(0));
            String candidates="{"
                    +String.join(",",parent.vars())+","+selected+"·"+domain+" ∧ ("+childSat+")∣("
                    +parent.value()+" ↦ "+selected+")}";
            observe.add(new AssignmentSpec("obs_"+variable,subsetAssignment(variable,candidates,
                    parent.type()+" × "+typeSet(pick.actorType()))));
        }
        return new State(List.copyOf(variables),List.copyOf(invariants),List.copyOf(init),
                List.copyOf(observe),List.copyOf(completion));
    }

    private String qualityContribution(String qualityId,Occurrence target,boolean positive) {
        List<String> contributors=new ArrayList<>();
        for(Actor actor:model.getActors()) for(Contribution contribution:actor.contributions()) {
            if(!contribution.quality().equals(qualityId)) continue;
            boolean polarity=contribution.type()==ContributionType.MAKE||contribution.type()==ContributionType.HELP;
            if(polarity!=positive) continue;
            IntentionalElement source=model.findElement(contribution.element()).orElse(null);
            Occurrence sourceOccurrence=occurrences.get(contribution.element());
            if(source==null||sourceOccurrence==null) continue;
            if(sourceOccurrence.types().equals(target.types())) {
                String predicate=satisfaction(source,target.types(),target.vars(),new LinkedHashSet<>());
                if(predicate!=null) contributors.add("("+predicate+")");
                continue;
            }
            // A contributor may live below a forall/pick context while its Quality is
            // declared at the enclosing actor. Aggregate all matching inner occurrences.
            int extra=sourceOccurrence.types().size()-target.types().size();
            if(extra<=0||!sourceOccurrence.types().subList(extra,sourceOccurrence.types().size()).equals(target.types())) continue;
            List<String> sourceVars=new ArrayList<>(),bound=new ArrayList<>();
            for(int i=0;i<extra;i++) { String variable="qualitySelected"+(i+1); sourceVars.add(variable); bound.add(variable); }
            sourceVars.addAll(target.vars());
            String predicate=satisfaction(source,sourceOccurrence.types(),sourceVars,new LinkedHashSet<>());
            if(predicate!=null) contributors.add("(∃"+String.join(",",bound)+"·"
                    +contextDomain(sourceOccurrence.types(),sourceVars)+" ∧ ("+predicate+"))");
        }
        return contributors.isEmpty()?"FALSE = TRUE":String.join(" ∨ ",contributors);
    }

    private String satisfaction(IntentionalElement element, List<String> types, List<String> vars, Set<String> stack) {
        if(!stack.add(element.id())) return null;
        if(element instanceof Quality) {
            Occurrence occurrence=occurrences.get(element.id());
            String positive=qualityContribution(element.id(),occurrence,true);
            String negative=qualityContribution(element.id(),occurrence,false);
            return "("+positive+") ∧ ¬("+negative+")";
        }
        List<IStarOclConstraint> conditions=element instanceof Goal g?g.conditions()
                :element instanceof GoalTaskElement gt?gt.postconditions():List.of();
        String direct=constraints(conditions,types,vars);
        if(direct!=null) return direct;
        List<String> alternatives=new ArrayList<>();
        // A depender goal with no local condition is fulfilled by the intentional element at
        // the dependee boundary.  In the well-formed model both ends have the same occurrence
        // context (including forall/pick bindings), so the provider predicate can be inlined.
        // This avoids treating dependency-backed goals as the constant FALSE predicate and also
        // lets one observation compute the complete goal tree from the current data state.
        for (Dependency dependency : dependenciesByRequester.getOrDefault(element.id(), List.of())) {
            IntentionalElement provider = model.findElement(dependency.dependeeElmt()).orElse(null);
            Occurrence providerOccurrence = occurrences.get(dependency.dependeeElmt());
            if (provider == null || providerOccurrence == null || !providerOccurrence.types().equals(types)) continue;
            String provided = satisfaction(provider, types, vars, new LinkedHashSet<>(stack));
            if (provided != null) alternatives.add(provided);
        }
        for(Refinement refinement:refinements.getOrDefault(element.id(),List.of())) {
            if(refinement instanceof AndRefinement and) {
                List<String> children=new ArrayList<>();
                for(String childId:and.children()) {
                    IntentionalElement child=model.findElement(childId).orElse(null);
                    if(child!=null) children.add(satisfaction(child,types,vars,new LinkedHashSet<>(stack)));
                }
                if(children.stream().noneMatch(Objects::isNull)) alternatives.add("("+String.join(" ∧ ",children)+")");
            } else if(refinement instanceof OrRefinement or) {
                IntentionalElement child=model.findElement(or.child()).orElse(null);
                if(child!=null) alternatives.add(satisfaction(child,types,vars,new LinkedHashSet<>(stack)));
            } else if(refinement instanceof ParameterRefinement parameter) {
                IntentionalElement child=model.findElement(parameter.child()).orElse(null);
                if(child==null) continue;
                String selected="selected"+(types.size()+1);
                List<String> childTypes=new ArrayList<>(); childTypes.add(parameter.actorType()); childTypes.addAll(types);
                List<String> childVars=new ArrayList<>(); childVars.add(selected); childVars.addAll(vars);
                String candidate=candidate(selected,parameter.actorType(),vars.get(0),types.get(0));
                String childSat=satisfaction(child,childTypes,childVars,new LinkedHashSet<>(stack));
                if(childSat==null) continue;
                if(parameter instanceof ForRefinement)
                    alternatives.add("((∃"+selected+"·"+candidate+") ∧ (∀"+selected+"·"+candidate+" ⇒ ("+childSat+")))");
                else alternatives.add("(∃"+selected+"·"+candidate+" ∧ ("+childSat+"))");
            }
        }
        String result=alternatives.isEmpty()?null:String.join(" ∨ ",alternatives);
        if(result==null && element instanceof Task) result="FALSE = TRUE";
        return result;
    }

    private String activation(Goal goal,List<String> types,List<String> vars) {
        String local=constraints(goal.activations(),types,vars);
        String gate="TRUE = TRUE";
        if(processCoupled) {
            String owner=attributes.get("$owner."+types.get(types.size()-1));
            gate=owner==null?"started ≠ ∅":"started["+owner+"∼[{"+vars.get(vars.size()-1)+"}]] ≠ ∅";
        }
        return local==null?gate:"("+gate+") ∧ ("+local+")";
    }

    private String constraints(List<IStarOclConstraint> constraints,List<String> types,List<String> vars) {
        List<String> translated=new ArrayList<>();
        for(IStarOclConstraint constraint:constraints) {
            String value=OclToEventB.translateContext(constraint.oclBody(),types,vars,attributes,acl);
            if(value==null) fail("Unsupported iStar OCL: "+constraint.oclBody().replaceAll("\\s+"," ").trim());
            translated.add(value);
        }
        return translated.stream().reduce((a,b)->"("+a+") ∧ ("+b+")").orElse(null);
    }

    private void fail(String message) {
        diagnostics.add(message);
        throw new IllegalArgumentException(message);
    }

    private String contextDomain(List<String> types,List<String> vars) {
        List<String> clauses=new ArrayList<>();
        for(int i=0;i<types.size();i++) clauses.add(vars.get(i)+"∈"+typeSet(types.get(i)));
        for(int i=0;i+1<types.size();i++) clauses.add(sameContext(vars.get(i),types.get(i),vars.get(i+1),types.get(i+1)));
        return String.join(" ∧ ",clauses);
    }

    private String candidate(String selected,String selectedType,String outer,String outerType) {
        return selected+"∈"+typeSet(selectedType)+" ∧ "+sameContext(selected,selectedType,outer,outerType);
    }

    private String sameContext(String a,String aType,String b,String bType) {
        String ar=attributes.get("$owner."+aType), br=attributes.get("$owner."+bType);
        if(ar==null||br==null) return "TRUE = TRUE";
        return ar+"∼[{"+a+"}] ≠ ∅ ∧ "+ar+"∼[{"+a+"}] = "+br+"∼[{"+b+"}]";
    }

    private String setWhere(Occurrence o,String predicate) {
        return "{"+String.join(",",o.vars())+"·"+o.domain()+" ∧ ("+predicate+")∣"+o.value()+"}";
    }
    /** Intersecting with the declared occurrence type is semantically neutral for a well-formed
     * comprehension and makes the range visible to Rodin's automatic set prover. */
    private static String subsetAssignment(String variable,String value,String type) {
        return variable+" ≔ ("+value+") ∩ ("+type+")";
    }
    private String quantify(Occurrence o,String predicate,boolean universal) {
        return "("+(universal?"∀":"∃")+String.join(",",o.vars())+"·"+o.domain()
                +(universal?" ⇒ ":" ∧ ")+"("+predicate+"))";
    }
    private String completionSet(Occurrence o){return o.goal()?o.progress():o.result();}
    private String typeSet(String type){return attributes.getOrDefault("$type."+type,"R_"+id(type));}
    private static String tuple(List<String> vars){
        String value=vars.get(vars.size()-1);
        for(int i=vars.size()-2;i>=0;i--) value="("+value+" ↦ "+vars.get(i)+")";
        return value;
    }
    private static String id(String value){return AclIStarBpmn2EventBTranslator.id(value);}
}
