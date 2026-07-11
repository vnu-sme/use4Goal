package org.vnu.sme.goal.istar.mm;

/**
 * The intentional elements that live "inside" an actor's boundary as a
 * concrete box (as opposed to {@link Quality}, which qualifies/contributes-to
 * but isn't itself decomposed): {@link Resource} (rectangle),
 * {@link GoalTaskElement} (Goal/Task), and {@link Obstacle}.
 */
public sealed interface ConcreteIntentionalElement extends IntentionalElement
        permits Resource, GoalTaskElement, Obstacle {
}
