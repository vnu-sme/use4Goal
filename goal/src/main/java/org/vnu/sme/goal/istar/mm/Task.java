package org.vnu.sme.goal.istar.mm;

/** iStar 2.0 Task (hexagon). */
public final class Task implements GoalTaskElement {

    private String id;
    private String oclSource;

    public Task(String id) {
        this(id, null);
    }

    public Task(String id, String oclSource) {
        this.id = id;
        this.oclSource = oclSource;
    }

    @Override public String id() { return id; }
    @Override public String oclSource() { return oclSource; }
    public void setId(String id) { this.id = id; }
    public void setOclSource(String oclSource) { this.oclSource = oclSource; }
}
