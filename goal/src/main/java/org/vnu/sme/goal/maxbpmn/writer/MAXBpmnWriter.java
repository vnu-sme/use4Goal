package org.vnu.sme.goal.maxbpmn.writer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

import org.vnu.sme.goal.maxbpmn.mm.*;

/**
 * Serialises a BpmnProcess to the MAXBpmn text format.
 */
public final class MAXBpmnWriter {

    private MAXBpmnWriter() {}

    public static String write(BpmnProcess process) {
        StringBuilder sb = new StringBuilder();
        sb.append("process ").append(process.getName()).append(" {\n\n");

        for (Lane lane : process.getLanes()) {
            sb.append("  lane ").append(lane.id())
              .append(" \"").append(lane.actorName())
              .append(" (").append(lane.actorKind()).append(")\" {\n");

            for (BpmnNode node : process.getNodes()) {
                if (!node.laneId().equals(lane.id())) continue;
                switch (node) {
                    case StartEvent  s -> sb.append("    start   ").append(s.id()).append("\n");
                    case EndEvent    e -> sb.append("    end     ").append(e.id()).append("\n");
                    case BpmnTask    t -> sb.append("    task    ").append(t.id())
                                           .append(" \"").append(t.label()).append("\"\n");
                    case Gateway     g -> sb.append("    gateway ").append(g.id())
                                           .append(" : ").append(g.type()).append(" ")
                                           .append(g.role().name().toLowerCase()).append("\n");
                }
            }
            sb.append("  }\n\n");
        }

        for (SequenceFlow f : process.getFlows()) {
            sb.append("  flow ").append(f.sourceId()).append(" -> ").append(f.targetId());
            if (f.label() != null && !f.label().isBlank()) {
                sb.append(" : \"").append(f.label()).append("\"");
            }
            sb.append("\n");
        }

        if (!process.getFlows().isEmpty()) sb.append("\n");

        for (MessageFlow m : process.getMessages()) {
            sb.append("  message ").append(m.sourceId()).append(" -> ").append(m.targetId()).append("\n");
        }

        sb.append("}\n");
        return sb.toString();
    }

    public static void saveTo(BpmnProcess process, Path path) throws IOException {
        String text = write(process);
        Files.writeString(path, text, StandardCharsets.UTF_8);
    }

    public static void saveTo(BpmnProcess process, File file) throws IOException {
        saveTo(process, file.toPath());
    }
}
