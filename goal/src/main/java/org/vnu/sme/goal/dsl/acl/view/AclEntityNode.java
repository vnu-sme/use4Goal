package org.vnu.sme.goal.dsl.acl.view;

import org.tzi.use.gui.views.diagrams.classdiagram.ClassNode;
import org.tzi.use.uml.mm.MClass;

/** Entity rendered by USE's unchanged UML class-diagram component. */
public final class AclEntityNode extends ClassNode {
    private final String aclId;

    public AclEntityNode(MClass entity, AclDiagramOptions options) {
        super(entity, options);
        this.aclId = "entity::" + entity.name();
    }

    @Override public String getId() { return aclId; }
    @Override public String name() { return aclId; }
}
