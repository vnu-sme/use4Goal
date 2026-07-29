package org.vnu.sme.goal.bpmn2.execution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.vnu.sme.goal.bpmn2.mm.EndEvent;
import org.vnu.sme.goal.bpmn2.mm.EventTrigger;
import org.vnu.sme.goal.bpmn2.mm.Gateway;
import org.vnu.sme.goal.bpmn2.mm.GatewayKind;
import org.vnu.sme.goal.bpmn2.mm.SequenceFlow;
import org.vnu.sme.goal.bpmn2.mm.StartEvent;
import org.vnu.sme.goal.bpmn2.mm.Task;
import org.vnu.sme.goal.bpmn2.mm.SubProcess;

class Bpmn2ExecutionEngineTest {
    private static final Bpmn2ExecutionEngine.PredicateEvaluator TRUE = ignored -> true;

    @Test
    void andJoinWaitsForEveryIncomingToken() throws Exception {
        var start = new StartEvent("start", EventTrigger.NONE);
        var split = new Gateway("split", GatewayKind.AND);
        var a = new Task("a", "A");
        var b = new Task("b", "B");
        var join = new Gateway("join", GatewayKind.AND);
        var end = new EndEvent("end", EventTrigger.NONE);
        var process = new org.vnu.sme.goal.bpmn2.mm.Process("p", "P", List.of(),
                List.of(start, split, a, b, join, end), List.of(
                flow(start, split), flow(split, a), flow(split, b), flow(a, join),
                flow(b, join), flow(join, end)));
        var engine = new Bpmn2ExecutionEngine(process);
        assertEquals(java.util.Set.of("a", "b"), engine.start(TRUE).enabled());
        engine.complete(engine.begin("a", TRUE), TRUE);
        assertFalse(engine.snapshot().ended());
        assertTrue(engine.snapshot().enabled().contains("b"));
        engine.complete(engine.begin("b", TRUE), TRUE);
        assertTrue(engine.snapshot().ended());
    }

    @Test
    void xorChoiceSelectorCanExploreAnotherTrueBranch() throws Exception {
        var start = new StartEvent("start", EventTrigger.NONE);
        var xor = new Gateway("choice", GatewayKind.XOR);
        var a = new Task("a", "A");
        var b = new Task("b", "B");
        var process = new org.vnu.sme.goal.bpmn2.mm.Process("p", "P", List.of(),
                List.of(start, xor, a, b), List.of(flow(start, xor), flow(xor, a), flow(xor, b)));
        var engine = new Bpmn2ExecutionEngine(process, (gateway, candidates) -> 1);
        assertEquals(java.util.Set.of("b"), engine.start(TRUE).enabled());
    }

    @Test
    void loopMayEnableAndExecuteTheSameActivityAgain() throws Exception {
        var start = new StartEvent("start", EventTrigger.NONE);
        var a = new Task("a", "A");
        var loop = new Gateway("loop", GatewayKind.XOR);
        var end = new EndEvent("end", EventTrigger.NONE);
        var process = new org.vnu.sme.goal.bpmn2.mm.Process("p", "P", List.of(),
                List.of(start, a, loop, end), List.of(flow(start, a), flow(a, loop),
                flow(loop, a), flow(loop, end)));
        int[] choice = {0};
        var engine = new Bpmn2ExecutionEngine(process, (gateway, candidates) -> choice[0]++ == 0 ? 0 : 1);
        engine.start(TRUE);
        engine.complete(engine.begin("a", TRUE), TRUE);
        assertTrue(engine.snapshot().enabled().contains("a"));
        engine.complete(engine.begin("a", TRUE), TRUE);
        assertTrue(engine.snapshot().ended());
    }

    @Test
    void inclusiveJoinWaitsForAllBranchesSelectedByStructuredOrSplit() throws Exception {
        var start = new StartEvent("start", EventTrigger.NONE);
        var split = new Gateway("split", GatewayKind.OR);
        var a = new Task("a", "A"); var b = new Task("b", "B");
        var join = new Gateway("join", GatewayKind.OR);
        var end = new EndEvent("end", EventTrigger.NONE);
        var process = new org.vnu.sme.goal.bpmn2.mm.Process("p", "P", List.of(),
                List.of(start, split, a, b, join, end), List.of(flow(start, split), flow(split, a),
                flow(split, b), flow(a, join), flow(b, join), flow(join, end)));
        var engine = new Bpmn2ExecutionEngine(process);
        engine.start(TRUE);
        engine.complete(engine.begin("a", TRUE), TRUE);
        assertFalse(engine.snapshot().ended());
        engine.complete(engine.begin("b", TRUE), TRUE);
        assertTrue(engine.snapshot().ended());
    }

    @Test
    void subprocessMayBeExecutedAsContractedOpaqueActivity() throws Exception {
        var start = new StartEvent("start", EventTrigger.NONE);
        var subprocess = new SubProcess("sub", "Sub", List.of(), List.of());
        var end = new EndEvent("end", EventTrigger.NONE);
        var process = new org.vnu.sme.goal.bpmn2.mm.Process("p", "P", List.of(),
                List.of(start, subprocess, end), List.of(flow(start, subprocess), flow(subprocess, end)));
        var engine = new Bpmn2ExecutionEngine(process);
        engine.start(TRUE);
        engine.complete(engine.begin("sub", TRUE), TRUE);
        assertTrue(engine.snapshot().ended());
    }

    private static SequenceFlow flow(org.vnu.sme.goal.bpmn2.mm.FlowElement source,
                                     org.vnu.sme.goal.bpmn2.mm.FlowElement target) {
        return new SequenceFlow(source, target, null);
    }
}
