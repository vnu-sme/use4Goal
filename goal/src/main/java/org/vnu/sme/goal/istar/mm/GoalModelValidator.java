package org.vnu.sme.goal.istar.mm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Semantic invariants of the i* MM.
 *
 * <p>The parser guarantees concrete syntax. This validator guarantees that the resulting
 * {@link GoalModel} is a well-formed i* model before any diagram, scenario, or transformation
 * consumes it.
 */
public final class GoalModelValidator {

    private GoalModelValidator() {}

    public static List<String> validate(GoalModel model) {
        List<String> errors = new ArrayList<>();
        validateElementIdUniqueness(model, errors);
        validateRefinements(model, errors);
        validateRefinementKindConsistency(model, errors);
        validateRefinementAcyclic(model, errors);
        validateParameterRefinementActorTypes(model, errors);
        validateContributions(model, errors);
        validateQualifications(model, errors);
        validateNeededBy(model, errors);
        validateObstructions(model, errors);
        validateResolutions(model, errors);
        validateAssociations(model, errors);
        validateDependencies(model, errors);
        return List.copyOf(errors);
    }

    private static void validateElementIdUniqueness(GoalModel model, List<String> errors) {
        Map<String, String> firstOwner = new HashMap<>();
        for (Actor actor : model.getActors()) {
            for (IntentionalElement e : actor.elements()) {
                String prevOwner = firstOwner.putIfAbsent(e.id(), actor.name());
                if (prevOwner != null) {
                    errors.add("semantic: element id '" + e.id() + "' is declared more than once (actors '"
                            + prevOwner + "' and '" + actor.name()
                            + "') — element ids must be unique across the model");
                }
            }
        }
    }

    private static void validateRefinements(GoalModel model, List<String> errors) {
        for (Actor actor : model.getActors()) {
            for (Refinement ref : actor.refinements()) {
                if (ref instanceof AndRefinement and && and.children().size() < 2) {
                    errors.add("semantic: and-refine '" + and.parent()
                            + "' in actor '" + actor.name()
                            + "' must have at least 2 children (found "
                            + and.children().size() + ")");
                }
                validateRefinementEnd(model, actor, ref.parent(), "parent", ref, errors);
                for (String child : refinementChildren(ref)) {
                    validateRefinementEnd(model, actor, child, "child", ref, errors);
                }
            }
        }
    }

    private static void validateRefinementEnd(GoalModel model, Actor actor, String elementId,
                                              String role, Refinement ref, List<String> errors) {
        Optional<IntentionalElement> element = model.findElement(elementId);
        if (element.isEmpty()) {
            errors.add("semantic: refinement '" + refinementLabel(ref) + "' in actor '" + actor.name()
                    + "' has unknown " + role + " element '" + elementId + "'");
            return;
        }
        if (model.ownerOf(elementId).filter(actor.name()::equals).isEmpty()) {
            errors.add("semantic: refinement '" + refinementLabel(ref) + "' in actor '" + actor.name()
                    + "' references " + role + " '" + elementId + "' owned by actor '"
                    + model.ownerOf(elementId).orElse("<none>")
                    + "' — a refinement must stay within a single actor (use 'depend' to cross actors)");
        }
        if (!(element.get() instanceof Goal || element.get() instanceof Task)) {
            errors.add("semantic: refinement '" + refinementLabel(ref) + "' in actor '" + actor.name()
                    + "' has " + role + " '" + elementId + "' of kind " + kindOf(element.get())
                    + " — refinements can only connect goals and tasks");
        }
    }

    private static List<String> refinementChildren(Refinement ref) {
        return switch (ref) {
            case AndRefinement and -> and.children();
            case OrRefinement or -> List.of(or.child());
            case ForRefinement forR -> List.of(forR.child());
            case PickRefinement p -> List.of(p.child());
        };
    }

    private static void validateRefinementKindConsistency(GoalModel model, List<String> errors) {
        Map<String, Set<String>> kindsByParent = new LinkedHashMap<>();
        for (Actor actor : model.getActors()) {
            for (Refinement ref : actor.refinements()) {
                kindsByParent.computeIfAbsent(ref.parent(), k -> new LinkedHashSet<>()).add(kindTag(ref));
            }
        }
        kindsByParent.forEach((parent, kinds) -> {
            if (kinds.size() > 1) {
                errors.add("semantic: element '" + parent + "' is decomposed by more than one refinement kind "
                        + kinds + " — an element must be refined by exactly one kind (AND, OR, forall or pick)");
            }
        });
    }

