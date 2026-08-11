package org.vnu.sme.goal.dsl.istar.mm;

/** iStar 2.0 obstacle types (optional on {@link Obstacle}). */
public enum ObstacleType {
    PREVENTION, RESTORATION, MITIGATION;

    public static ObstacleType from(String s) {
        return switch (s.toLowerCase()) {
            case "prevention"  -> PREVENTION;
            case "restoration" -> RESTORATION;
            case "mitigation"  -> MITIGATION;
            default            -> throw new IllegalArgumentException("Unknown obstacle type: " + s);
        };
    }
}
