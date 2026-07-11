package org.vnu.sme.goal.dcr.view;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.vnu.sme.goal.dcr.mm.DcrModel;

public final class DcrLayoutBuilder {
    private static final int MARGIN = 60;
    private static final int X_GAP = 115;
    private static final int Y_GAP = 170;

    private DcrLayoutBuilder() {}

    public static DcrLayout build(DcrModel model) {
        Map<String, DcrNode> nodes = new LinkedHashMap<>();
        List<DcrEdge> edges = new ArrayList<>();

        int index = 0;
        int maxX = MARGIN;
        int maxY = MARGIN;
        for (var event : model.events()) {
            DcrNode node = new DcrNode(event);
            int row;
            int col;
            if (index == 0) {
                row = 0;
                col = 0;
            } else {
                row = index == 1 ? 1 : index - 2;
                col = index == 1 ? 1 : 2;
            }
            node.x = MARGIN + col * (node.w + X_GAP);
            node.y = MARGIN + row * Y_GAP;
            maxX = Math.max(maxX, node.x + node.w);
            maxY = Math.max(maxY, node.y + node.h);
            nodes.put(node.id, node);
            index++;
        }

        for (var relation : model.relations()) {
            edges.add(new DcrEdge(relation.source(), relation.target(), relation.kind(), relation.time()));
        }
        return new DcrLayout(nodes, edges, maxX + MARGIN, maxY + MARGIN);
    }
}
