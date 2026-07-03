package org.vnu.sme.goal.istar.mm;

public enum ActorKind {
    ACTOR, ROLE, AGENT;

    public static ActorKind from(String s) {
        return switch (s.toLowerCase()) {
            case "role"  -> ROLE;
            case "agent" -> AGENT;
            default      -> ACTOR;
        };
    }
}
