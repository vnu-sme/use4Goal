package org.vnu.sme.goal.maxgoal.mm;

public record GoalDef(
        String     name,
        String     owner,
        String     intentClause,
        String     intentExpr,
        RefineSpec refine
) implements Intentional {}
