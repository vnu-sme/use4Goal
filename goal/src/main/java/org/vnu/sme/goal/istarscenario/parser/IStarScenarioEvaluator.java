package org.vnu.sme.goal.istarscenario.parser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vnu.sme.goal.conformance.semantics.GoalTaskStatus;
import org.vnu.sme.goal.conformance.semantics.IStarMarking;
import org.vnu.sme.goal.conformance.semantics.IStarPropagation;
import org.vnu.sme.goal.conformance.semantics.QualityStatus;
import org.vnu.sme.goal.istar.mm.Actor;
import org.vnu.sme.goal.istar.mm.Agent;
import org.vnu.sme.goal.istar.mm.AndRefinement;
import org.vnu.sme.goal.istar.mm.Contribution;
import org.vnu.sme.goal.istar.mm.Dependency;
import org.vnu.sme.goal.istar.mm.ForRefinement;
import org.vnu.sme.goal.istar.mm.Goal;
import org.vnu.sme.goal.istar.mm.GoalModel;
import org.vnu.sme.goal.istar.mm.GoalTaskElement;
import org.vnu.sme.goal.istar.mm.IntentionalElement;
import org.vnu.sme.goal.istar.mm.Obstacle;
import org.vnu.sme.goal.istar.mm.OrRefinement;
import org.vnu.sme.goal.istar.mm.PickRefinement;
import org.vnu.sme.goal.istar.mm.Quality;
import org.vnu.sme.goal.istar.mm.Refinement;
import org.vnu.sme.goal.istar.mm.Resource;
import org.vnu.sme.goal.istar.mm.Role;
import org.vnu.sme.goal.istar.mm.Task;
import org.vnu.sme.goal.istarscenario.mm.AggregateMode;
import org.vnu.sme.goal.istarscenario.mm.AggregateResult;
import org.vnu.sme.goal.istarscenario.mm.IStarScenarioModel;
import org.vnu.sme.goal.istarscenario.mm.ScenarioInstance;
import org.vnu.sme.goal.istarscenario.mm.ScenarioStmt;

public final class IStarScenarioEvaluator {

    private static final int MAX_DEPENDENCY_STEPS = 10_000;
    private record TupleOccurrence(List<String> instanceIds) {
        TupleOccurrence {
            instanceIds = List.copyOf(instanceIds);
        }
    }

    private IStarScenarioEvaluator() {}

    public static IStarScenarioEvaluation evaluate(GoalModel source, IStarScenarioModel scenario) {
        Map<String, String> instanceActorType = instanceActorTypes(scenario);
        Map<String, List<String>> instancesByType = instancesByType(instanceActorType);
        Map<String, Actor> actors = actors(source);
        Map<String, String> ownerOf = ownerOf(source);
        Map<String, List<String>> scopeTypes = inferScopeActorTypes(source, scenario, instanceActorType, instancesByType, ownerOf);

        GoalModel instanceModel = instantiate(source, scenario, instanceActorType, instancesByType, actors, ownerOf,
                scopeTypes);
        IStarMarking marking = executeTrace(instanceModel, scenario, instanceActorType, scopeTypes);
        List<AggregateResult> aggregates = evaluateAggregates(source, scenario, marking, instanceActorType, instancesByType, scopeTypes);
        Map<String, String> actorLabels = actorLabels(scenario);
        Map<String, String> nodeLabels = nodeLabels(source, scopeTypes, instancesByType);
        return new IStarScenarioEvaluation(instanceModel, marking, aggregates, actorLabels, nodeLabels, firstScopeTypes(scopeTypes));
    }

