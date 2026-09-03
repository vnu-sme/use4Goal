package org.vnu.sme.goal.dsl.bpmnscenario.parser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.vnu.sme.goal.dsl.bpmn.mm.BpmnModel;
import org.vnu.sme.goal.dsl.bpmn.mm.FlowElement;
import org.vnu.sme.goal.dsl.bpmn.mm.SequenceFlow;
import org.vnu.sme.goal.dsl.bpmn.mm.SubProcess;
import org.vnu.sme.goal.dsl.bpmn.parser.BpmnCompiler;
import org.vnu.sme.goal.dsl.bpmnscenario.mm.AssertionResult;
import org.vnu.sme.goal.dsl.bpmnscenario.mm.BpmnScenarioModel;
import org.vnu.sme.goal.dsl.bpmnscenario.mm.BpmnScenarioSnapshot;
// BpmnScenarioLexer/BpmnScenarioParser are ANTLR-generated from
// grammars/BpmnScenario.g4, whose '@header' targets this same package --
// no cross-package import needed.
import org.vnu.sme.goal.dsl.bpmnscenario.mm.NodeOccurrence;
import org.vnu.sme.goal.dsl.bpmnscenario.mm.ScenarioStmt;
import org.vnu.sme.goal.dsl.bpmnscenario.mm.TokenMark;
import org.vnu.sme.goal.dsl.bpmnscenario.mm.Value;

/** End-to-end compiler for .bscn BPMN scenario files. */
public final class BpmnScenarioCompiler {

    private static final Pattern COUNT_ASSERT = Pattern.compile(
            "count\\(\\s*(\\w+)\\s+where\\s+(\\w+)\\s*=\\s*(\\w+)\\s*\\)\\s*(==|!=|>=|<=|>|<)\\s*(\\d+)");

    private BpmnScenarioCompiler() {}

    public record Result(BpmnModel model, Path modelFile, BpmnScenarioModel scenario,
                         BpmnScenarioSnapshot snapshot, List<AssertionResult> assertions,
                         List<String> errors) {
        public boolean ok() { return errors.isEmpty(); }
    }

