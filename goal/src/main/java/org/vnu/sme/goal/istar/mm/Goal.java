package org.vnu.sme.goal.istar.mm;

/** iStar 2.0 Goal (oval) — {@code goalType} is optional (KAOS-style Achieve/Maintain/Avoid). */
public final class Goal implements GoalTaskElement {

    private String   id;
    private GoalType goalType;

    public Goal(String id, GoalType goalType) {
        this.id       = id;
        this.goalType = goalType;
    }

    @Override public String id() { return id; }
    public GoalType goalType()   { return goalType; }

    public void setId(String id)             { this.id = id; }
    public void setGoalType(GoalType goalType) { this.goalType = goalType; }
}
