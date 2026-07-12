package org.vnu.sme.goal.istar.mm;

/**
 * The subset of {@link IntentionalElement} that Refinement (refines/to)
 * targets — Resource/Quality/Obstacle are excluded.
 */
public sealed interface GoalTaskElement extends ConcreteIntentionalElement
        permits Goal, Task {
    /** Raw optional OCL guard source captured from .istar; parsed later by the OCL bridge. */
    String oclSource();
}
