package org.vnu.sme.goal.verify.aclstate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vnu.sme.goal.dsl.aol.state.AclSystemState;
import org.vnu.sme.goal.dsl.bpmn.mm.EndEvent;
import org.vnu.sme.goal.dsl.bpmn.mm.FlowElement;
import org.vnu.sme.goal.dsl.bpmn.mm.Gateway;
import org.vnu.sme.goal.dsl.bpmn.mm.GatewayKind;
import org.vnu.sme.goal.dsl.bpmn.mm.SequenceFlow;
import org.vnu.sme.goal.dsl.bpmn.mm.StartEvent;

/** Executable form of the flow-based Pass/Next semantics in bpmn.tex. */
final class AclBpmnFlowRuntime {
    private static final int MAX_SUBSETS = 4096;

    record FlowRef(String id, FlowElement source, FlowElement target,
                   SequenceFlow sequenceFlow, boolean initial) {
        String sourceId() { return source == null ? "bottom" : source.id(); }
        String targetId() { return target.id(); }
    }

    record FlowStep(FlowRef flow, Set<String> pass, Set<String> next) {
        FlowStep {
            pass = Set.copyOf(pass);
            next = Set.copyOf(next);
        }

        String label() { return flow.sourceId() + " -> " + flow.targetId(); }
    }

    private final org.vnu.sme.goal.dsl.bpmn.mm.Process process;
    private final Map<String, FlowRef> flows = new LinkedHashMap<>();
    private final Map<String, List<FlowRef>> incoming = new LinkedHashMap<>();
    private final Map<String, List<FlowRef>> outgoing = new LinkedHashMap<>();
    private final Set<String> initial;

    AclBpmnFlowRuntime(org.vnu.sme.goal.dsl.bpmn.mm.Process process) {
        this.process = process;
        for (SequenceFlow flow : process.sequenceFlows()) {
            FlowRef ref = new FlowRef(arcId(flow), flow.source(), flow.target(), flow, false);
            if (flows.put(ref.id(), ref) != null) {
                throw new IllegalArgumentException("duplicate BPMN flow " + ref.id());
            }
            incoming.computeIfAbsent(flow.target().id(), ignored -> new ArrayList<>()).add(ref);
            outgoing.computeIfAbsent(flow.source().id(), ignored -> new ArrayList<>()).add(ref);
        }
        Set<String> entries = new LinkedHashSet<>();
        for (FlowElement element : process.flowElements()) {
            if (element instanceof StartEvent) {
                FlowRef ref = new FlowRef("bottom::" + element.id(), null, element, null, true);
                flows.put(ref.id(), ref);
                incoming.computeIfAbsent(element.id(), ignored -> new ArrayList<>()).add(ref);
                entries.add(ref.id());
            }
        }
        initial = Set.copyOf(entries);
    }

    org.vnu.sme.goal.dsl.bpmn.mm.Process process() { return process; }
    Set<String> initialMarking() { return initial; }
    FlowRef flow(String id) { return flows.get(id); }
    boolean fullyConsumed(Set<String> marking) { return marking.isEmpty(); }
    boolean accepting(Set<String> marking) {
        return marking.stream().allMatch(id -> flows.get(id).target() instanceof EndEvent);
    }

    List<FlowStep> enabledSteps(Set<String> marking) {
        Map<String, FlowStep> result = new LinkedHashMap<>();
        for (String active : marking.stream().sorted().toList()) {
            FlowRef trigger = flows.get(active);
            if (trigger == null) continue;
            FlowElement target = trigger.target();
            List<Set<String>> passChoices = passChoices(trigger, target, marking);
            if (passChoices.isEmpty()) continue;
            List<Set<String>> nextChoices = nextChoices(target);
            for (Set<String> pass : passChoices) {
                if (!marking.containsAll(pass)) continue;
                for (Set<String> next : nextChoices) {
                    FlowStep step = new FlowStep(trigger, pass, next);
                    result.putIfAbsent(stepKey(step), step);
                }
            }
        }
        return result.values().stream().sorted(Comparator.comparing(AclBpmnFlowRuntime::stepKey)).toList();
    }

    Set<String> execute(Set<String> marking, FlowStep step) {
        Set<String> result = new LinkedHashSet<>(marking);
        result.removeAll(step.pass());
        result.addAll(step.next());
        return Set.copyOf(result);
    }

    boolean contractHolds(FlowStep step, AclSystemState before,
                          AclSystemState after, String selfObject) {
        return preHolds(step, before, selfObject) && postHolds(step, before, after, selfObject);
    }

    boolean preHolds(FlowStep step, AclSystemState before, String selfObject) {
        return AclOclTransitionEvaluator.preconditionsHold(
                executedElement(step.flow()).preconditions(), before, selfObject);
    }

    boolean postHolds(FlowStep step, AclSystemState before,
                      AclSystemState after, String selfObject) {
        FlowRef flow = step.flow();
        if (flow.initial()) return true; // Post_B((bottom,start)) = true
        SequenceFlow sequence = flow.sequenceFlow();
        if (sequence.postSource() != null && !sequence.postSource().isBlank()) {
            return AclOclTransitionEvaluator.postExpressionHolds(
                    sequence.postSource(), before, after, selfObject);
        }
        if (sequence.guardSource() != null && !sequence.guardSource().isBlank()) {
            // Compatibility: an old `when` condition is the branch Post_B.
            return AclOclTransitionEvaluator.postExpressionHolds(
                    sequence.guardSource(), before, after, selfObject);
        }
        if (sequence.isDefault()) {
            String fallback = legacyDefaultPost(flow.source());
            if (fallback != null) {
                return AclOclTransitionEvaluator.postExpressionHolds(
                        fallback, before, after, selfObject);
            }
        }
        return AclOclTransitionEvaluator.postconditionsHold(
                flow.source().postconditions(), before, after, selfObject);
    }

