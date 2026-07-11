package org.vnu.sme.goal.bpmn2scenario.ast;

import java.util.List;

/** Literal value in a BPMN scenario. */
public sealed interface ValueCS permits ValueCS.AtomCS, ValueCS.ListCS {
    record AtomCS(String text) implements ValueCS {}

    record ListCS(java.util.List<String> items) implements ValueCS {
        public ListCS {
            items = List.copyOf(items);
        }
    }
}
