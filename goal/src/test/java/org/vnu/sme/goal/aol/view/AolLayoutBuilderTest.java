package org.vnu.sme.goal.dsl.aol.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import org.vnu.sme.goal.dsl.aol.parser.AolCompiler;

class AolLayoutBuilderTest {
    private static final Path BASE = Path.of("src/main/resources/examples/mtg");

    @Test
    void agentAnchoredLinkFromAnAbstractRoleRelationProducesAnEdge() throws Exception {
        var result = AolCompiler.compile(BASE.resolve("mtg.aol"));
        assertTrue(result.ok(), () -> String.join("\n", result.errors()));

        // mtg.acl's knowsPhoneOf is declared on the abstract role MeetingParty, so it
        // realizes as an Agent-to-Agent link ("bob -> alice, carol" in mtg.aol) -- exactly
        // the shape that used to be silently dropped because agent ids never made it into
        // AolLayoutBuilder's userIdIndex.
        assertEquals(1, result.model().links().size());

        AolLayout layout = AolLayoutBuilder.build(result.model());
        var linkEdges = layout.edges.stream().filter(e -> e.kind() == AolEdgeKind.LINK).toList();
        assertEquals(2, linkEdges.size(), "one LINK edge per target (bob -> alice, bob -> carol)");
        assertTrue(linkEdges.stream().allMatch(e -> e.label().equals("knowsPhoneOf")));

        // Both endpoints must resolve to real agent nodes, not be silently skipped.
        var agentNodeIds = layout.nodes.values().stream()
                .filter(n -> n.kind == AolNodeKind.AGENT).map(n -> n.id).toList();
        for (var edge : linkEdges) {
            assertTrue(agentNodeIds.contains(edge.fromId()), "source must resolve to an agent node");
            assertTrue(agentNodeIds.contains(edge.toId()), "target must resolve to an agent node");
        }
    }
}
