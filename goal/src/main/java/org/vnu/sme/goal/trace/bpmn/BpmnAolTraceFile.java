package org.vnu.sme.goal.trace.bpmn;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.vnu.sme.goal.dsl.aol.mm.AolGroupInstance;
import org.vnu.sme.goal.dsl.aol.mm.AolLink;
import org.vnu.sme.goal.dsl.aol.mm.AolModel;
import org.vnu.sme.goal.dsl.aol.mm.AolPlay;
import org.vnu.sme.goal.dsl.aol.view.AolStateModel;
import org.vnu.sme.goal.verify.conformance.AolBpmnTraceRunner;

/**
 * Reads and writes a {@code .bpmntrace} file: a fully self-contained, already-computed
 * execution trace (one {@link AolModel} snapshot per step, per process instance). The
 * generator ({@link AolBpmnTraceRunner}) needs a live USE system to compute a trace; this
 * format exists so that inspecting an already-computed trace never needs one again — no ACL,
 * AOL, or BPMN file has to be re-read or re-run to view it. That is the whole point of
 * splitting "generate a trace" from "look at a trace" into two independent tools.
 *
 * <p>Line-oriented, deliberately not the {@code .aol} grammar: a trace has many snapshots
 * where {@code .aol} has exactly one, and nothing here is meant to be hand-authored, so there
 * is no reason to pay for a real grammar. Format:
 * <pre>
 * bpmn-trace v1
 * acl &lt;label&gt;
 *
 * PROCESS &lt;id&gt; &lt;groupClass|-&gt; &lt;selfObjectName|-&gt; &lt;ended&gt;
 * STEP &lt;index&gt; &lt;activityId|INITIAL&gt;
 * DELTA &lt;free text, one state-delta line&gt;
 * AGENT &lt;name&gt;
 * ATTR &lt;key&gt;=&lt;value&gt;
 * GROUP &lt;groupClass&gt; &lt;instanceId&gt;
 * ATTR &lt;key&gt;=&lt;value&gt;
 * PLAY &lt;roleType&gt; &lt;instanceId&gt; &lt;agentId&gt;
 * ATTR &lt;key&gt;=&lt;value&gt;
 * LINK &lt;relationName&gt; &lt;sourceInstanceId&gt; &lt;targetInstanceId&gt;[,&lt;targetInstanceId&gt;...]
 * ENDPROCESS
 * </pre>
 * {@code ATTR} always belongs to whichever of AGENT/GROUP/PLAY came most recently.
 */
public final class BpmnAolTraceFile {

    public record Step(int index, String activityId, List<String> delta, AolModel model) {}

    public record InstanceTrace(String processId, String groupClass, String selfObjectName,
            boolean ended, List<Step> steps) {}

    public record TraceFile(String aclLabel, List<InstanceTrace> traces) {}

    private BpmnAolTraceFile() {}

    public static void write(Path file, String aclLabel, AolBpmnTraceRunner.Result runResult) throws IOException {
        StringBuilder out = new StringBuilder();
        out.append("bpmn-trace v1\n");
        out.append("acl ").append(aclLabel).append("\n\n");
        for (AolBpmnTraceRunner.InstanceTrace trace : runResult.traces()) {
            out.append("PROCESS ").append(trace.processId()).append(' ')
                    .append(nz(trace.groupClass())).append(' ')
                    .append(nz(trace.selfObjectName())).append(' ')
                    .append(trace.ended()).append('\n');
            for (AolBpmnTraceRunner.Frame frame : trace.frames()) {
                out.append("STEP ").append(frame.index()).append(' ')
                        .append(frame.activityId() == null ? "INITIAL" : frame.activityId()).append('\n');
                for (String delta : frame.stateDelta()) out.append("DELTA ").append(delta).append('\n');

                var self = trace.selfObjectName() == null ? null
                        : frame.state().objectByName(trace.selfObjectName());
                AolModel model = AolStateModel.build(runResult.aclModel(), frame.state(), trace.groupClass(), self);
                for (String agent : model.agents()) {
                    out.append("AGENT ").append(agent).append('\n');
                    appendAttrs(out, model.agentAttributeValues().getOrDefault(agent, Map.of()));
                }
                if (!model.groupInstances().isEmpty()) {
                    AolGroupInstance group = model.groupInstances().get(0);
                    out.append("GROUP ").append(group.typeName()).append(' ').append(group.instanceId()).append('\n');
                    appendAttrs(out, group.attributeValues());
                    for (AolPlay play : group.plays()) {
                        out.append("PLAY ").append(play.roleType()).append(' ').append(play.instanceId())
                                .append(' ').append(play.agentId()).append('\n');
                        appendAttrs(out, play.attributeValues());
                    }
                    for (AolLink link : model.links()) {
                        out.append("LINK ").append(link.relationName()).append(' ')
                                .append(link.sourceInstanceId()).append(' ')
                                .append(String.join(",", link.targetInstanceIds())).append('\n');
                    }
                }
            }
            out.append("ENDPROCESS\n\n");
        }
        Files.writeString(file, out.toString());
    }

    private static void appendAttrs(StringBuilder out, Map<String, String> attrs) {
        attrs.forEach((key, value) -> out.append("ATTR ").append(key).append('=').append(value).append('\n'));
    }

