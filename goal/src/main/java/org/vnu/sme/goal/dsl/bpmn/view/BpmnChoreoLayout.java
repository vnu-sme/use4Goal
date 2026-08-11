package org.vnu.sme.goal.dsl.bpmn.view;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Layout result for the Choreography view. Pure data, no Swing/AWT. */
public final class BpmnChoreoLayout {

    public final Map<String, BpmnChoreoParticipant> participants; // by Process id
    public final List<BpmnChoreoMessage>             messages;
    public final int width, height;

    public BpmnChoreoLayout(Map<String, BpmnChoreoParticipant> participants,
                              List<BpmnChoreoMessage> messages, int width, int height) {
        this.participants = Collections.unmodifiableMap(new LinkedHashMap<>(participants));
        this.messages     = List.copyOf(messages);
        this.width        = width;
        this.height       = height;
    }
}
