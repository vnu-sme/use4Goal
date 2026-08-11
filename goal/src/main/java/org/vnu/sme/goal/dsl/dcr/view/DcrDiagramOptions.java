package org.vnu.sme.goal.dsl.dcr.view;

import java.awt.Color;
import java.nio.file.Path;

import org.tzi.use.gui.views.diagrams.DiagramOptions;

public final class DcrDiagramOptions extends DiagramOptions {
    public static final String CONDITION_COLOR = "DCR_CONDITION_COLOR";
    public static final String RESPONSE_COLOR = "DCR_RESPONSE_COLOR";
    public static final String INCLUDE_COLOR = "DCR_INCLUDE_COLOR";
    public static final String EXCLUDE_COLOR = "DCR_EXCLUDE_COLOR";
    public static final String MILESTONE_COLOR = "DCR_MILESTONE_COLOR";

    public DcrDiagramOptions() {}

    public DcrDiagramOptions(Path modelFile) {
        this.modelFileName = modelFile;
    }

    public DcrDiagramOptions(DcrDiagramOptions source) {
        super(source);
    }

    @Override
    protected void registerAdditionalColors() {
        registerTypeColor(NODE_COLOR, new Color(0xff, 0xf8, 0xb4), new Color(0xf0, 0xf0, 0xf0));
        registerTypeColor(NODE_SELECTED_COLOR, Color.ORANGE, new Color(0xd0, 0xd0, 0xd0));
        registerTypeColor(NODE_FRAME_COLOR, Color.BLACK, Color.BLACK);
        registerTypeColor(NODE_LABEL_COLOR, Color.BLACK, Color.BLACK);
        registerTypeColor(EDGE_COLOR, Color.BLACK, Color.BLACK);
        registerTypeColor(EDGE_LABEL_COLOR, Color.DARK_GRAY, Color.BLACK);
        registerTypeColor(EDGE_SELECTED_COLOR, Color.ORANGE, new Color(0x50, 0x50, 0x50));
        registerTypeColor(CONDITION_COLOR, new Color(0, 145, 25), Color.BLACK);
        registerTypeColor(RESPONSE_COLOR, new Color(255, 150, 0), Color.DARK_GRAY);
        registerTypeColor(INCLUDE_COLOR, new Color(30, 120, 220), Color.DARK_GRAY);
        registerTypeColor(EXCLUDE_COLOR, new Color(220, 0, 0), Color.BLACK);
        registerTypeColor(MILESTONE_COLOR, new Color(135, 50, 190), Color.DARK_GRAY);
    }

    @Override
    public boolean isShowMutliplicities() {
        return false;
    }

    @Override
    public void setShowMutliplicities(boolean showMutliplicities) {}
}
