package org.vnu.sme.goal.dsl.istar.mm;

/** iStar 2.0 Task (hexagon). */
public final class Task implements GoalTaskElement {

    private String id;
    private java.util.List<IStarOclConstraint> constraints;

    public Task(String id) {
        this(id, java.util.List.of());
    }

    public Task(String id, String oclSource) {
        this(id, oclSource == null ? java.util.List.of() : java.util.List.of(
                new IStarOclConstraint(IStarOclConstraint.Kind.POST, oclSource)));
    }

    public Task(String id, java.util.List<IStarOclConstraint> constraints) {
        this.id = id;
        this.constraints = java.util.List.copyOf(constraints);
    }

    @Override public String id() { return id; }
    @Override public java.util.List<IStarOclConstraint> constraints() { return constraints; }
    public void setId(String id) { this.id = id; }
    public void setOclSource(String oclSource) {
        constraints = oclSource == null ? java.util.List.of() : java.util.List.of(
                new IStarOclConstraint(IStarOclConstraint.Kind.POST, oclSource));
    }
}
