package org.vnu.sme.goal.verify.conformance;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tzi.use.parser.Symtable;
import org.tzi.use.parser.ocl.OCLCompiler;
import org.tzi.use.parser.soil.SoilCompiler;
import org.tzi.use.parser.use.USECompiler;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.ModelFactory;
import org.tzi.use.uml.ocl.expr.EvalContext;
import org.tzi.use.uml.ocl.expr.Expression;
import org.tzi.use.uml.ocl.expr.SimpleEvalContext;
import org.tzi.use.uml.ocl.value.BooleanValue;
import org.tzi.use.uml.ocl.value.VarBindings;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.uml.sys.MSystemState;
import org.tzi.use.uml.sys.StatementEvaluationResult;
import org.tzi.use.uml.sys.soil.MStatement;
import org.tzi.use.uml.sys.soil.SoilEvaluationContext;

import org.vnu.sme.goal.dsl.acl.mm.AclModel;
import org.vnu.sme.goal.dsl.acl.parser.AclCompiler;
import org.vnu.sme.goal.translate.acl2use.Acl2UseTranslator;
import org.vnu.sme.goal.dsl.aol.parser.AolCompiler;
import org.vnu.sme.goal.translate.aclaol2soil.AclAol2SoilTranslator;
import org.vnu.sme.goal.trace.bpmn.BpmnExecutionEngine;
import org.vnu.sme.goal.dsl.bpmn.mm.Activity;
import org.vnu.sme.goal.dsl.bpmn.mm.FlowElement;
import org.vnu.sme.goal.dsl.bpmn.mm.Process;
import org.vnu.sme.goal.trace.bpmn.BpmnPostEffect;
import org.vnu.sme.goal.dsl.bpmn.parser.BpmnCompiler;

/**
 * Runs a BPMN process to one concrete execution trace, seeded by a concrete
 * AOL population (agents/groups/links) instead of a hand-written {@code
 * .soil} scenario. There is no goal model involved: this produces a trace,
 * it does not judge conformance against one — see {@link
 * AclBpmnIStarConformanceChecker} / {@link ConformanceFlowRunner} for that.
 *
 * <p>A gateway always takes its first accepted outgoing flow. A concrete AOL
 * population plus deterministic activity effects mean exactly one execution
 * is possible per process instance; if a gateway genuinely has more than one
 * accepted branch at runtime that is a modeling defect (in the BPMN or the
 * AOL), not something this runner resolves by exploring alternatives.
 */
public final class AolBpmnTraceRunner {

    private static final Pattern SHELL_CREATE =
            Pattern.compile("!create\\s+([A-Za-z_][A-Za-z0-9_]*)\\s*:\\s*([A-Za-z_][A-Za-z0-9_]*)");

    /** One point along the trace: {@code activityId} is null only for frame 0, the initial
     *  AOL-seeded state before any activity has run. {@code state} is a frozen snapshot —
     *  safe to keep and render after the run (and the whole system) has moved on. */
    public record Frame(int index, String activityId, MSystemState state, List<String> stateDelta) {}

    public record InstanceTrace(String processId, String selfObjectName, String groupClass,
            List<Frame> frames, boolean ended) {}

    public record Result(MModel useModel, AclModel aclModel, List<InstanceTrace> traces, List<String> errors) {
        public Result {
            traces = List.copyOf(traces);
            errors = List.copyOf(errors);
        }
        public boolean ok() { return errors.isEmpty(); }
    }

    private AolBpmnTraceRunner() {}