    private static String kindTag(Refinement ref) {
        return switch (ref) {
            case AndRefinement ignored -> "AND";
            case OrRefinement ignored -> "OR";
            case ForRefinement ignored -> "forall";
            case PickRefinement ignored -> "pick";
        };
    }

    private static void validateRefinementAcyclic(GoalModel model, List<String> errors) {
        Map<String, List<String>> childToParents = new LinkedHashMap<>();
        for (Actor actor : model.getActors()) {
            for (Refinement ref : actor.refinements()) {
                for (String child : refinementChildren(ref)) {
                    childToParents.computeIfAbsent(child, k -> new ArrayList<>()).add(ref.parent());
                }
            }
        }
        Map<String, Integer> color = new HashMap<>();
        for (String node : childToParents.keySet()) {
            if (color.getOrDefault(node, 0) == 0 && hasCycle(node, childToParents, color)) {
                errors.add("semantic: refinement cycle detected involving element '" + node + "'");
                break;
            }
        }
    }

    private static boolean hasCycle(String node, Map<String, List<String>> edges, Map<String, Integer> color) {
        color.put(node, 1);
        for (String next : edges.getOrDefault(node, List.of())) {
            int c = color.getOrDefault(next, 0);
            if (c == 1) return true;
            if (c == 0 && hasCycle(next, edges, color)) return true;
        }
        color.put(node, 2);
        return false;
    }

    private static void validateParameterRefinementActorTypes(GoalModel model, List<String> errors) {
        for (Actor actor : model.getActors()) {
            for (Refinement ref : actor.refinements()) {
                if (ref instanceof ParameterRefinement parameter
                        && model.findActor(parameter.actorType()).isEmpty()) {
                    errors.add("semantic: parameter-refine '" + parameter.child() + " -> " + parameter.parent()
                            + "' in actor '" + actor.name()
                            + "' references unknown actor type '" + parameter.actorType() + "'");
                }
            }
        }
    }

    private static void validateContributions(GoalModel model, List<String> errors) {
        for (Actor actor : model.getActors()) {
            for (Contribution c : actor.contributions()) {
                model.findElement(c.element()).ifPresentOrElse(source -> {
                    if (!sameOwner(model, actor, c.element())) {
                        errors.add("semantic: contribution source '" + c.element() + "' in actor '" + actor.name()
                                + "' is owned by '" + model.ownerOf(c.element()).orElse("<none>") + "'");
                    }
                }, () -> errors.add("semantic: contribution in actor '" + actor.name()
                        + "' has unknown source element '" + c.element() + "'"));
                model.findElement(c.quality()).ifPresentOrElse(target -> {
                    if (!(target instanceof Quality)) {
                        errors.add("semantic: contribution '" + c.element() + " > " + c.type().label().toLowerCase()
                                + " " + c.quality() + "' in actor '" + actor.name()
                                + "' must target a quality, but '" + c.quality() + "' is a " + kindOf(target));
                    }
                }, () -> errors.add("semantic: contribution in actor '" + actor.name()
                        + "' has unknown target quality '" + c.quality() + "'"));
                if (c.element().equals(c.quality())) {
                    errors.add("semantic: quality '" + c.quality() + "' cannot contribute to itself");
                }
            }
        }
    }

    private static void validateQualifications(GoalModel model, List<String> errors) {
        for (Actor actor : model.getActors()) {
            for (Qualification q : actor.qualifications()) {
                model.findElement(q.quality()).ifPresentOrElse(source -> {
                    if (!(source instanceof Quality)) {
                        errors.add("semantic: qualification in actor '" + actor.name()
                                + "' must originate from a quality, but '" + q.quality()
                                + "' is a " + kindOf(source));
                    }
                }, () -> errors.add("semantic: qualification in actor '" + actor.name()
                        + "' has unknown quality '" + q.quality() + "'"));
                model.findElement(q.element()).ifPresentOrElse(target -> {
                    if (!(target instanceof Goal || target instanceof Task || target instanceof Resource)) {
                        errors.add("semantic: qualification '" + q.quality() + " > qualifies " + q.element()
                                + "' in actor '" + actor.name()
                                + "' must target a goal, task or resource, but '" + q.element()
                                + "' is a " + kindOf(target));
                    }
                }, () -> errors.add("semantic: qualification has unknown target element '" + q.element() + "'"));
            }
        }
    }

