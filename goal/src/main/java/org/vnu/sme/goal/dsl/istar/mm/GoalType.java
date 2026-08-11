package org.vnu.sme.goal.dsl.istar.mm;

/** Temporal contract attached to an iStar goal. */
public enum GoalType {
    ACHIEVE, MAINTAIN, SUSTAIN, RECUR;

    public static GoalType from(String s) {
        return switch (s.toLowerCase()) {
            case "achieve"  -> ACHIEVE;
            case "maintain" -> MAINTAIN;
            case "sustain"   -> SUSTAIN;
            case "recur"     -> RECUR;
            default         -> throw new IllegalArgumentException("Unknown goal type: " + s);
        };
    }
}
