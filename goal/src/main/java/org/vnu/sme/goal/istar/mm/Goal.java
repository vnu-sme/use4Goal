package org.vnu.sme.goal.istar.mm;

/** iStar 2.0 Goal (oval) — {@code goalType} is optional (KAOS-style Achieve/Maintain/Avoid). */
public final class Goal implements GoalTaskElement {

    private String   id;
    private GoalType goalType;
    private String   oclSource;

    public Goal(String id, GoalType goalType) {
        this(id, goalType, null);
    }

    public Goal(String id, GoalType goalType, String oclSource) {
        this.id       = id;
        this.goalType = goalType;
        this.oclSource = oclSource;
    }

    @Override public String id() { return id; }
    public GoalType goalType()   { return goalType; }
    @Override public String oclSource() { return oclSource; }

    public void setId(String id)             { this.id = id; }
    public void setGoalType(GoalType goalType) { this.goalType = goalType; }
    public void setOclSource(String oclSource) { this.oclSource = oclSource; }
}
