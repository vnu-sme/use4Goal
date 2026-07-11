package org.vnu.sme.goal.bpmn2.mm;

public final class Message {

    private final String id;
    private final String name;

    public Message(String id, String name) {
        this.id   = id;
        this.name = name;
    }

    public String id()   { return id; }
    public String name() { return name; }
}
