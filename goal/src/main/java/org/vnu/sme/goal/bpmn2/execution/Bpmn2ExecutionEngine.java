package org.vnu.sme.goal.bpmn2.execution;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vnu.sme.goal.bpmn2.mm.Activity;
import org.vnu.sme.goal.bpmn2.mm.EndEvent;
import org.vnu.sme.goal.bpmn2.mm.FlowElement;
import org.vnu.sme.goal.bpmn2.mm.Gateway;
import org.vnu.sme.goal.bpmn2.mm.GatewayKind;
import org.vnu.sme.goal.bpmn2.mm.Process;
import org.vnu.sme.goal.bpmn2.mm.SequenceFlow;
import org.vnu.sme.goal.bpmn2.mm.StartEvent;

/**
 * Token executor for one BPMN process. It evaluates boolean state predicates
 * but deliberately never mutates domain state. An external adapter performs
 * the real work between {@link #begin} and {@link #complete}.
 */
public final class Bpmn2ExecutionEngine {

    @FunctionalInterface
    public interface PredicateEvaluator {
        boolean evaluate(String booleanOcl) throws Exception;
    }

    public record RunningStep(String elementId) {}
    public record Snapshot(Set<String> enabled, Set<String> running,
                           Set<String> completed, boolean ended) {}

    private final Map<String, FlowElement> elements = new LinkedHashMap<>();
    private final Map<String, List<SequenceFlow>> outgoing = new LinkedHashMap<>();
    private final Set<String> enabled = new LinkedHashSet<>();
    private final Set<String> running = new LinkedHashSet<>();
    private final Set<String> completed = new LinkedHashSet<>();
    private boolean ended;

    public Bpmn2ExecutionEngine(Process process) {
        process.flowElements().forEach(value -> elements.put(value.id(), value));
        process.sequenceFlows().forEach(value -> outgoing
                .computeIfAbsent(value.source().id(), ignored -> new ArrayList<>()).add(value));
    }

    /** Places tokens at every start event whose precondition is true. */
    public Snapshot start(PredicateEvaluator state) throws Exception {
        requireNotStarted();
        for (FlowElement element : elements.values()) {
            if (element instanceof StartEvent && conditionsTrue(element.preconditions(), state)) {
                enabled.add(element.id());
            }
        }
        advanceAutomatic(state);
        return snapshot();
    }

    /** Checks preconditions and hands an enabled activity to an external adapter. */
    public RunningStep begin(String activityId, PredicateEvaluator beforeState) throws Exception {
        FlowElement element = elements.get(activityId);
        if (!(element instanceof Activity)) throw new IllegalArgumentException(activityId + " is not an activity");
        if (!enabled.contains(activityId)) throw new IllegalStateException(activityId + " is not enabled");
        if (!conditionsTrue(element.preconditions(), beforeState)) {
            throw new IllegalStateException("precondition is false for " + activityId);
        }
        enabled.remove(activityId);
        running.add(activityId);
        return new RunningStep(activityId);
    }

    /** Validates postconditions in the adapter's resulting state, then moves the token. */
    public Snapshot complete(RunningStep step, PredicateEvaluator afterState) throws Exception {
        FlowElement element = elements.get(step.elementId());
        if (element == null || !running.contains(step.elementId())) {
            throw new IllegalStateException(step.elementId() + " is not running");
        }
        if (!conditionsTrue(element.postconditions(), afterState)) {
            throw new IllegalStateException("postcondition is false for " + step.elementId());
        }
        running.remove(step.elementId());
        completed.add(step.elementId());
        route(element, afterState);
        advanceAutomatic(afterState);
        return snapshot();
    }

    public Snapshot snapshot() {
        return new Snapshot(Set.copyOf(enabled), Set.copyOf(running), Set.copyOf(completed), ended);
    }

    private void advanceAutomatic(PredicateEvaluator state) throws Exception {
        boolean changed;
        do {
            changed = false;
            for (String id : List.copyOf(enabled)) {
                FlowElement element = elements.get(id);
                if (element instanceof Activity) continue;
                if (!conditionsTrue(element.preconditions(), state)
                        || !conditionsTrue(element.postconditions(), state)) continue;
                enabled.remove(id);
                completed.add(id);
                if (element instanceof EndEvent) ended = true;
                else route(element, state);
                changed = true;
            }
        } while (changed);
    }

    private void route(FlowElement source, PredicateEvaluator state) throws Exception {
        List<SequenceFlow> candidates = outgoing.getOrDefault(source.id(), List.of());
        List<SequenceFlow> accepted = new ArrayList<>();
        for (SequenceFlow flow : candidates) {
            if (flow.guardSource() == null || flow.guardSource().isBlank()
                    || state.evaluate(flow.guardSource())) {
                accepted.add(flow);
                // Friendly, deterministic XOR semantics: declaration order is
                // priority order, so the first satisfied branch wins.
                if (source instanceof Gateway gateway && gateway.kind() == GatewayKind.XOR) break;
            }
        }
        if (source instanceof Gateway gateway && gateway.kind() == GatewayKind.XOR && accepted.isEmpty()) {
            throw new IllegalStateException("XOR gateway " + source.id() + " has no true outgoing guard");
        }
        accepted.forEach(flow -> enabled.add(flow.target().id()));
    }

    private static boolean conditionsTrue(List<org.vnu.sme.goal.bpmn2.mm.ActivityConstraint> conditions,
            PredicateEvaluator state) throws Exception {
        for (var condition : conditions) if (!state.evaluate(condition.oclBody())) return false;
        return true;
    }

    private void requireNotStarted() {
        if (!enabled.isEmpty() || !running.isEmpty() || !completed.isEmpty()) {
            throw new IllegalStateException("execution has already started");
        }
    }
}
