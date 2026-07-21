package org.vnu.sme.goal.acl.view;

import org.tzi.use.gui.views.diagrams.classdiagram.ClassNode;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MInvalidModelException;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.ModelFactory;
import org.tzi.use.uml.ocl.type.TypeFactory;
import org.vnu.sme.goal.acl.mm.AclEntity;

/** Entity rendered by USE's unchanged UML class-diagram component. */
public final class AclEntityNode extends ClassNode {
    private final String aclId;

    public AclEntityNode(AclEntity entity, AclDiagramOptions options) {
        super(toUseClass(entity), options);
        this.aclId = "entity::" + entity.name();
    }

    @Override public String getId() { return aclId; }
    @Override public String name() { return aclId; }

    private static MClass toUseClass(AclEntity entity) {
        ModelFactory factory = new ModelFactory();
        MModel model = factory.createModel("__acl_view__");
        MClass result = factory.createClass(entity.name(), false);
        try {
            // USE's MClass implementation consults its owning model while an
            // attribute is added (e.g. to inspect inherited attributes).
            model.addClass(result);
            for (var attribute : entity.attributes()) {
                result.addAttribute(factory.createAttribute(attribute.name(),
                        TypeFactory.mkSimpleType(attribute.type().sourceName())));
            }
        } catch (MInvalidModelException exception) {
            throw new IllegalArgumentException("Invalid Entity class '" + entity.name() + "'", exception);
        }
        return result;
    }
}
