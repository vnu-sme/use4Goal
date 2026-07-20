package org.vnu.sme.goal.bpmn2.mm;

public final class Task implements Activity {

    private final String id;
    private final String name;
    private final String oclSource;

    public Task(String id, String name) {
        this(id, name, null);
    }

    public Task(String id, String name, String oclSource) {
        this.id   = id;
        this.name = name;
        this.oclSource = oclSource;
    }

    @Override public String id()   { return id; }
    public               String name() { return name; }
    @Override public String oclSource() { return oclSource; }
}
