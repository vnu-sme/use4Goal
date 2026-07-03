package org.vnu.sme.goal.istar.ast;

/** Cross-actor dependency in concrete syntax: depender → dependum → dependee. */
public record DependencyCS(
        String depender,
        String dependum,
        String dependee
) {}
