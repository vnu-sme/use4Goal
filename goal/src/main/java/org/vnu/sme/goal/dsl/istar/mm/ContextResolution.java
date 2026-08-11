package org.vnu.sme.goal.dsl.istar.mm;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Resolves which ActorType a Goal/Task's {@code self} is bound to, reading .istar's own
 * refinement structure directly -- no separate context declaration on the ocl clause, no
 * mapping file between istar and .use. Default: the actor block the element is declared
 * in. Override: the ActorType named in the nearest forall/pick relation reached by walking
 * up the element's own AND/OR chain (not just its own edge) -- restoring the original SR
 * structure put plain AND/OR children (CollectFromCalendar, ContactedByPhone) of a
 * forall/pick-quantified goal (TimetableCollected) back under a *different* actor
 * (Organizer) than the one that actually governs them (Participant); the walk finds that
 * governing ActorType through the chain instead of requiring every such element to carry
 * its own duplicate ocl clause on the quantified node itself.
 */
public final class ContextResolution {

    public record Edge(String parentId, ParameterRefinement quantifier) {}

    private final Map<String, Edge> childToEdge = new HashMap<>();

    private ContextResolution() {}

    public static ContextResolution of(GoalModel gm) {
        ContextResolution r = new ContextResolution();
        for (Actor actor : gm.getActors()) {
            for (Refinement ref : actor.refinements()) {
                switch (ref) {
                    case AndRefinement and ->
                            and.children().forEach(c -> r.childToEdge.put(c, new Edge(and.parent(), null)));
                    case OrRefinement or ->
                            r.childToEdge.put(or.child(), new Edge(or.parent(), null));
                    case ForRefinement forR ->
                            r.childToEdge.put(forR.child(), new Edge(forR.parent(), forR));
                    case PickRefinement p ->
                            r.childToEdge.put(p.child(), new Edge(p.parent(), p));
                }
            }
        }
        return r;
    }

    /** The actor type name self should be bound to when compiling/evaluating this element's ocl
     *  clause: the nearest forall/pick ActorType found by walking up the AND/OR chain from this
     *  element, or its owning actor when no such ancestor exists anywhere in the chain. */
    public String actorTypeOf(GoalModel gm, String elementId) {
        return contextTypesOf(gm, elementId).get(0);
    }

    /**
     * Context stack for an element, from the innermost binding ({@code self}) to
     * successively enclosing forall/pick bindings ({@code self.outer}, ...).
     * A dependency transfers the depender element's complete stack to its dependum and
     * dependee element; this preserves bindings such as Secretary -> Participant -> Organizer.
     */
    public List<String> contextTypesOf(GoalModel gm, String elementId) {
        return contextTypesOf(gm, elementId, new LinkedHashSet<>());
    }

    private List<String> contextTypesOf(GoalModel gm, String elementId, Set<String> visiting) {
        if (!visiting.add(elementId)) return baseContextTypes(gm, elementId);
        for (var dependency : gm.getDependencies()) {
            boolean receivesContext = elementId.equals(dependency.dependum())
                    || elementId.equals(dependency.dependeeElmt());
            if (!receivesContext || dependency.dependerElmt() == null) continue;
            List<String> upstream = contextTypesOf(gm, dependency.dependerElmt(), visiting);
            String owner = gm.ownerOf(elementId).orElse(null);
            if (owner != null) {
                List<String> transferred = new ArrayList<>();
                transferred.add(owner);
                for (String type : upstream) if (!transferred.contains(type)) transferred.add(type);
                return List.copyOf(transferred);
            }
        }
        return baseContextTypes(gm, elementId);
    }

    private List<String> baseContextTypes(GoalModel gm, String elementId) {
        String current = elementId;
        Map<String, Boolean> visited = new HashMap<>();
        List<String> result = new ArrayList<>();
        while (visited.put(current, Boolean.TRUE) == null) {
            Edge edge = childToEdge.get(current);
            if (edge == null) break;
            if (edge.quantifier() != null && !result.contains(edge.quantifier().actorType())) {
                result.add(edge.quantifier().actorType());
            }
            current = edge.parentId();
        }
        String owner = gm.ownerOf(elementId).orElseThrow(() ->
                new IllegalStateException("element '" + elementId + "' has no owning actor"));
        if (!result.contains(owner)) result.add(owner);
        return List.copyOf(result);
    }

    /** Present when elementId is itself the *direct* quantified child of a forall/pick relation
     *  (used to decide where cross-actor aggregation must be injected) -- deliberately not
     *  chain-walked: only the element actually named in a forall/pick clause triggers
     *  aggregation into a different owner; an AND/OR descendant of that element just gets
     *  replicated per governing instance (see {@link #actorTypeOf}), it is not itself
     *  aggregated further. */
    public Optional<Edge> quantifiedEdge(String elementId) {
        Edge edge = childToEdge.get(elementId);
        return (edge != null && edge.quantifier() != null) ? Optional.of(edge) : Optional.empty();
    }
}
