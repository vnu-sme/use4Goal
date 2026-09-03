package org.vnu.sme.goal.trace.bpmn;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vnu.sme.goal.dsl.bpmn.mm.Activity;
import org.vnu.sme.goal.dsl.bpmn.mm.EndEvent;
import org.vnu.sme.goal.dsl.bpmn.mm.FlowElement;
import org.vnu.sme.goal.dsl.bpmn.mm.Gateway;
import org.vnu.sme.goal.dsl.bpmn.mm.GatewayKind;
import org.vnu.sme.goal.dsl.bpmn.mm.Process;
import org.vnu.sme.goal.dsl.bpmn.mm.SequenceFlow;
import org.vnu.sme.goal.dsl.bpmn.mm.StartEvent;

/**
 * Token executor for one BPMN process. It evaluates boolean state predicates
 * but deliberately never mutates domain state. An external adapter performs
 * the real work between {@link #begin} and {@link #complete}.
 */
public final class BpmnExecutionEngine {

    @FunctionalInterface
    public interface PredicateEvaluator {
        boolean evaluate(String booleanOcl) throws Exception;
    }
    @FunctionalInterface
    public interface ChoiceSelector {
        int choose(String gatewayId, List<SequenceFlow> acceptedFlows);
    }

    public record RunningStep(String elementId) {}
    public record Snapshot(Set<String> enabled, Set<String> running,
                           Set<String> completed, boolean ended) {}

    private final Map<String, FlowElement> elements = new LinkedHashMap<>();
    private final Map<String, List<SequenceFlow>> outgoing = new LinkedHashMap<>();
    private final Map<String, List<SequenceFlow>> incoming = new LinkedHashMap<>();
    private final Map<String, Set<String>> arrivals = new LinkedHashMap<>();
    private final Map<String, String> orJoinForSplit = new LinkedHashMap<>();
    private final Map<String, Integer> orExpectedArrivals = new LinkedHashMap<>();
    private final Set<String> enabled = new LinkedHashSet<>();
    private final Set<String> running = new LinkedHashSet<>();
    private final Set<String> completed = new LinkedHashSet<>();
    private boolean ended;
    private final ChoiceSelector choiceSelector;

    public BpmnExecutionEngine(Process process) {
        this(process, (gateway, flows) -> 0);
    }

    public BpmnExecutionEngine(Process process, ChoiceSelector choiceSelector) {
        this.choiceSelector = choiceSelector;
        process.flowElements().forEach(value -> elements.put(value.id(), value));
        process.sequenceFlows().forEach(value -> {
            outgoing.computeIfAbsent(value.source().id(), ignored -> new ArrayList<>()).add(value);
            incoming.computeIfAbsent(value.target().id(), ignored -> new ArrayList<>()).add(value);
        });
        indexStructuredOrPairs();
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
        SequenceFlow defaultFlow = null;
        for (SequenceFlow flow : candidates) {
            if (flow.isDefault()) {
                defaultFlow = flow;
                continue;
            }
            if (flow.guardSource() == null || flow.guardSource().isBlank()
                    || state.evaluate(flow.guardSource())) {
                accepted.add(flow);
            }
        }
        // The default flow is the fallback: taken only if nothing else matched.
        if (accepted.isEmpty() && defaultFlow != null) accepted.add(defaultFlow);
        if (source instanceof Gateway gateway && gateway.kind() == GatewayKind.XOR && accepted.isEmpty()) {
            throw new IllegalStateException("XOR gateway " + source.id() + " has no true outgoing guard");
        }
        if (source instanceof Gateway gateway
                && (gateway.kind() == GatewayKind.XOR || gateway.kind() == GatewayKind.EVENT_BASED)
                && accepted.size() > 1) {
            int selected = choiceSelector.choose(source.id(), List.copyOf(accepted));
            if (selected < 0 || selected >= accepted.size()) {
                throw new IllegalStateException("invalid branch " + selected + " for " + source.id());
            }
            arrive(accepted.get(selected));
        } else {
            if (source instanceof Gateway gateway && gateway.kind() == GatewayKind.OR && accepted.size() > 1) {
                String join = orJoinForSplit.get(source.id());
                if (join != null) orExpectedArrivals.merge(join, accepted.size(), Integer::sum);
            }
            accepted.forEach(this::arrive);
        }
    }

    private void arrive(SequenceFlow flow) {
        String targetId = flow.target().id();
        Set<String> sources = arrivals.computeIfAbsent(targetId, ignored -> new LinkedHashSet<>());
        sources.add(flow.source().id());
        List<SequenceFlow> required = incoming.getOrDefault(targetId, List.of());
        boolean andJoin = flow.target() instanceof Gateway gateway
                && gateway.kind() == GatewayKind.AND && required.size() > 1;
        int orExpected = flow.target() instanceof Gateway gateway && gateway.kind() == GatewayKind.OR
                ? orExpectedArrivals.getOrDefault(targetId, 1) : 1;
        boolean ready = andJoin ? required.stream().allMatch(in -> sources.contains(in.source().id()))
                : sources.size() >= orExpected;
        if (ready) {
            enabled.add(targetId);
            sources.clear();
            orExpectedArrivals.remove(targetId);
        }
    }

    /** Pairs a structured inclusive split with the first inclusive join reachable from every branch. */
    private void indexStructuredOrPairs() {
        for (FlowElement element : elements.values()) {
            if (!(element instanceof Gateway split) || split.kind() != GatewayKind.OR) continue;
            List<SequenceFlow> branches = outgoing.getOrDefault(split.id(), List.of());
            if (branches.size() < 2) continue;
            for (FlowElement candidate : elements.values()) {
                if (!(candidate instanceof Gateway join) || join.kind() != GatewayKind.OR
                        || incoming.getOrDefault(join.id(), List.of()).size() < 2) continue;
                if (branches.stream().allMatch(branch -> reachable(branch.target().id(), join.id(), new LinkedHashSet<>()))) {
                    orJoinForSplit.put(split.id(), join.id());
                    break;
                }
            }
        }
    }

    private boolean reachable(String current, String target, Set<String> seen) {
        if (current.equals(target)) return true;
        if (!seen.add(current)) return false;
        return outgoing.getOrDefault(current, List.of()).stream()
                .anyMatch(flow -> reachable(flow.target().id(), target, seen));
    }

    private static boolean conditionsTrue(List<org.vnu.sme.goal.dsl.bpmn.mm.ActivityConstraint> conditions,
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