    public static Result run(Path aclFile, Path aolFile, Path bpmnFile) {
        List<String> errors = new ArrayList<>();

        AclCompiler.Result acl;
        try {
            acl = AclCompiler.compile(aclFile);
        } catch (Exception ex) {
            return failed(errors, "ACL compiler crashed: " + message(ex));
        }
        if (!acl.ok()) return failed(errors, acl.errors());

        AolCompiler.Result aol;
        try {
            aol = AolCompiler.compile(aolFile);
        } catch (Exception ex) {
            return failed(errors, "AOL compiler crashed: " + message(ex));
        }
        if (!aol.ok()) return failed(errors, aol.errors());
        try {
            if (!Files.isSameFile(aclFile, aol.aclFile())) {
                return failed(errors, "AOL references a different ACL: " + aol.aclFile()
                        + " (selected: " + aclFile + ")");
            }
        } catch (Exception ex) {
            return failed(errors, "Cannot compare ACL references: " + message(ex));
        }

        BpmnCompiler.Result bpmn;
        try {
            bpmn = BpmnCompiler.compile(bpmnFile);
        } catch (Exception ex) {
            return failed(errors, "BPMN compiler crashed: " + message(ex));
        }
        if (!bpmn.ok()) return failed(errors, bpmn.errors());

        MModel useModel;
        MSystem system;
        try {
            String useText = Acl2UseTranslator.translate(acl.model());
            StringWriter useErr = new StringWriter();
            useModel = USECompiler.compileSpecification(useText, "acl-generated.use",
                    new PrintWriter(useErr), new ModelFactory());
            if (useModel == null) return failed(errors, "errors compiling generated .use: " + useErr);
            system = new MSystem(useModel);
            String initialSoil = AclAol2SoilTranslator.transform(acl.model(), aol.model());
            executeInitialSoil(system, useModel, initialSoil, errors);
            if (!errors.isEmpty()) return new Result(useModel, acl.model(), List.of(), errors);
        } catch (Exception ex) {
            return failed(errors, "Cannot build initial state from ACL+AOL: " + message(ex));
        }

        List<InstanceTrace> traces = new ArrayList<>();
        for (Process process : bpmn.model().processes()) {
            if (process.groupClass() == null) {
                traces.add(runOne(system, useModel, process, null, null, errors));
                continue;
            }
            MClass selfClass = useModel.getClass(process.groupClass());
            if (selfClass == null) {
                errors.add("Unknown ACL group class '" + process.groupClass() + "' named by pool " + process.id());
                continue;
            }
            List<MObject> instances = new ArrayList<>(system.state().objectsOfClass(selfClass));
            if (instances.isEmpty()) {
                errors.add("AOL population has no '" + process.groupClass() + "' instance for pool " + process.id());
                continue;
            }
            for (MObject self : instances) {
                traces.add(runOne(system, useModel, process, selfClass, self, errors));
            }
        }

        return new Result(useModel, acl.model(), traces, errors);
    }

    private static InstanceTrace runOne(MSystem system, MModel useModel, Process process,
            MClass selfClass, MObject self, List<String> errors) {
        String label = self == null ? process.id() : process.id() + "#" + self.name();
        Map<String, FlowElement> byId = new LinkedHashMap<>();
        process.flowElements().forEach(e -> byId.put(e.id(), e));
        BpmnExecutionEngine.PredicateEvaluator evaluator = ocl -> evaluate(system, useModel, ocl, selfClass, self);
        BpmnExecutionEngine engine = new BpmnExecutionEngine(process);
        List<Frame> frames = new ArrayList<>();
        try {
            engine.start(evaluator);
            frames.add(new Frame(0, null, snapshot(system, label + "#0"), List.of()));
            int guard = 0;
            while (true) {
                if (++guard > 10_000) {
                    errors.add(label + ": exceeded step limit, likely a routing cycle");
                    break;
                }
                String activityId = engine.snapshot().enabled().stream()
                        .filter(id -> byId.get(id) instanceof Activity)
                        .findFirst().orElse(null);
                if (activityId == null) break;
                Activity activity = (Activity) byId.get(activityId);

                Map<String, String> before = attributeValues(system.state());
                BpmnExecutionEngine.RunningStep running = engine.begin(activityId, evaluator);
                String soil = activity.postconditions().isEmpty() ? null
                        : BpmnPostEffect.toSoil(activity.postconditions().get(0).oclBody());
                if (soil != null) {
                    if (self != null) soil = soil.replaceAll("\\bself\\b", Matcher.quoteReplacement(self.name()));
                    executeStatement(system, useModel, "begin " + soil.replaceAll("\\s+", " ").strip() + " end",
                            label + "/" + activityId, errors);
                    if (!errors.isEmpty()) break;
                }
                requireValidState(system.state(), label + " after " + activityId);
                engine.complete(running, evaluator);
                frames.add(new Frame(frames.size(), activityId, snapshot(system, label + "#" + frames.size()),
                        delta(before, attributeValues(system.state()))));
            }
        } catch (Exception ex) {
            errors.add(label + ": " + message(ex));
        }
        return new InstanceTrace(process.id(), self == null ? null : self.name(),
                process.groupClass(), frames, engine.snapshot().ended());
    }

    private static MSystemState snapshot(MSystem system, String label) {
        return new MSystemState(label, system.state());
    }

