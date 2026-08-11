package org.vnu.sme.goal.dsl.bpmn.view;

import java.awt.Color;
import java.nio.file.Path;

import org.tzi.use.gui.views.diagrams.DiagramOptions;

public final class BpmnDiagramOptions extends DiagramOptions {
    public static final String POOL_COLOR = "BPMN_POOL_COLOR";
    public static final String LANE_COLOR = "BPMN_LANE_COLOR";
    public static final String MESSAGE_COLOR = "BPMN_MESSAGE_COLOR";

    public BpmnDiagramOptions() {}

    public BpmnDiagramOptions(Path modelFile) {
        this.modelFileName = modelFile;
    }

    @Override
    protected void registerAdditionalColors() {
        registerTypeColor(NODE_COLOR, new Color(255, 255, 235), Color.WHITE);
        registerTypeColor(NODE_SELECTED_COLOR, Color.ORANGE, new Color(0xd0, 0xd0, 0xd0));
        registerTypeColor(NODE_FRAME_COLOR, new Color(70, 90, 170), Color.BLACK);
        registerTypeColor(NODE_LABEL_COLOR, new Color(30, 30, 60), Color.BLACK);
        registerTypeColor(EDGE_COLOR, new Color(40, 40, 80), Color.BLACK);
        registerTypeColor(EDGE_LABEL_COLOR, new Color(80, 100, 130), Color.BLACK);
        registerTypeColor(EDGE_SELECTED_COLOR, Color.ORANGE, new Color(0x50, 0x50, 0x50));
        registerTypeColor(POOL_COLOR, new Color(242, 245, 252), Color.WHITE);
        registerTypeColor(LANE_COLOR, new Color(252, 253, 255), Color.WHITE);
        registerTypeColor(MESSAGE_COLOR, new Color(65, 100, 190), Color.BLACK);
    }

    @Override
    public boolean isShowMutliplicities() {
        return false;
    }

    @Override
    public void setShowMutliplicities(boolean showMutliplicities) {}
}
