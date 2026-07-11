package org.vnu.sme.goal.dcr.parser;

import java.util.LinkedHashMap;
import java.util.Map;

import org.vnu.sme.goal.dcr.ast.DcrModelCS;
import org.vnu.sme.goal.dcr.ast.MarkingCS;
import org.vnu.sme.goal.dcr.ast.MarkingFlagCS;
import org.vnu.sme.goal.dcr.mm.DcrEvent;
import org.vnu.sme.goal.dcr.mm.DcrMarking;
import org.vnu.sme.goal.dcr.mm.DcrModel;
import org.vnu.sme.goal.dcr.mm.DcrRelation;
import org.vnu.sme.goal.dcr.mm.DcrRelationKind;

public final class DcrModelFactory {
    private DcrModelFactory() {}

    public static DcrModel build(DcrModelCS cs) {
        DcrModel model = new DcrModel(cs.name());
        Map<String, DcrMarking> markings = new LinkedHashMap<>();
        for (MarkingCS marking : cs.markings()) {
            markings.put(marking.eventId(), toMarking(marking));
        }
        for (var event : cs.events()) {
            model.addEvent(new DcrEvent(event.id(), event.label(),
                    markings.getOrDefault(event.id(), new DcrMarking(false, false, false))));
        }
        for (var relation : cs.relations()) {
            model.addRelation(new DcrRelation(DcrRelationKind.valueOf(relation.kind().name()),
                    relation.source(), relation.target(), relation.time()));
        }
        return model;
    }

    private static DcrMarking toMarking(MarkingCS cs) {
        boolean executed = false;
        boolean included = false;
        boolean pending = false;
        Integer happenedAge = null;
        Integer pendingDeadline = null;
        for (var item : cs.items()) {
            switch (item.flag()) {
                case EXECUTED -> executed = true;
                case INCLUDED -> included = true;
                case PENDING -> pending = true;
            }
            if (item.flag() == MarkingFlagCS.EXECUTED) {
                happenedAge = item.value();
            } else if (item.flag() == MarkingFlagCS.PENDING) {
                pendingDeadline = item.value();
            }
        }
        return new DcrMarking(executed, included, pending, happenedAge, pendingDeadline);
    }
}