    private static boolean evaluate(MSystem system, MModel useModel, String ocl,
            MClass selfClass, MObject self) throws Exception {
        StringWriter err = new StringWriter();
        if (selfClass == null) {
            Expression expr = OCLCompiler.compileExpression(useModel, ocl, "BPMN runtime OCL",
                    new PrintWriter(err), new Symtable());
            if (expr == null) throw new IllegalArgumentException(err.toString().strip());
            return asBoolean(expr.eval(new SimpleEvalContext(
                    system.state(), system.state(), new VarBindings())), ocl);
        }
        MObject currentSelf = system.state().objectByName(self.name());
        if (currentSelf == null) {
            throw new IllegalStateException("BPMN self context no longer exists: " + self.name());
        }
        Symtable vars = new Symtable();
        vars.add("self", selfClass, null);
        Expression expr = OCLCompiler.compileExpression(useModel, ocl, "BPMN runtime OCL",
                new PrintWriter(err), vars);
        if (expr == null) throw new IllegalArgumentException(err.toString().strip());
        EvalContext ctx = new SimpleEvalContext(system.state(), system.state(), new VarBindings());
        ctx.pushVarBinding("self", currentSelf.value());
        return asBoolean(expr.eval(ctx), ocl);
    }

    private static boolean asBoolean(Object value, String ocl) {
        if (!(value instanceof BooleanValue bv)) {
            throw new IllegalArgumentException("BPMN runtime OCL is not Boolean: " + ocl);
        }
        return bv.isTrue();
    }

    /** Accepts the same {@code !create}/{@code !set}/{@code !insert} shell syntax AclAol2SoilTranslator emits. */
    private static void executeInitialSoil(MSystem system, MModel useModel, String soilText, List<String> errors) {
        int idx = 0;
        for (String raw : soilText.lines().toList()) {
            String line = raw.strip();
            if (line.isEmpty() || line.startsWith("--")) continue;
            idx++;
            executeStatement(system, useModel, normalizeShellSoil(line), "aol-initial:" + idx, errors);
            if (!errors.isEmpty()) return;
        }
    }

    private static void executeStatement(MSystem system, MModel useModel, String statementText,
            String label, List<String> errors) {
        StringWriter stmtErr = new StringWriter();
        MStatement stmt = SoilCompiler.compileStatement(useModel, system.state(), system.getVariableEnvironment(),
                statementText, label, new PrintWriter(stmtErr), false);
        if (stmt == null) {
            errors.add(label + " '" + statementText + "': " + stmtErr);
            return;
        }
        try {
            stmt.execute(new SoilEvaluationContext(system), new StatementEvaluationResult(stmt));
        } catch (Exception ex) {
            errors.add(label + " '" + statementText + "': " + message(ex));
        }
    }

    private static String normalizeShellSoil(String line) {
        Matcher create = SHELL_CREATE.matcher(line);
        if (create.matches()) return create.group(1) + " := new " + create.group(2) + "('" + create.group(1) + "')";
        if (line.startsWith("!set ")) return line.substring("!set ".length()).stripLeading();
        return line.startsWith("!") ? line.substring(1).stripLeading() : line;
    }

    private static void requireValidState(MSystemState state, String phase) {
        StringWriter report = new StringWriter();
        if (!state.check(new PrintWriter(report), false, true, true, List.of())) {
            throw new IllegalStateException(phase + " failed ACL/USE constraints:\n" + report.toString().strip());
        }
    }

    private static Map<String, String> attributeValues(MSystemState state) {
        Map<String, String> values = new LinkedHashMap<>();
        state.allObjects().stream().sorted(Comparator.comparing(MObject::name)).forEach(object ->
                object.state(state).attributeValueMap().forEach((attribute, value) ->
                        values.put(object.name() + "." + attribute.name(), String.valueOf(value))));
        return values;
    }

    private static List<String> delta(Map<String, String> before, Map<String, String> after) {
        Set<String> keys = new LinkedHashSet<>(before.keySet());
        keys.addAll(after.keySet());
        List<String> out = new ArrayList<>();
        for (String key : keys) if (!Objects.equals(before.get(key), after.get(key))) {
            out.add(key + ": " + before.getOrDefault(key, "<absent>") + " -> " + after.getOrDefault(key, "<absent>"));
        }
        return out;
    }

    private static Result failed(List<String> errors, String detail) {
        errors.add(detail);
        return new Result(null, null, List.of(), errors);
    }

    private static Result failed(List<String> errors, List<String> details) {
        errors.addAll(details);
        return new Result(null, null, List.of(), errors);
    }

    private static String message(Exception ex) {
        String m = ex.getMessage();
        return m == null || m.isBlank() ? ex.toString() : m;
    }
}
