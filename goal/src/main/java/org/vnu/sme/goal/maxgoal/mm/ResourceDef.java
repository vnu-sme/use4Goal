package org.vnu.sme.goal.maxgoal.mm;

public record ResourceDef(
        String  name,
        String  owner,
        ResKind kind
) implements Intentional {}
