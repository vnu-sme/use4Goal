package org.vnu.sme.goal.istar.ast;

import java.util.List;

/** Actor definition in concrete syntax. */
public record ActorDefCS(
        String              id,
        String              kind,     // "actor" | "role" | "agent"
        List<ElementBodyCS> body
) {}