    private static Map<String, List<String>> inferScopeActorTypes(GoalModel source, IStarScenarioModel scenario,
                                                                  Map<String, String> instanceActorType,
                                                                  Map<String, List<String>> instancesByType,
                                                                  Map<String, String> ownerOf) {
        Map<String, List<String>> scopeTypes = new LinkedHashMap<>();

        ownerOf.forEach((elementId, actorType) -> {
            if (instancesByType.getOrDefault(actorType, List.of()).size() > 1
                    && source.findElement(elementId).map(IStarScenarioEvaluator::isInstanceElement).orElse(false)) {
                scopeTypes.put(elementId, List.of(actorType));
            }
        });

        for (ScenarioStmt stmt : scenario.statements()) {
            switch (stmt) {
                case ScenarioStmt.Fire f -> {
                    if (f.objectInstanceId() == null) {
                        addQualifiedScope(scopeTypes, f.instanceId(), f.elementId(), instanceActorType);
                    } else {
                        addDependencyObjectScope(source, scopeTypes, f.elementId(), f.objectInstanceId(), instanceActorType);
                    }
                }
                case ScenarioStmt.Assign a -> addQualifiedScope(scopeTypes, a.instanceId(), a.elementId(), instanceActorType);
                case ScenarioStmt.Aggregate ignored -> { }
            }
        }

        boolean changed = true;
        while (changed) {
            changed = false;
            changed |= propagateParameterScopes(source, scopeTypes);
            changed |= propagateChildScopes(source, scopeTypes);
            changed |= propagateDependencyScopes(source, scopeTypes);
        }
        return scopeTypes;
    }

    private static void addDependencyObjectScope(GoalModel source, Map<String, List<String>> scopeTypes,
                                                 String dependumId, String objectInstanceId,
                                                 Map<String, String> instanceActorType) {
        String objectActorType = instanceActorType.get(objectInstanceId);
        if (objectActorType == null) return;
        for (Dependency d : source.getDependencies()) {
            if (dependumId.equals(d.dependum()) && d.dependerElmt() != null) {
                putScope(scopeTypes, d.dependerElmt(), List.of(objectActorType));
            }
        }
    }

    private static boolean propagateDependencyScopes(GoalModel source, Map<String, List<String>> scopeTypes) {
        boolean changed = false;
        for (Dependency d : source.getDependencies()) {
            List<String> scope = longestScope(scopeTypes.get(d.dependerElmt()), scopeTypes.get(d.dependum()),
                    scopeTypes.get(d.dependeeElmt()));
            if (scope == null) continue;
            changed |= putScope(scopeTypes, d.dependerElmt(), scope);
            changed |= putScope(scopeTypes, d.dependum(), scope);
            changed |= putScope(scopeTypes, d.dependeeElmt(), scope);
        }
        return changed;
    }

    private static boolean propagateChildScopes(GoalModel source, Map<String, List<String>> scopeTypes) {
        boolean changed = false;
        for (Actor actor : source.getActors()) {
            for (Refinement r : actor.refinements()) {
                switch (r) {
                    case AndRefinement and -> {
                        List<String> parentScope = scopeTypes.get(and.parent());
                        if (parentScope != null) {
                            for (String child : and.children()) {
                                changed |= putScope(scopeTypes, child, parentScope);
                            }
                        }
                    }
                    case OrRefinement or -> {
                        List<String> parentScope = scopeTypes.get(or.parent());
                        if (parentScope != null) {
                            changed |= putScope(scopeTypes, or.child(), parentScope);
                        }
                    }
                    case ForRefinement ignored -> { }
                    case PickRefinement ignored -> { }
                }
            }
        }
        return changed;
    }

    private static boolean propagateParameterScopes(GoalModel source, Map<String, List<String>> scopeTypes) {
        boolean changed = false;
        for (Actor actor : source.getActors()) {
            for (Refinement r : actor.refinements()) {
                switch (r) {
                    case ForRefinement forRef -> {
                        changed |= putScope(scopeTypes, forRef.child(), prependScope(forRef.actorType(), scopeTypes.get(forRef.parent())));
                    }
                    case PickRefinement pick -> {
                        changed |= putScope(scopeTypes, pick.child(), prependScope(pick.actorType(), scopeTypes.get(pick.parent())));
                    }
                    case AndRefinement ignored -> { }
                    case OrRefinement ignored -> { }
                }
            }
        }
        return changed;
    }

