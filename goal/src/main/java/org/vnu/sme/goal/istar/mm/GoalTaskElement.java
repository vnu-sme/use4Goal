package org.vnu.sme.goal.istar.mm;

/**
 * The subset of {@link IntentionalElement} that Refinement (refines/to)
 * targets — Resource/Quality/Obstacle are excluded.
 */
public sealed interface GoalTaskElement extends ConcreteIntentionalElement
        permits Goal, Task {
    java.util.List<IStarOclConstraint> constraints();
    default java.util.List<IStarOclConstraint> preconditions() {
        return constraints().stream().filter(x -> x.kind() == IStarOclConstraint.Kind.PRE).toList();
    }
    default java.util.List<IStarOclConstraint> postconditions() {
        return constraints().stream().filter(x -> x.kind() == IStarOclConstraint.Kind.POST).toList();
    }
    default String oclSource() {
        return postconditions().stream().map(x -> "(" + x.oclBody() + ")")
                .reduce((a, b) -> a + " and " + b).orElse(null);
    }
}
