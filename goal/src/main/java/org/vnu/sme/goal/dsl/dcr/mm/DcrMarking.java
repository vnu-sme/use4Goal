package org.vnu.sme.goal.dsl.dcr.mm;

public record DcrMarking(boolean executed, boolean included, boolean pending,
                         Integer happenedAge, Integer pendingDeadline) {
    public static final int OMEGA = -1;

    public DcrMarking(boolean executed, boolean included, boolean pending) {
        this(executed, included, pending, null, null);
    }
}