    private static GoalModel instantiate(GoalModel source, IStarScenarioModel scenario,
                                         Map<String, String> instanceActorType,
                                         Map<String, List<String>> instancesByType, Map<String, Actor> actors,
                                         Map<String, String> ownerOf, Map<String, List<String>> scopeTypes) {
        GoalModel out = new GoalModel(source.getName() + "Scenario");
        for (ScenarioInstance instance : scenario.instances()) {
            Actor actor = actors.get(instance.actorType());
            if (actor == null) continue;
            String actorId = actorInstanceId(instance.actorType(), instance.name());
            List<IntentionalElement> elements = elementsForActorInstance(actor, instance, instanceActorType,
                    instancesByType, ownerOf, scopeTypes);
            List<Refinement> refinements = refinementsForActorInstance(actor, instance, instancesByType, scopeTypes);
            List<Contribution> contributions = contributionsForActorInstance(actor, instance, instancesByType,
                    scopeTypes);
            Actor copy = actor instanceof Role
                    ? new Role(actorId, elements, refinements, contributions, List.of(), List.of(), List.of(), List.of(), List.of())
                    : new Agent(actorId, elements, refinements, contributions, List.of(), List.of(), List.of(), List.of(), List.of());
            out.addActor(copy);
        }
        addDependencies(source, out, scenario, instanceActorType, instancesByType, scopeTypes);
        return out;
    }

    private static List<IntentionalElement> elementsForActorInstance(Actor actor, ScenarioInstance instance,
                                                                     Map<String, String> instanceActorType,
                                                                     Map<String, List<String>> instancesByType,
                                                                     Map<String, String> ownerOf,
                                                                     Map<String, List<String>> scopeTypes) {
        List<IntentionalElement> elements = new ArrayList<>();
        for (IntentionalElement elem : actor.elements()) {
            List<String> scope = scopeTypes.get(elem.id());
            if (scope == null || scope.isEmpty()) {
                if (isFirstInstance(instance, instancesByType)) elements.add(copyElement(elem, elem.id()));
                continue;
            }
            String ownerType = ownerOf.get(elem.id());
            int ownerIndex = firstIndexOf(scope, ownerType);
            for (TupleOccurrence tuple : tuplesFor(scope, instancesByType)) {
                if (ownerIndex >= 0) {
                    if (instance.name().equals(tuple.instanceIds().get(ownerIndex))) {
                        elements.add(copyElement(elem, occurrenceId(elem.id(), tuple)));
                    }
                } else if (isFirstInstance(instance, instancesByType)) {
                    elements.add(copyElement(elem, occurrenceId(elem.id(), tuple)));
                }
            }
        }
        return elements;
    }

    private static List<Refinement> refinementsForActorInstance(Actor actor, ScenarioInstance instance,
                                                                Map<String, List<String>> instancesByType,
                                                                Map<String, List<String>> scopeTypes) {
        if (!isFirstInstance(instance, instancesByType)) return List.of();
        List<Refinement> out = new ArrayList<>();
        Map<String, Integer> orCounts = orChildCounts(actor);
        for (Refinement ref : actor.refinements()) {
            switch (ref) {
                case AndRefinement and -> addAnd(out, and, scopeTypes, instancesByType);
                case OrRefinement or -> addOr(out, or, scopeTypes, instancesByType, orCounts);
                case ForRefinement forRef -> addFor(out, forRef, scopeTypes, instancesByType);
                case PickRefinement pick -> addOr(out, new OrRefinement(pick.parent(), pick.child()),
                        scopeTypes, instancesByType, orCounts);
            }
        }
        return out;
    }

