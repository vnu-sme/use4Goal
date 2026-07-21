package org.vnu.sme.goal.aol.mm;

import java.util.List;
import java.util.Objects;

public record AolGroupInstance(String typeName, String instanceId,
                               List<AolGroupInstance> subgroups,
                               List<AolPlay> plays,
                               List<AolEntityInstance> entities) {
    public AolGroupInstance {
        Objects.requireNonNull(typeName, "typeName");
        Objects.requireNonNull(instanceId, "instanceId");
        subgroups = List.copyOf(subgroups);
        plays = List.copyOf(plays);
        entities = List.copyOf(entities);
    }
}
