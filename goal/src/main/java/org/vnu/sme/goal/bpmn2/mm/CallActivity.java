package org.vnu.sme.goal.bpmn2.mm;

public final class CallActivity implements Activity {

    private final String id;
    private final String oclSource;

    public CallActivity(String id) {
        this(id, null);
    }

    public CallActivity(String id, String oclSource) {
        this.id = id;
        this.oclSource = oclSource;
    }

    @Override public String id() { return id; }
    @Override public String oclSource() { return oclSource; }
}
