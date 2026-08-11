package org.vnu.sme.goal.dsl.aol.ast;

import java.util.List;
import java.util.Objects;

import org.vnu.sme.goal.dsl.acl.ast.AclSourceLocationCS;

public record AolModelCS(String version, String name, String aclFile,
                         List<AolAgentCS> agents, List<AolGroupInstanceCS> groupInstances,
                         List<AolEntityInstanceCS> entities, List<AolLinkCS> links,
                         AclSourceLocationCS location) {
    public AolModelCS {
        Objects.requireNonNull(version, "version");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(aclFile, "aclFile");
        agents = List.copyOf(agents);
        groupInstances = List.copyOf(groupInstances);
        entities = List.copyOf(entities);
        links = List.copyOf(links);
        Objects.requireNonNull(location, "location");
    }
}
