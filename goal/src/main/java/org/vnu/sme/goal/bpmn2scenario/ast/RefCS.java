package org.vnu.sme.goal.bpmn2scenario.ast;

import java.util.List;

/** Dotted reference such as {@code m1.participants} or {@code p1.timetable}. */
public record RefCS(List<String> parts) {
    public RefCS {
        parts = List.copyOf(parts);
    }

    public String text() {
        return String.join(".", parts);
    }
}