    private static void addAnd(List<Refinement> out, AndRefinement and, Map<String, List<String>> scopeTypes,
                               Map<String, List<String>> instancesByType) {
        List<String> parentScope = scopeTypes.get(and.parent());
        if (parentScope == null || parentScope.isEmpty()) {
            List<String> children = new ArrayList<>();
            for (String child : and.children()) addScopedOrSingleton(children, child, scopeTypes, instancesByType);
            out.add(new AndRefinement(and.parent(), children));
        } else {
            for (TupleOccurrence tuple : tuplesFor(parentScope, instancesByType)) {
                List<String> children = new ArrayList<>();
                for (String child : and.children()) children.addAll(idsInScope(child, tuple, scopeTypes, instancesByType));
                out.add(new AndRefinement(occurrenceId(and.parent(), tuple), children));
            }
        }
    }

    private static void addOr(List<Refinement> out, OrRefinement or, Map<String, List<String>> scopeTypes,
                              Map<String, List<String>> instancesByType, Map<String, Integer> orCounts) {
        List<String> parentScope = scopeTypes.get(or.parent());
        List<String> childScope = scopeTypes.get(or.child());
        boolean fakeMultiplicityOr = orCounts.getOrDefault(or.parent(), 0) == 1
                && (parentScope == null || parentScope.isEmpty()) && childScope != null && !childScope.isEmpty();
        if (fakeMultiplicityOr) {
            for (TupleOccurrence childTuple : tuplesFor(childScope, instancesByType)) {
                out.add(new AndRefinement(or.parent(), List.of(occurrenceId(or.child(), childTuple))));
            }
        } else if (parentScope != null && !parentScope.isEmpty()) {
            for (TupleOccurrence parentTuple : tuplesFor(parentScope, instancesByType)) {
                for (String childId : idsInScope(or.child(), parentTuple, scopeTypes, instancesByType)) {
                    out.add(new OrRefinement(occurrenceId(or.parent(), parentTuple), childId));
                }
            }
        } else if (childScope != null && !childScope.isEmpty()) {
            for (TupleOccurrence childTuple : tuplesFor(childScope, instancesByType)) {
                out.add(new OrRefinement(or.parent(), occurrenceId(or.child(), childTuple)));
            }
        } else {
            out.add(new OrRefinement(or.parent(), or.child()));
        }
    }

    private static void addFor(List<Refinement> out, ForRefinement forRef, Map<String, List<String>> scopeTypes,
                               Map<String, List<String>> instancesByType) {
        List<String> parentScope = scopeTypes.get(forRef.parent());
        if (parentScope == null || parentScope.isEmpty()) {
            List<String> children = new ArrayList<>();
            addScopedOrSingleton(children, forRef.child(), scopeTypes, instancesByType);
            out.add(new AndRefinement(forRef.parent(), children));
        } else {
            for (TupleOccurrence parentTuple : tuplesFor(parentScope, instancesByType)) {
                List<String> children = idsInScope(forRef.child(), parentTuple, scopeTypes, instancesByType);
                out.add(new AndRefinement(occurrenceId(forRef.parent(), parentTuple), children));
            }
        }
    }

    private static List<Contribution> contributionsForActorInstance(Actor actor, ScenarioInstance instance,
                                                                    Map<String, List<String>> instancesByType,
                                                                    Map<String, List<String>> scopeTypes) {
        if (!isFirstInstance(instance, instancesByType)) return List.of();
        List<Contribution> out = new ArrayList<>();
        for (Contribution c : actor.contributions()) {
            List<String> elementScope = scopeTypes.get(c.element());
            if (elementScope == null || elementScope.isEmpty()) {
                out.add(c);
            } else {
                for (TupleOccurrence tuple : tuplesFor(elementScope, instancesByType)) {
                    out.add(new Contribution(occurrenceId(c.element(), tuple), c.type(),
                            idInTuple(c.quality(), tuple, scopeTypes, instancesByType)));
                }
            }
        }
        return out;
    }