    String contractDescription(FlowStep step) {
        FlowRef flow = step.flow();
        String pre = executedElement(flow).preconditions().stream()
                .map(c -> c.oclBody()).reduce((a, b) -> a + " and " + b).orElse("true");
        String post;
        if (flow.initial()) post = "true";
        else if (flow.sequenceFlow().postSource() != null) post = flow.sequenceFlow().postSource();
        else if (flow.sequenceFlow().guardSource() != null) post = flow.sequenceFlow().guardSource();
        else if (flow.sequenceFlow().isDefault() && legacyDefaultPost(flow.source()) != null) {
            post = legacyDefaultPost(flow.source());
        } else {
            post = flow.source().postconditions().stream()
                    .map(c -> c.oclBody()).reduce((a, b) -> a + " and " + b).orElse("true");
        }
        return "Pre_B=" + pre + "; Post_B=" + post;
    }

    List<String> preExpressions(FlowStep step) {
        return executedElement(step.flow()).preconditions().stream()
                .map(constraint -> constraint.oclBody()).toList();
    }

    private static FlowElement executedElement(FlowRef flow) {
        // The synthetic bottom -> Start step executes the Start Event. Every
        // ordinary sequence-flow step executes its source element; its source
        // pre- and postconditions must therefore be evaluated at the same step.
        return flow.initial() ? flow.target() : flow.source();
    }

    List<String> postExpressions(FlowStep step) {
        FlowRef flow = step.flow();
        if (flow.initial()) return List.of();
        SequenceFlow sequence = flow.sequenceFlow();
        if (sequence.postSource() != null && !sequence.postSource().isBlank()) {
            return List.of(sequence.postSource());
        }
        if (sequence.guardSource() != null && !sequence.guardSource().isBlank()) {
            return List.of(sequence.guardSource());
        }
        if (sequence.isDefault() && legacyDefaultPost(flow.source()) != null) {
            return List.of(legacyDefaultPost(flow.source()));
        }
        return flow.source().postconditions().stream().map(constraint -> constraint.oclBody()).toList();
    }

    private List<Set<String>> passChoices(FlowRef trigger, FlowElement target, Set<String> marking) {
        if (!(target instanceof Gateway gateway)) return List.of(Set.of(trigger.id()));
        List<String> allIncoming = incoming.getOrDefault(target.id(), List.of()).stream()
                .filter(flow -> !flow.initial()).map(FlowRef::id).toList();
        Set<String> present = new LinkedHashSet<>(allIncoming);
        present.retainAll(marking);
        return switch (gateway.kind()) {
            case XOR, EVENT_BASED -> List.of(Set.of(trigger.id()));
            case AND -> marking.containsAll(allIncoming) ? List.of(Set.copyOf(allIncoming)) : List.of();
            case OR -> subsetsContaining(present, trigger.id());
        };
    }

    private List<Set<String>> nextChoices(FlowElement target) {
        if (target instanceof EndEvent) return List.of(Set.of());
        List<String> all = outgoing.getOrDefault(target.id(), List.of()).stream().map(FlowRef::id).toList();
        if (!(target instanceof Gateway gateway)) return List.of(Set.copyOf(all));
        return switch (gateway.kind()) {
            case XOR, EVENT_BASED -> all.stream().map(Set::of).toList();
            case AND -> List.of(Set.copyOf(all));
            case OR -> nonEmptySubsets(new LinkedHashSet<>(all));
        };
    }

    private String legacyDefaultPost(FlowElement source) {
        List<String> guards = outgoing.getOrDefault(source.id(), List.of()).stream()
                .map(FlowRef::sequenceFlow).filter(java.util.Objects::nonNull)
                .filter(flow -> !flow.isDefault())
                .map(SequenceFlow::guardSource).filter(java.util.Objects::nonNull)
                .filter(value -> !value.isBlank()).toList();
        if (guards.isEmpty()) return null;
        return "not (" + String.join(" or ", guards.stream().map(g -> "(" + g + ")").toList()) + ")";
    }

    private static List<Set<String>> subsetsContaining(Set<String> values, String required) {
        if (!values.contains(required)) return List.of();
        return nonEmptySubsets(values).stream().filter(set -> set.contains(required)).toList();
    }

    private static List<Set<String>> nonEmptySubsets(Set<String> values) {
        List<String> ordered = values.stream().sorted().toList();
        if (ordered.size() >= Integer.SIZE - 1 || (1L << ordered.size()) - 1 > MAX_SUBSETS) {
            throw new IllegalArgumentException("inclusive gateway expands to more than "
                    + MAX_SUBSETS + " flow subsets");
        }
        List<Set<String>> result = new ArrayList<>();
        int combinations = 1 << ordered.size();
        for (int mask = 1; mask < combinations; mask++) {
            Set<String> subset = new LinkedHashSet<>();
            for (int bit = 0; bit < ordered.size(); bit++) {
                if ((mask & (1 << bit)) != 0) subset.add(ordered.get(bit));
            }
            result.add(Set.copyOf(subset));
        }
        return result;
    }

    private static String stepKey(FlowStep step) {
        return step.flow().id() + "|" + sorted(step.pass()) + "|" + sorted(step.next());
    }

    private static List<String> sorted(Collection<String> values) {
        return values.stream().sorted().toList();
    }

    static String arcId(SequenceFlow flow) {
        return flow.source().id() + "::" + flow.target().id();
    }
}
