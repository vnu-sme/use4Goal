package org.vnu.sme.goal.dsl.bpmnscenario.mm;

import java.util.List;

/** Simple scenario value: either an atom or a list of ids. */
public sealed interface Value permits Value.Atom, Value.ListValue {
    record Atom(String text) implements Value {}

    record ListValue(List<String> items) implements Value {
        public ListValue {
            items = List.copyOf(items);
        }
    }

    default String display() {
        return switch (this) {
            case Atom a -> a.text();
            case ListValue l -> "[" + String.join(", ", l.items()) + "]";
        };
    }
}
