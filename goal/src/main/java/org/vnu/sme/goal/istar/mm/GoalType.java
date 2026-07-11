package org.vnu.sme.goal.istar.mm;

/** iStar 2.0 goal types (KAOS-style, optional on {@link GoalTaskElement.Goal}). */
public enum GoalType {
    ACHIEVE, MAINTAIN, AVOID;

    public static GoalType from(String s) {
        return switch (s.toLowerCase()) {
            case "achieve"  -> ACHIEVE;
            case "maintain" -> MAINTAIN;
            case "avoid"    -> AVOID;
            default         -> throw new IllegalArgumentException("Unknown goal type: " + s);
        };
    }
}
