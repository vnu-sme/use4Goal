package org.vnu.sme.goal.istar.mm;

/** iStar 2.0 Task (hexagon). */
public final class Task implements GoalTaskElement {

    private String id;

    public Task(String id) { this.id = id; }

    @Override public String id() { return id; }
    public void setId(String id) { this.id = id; }
}
