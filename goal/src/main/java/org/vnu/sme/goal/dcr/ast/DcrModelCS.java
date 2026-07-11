package org.vnu.sme.goal.dcr.ast;

import java.util.List;

public record DcrModelCS(String name, List<EventCS> events, List<MarkingCS> markings,
                         List<RelationCS> relations) {
    public DcrModelCS {
        events = List.copyOf(events);
        markings = List.copyOf(markings);
        relations = List.copyOf(relations);
    }
}
