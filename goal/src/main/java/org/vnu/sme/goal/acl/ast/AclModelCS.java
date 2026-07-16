package org.vnu.sme.goal.acl.ast;

import java.util.List;

public record AclModelCS(String version,
                         String name,
                         List<AclEnumCS> enums,
                         List<AclEntityCS> entities,
                         List<AclActorCS> actors,
                         List<AclRelationCS> relations,
                         List<AclGroupCS> groups,
                         List<AclLinkCS> links,
                         List<AclInvariantCS> invariants) {
    public AclModelCS {
        enums = List.copyOf(enums);
        entities = List.copyOf(entities);
        actors = List.copyOf(actors);
        relations = List.copyOf(relations);
        groups = List.copyOf(groups);
        links = List.copyOf(links);
        invariants = List.copyOf(invariants);
    }
}
