package org.vnu.sme.goal.acl.view;

import java.awt.Color;
import java.nio.file.Path;

import org.tzi.use.gui.views.diagrams.DiagramOptions;

public final class AclDiagramOptions extends DiagramOptions {
    public static final String ENTITY_FILL = "ACL_ENTITY_FILL";
    public static final String ROLE_FILL = "ACL_ROLE_FILL";
    public static final String AGENT_FILL = "ACL_AGENT_FILL";
    public static final String GROUP_FILL = "ACL_GROUP_FILL";
    public static final String PART_OF_COLOR = "ACL_PART_OF_COLOR";
    public static final String LINK_COLOR = "ACL_LINK_COLOR";
    public static final String MEMBER_COLOR = "ACL_MEMBER_COLOR";

    public AclDiagramOptions() {}

    public AclDiagramOptions(Path modelFile) {
        this.modelFileName = modelFile;
    }

    @Override
    protected void registerAdditionalColors() {
        registerTypeColor(NODE_COLOR, Color.WHITE, Color.WHITE);
        registerTypeColor(NODE_SELECTED_COLOR, Color.ORANGE, new Color(0xd0, 0xd0, 0xd0));
        registerTypeColor(NODE_FRAME_COLOR, new Color(60, 74, 91), Color.BLACK);
        registerTypeColor(NODE_LABEL_COLOR, Color.BLACK, Color.BLACK);
        registerTypeColor(EDGE_COLOR, new Color(70, 84, 102), Color.BLACK);
        registerTypeColor(EDGE_LABEL_COLOR, Color.DARK_GRAY, Color.BLACK);
        registerTypeColor(EDGE_SELECTED_COLOR, Color.ORANGE, new Color(0x50, 0x50, 0x50));
        registerTypeColor(ENTITY_FILL, new Color(238, 248, 255), new Color(240, 240, 240));
        registerTypeColor(ROLE_FILL, new Color(244, 239, 255), new Color(240, 240, 240));
        registerTypeColor(AGENT_FILL, new Color(236, 249, 242), new Color(240, 240, 240));
        registerTypeColor(GROUP_FILL, new Color(255, 249, 232), new Color(240, 240, 240));
        registerTypeColor(PART_OF_COLOR, new Color(35, 121, 96), Color.BLACK);
        registerTypeColor(LINK_COLOR, new Color(157, 82, 40), Color.BLACK);
        registerTypeColor(MEMBER_COLOR, new Color(132, 143, 154), Color.DARK_GRAY);
    }

    @Override
    public boolean isShowMutliplicities() {
        return false;
    }

    @Override
    public void setShowMutliplicities(boolean showMutliplicities) {}
}
