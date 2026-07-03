package org.vnu.sme.goal.istar.mm;

/**
 * iStar 2.0 intentional elements: Goal (oval), Task (hexagon),
 * Resource (rectangle), Quality (cloud).
 */
public sealed interface IntentionalElement
        permits IntentionalElement.Goal, IntentionalElement.Task,
                IntentionalElement.Resource, IntentionalElement.Quality {

    String id();

    record Goal    (String id) implements IntentionalElement {}
    record Task    (String id) implements IntentionalElement {}
    record Resource(String id) implements IntentionalElement {}
    record Quality (String id) implements IntentionalElement {}
}