    public static Result compile(Path file) throws IOException {
        List<String> errors = new ArrayList<>();

        BpmnScenarioLexer lexer = new BpmnScenarioLexer(CharStreams.fromPath(file));
        BpmnScenarioParser parser = new BpmnScenarioParser(new CommonTokenStream(lexer));

        lexer.removeErrorListeners();
        parser.removeErrorListeners();
        ANTLRErrorListener errListener = new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> rec, Object sym, int line, int col,
                                    String msg, RecognitionException e) {
                errors.add("line " + line + ":" + col + " " + msg);
            }
        };
        lexer.addErrorListener(errListener);
        parser.addErrorListener(errListener);

        BpmnScenarioParser.ScenarioContext tree = parser.scenario();
        if (!errors.isEmpty()) return new Result(null, null, null, null, List.of(), errors);

        var ast = BpmnScenarioBuildingVisitor.build(tree);
        var scenario = BpmnScenarioModelFactory.build(ast);

        Path modelFile = file.toAbsolutePath().getParent().resolve(scenario.modelFile()).normalize();
        BpmnCompiler.Result modelResult;
        try {
            modelResult = BpmnCompiler.compile(modelFile);
        } catch (IOException ex) {
            errors.add("cannot read BPMN model file '" + modelFile + "': " + ex.getMessage());
            return new Result(null, modelFile, scenario, null, List.of(), errors);
        }
        if (!modelResult.ok()) {
            errors.add("errors in referenced BPMN model '" + modelFile + "':");
            errors.addAll(modelResult.errors());
            return new Result(null, modelFile, scenario, null, List.of(), errors);
        }

        BpmnScenarioSnapshot snapshot = materialize(scenario);
        validate(modelResult.model(), modelFile, snapshot, errors);
        List<AssertionResult> assertions = evaluateAssertions(snapshot);
        for (AssertionResult assertion : assertions) {
            if (!assertion.holds()) {
                errors.add("assertion failed: " + assertion.expression() + " (" + assertion.detail() + ")");
            }
        }

        return new Result(modelResult.model(), modelFile, scenario, snapshot, assertions,
                errors.isEmpty() ? Collections.emptyList() : errors);
    }

    private static BpmnScenarioSnapshot materialize(BpmnScenarioModel scenario) {
        Map<String, String> processInstances = new LinkedHashMap<>();
        Map<String, String> actors = new LinkedHashMap<>();
        Map<String, Value> values = new LinkedHashMap<>();
        List<NodeOccurrence> fired = new ArrayList<>();
        List<NodeOccurrence> completed = new ArrayList<>();
        List<NodeOccurrence> active = new ArrayList<>();
        List<TokenMark> tokens = new ArrayList<>();
        List<String> assertions = new ArrayList<>();

        for (ScenarioStmt stmt : scenario.statements()) {
            switch (stmt) {
                case ScenarioStmt.ProcessDecl p -> processInstances.put(p.instanceId(), p.processId());
                case ScenarioStmt.ActorDecl a -> a.names().forEach(name -> actors.put(name, a.actorType()));
                case ScenarioStmt.Bind b -> values.put(b.target(), b.value());
                case ScenarioStmt.Fire f -> fired.add(f.occurrence());
                case ScenarioStmt.Completed c -> completed.add(c.occurrence());
                case ScenarioStmt.Active a -> active.add(a.occurrence());
                case ScenarioStmt.Token t -> tokens.add(t.mark());
                case ScenarioStmt.ValueStmt v -> values.put(v.target(), v.value());
                case ScenarioStmt.Assert a -> assertions.add(a.expression());
            }
        }

        return new BpmnScenarioSnapshot(processInstances, actors, values, fired, completed, active, tokens, assertions);
    }

    private static void validate(BpmnModel model, Path modelFile, BpmnScenarioSnapshot snapshot, List<String> errors) {
        Set<String> seenProcesses = new LinkedHashSet<>();
        snapshot.processInstances().forEach((instanceId, processId) -> {
            if (!seenProcesses.add(instanceId)) {
                errors.add("semantic: process instance '" + instanceId + "' declared more than once");
            }
            if (model.findProcess(processId).isEmpty()) {
                errors.add("semantic: process '" + instanceId + " : " + processId
                        + "' — no such pool/process in '" + modelFile + "'");
            }
        });

        validateOccurrences("fire", snapshot.fired(), model, snapshot, errors);
        validateOccurrences("completed", snapshot.completed(), model, snapshot, errors);
        validateOccurrences("active", snapshot.active(), model, snapshot, errors);

        for (TokenMark token : snapshot.tokens()) {
            validateProcessInstance(token.processInstanceId(), snapshot, errors);
            if (model.findFlowElement(token.sourceId()).isEmpty()) {
                errors.add("semantic: token '" + token.display() + "' — no such source flow node");
            }
            if (model.findFlowElement(token.targetId()).isEmpty()) {
                errors.add("semantic: token '" + token.display() + "' — no such target flow node");
            }
            if (!hasArc(model, token.sourceId(), token.targetId())) {
                errors.add("semantic: token '" + token.display() + "' — no sequence flow "
                        + token.sourceId() + " -> " + token.targetId());
            }
            validateObject(token.objectId(), snapshot, "token '" + token.display() + "'", errors);
        }
    }

    private static void validateOccurrences(String keyword, List<NodeOccurrence> occurrences,
                                            BpmnModel model, BpmnScenarioSnapshot snapshot, List<String> errors) {
        for (NodeOccurrence occurrence : occurrences) {
            validateProcessInstance(occurrence.processInstanceId(), snapshot, errors);
            Optional<FlowElement> element = model.findFlowElement(occurrence.elementId());
            if (element.isEmpty()) {
                errors.add("semantic: " + keyword + " '" + occurrence.display() + "' — no such BPMN flow node");
            } else {
                String declaredProcess = snapshot.processInstances().get(occurrence.processInstanceId());
                model.ownerProcessId(occurrence.elementId())
                        .filter(owner -> declaredProcess != null && !owner.equals(declaredProcess))
                        .ifPresent(owner -> errors.add("semantic: " + keyword + " '" + occurrence.display()
                                + "' — node belongs to process '" + owner + "', not '" + declaredProcess + "'"));
            }
            validateObject(occurrence.objectId(), snapshot, keyword + " '" + occurrence.display() + "'", errors);
            validateActor(occurrence.actorId(), snapshot, keyword + " '" + occurrence.display() + "'", errors);
        }
    }

    private static void validateProcessInstance(String processInstanceId, BpmnScenarioSnapshot snapshot, List<String> errors) {
        if (!snapshot.processInstances().containsKey(processInstanceId)) {
            errors.add("semantic: process instance '" + processInstanceId + "' is not declared");
        }
    }

    private static void validateObject(String objectId, BpmnScenarioSnapshot snapshot, String owner, List<String> errors) {
        if (objectId != null && !snapshot.actors().containsKey(objectId)) {
            errors.add("semantic: " + owner + " — object/actor '" + objectId + "' is not declared");
        }
    }

    private static void validateActor(String actorId, BpmnScenarioSnapshot snapshot, String owner, List<String> errors) {
        if (actorId != null && !snapshot.actors().containsKey(actorId)) {
            errors.add("semantic: " + owner + " — actor '" + actorId + "' is not declared");
        }
    }

    private static boolean hasArc(BpmnModel model, String sourceId, String targetId) {
        for (org.vnu.sme.goal.dsl.bpmn.mm.Process process : model.processes()) {
            if (hasArc(process.sequenceFlows(), sourceId, targetId)) return true;
            if (hasArcInSubprocesses(process.flowElements(), sourceId, targetId)) return true;
        }
        return false;
    }

    private static boolean hasArcInSubprocesses(List<FlowElement> elements, String sourceId, String targetId) {
        for (FlowElement element : elements) {
            if (element instanceof SubProcess sp) {
                if (hasArc(sp.sequenceFlows(), sourceId, targetId)) return true;
                if (hasArcInSubprocesses(sp.flowElements(), sourceId, targetId)) return true;
            }
        }
        return false;
    }

    private static boolean hasArc(List<SequenceFlow> flows, String sourceId, String targetId) {
        return flows.stream().anyMatch(f -> f.source().id().equals(sourceId) && f.target().id().equals(targetId));
    }

    private static List<AssertionResult> evaluateAssertions(BpmnScenarioSnapshot snapshot) {
        List<AssertionResult> results = new ArrayList<>();
        for (String expression : snapshot.assertions()) {
            Matcher matcher = COUNT_ASSERT.matcher(expression);
            if (matcher.matches()) {
                results.add(evaluateCountAssertion(snapshot, expression, matcher));
            } else {
                results.add(new AssertionResult(expression, true, "stored, not evaluated by MVP compiler"));
            }
        }
        return results;
    }

    private static AssertionResult evaluateCountAssertion(BpmnScenarioSnapshot snapshot, String expression, Matcher matcher) {
        String collectionName = matcher.group(1);
        String property = matcher.group(2);
        String expectedValue = matcher.group(3);
        String op = matcher.group(4);
        int expectedCount = Integer.parseInt(matcher.group(5));

        List<String> items = findCollection(snapshot.values(), collectionName);
        int actual = 0;
        for (String item : items) {
            Value value = snapshot.values().get(item + "." + property);
            if (value instanceof Value.Atom atom && atom.text().equals(expectedValue)) {
                actual++;
            }
        }
        boolean holds = compare(actual, op, expectedCount);
        return new AssertionResult(expression, holds,
                "actual count is " + actual + " over " + items.size() + " item(s)");
    }

    private static List<String> findCollection(Map<String, Value> values, String collectionName) {
        Value direct = values.get(collectionName);
        if (direct instanceof Value.ListValue list) return list.items();
        for (Map.Entry<String, Value> entry : values.entrySet()) {
            if ((entry.getKey().equals(collectionName) || entry.getKey().endsWith("." + collectionName))
                    && entry.getValue() instanceof Value.ListValue list) {
                return list.items();
            }
        }
        return List.of();
    }

    private static boolean compare(int actual, String op, int expected) {
        return switch (op) {
            case "==" -> actual == expected;
            case "!=" -> actual != expected;
            case ">=" -> actual >= expected;
            case "<=" -> actual <= expected;
            case ">" -> actual > expected;
            case "<" -> actual < expected;
            default -> false;
        };
    }
}
