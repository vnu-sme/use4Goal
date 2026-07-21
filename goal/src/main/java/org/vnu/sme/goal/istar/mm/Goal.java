package org.vnu.sme.goal.istar.mm;

/** iStar 2.0 Goal (oval) — {@code goalType} is optional (KAOS-style Achieve/Maintain/Avoid). */
public final class Goal implements GoalTaskElement {

    private String   id;
    private GoalType goalType;
    private java.util.List<IStarOclConstraint> constraints;

    public Goal(String id, GoalType goalType) {
        this(id, goalType, java.util.List.of());
    }

    public Goal(String id, GoalType goalType, String oclSource) {
        this(id, goalType, oclSource == null ? java.util.List.of() : java.util.List.of(
                new IStarOclConstraint(IStarOclConstraint.Kind.POST, oclSource)));
    }

    public Goal(String id, GoalType goalType, java.util.List<IStarOclConstraint> constraints) {
        this.id       = id;
        this.goalType = goalType;
        this.constraints = java.util.List.copyOf(constraints);
    }

    @Override public String id() { return id; }
    public GoalType goalType()   { return goalType; }
    @Override public java.util.List<IStarOclConstraint> constraints() { return constraints; }

    public void setId(String id)             { this.id = id; }
    public void setGoalType(GoalType goalType) { this.goalType = goalType; }
    public void setOclSource(String oclSource) {
        constraints = oclSource == null ? java.util.List.of() : java.util.List.of(
                new IStarOclConstraint(IStarOclConstraint.Kind.POST, oclSource));
    }
}