    private static void addDependencies(GoalModel source, GoalModel out, IStarScenarioModel scenario,
                                        Map<String, String> instanceActorType,
                                        Map<String, List<String>> instancesByType, Map<String, List<String>> scopeTypes) {
        for (Dependency d : source.getDependencies()) {
            List<String> scope = longestScope(scopeTypes.get(d.dependerElmt()), scopeTypes.get(d.dependum()),
                    scopeTypes.get(d.dependeeElmt()));
            if (scope == null || scope.isEmpty()) {
                out.addDependency(new Dependency(firstActorInstanceId(d.depender(), instancesByType), d.dependerElmt(),
                        d.dependumKind(), d.dependum(), firstActorInstanceId(d.dependee(), instancesByType), d.dependeeElmt()));
            } else {
                for (TupleOccurrence tuple : tuplesFor(scope, instancesByType)) {
                    out.addDependency(new Dependency(actorForDependencyEnd(d.depender(), tuple, scope, scenario, instancesByType),
                            idInTuple(d.dependerElmt(), tuple, scopeTypes, instancesByType), d.dependumKind(),
                            idInTuple(d.dependum(), tuple, scopeTypes, instancesByType),
                            actorForDependencyEnd(d.dependee(), tuple, scope, scenario, instancesByType),
                            idInTuple(d.dependeeElmt(), tuple, scopeTypes, instancesByType)));
                }
            }
        }
    }

    private static IStarMarking executeTrace(GoalModel model, IStarScenarioModel scenario,
                                             Map<String, String> instanceActorType,
                                             Map<String, List<String>> scopeTypes) {
        IStarMarking marking = IStarMarking.initial(model);
        for (ScenarioStmt stmt : scenario.statements()) {
            switch (stmt) {
                case ScenarioStmt.Fire f -> {
                    for (String id : resolveTargetIds(f.instanceId(), f.elementId(), f.objectInstanceId(),
                            instanceActorType, scopeTypes)) {
                        marking = IStarPropagation.fire(model, marking, id);
                        marking = saturateDependencies(model, marking);
                    }
                }
                case ScenarioStmt.Assign a -> {
                    for (String id : resolveTargetIds(a.instanceId(), a.elementId(), null,
                            instanceActorType, scopeTypes)) {
                        marking = applyAssign(model, marking, id, a.statusValue());
                        marking = saturateDependencies(model, marking);
                    }
                }
                case ScenarioStmt.Aggregate ignored -> { }
            }
        }
        return saturateDependencies(model, marking);
    }

    private static IStarMarking saturateDependencies(GoalModel model, IStarMarking marking) {
        IStarMarking current = marking;
        boolean changed = true;
        int steps = 0;
        while (changed && steps++ < MAX_DEPENDENCY_STEPS) {
            changed = false;
            for (Dependency d : model.getDependencies()) {
                if (d.dependerElmt() == null) continue;
                if (current.goalTaskStatus(d.dependerElmt()) == GoalTaskStatus.FULFILLED) continue;
                if (current.goalTaskStatus(d.dependum()) == GoalTaskStatus.FULFILLED) {
                    current = IStarPropagation.assignGoalTask(model, current, d.dependerElmt(), GoalTaskStatus.FULFILLED);
                    changed = true;
                }
            }
        }
        return current;
    }

    private static IStarMarking applyAssign(GoalModel model, IStarMarking marking, String id, String statusValue) {
        return switch (statusValue) {
            case "Fulfilled" -> IStarPropagation.assignGoalTask(model, marking, id, GoalTaskStatus.FULFILLED);
            case "Pending" -> IStarPropagation.assignGoalTask(model, marking, id, GoalTaskStatus.PENDING);
            case "True" -> IStarPropagation.assignQuality(model, marking, id, QualityStatus.TRUE);
            case "False" -> IStarPropagation.assignQuality(model, marking, id, QualityStatus.FALSE);
            default -> marking;
        };
    }

    private static List<String> resolveTargetIds(String instanceId, String elementId, String objectInstanceId,
                                                 Map<String, String> instanceActorType,
                                                 Map<String, List<String>> scopeTypes) {
        List<String> scope = scopeTypes.get(elementId);
        if (instanceId != null && objectInstanceId != null) {
            if (scope != null && scope.size() >= 2) {
                return List.of(occurrenceId(elementId, new TupleOccurrence(List.of(instanceId, objectInstanceId))));
            }
        }
        if (instanceId != null && scope != null && scope.size() == 1 && instanceActorType.containsKey(instanceId)) {
            return List.of(occurrenceId(elementId, new TupleOccurrence(List.of(instanceId))));
        }
        if (instanceId != null) return List.of(elementId);
        return List.of(elementId);
    }