    private static void validateNeededBy(GoalModel model, List<String> errors) {
        for (Actor actor : model.getActors()) {
            for (NeededBy nb : actor.neededBys()) {
                model.findElement(nb.resource()).ifPresentOrElse(source -> {
                    if (!(source instanceof Resource)) {
                        errors.add("semantic: needed-by in actor '" + actor.name()
                                + "' must originate from a resource, but '" + nb.resource()
                                + "' is a " + kindOf(source));
                    }
                }, () -> errors.add("semantic: needed-by in actor '" + actor.name()
                        + "' has unknown source resource '" + nb.resource() + "'"));
                model.findElement(nb.task()).ifPresentOrElse(target -> {
                    if (!(target instanceof Task)) {
                        errors.add("semantic: needed-by '" + nb.resource() + " > needed-by " + nb.task()
                                + "' in actor '" + actor.name() + "' must target a task, but '"
                                + nb.task() + "' is a " + kindOf(target));
                    }
                }, () -> errors.add("semantic: needed-by has unknown target task '" + nb.task() + "'"));
            }
        }
    }

    private static void validateObstructions(GoalModel model, List<String> errors) {
        for (Actor actor : model.getActors()) {
            for (Obstruction ob : actor.obstructions()) {
                model.findElement(ob.obstacle()).ifPresentOrElse(source -> {
                    if (!(source instanceof Obstacle)) {
                        errors.add("semantic: obstructs in actor '" + actor.name()
                                + "' must originate from an obstacle, but '" + ob.obstacle()
                                + "' is a " + kindOf(source));
                    }
                }, () -> errors.add("semantic: obstructs in actor '" + actor.name()
                        + "' has unknown source obstacle '" + ob.obstacle() + "'"));
                model.findElement(ob.element()).ifPresentOrElse(target -> {
                    if (!(target instanceof Goal || target instanceof Task)) {
                        errors.add("semantic: obstructs '" + ob.obstacle() + " > obstructs " + ob.element()
                                + "' in actor '" + actor.name() + "' must target a goal or task, but '"
                                + ob.element() + "' is a " + kindOf(target));
                    }
                }, () -> errors.add("semantic: obstructs has unknown target element '" + ob.element() + "'"));
            }
        }
    }

    private static void validateResolutions(GoalModel model, List<String> errors) {
        for (Actor actor : model.getActors()) {
            for (Resolution rs : actor.resolutions()) {
                model.findElement(rs.element()).ifPresentOrElse(source -> {
                    if (!(source instanceof Goal || source instanceof Task)) {
                        errors.add("semantic: resolves in actor '" + actor.name()
                                + "' must originate from a goal or task, but '" + rs.element()
                                + "' is a " + kindOf(source));
                    }
                }, () -> errors.add("semantic: resolves in actor '" + actor.name()
                        + "' has unknown source element '" + rs.element() + "'"));
                model.findElement(rs.obstacle()).ifPresentOrElse(target -> {
                    if (!(target instanceof Obstacle)) {
                        errors.add("semantic: resolves '" + rs.element() + " > resolves " + rs.obstacle()
                                + "' in actor '" + actor.name() + "' must target an obstacle, but '"
                                + rs.obstacle() + "' is a " + kindOf(target));
                    }
                }, () -> errors.add("semantic: resolves has unknown target obstacle '" + rs.obstacle() + "'"));
            }
        }
    }

    private static void validateAssociations(GoalModel model, List<String> errors) {
        Map<AssocKind, Map<String, List<String>>> graphs = new HashMap<>();
        for (Actor actor : model.getActors()) {
            for (Association a : actor.associations()) {
                String relation = a.kind() == AssocKind.IS_A ? "is-a" : "participates-in";
                if (a.actor().equals(a.target())) {
                    errors.add("semantic: actor '" + a.actor() + "' cannot " + relation + " itself");
                    continue;
                }
                if (model.findActor(a.actor()).isEmpty())
                    errors.add("semantic: unknown actor '" + a.actor() + "' in '" + relation + "' relation");
                if (model.findActor(a.target()).isEmpty())
                    errors.add("semantic: unknown actor '" + a.target() + "' in '" + relation + "' relation");
                graphs.computeIfAbsent(a.kind(), k -> new LinkedHashMap<>())
                        .computeIfAbsent(a.actor(), k -> new ArrayList<>()).add(a.target());
            }
        }
        graphs.forEach((kind, graph) -> {
            Map<String, Integer> color = new HashMap<>();
            for (String node : graph.keySet()) {
                if (color.getOrDefault(node, 0) == 0 && hasCycle(node, graph, color)) {
                    errors.add("semantic: actor " + (kind == AssocKind.IS_A ? "is-a" : "participates-in")
                            + " cycle detected involving actor '" + node + "'");
                    break;
                }
            }
        });
    }

