package org.vnu.sme.goal.bpmn2scenario.mm;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Materialized view of a scenario: declarations plus trace/current-state statements. */
public record Bpmn2ScenarioSnapshot(
        Map<String, String> processInstances,
        Map<String, String> actors,
        Map<String, Value> values,
        List<NodeOccurrence> fired,
        List<NodeOccurrence> completed,
        List<NodeOccurrence> active,
        List<TokenMark> tokens,
        List<String> assertions) {

    public Bpmn2ScenarioSnapshot {
        processInstances = Collections.unmodifiableMap(new LinkedHashMap<>(processInstances));
        actors = Collections.unmodifiableMap(new LinkedHashMap<>(actors));
        values = Collections.unmodifiableMap(new LinkedHashMap<>(values));
        fired = List.copyOf(fired);
        completed = List.copyOf(completed);
        active = List.copyOf(active);
        tokens = List.copyOf(tokens);
        assertions = List.copyOf(assertions);
    }
}
