package org.vnu.sme.goal.aol.mm;

import java.util.List;
import java.util.Objects;

/** An object-diagram-level snapshot conforming to one ACL StructuralSpecification. */
public record AolModel(String version, String name, String aclFile,
                       List<String> agents, List<AolGroupInstance> groupInstances,
                       List<AolEntityInstance> topLevelEntities, List<AolLink> links) {
    public AolModel {
        Objects.requireNonNull(version, "version");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(aclFile, "aclFile");
        agents = List.copyOf(agents);
        groupInstances = List.copyOf(groupInstances);
        topLevelEntities = List.copyOf(topLevelEntities);
        links = List.copyOf(links);
    }
}