    private static String nz(String value) { return value == null ? "-" : value; }

    public static TraceFile read(Path file) throws IOException {
        List<String> lines = Files.readAllLines(file);
        String aclLabel = "";
        int i = 0;
        while (i < lines.size()) {
            String line = lines.get(i).strip();
            if (line.startsWith("acl ")) { aclLabel = line.substring(4).strip(); i++; continue; }
            if (line.startsWith("PROCESS ")) break;
            i++;
        }

        List<InstanceTrace> traces = new ArrayList<>();
        while (i < lines.size()) {
            String line = lines.get(i).strip();
            if (line.isEmpty()) { i++; continue; }
            if (!line.startsWith("PROCESS ")) { i++; continue; }
            String[] header = line.split("\\s+");
            String processId = header[1];
            String groupClass = "-".equals(header[2]) ? null : header[2];
            String selfObjectName = "-".equals(header[3]) ? null : header[3];
            boolean ended = Boolean.parseBoolean(header[4]);
            i++;

            List<Step> steps = new ArrayList<>();
            while (i < lines.size() && !lines.get(i).strip().equals("ENDPROCESS")) {
                if (!lines.get(i).strip().startsWith("STEP ")) { i++; continue; }
                steps.add(readStep(lines, i, processId, aclLabel));
                i = skipPastStep(lines, i);
            }
            if (i < lines.size()) i++; // consume ENDPROCESS

            traces.add(new InstanceTrace(processId, groupClass, selfObjectName, ended, steps));
        }
        return new TraceFile(aclLabel, traces);
    }

    private static int skipPastStep(List<String> lines, int i) {
        i++;
        while (i < lines.size()) {
            String t = lines.get(i).strip();
            if (t.startsWith("STEP ") || t.equals("ENDPROCESS")) break;
            i++;
        }
        return i;
    }

    private static Step readStep(List<String> lines, int start, String processId, String aclLabel) {
        String[] header = lines.get(start).strip().split("\\s+", 3);
        int index = Integer.parseInt(header[1]);
        String activityId = "INITIAL".equals(header[2]) ? null : header[2];

        List<String> delta = new ArrayList<>();
        List<String> agents = new ArrayList<>();
        Map<String, Map<String, String>> agentAttributeValues = new LinkedHashMap<>();
        String groupType = null, groupId = null;
        Map<String, String> groupAttrs = new LinkedHashMap<>();
        List<AolPlay> plays = new ArrayList<>();
        List<AolLink> links = new ArrayList<>();

        String mode = null;
        String currentAgent = null;
        String playRole = null, playId = null, playAgent = null;
        Map<String, String> playAttrs = null;

        int i = start + 1;
        while (i < lines.size()) {
            String raw = lines.get(i);
            String t = raw.strip();
            if (t.startsWith("STEP ") || t.equals("ENDPROCESS")) break;
            if (t.startsWith("DELTA ")) {
                delta.add(t.substring("DELTA ".length()));
            } else if (t.startsWith("AGENT ")) {
                if (playRole != null) { plays.add(new AolPlay(playRole, playId, playAgent, playAttrs)); playRole = null; }
                currentAgent = t.substring("AGENT ".length()).strip();
                agents.add(currentAgent);
                agentAttributeValues.put(currentAgent, new LinkedHashMap<>());
                mode = "AGENT";
            } else if (t.startsWith("GROUP ")) {
                if (playRole != null) { plays.add(new AolPlay(playRole, playId, playAgent, playAttrs)); playRole = null; }
                String[] g = t.split("\\s+");
                groupType = g[1];
                groupId = g[2];
                mode = "GROUP";
            } else if (t.startsWith("PLAY ")) {
                if (playRole != null) plays.add(new AolPlay(playRole, playId, playAgent, playAttrs));
                String[] p = t.split("\\s+");
                playRole = p[1];
                playId = p[2];
                playAgent = p[3];
                playAttrs = new LinkedHashMap<>();
                mode = "PLAY";
            } else if (t.startsWith("LINK ")) {
                if (playRole != null) { plays.add(new AolPlay(playRole, playId, playAgent, playAttrs)); playRole = null; }
                String[] l = t.split("\\s+");
                links.add(new AolLink(l[1], l[2], List.of(l[3].split(","))));
                mode = null;
            } else if (t.startsWith("ATTR ")) {
                String rest = t.substring("ATTR ".length());
                int eq = rest.indexOf('=');
                String key = rest.substring(0, eq);
                String value = rest.substring(eq + 1);
                switch (mode) {
                    case "AGENT" -> agentAttributeValues.get(currentAgent).put(key, value);
                    case "GROUP" -> groupAttrs.put(key, value);
                    case "PLAY" -> playAttrs.put(key, value);
                    default -> { /* malformed line before any block header: ignore */ }
                }
            }
            i++;
        }
        if (playRole != null) plays.add(new AolPlay(playRole, playId, playAgent, playAttrs));

        List<AolGroupInstance> groupInstances = groupType == null ? List.of()
                : List.of(new AolGroupInstance(groupType, groupId, List.of(), plays, List.of(), groupAttrs));
        AolModel model = new AolModel("v1.0", processId + "-step" + index, aclLabel,
                agents, Map.of(), agentAttributeValues, groupInstances, List.of(), links);
        return new Step(index, activityId, delta, model);
    }
}