    private static void validateDependencies(GoalModel model, List<String> errors) {
        Set<String> decomposedElements = refinementParents(model);
        for (Dependency d : model.getDependencies()) {
            validateActor(model, d.depender(), "depender", d, errors);
            validateActor(model, d.dependee(), "dependee", d, errors);
            validateEndpointElement(model, d.depender(), d.dependerElmt(), "depender", d, errors);
            validateEndpointElement(model, d.dependee(), d.dependeeElmt(), "dependee", d, errors);
            validateDependum(model, d, errors);

            if (d.depender().equals(d.dependee())) {
                errors.add("semantic: dependency '" + dependencyLabel(d)
                        + "' has the same actor '" + d.depender() + "' as both depender and dependee");
            }
            if (d.dependeeElmt() != null && decomposedElements.contains(d.dependeeElmt())) {
                errors.add("semantic: dependency '" + dependencyLabel(d) + "' attaches to dependee element '"
                        + d.dependeeElmt() + "', which is itself further decomposed by a refinement"
                        + " — the dependee endpoint must be a leaf element");
            }
        }
    }

    private static Set<String> refinementParents(GoalModel model) {
        Set<String> parents = new HashSet<>();
        for (Actor actor : model.getActors())
            for (Refinement ref : actor.refinements())
                parents.add(ref.parent());
        return parents;
    }

    private static void validateActor(GoalModel model, String actorId, String role,
                                      Dependency d, List<String> errors) {
        if (model.findActor(actorId).isEmpty()) {
            errors.add("semantic: dependency '" + dependencyLabel(d) + "' has unknown " + role
                    + " actor '" + actorId + "'");
        }
    }

    private static void validateEndpointElement(GoalModel model, String actorId, String elementId,
                                                String role, Dependency d, List<String> errors) {
        if (elementId == null) return;
        Optional<Actor> actor = model.findActor(actorId);
        Optional<IntentionalElement> element = model.findElement(elementId);
        if (element.isEmpty()) {
            errors.add("semantic: dependency '" + dependencyLabel(d) + "' has unknown " + role
                    + " endpoint element '" + elementId + "'");
            return;
        }
        if (actor.isPresent() && model.ownerOf(elementId).filter(actorId::equals).isEmpty()) {
            errors.add("semantic: dependency '" + dependencyLabel(d) + "' has " + role
                    + " endpoint '" + actorId + "." + elementId + "', but element is owned by '"
                    + model.ownerOf(elementId).orElse("<none>") + "'");
        }
    }

    private static void validateDependum(GoalModel model, Dependency d, List<String> errors) {
        Optional<IntentionalElement> dependum = model.findElement(d.dependum());
        if (dependum.isEmpty()) {
            errors.add("semantic: dependency '" + dependencyLabel(d) + "' has unknown dependum element '"
                    + d.dependum() + "'");
            return;
        }
        String actualKind = kindOf(dependum.get());
        if (!actualKind.equals(d.dependumKind())) {
            errors.add("semantic: dependency '" + dependencyLabel(d) + "' declares dependum as '"
                    + d.dependumKind() + "', but '" + d.dependum() + "' is a '" + actualKind + "'");
        }
    }

    private static boolean sameOwner(GoalModel model, Actor actor, String elementId) {
        return model.ownerOf(elementId).filter(actor.name()::equals).isPresent();
    }

    private static String refinementLabel(Refinement ref) {
        return switch (ref) {
            case AndRefinement and -> and.children() + " > " + and.parent();
            case OrRefinement or -> or.child() + " > or " + or.parent();
            case ForRefinement forR -> forR.child() + " > forall " + forR.actorType() + " " + forR.parent();
            case PickRefinement p -> p.child() + " > pick " + p.actorType() + " " + p.parent();
        };
    }

    private static String kindOf(IntentionalElement element) {
        if (element instanceof Goal) return "goal";
        if (element instanceof Task) return "task";
        if (element instanceof Resource) return "resource";
        if (element instanceof Quality) return "quality";
        if (element instanceof Obstacle) return "obstacle";
        return element.getClass().getSimpleName();
    }

    private static String dependencyLabel(Dependency d) {
        return d.depender() + endpointSuffix(d.dependerElmt()) + " -> "
                + d.dependumKind() + " " + d.dependum() + " -> "
                + d.dependee() + endpointSuffix(d.dependeeElmt());
    }

    private static String endpointSuffix(String elementId) {
        return elementId == null ? "" : "." + elementId;
    }
}
