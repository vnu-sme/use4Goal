package org.vnu.sme.goal.bpmn2.mm;

public final class CallActivity implements Activity {

    private final String id;

    public CallActivity(String id) {
        this.id = id;
    }

    @Override public String id() { return id; }
}
