package org.vnu.sme.goal.dsl.acl.view;

import java.awt.Color;
import java.nio.file.Path;

import org.tzi.use.gui.views.diagrams.classdiagram.ClassDiagramOptions;

/** ACL-specific colors on top of the complete USE ClassDiagram option set. */
public final class AclDiagramOptions extends ClassDiagramOptions {
    public static final String ENTITY_FILL = "ACL_ENTITY_FILL";
    public static final String ROLE_FILL = "ACL_ROLE_FILL";
    public static final String GROUP_FILL = "ACL_GROUP_FILL";
    public static final String ENUM_FILL = "ACL_ENUM_FILL";
    public static final String MOISE_EDGE_COLOR = "ACL_MOISE_EDGE_COLOR";

    public AclDiagramOptions() {}

    public AclDiagramOptions(Path modelFile) {
        this.modelFileName = modelFile;
    }

    @Override
    protected void registerAdditionalColors() {
        registerTypeColor(NODE_COLOR, Color.WHITE, Color.WHITE);
        registerTypeColor(NODE_SELECTED_COLOR, new Color(255, 235, 170), new Color(0xd0, 0xd0, 0xd0));
        registerTypeColor(NODE_FRAME_COLOR, Color.BLACK, Color.BLACK);
        registerTypeColor(NODE_LABEL_COLOR, Color.BLACK, Color.BLACK);
        registerTypeColor(EDGE_COLOR, Color.BLACK, Color.BLACK);
        registerTypeColor(EDGE_LABEL_COLOR, Color.BLACK, Color.BLACK);
        registerTypeColor(EDGE_SELECTED_COLOR, Color.ORANGE, new Color(0x50, 0x50, 0x50));
        registerTypeColor(ENTITY_FILL, new Color(238, 248, 255), new Color(240, 240, 240));
        registerTypeColor(ROLE_FILL, Color.WHITE, new Color(240, 240, 240));
        registerTypeColor(GROUP_FILL, Color.WHITE, new Color(240, 240, 240));
        registerTypeColor(ENUM_FILL, new Color(255, 249, 218), new Color(240, 240, 240));
        registerTypeColor(MOISE_EDGE_COLOR, Color.BLACK, Color.BLACK);
    }

}
