package org.vnu.sme.goal.dsl.istar.mm;

/** iStar 2.0 Goal (oval) with an optional temporal contract. */
public final class Goal implements GoalTaskElement {

    private String   id;
    private GoalType goalType;
    private java.util.List<IStarOclConstraint> constraints;

    public Goal(String id, GoalType goalType) {
        this(id, goalType, java.util.List.of());
    }

    public Goal(String id, GoalType goalType, String oclSource) {
        this(id, goalType, oclSource == null ? java.util.List.of() : java.util.List.of(
                new IStarOclConstraint(IStarOclConstraint.Kind.CONDITION, oclSource)));
    }

    public Goal(String id, GoalType goalType, java.util.List<IStarOclConstraint> constraints) {
        this.id       = id;
        this.goalType = goalType;
        this.constraints = java.util.List.copyOf(constraints);
    }

    @Override public String id() { return id; }
    public GoalType goalType()   { return goalType; }
    @Override public java.util.List<IStarOclConstraint> constraints() { return constraints; }
    public java.util.List<IStarOclConstraint> activations() {
        return constraints.stream()
                .filter(x -> x.kind() == IStarOclConstraint.Kind.ACTIVATION).toList();
    }
    public java.util.List<IStarOclConstraint> conditions() {
        return constraints.stream().filter(x -> x.kind() == IStarOclConstraint.Kind.CONDITION).toList();
    }
    public java.util.List<IStarOclConstraint> releases() {
        return constraints.stream().filter(x -> x.kind() == IStarOclConstraint.Kind.RELEASE).toList();
    }
    @Override public String oclSource() {
        return conditions().stream().map(x -> "(" + x.oclBody() + ")")
                .reduce((a, b) -> a + " and " + b).orElse(null);
    }

    public void setId(String id)             { this.id = id; }
    public void setGoalType(GoalType goalType) { this.goalType = goalType; }
    public void setOclSource(String oclSource) {
        constraints = oclSource == null ? java.util.List.of() : java.util.List.of(
                new IStarOclConstraint(IStarOclConstraint.Kind.CONDITION, oclSource));
    }
}
