package org.vnu.sme.goal.acl.mm;

import java.util.List;

public record AclModel(String version,
                       String name,
                       List<AclEnum> enums,
                       List<AclEntity> entities,
                       List<AclActor> actors,
                       List<AclRelation> relations,
                       List<AclGroup> groups,
                       List<AclLink> links,
                       List<AclInvariant> invariants) {
    public AclModel {
        enums = List.copyOf(enums);
        entities = List.copyOf(entities);
        actors = List.copyOf(actors);
        relations = List.copyOf(relations);
        groups = List.copyOf(groups);
        links = List.copyOf(links);
        invariants = List.copyOf(invariants);
    }
}
