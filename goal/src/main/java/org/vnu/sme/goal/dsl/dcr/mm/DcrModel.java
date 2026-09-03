package org.vnu.sme.goal.dsl.dcr.mm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class DcrModel {
    private final String name;
    private final Map<String, DcrEvent> events = new LinkedHashMap<>();
    private final List<DcrRelation> relations = new ArrayList<>();

    public DcrModel(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public void addEvent(DcrEvent event) {
        events.put(event.id(), event);
    }

    public void addRelation(DcrRelation relation) {
        relations.add(relation);
    }

    public Collection<DcrEvent> events() {
        return Collections.unmodifiableCollection(events.values());
    }

    public List<DcrRelation> relations() {
        return Collections.unmodifiableList(relations);
    }

    public Optional<DcrEvent> findEvent(String id) {
        return Optional.ofNullable(events.get(id));
    }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        for (DcrRelation relation : relations) {
            if (!events.containsKey(relation.source())) {
                errors.add("semantic: unknown source event '" + relation.source() + "'");
            }
            if (!events.containsKey(relation.target())) {
                errors.add("semantic: unknown target event '" + relation.target() + "'");
            }
            if (relation.time() != null
                    && relation.kind() != DcrRelationKind.CONDITION
                    && relation.kind() != DcrRelationKind.RESPONSE) {
                errors.add("semantic: only condition and response relations may carry time: "
                        + relation.kind().name().toLowerCase() + " "
                        + relation.source() + " -> " + relation.target());
            }
            if (relation.kind() == DcrRelationKind.CONDITION
                    && relation.time() != null
                    && relation.time() == DcrMarking.OMEGA) {
                errors.add("semantic: condition time must be a finite non-negative integer: "
                        + relation.source() + " -> " + relation.target());
            }
        }
        return errors;
    }
}
