package org.vnu.sme.goal.maxgoal.mm;

public sealed interface Intentional permits GoalDef, TaskDef, ResourceDef {
    String name();
    String owner();
}
