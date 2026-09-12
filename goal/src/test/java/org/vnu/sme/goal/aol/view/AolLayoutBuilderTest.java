package org.vnu.sme.goal.dsl.aol.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import org.vnu.sme.goal.dsl.aol.parser.AolCompiler;

class AolLayoutBuilderTest {
    private static final Path BASE = Path.of("src/main/resources/examples/enterprise");

    @Test
    void layoutBuilderCreatesNodesAndEdgesForPopulation() throws Exception {
        var result = AolCompiler.compile(BASE.resolve("enterprise_minimal.aol"));
        assertTrue(result.ok(), () -> String.join("\n", result.errors()));

        AolLayout layout = AolLayoutBuilder.build(result.model());
        assertTrue(!layout.nodes.isEmpty(), "must generate nodes");
        assertTrue(!layout.edges.isEmpty(), "must generate edges");
    }

    @Test
    void playedByEdgesConnectToAgentOrOrgCtxNodes() throws Exception {
        var result = AolCompiler.compile(BASE.resolve("enterprise_minimal.aol"));
        assertTrue(result.ok(), () -> String.join("\n", result.errors()));

        AolLayout layout = AolLayoutBuilder.build(result.model());
        var playedByEdges = layout.edges.stream().filter(e -> e.kind() == AolEdgeKind.PLAYED_BY).toList();
        assertTrue(!playedByEdges.isEmpty(), "must generate playedBy edges");

        for (var edge : playedByEdges) {
            assertTrue(layout.nodes.containsKey(edge.fromId()), "playedBy source (role) must exist");
            assertTrue(layout.nodes.containsKey(edge.toId()), "playedBy target (agent/orgctx) must exist in nodes map");
        }
    }
}
