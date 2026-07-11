package org.vnu.sme.goal.bpmn2.mm;

public final class Task implements Activity {

    private final String id;
    private final String name;

    public Task(String id, String name) {
        this.id   = id;
        this.name = name;
    }

    @Override public String id()   { return id; }
    public               String name() { return name; }
}