    private static List<AggregateResult> evaluateAggregates(GoalModel source, IStarScenarioModel scenario,
                                                            IStarMarking marking,
                                                            Map<String, String> instanceActorType,
                                                            Map<String, List<String>> instancesByType,
                                                            Map<String, List<String>> scopeTypes) {
        List<AggregateResult> out = new ArrayList<>();
        Set<String> declared = new LinkedHashSet<>();
        for (ScenarioStmt stmt : scenario.statements()) {
            if (stmt instanceof ScenarioStmt.Aggregate a) {
                declared.add(a.label());
                String actorType = a.actorType() != null ? a.actorType() : firstScopeType(scopeTypes.get(a.elementId()));
                List<String> universe = actorType == null ? new ArrayList<>(instanceActorType.keySet()) : instancesByType.getOrDefault(actorType, List.of());
                out.add(evaluateAggregate(a.label(), a.mode(), a.elementId(), actorType, universe, source, marking, scopeTypes));
            }
        }

        return out;
    }

    private static AggregateResult evaluateAggregate(String label, AggregateMode mode, String elementId, String actorType,
                                                     List<String> universe, GoalModel source, IStarMarking marking,
                                                     Map<String, List<String>> scopeTypes) {
        IntentionalElement elem = source.findElement(elementId).orElse(null);
        List<String> satisfied = new ArrayList<>();
        for (String inst : universe) {
            String id = aggregateId(elementId, actorType, inst, scopeTypes);
            if (isPositive(elem, id, marking)) satisfied.add(inst);
        }
        boolean holds = mode == AggregateMode.ALL ? satisfied.size() == universe.size() : !satisfied.isEmpty();
        return new AggregateResult(label, mode, elementId, holds, satisfied, universe);
    }

    private static boolean isPositive(IntentionalElement elem, String id, IStarMarking marking) {
        if (elem == null) return false;
        return switch (elem) {
            case Goal g -> marking.goalTaskStatus(id) == GoalTaskStatus.FULFILLED;
            case Task t -> marking.goalTaskStatus(id) == GoalTaskStatus.FULFILLED;
            case Quality q -> marking.qualityStatus(id) == QualityStatus.TRUE;
            case Resource r -> false;
            case Obstacle o -> false;
        };
    }

    private static Map<String, String> actorLabels(IStarScenarioModel scenario) {
        Map<String, String> labels = new LinkedHashMap<>();
        for (ScenarioInstance i : scenario.instances()) labels.put(actorInstanceId(i.actorType(), i.name()), i.actorType() + " [" + i.name() + "]");
        return labels;
    }

    private static Map<String, String> nodeLabels(GoalModel source, Map<String, List<String>> scopeTypes,
                                                  Map<String, List<String>> instancesByType) {
        Map<String, String> labels = new LinkedHashMap<>();
        for (var e : scopeTypes.entrySet()) {
            for (TupleOccurrence tuple : tuplesFor(e.getValue(), instancesByType)) {
                labels.put(occurrenceId(e.getKey(), tuple),
                        e.getKey() + " [" + String.join(",", tuple.instanceIds()) + "]");
            }
        }
        return labels;
    }

    private static void addQualifiedScope(Map<String, List<String>> scopeTypes, String instanceId, String elementId,
                                          Map<String, String> instanceActorType) {
        if (instanceId != null && instanceActorType.containsKey(instanceId)) {
            putScope(scopeTypes, elementId, List.of(instanceActorType.get(instanceId)));
        }
    }

