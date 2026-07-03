package org.vnu.sme.goal.maxgoal.mm;

public enum ActorKind {
    AGENT, ROLE, POSITION;

    public static ActorKind from(String text) {
        return switch (text.toLowerCase()) {
            case "agent"    -> AGENT;
            case "role"     -> ROLE;
            case "position" -> POSITION;
            default -> throw new IllegalArgumentException("Unknown actor kind: " + text);
        };
    }
}
