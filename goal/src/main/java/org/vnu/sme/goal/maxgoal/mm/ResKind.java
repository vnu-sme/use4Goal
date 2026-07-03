package org.vnu.sme.goal.maxgoal.mm;

public enum ResKind {
    VIRTUAL, PHYSICAL;

    public static ResKind from(String text) {
        return switch (text.toLowerCase()) {
            case "virtual"  -> VIRTUAL;
            case "physical" -> PHYSICAL;
            default -> throw new IllegalArgumentException("Unknown resource kind: " + text);
        };
    }
}