    private static void addScopedOrSingleton(List<String> out, String elementId, Map<String, List<String>> scopeTypes,
                                             Map<String, List<String>> instancesByType) {
        List<String> scope = scopeTypes.get(elementId);
        if (scope == null || scope.isEmpty()) out.add(elementId);
        else for (TupleOccurrence tuple : tuplesFor(scope, instancesByType)) out.add(occurrenceId(elementId, tuple));
    }

    private static List<String> idsInScope(String elementId, TupleOccurrence context,
                                           Map<String, List<String>> scopeTypes,
                                           Map<String, List<String>> instancesByType) {
        if (elementId == null) return List.of();
        List<String> scope = scopeTypes.get(elementId);
        if (scope == null || scope.isEmpty()) return List.of(elementId);
        if (scope.size() == context.instanceIds().size()) return List.of(occurrenceId(elementId, context));
        if (scope.size() > context.instanceIds().size()) {
            List<String> out = new ArrayList<>();
            List<String> suffix = context.instanceIds();
            List<String> freeScope = scope.subList(0, scope.size() - suffix.size());
            for (TupleOccurrence free : tuplesFor(freeScope, instancesByType)) {
                List<String> ids = new ArrayList<>(free.instanceIds());
                ids.addAll(suffix);
                out.add(occurrenceId(elementId, new TupleOccurrence(ids)));
            }
            return out;
        }
        return List.of(occurrenceId(elementId,
                new TupleOccurrence(context.instanceIds().subList(context.instanceIds().size() - scope.size(),
                        context.instanceIds().size()))));
    }

    private static String idInTuple(String elementId, TupleOccurrence context,
                                    Map<String, List<String>> scopeTypes,
                                    Map<String, List<String>> instancesByType) {
        List<String> ids = idsInScope(elementId, context, scopeTypes, instancesByType);
        return ids.isEmpty() ? elementId : ids.get(0);
    }

    private static String actorForDependencyEnd(String actorType, TupleOccurrence tuple, List<String> scope,
                                                IStarScenarioModel scenario, Map<String, List<String>> instancesByType) {
        int index = firstIndexOf(scope, actorType);
        String scopeInstance = index >= 0 ? tuple.instanceIds().get(index) : null;
        for (ScenarioInstance i : scenario.instances()) {
            if (i.name().equals(scopeInstance) && i.actorType().equals(actorType)) return actorInstanceId(actorType, scopeInstance);
        }
        return firstActorInstanceId(actorType, instancesByType);
    }

    private static String firstActorInstanceId(String actorType, Map<String, List<String>> instancesByType) {
        List<String> instances = instancesByType.getOrDefault(actorType, List.of());
        return instances.isEmpty() ? actorType : actorInstanceId(actorType, instances.get(0));
    }

    private static boolean isFirstInstance(ScenarioInstance instance, Map<String, List<String>> instancesByType) {
        List<String> instances = instancesByType.getOrDefault(instance.actorType(), List.of());
        return !instances.isEmpty() && instances.get(0).equals(instance.name());
    }

