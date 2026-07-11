package org.vnu.sme.goal.bpmn2.view;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Layout result for the Choreography view. Pure data, no Swing/AWT. */
public final class Bpmn2ChoreoLayout {

    public final Map<String, Bpmn2ChoreoParticipant> participants; // by Process id
    public final List<Bpmn2ChoreoMessage>             messages;
    public final int width, height;

    public Bpmn2ChoreoLayout(Map<String, Bpmn2ChoreoParticipant> participants,
                              List<Bpmn2ChoreoMessage> messages, int width, int height) {
        this.participants = Collections.unmodifiableMap(new LinkedHashMap<>(participants));
        this.messages     = List.copyOf(messages);
        this.width        = width;
        this.height       = height;
    }
}
