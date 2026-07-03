package org.vnu.sme.goal.maxgoal.mm;

public record TaskDef(
        String     name,
        String     owner,
        String     pre,
        String     post,
        String     needby,
        RefineSpec refine
) implements Intentional {}