    private static Map<String, Integer> orChildCounts(Actor actor) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (Refinement r : actor.refinements()) if (r instanceof OrRefinement or) counts.merge(or.parent(), 1, Integer::sum);
        return counts;
    }

    private static Map<String, String> ownerOf(GoalModel source) {
        Map<String, String> map = new LinkedHashMap<>();
        for (Actor actor : source.getActors()) for (IntentionalElement e : actor.elements()) map.put(e.id(), actor.name());
        return map;
    }

    private static Map<String, Actor> actors(GoalModel source) {
        Map<String, Actor> map = new LinkedHashMap<>();
        for (Actor actor : source.getActors()) map.put(actor.name(), actor);
        return map;
    }

    private static Map<String, String> instanceActorTypes(IStarScenarioModel scenario) {
        Map<String, String> map = new LinkedHashMap<>();
        for (ScenarioInstance i : scenario.instances()) map.put(i.name(), i.actorType());
        return map;
    }

    private static Map<String, List<String>> instancesByType(Map<String, String> instanceActorType) {
        Map<String, List<String>> map = new LinkedHashMap<>();
        instanceActorType.forEach((id, type) -> map.computeIfAbsent(type, k -> new ArrayList<>()).add(id));
        return map;
    }

    private static boolean putScope(Map<String, List<String>> map, String key, List<String> value) {
        if (key == null || value == null || value.isEmpty()) return false;
        List<String> current = map.get(key);
        if (current != null && current.size() >= value.size()) return false;
        map.put(key, List.copyOf(value));
        return true;
    }

    private static List<String> prependScope(String actorType, List<String> parentScope) {
        List<String> out = new ArrayList<>();
        out.add(actorType);
        if (parentScope != null) out.addAll(parentScope);
        return out;
    }

    @SafeVarargs
    private static List<String> longestScope(List<String>... values) {
        List<String> best = null;
        for (List<String> value : values) {
            if (value != null && (best == null || value.size() > best.size())) best = value;
        }
        return best;
    }

    private static List<TupleOccurrence> tuplesFor(List<String> scope, Map<String, List<String>> instancesByType) {
        if (scope == null || scope.isEmpty()) return List.of(new TupleOccurrence(List.of()));
        List<TupleOccurrence> out = new ArrayList<>();
        buildTuples(scope, 0, new ArrayList<>(), instancesByType, out);
        return out;
    }

    private static void buildTuples(List<String> scope, int index, List<String> current,
                                    Map<String, List<String>> instancesByType, List<TupleOccurrence> out) {
        if (index == scope.size()) {
            out.add(new TupleOccurrence(current));
            return;
        }
        for (String instance : instancesByType.getOrDefault(scope.get(index), List.of())) {
            List<String> next = new ArrayList<>(current);
            next.add(instance);
            buildTuples(scope, index + 1, next, instancesByType, out);
        }
    }

    private static int firstIndexOf(List<String> values, String value) {
        if (values == null || value == null) return -1;
        for (int i = 0; i < values.size(); i++) if (value.equals(values.get(i))) return i;
        return -1;
    }

    private static boolean isInstanceElement(IntentionalElement elem) {
        return elem instanceof Goal || elem instanceof Task || elem instanceof Obstacle;
    }

    private static String firstScopeType(List<String> scope) {
        return scope == null || scope.isEmpty() ? null : scope.get(0);
    }

    private static Map<String, String> firstScopeTypes(Map<String, List<String>> scopes) {
        Map<String, String> out = new LinkedHashMap<>();
        for (var e : scopes.entrySet()) {
            String first = firstScopeType(e.getValue());
            if (first != null) out.put(e.getKey(), first);
        }
        return out;
    }

    private static String aggregateId(String elementId, String actorType, String instanceId,
                                      Map<String, List<String>> scopeTypes) {
        List<String> scope = scopeTypes.get(elementId);
        if (actorType == null || scope == null || scope.isEmpty()) return elementId;
        int index = firstIndexOf(scope, actorType);
        if (index < 0 || scope.size() != 1) return elementId;
        return occurrenceId(elementId, new TupleOccurrence(List.of(instanceId)));
    }

    private static String firstNonNull(String... values) {
        for (String value : values) if (value != null) return value;
        return null;
    }

    private static String actorInstanceId(String actorType, String instanceId) {
        return actorType + "__" + instanceId;
    }

    private static String occurrenceId(String elementId, String instanceId) {
        return elementId + "__" + instanceId;
    }

    private static String occurrenceId(String elementId, TupleOccurrence tuple) {
        if (tuple.instanceIds().isEmpty()) return elementId;
        return elementId + "__" + String.join("__", tuple.instanceIds());
    }

    private static IntentionalElement copyElement(IntentionalElement elem, String id) {
        return switch (elem) {
            case Goal g -> new Goal(id, g.goalType());
            case Task t -> new Task(id);
            case Resource r -> new Resource(id);
            case Quality q -> new Quality(id);
            case Obstacle o -> new Obstacle(id, o.type());
        };
    }
}
