package org.vnu.sme.goal.dcr.ast;

import java.util.List;

public record MarkingCS(String eventId, List<MarkingItemCS> items) {
    public MarkingCS {
        items = List.copyOf(items);
    }
}
