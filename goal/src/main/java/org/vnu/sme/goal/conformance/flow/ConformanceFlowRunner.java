package org.vnu.sme.goal.conformance.flow;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.vnu.sme.goal.acl.parser.AclCompiler;
import org.vnu.sme.goal.aol.parser.AolCompiler;
import org.vnu.sme.goal.aol.transform.AolToSoilTransformer;
import org.vnu.sme.goal.bpmn2.mm.Bpmn2Model;
import org.vnu.sme.goal.bpmn2.parser.Bpmn2Compiler;
import org.vnu.sme.goal.conformance.AclBpmnIStarConformanceChecker;
import org.vnu.sme.goal.conformance.ConformanceVerdict;
import org.vnu.sme.goal.conformance.ProcessConformanceClassifier;
import org.vnu.sme.goal.conformance.oracle.IscnOracleComparator;
import org.vnu.sme.goal.istar.mm.GoalModel;
import org.vnu.sme.goal.istar.parser.IStarCompiler;
import org.vnu.sme.goal.istarusebridge.IStarUseTraceCompiler;
import org.vnu.sme.goal.iscn.parser.IStarScenarioCompiler;

/**
 * Pure orchestration service for the complete concrete-trace conformance flow.
 *
 * <p>The service deliberately calls compiler/transformer APIs instead of
 * chaining plugin action delegates. Action delegates remain thin USE GUI
 * adapters; this class owns the ordered domain workflow.
 */
public final class ConformanceFlowRunner {

    public enum Stage {
        ACL("1. ACL schema"),
        AOL("2. AOL snapshot"),
        ISTAR("3. iStar goal model"),
        ISCN("4. ISCN oracle"),
        BPMN2("5. BPMN2 + OCL"),
        CONFORMANCE("6. Shared-state conformance");

        private final String label;

        Stage(String label) {
            this.label = label;
        }

        public String label() {
            return label;
        }
    }

    public enum StageState {
        PASSED,
        FAILED
    }

    public record StageResult(Stage stage, StageState state, String detail) {}
    private record InputIssue(Stage stage, String detail) {}

    public record TraceResult(
            int index,
            List<String> flowElementIds,
            List<String> activityIds,
            AclBpmnIStarConformanceChecker.Result conformance,
            List<String> oracleFailures,
            ConformanceVerdict verdict) {
        public TraceResult {
            flowElementIds = List.copyOf(flowElementIds);
            activityIds = List.copyOf(activityIds);
            oracleFailures = List.copyOf(oracleFailures);
        }

        public boolean conformant() {
            return verdict == ConformanceVerdict.TRACE_CONFORMANT;
        }
    }

    public record Result(
            List<StageResult> stages,
            Path generatedInitialSoil,
            AclBpmnIStarConformanceChecker.Result conformance,
            List<String> oracleFailures,
            List<String> errors,
            ConformanceVerdict verdict,
            List<TraceResult> traces,
            boolean completeExecutionSpace) {
        public Result {
            stages = List.copyOf(stages);
            oracleFailures = List.copyOf(oracleFailures);
            errors = List.copyOf(errors);
            traces = List.copyOf(traces);
        }

        public boolean ok() {
            return verdict != ConformanceVerdict.EXECUTION_ERROR;
        }

        public boolean conformant() {
            return verdict.isConformant();
        }

        public int conformantTraceCount() {
            return (int) traces.stream().filter(TraceResult::conformant).count();
        }

        public GoalModel goalModel() {
            return conformance == null ? null : conformance.goalModel();
        }

        public Bpmn2Model bpmnModel() {
            return conformance == null ? null : conformance.bpmnModel();
        }
    }

    private ConformanceFlowRunner() {}

    public static Result run(
            Path aclFile,
            Path aolFile,
            Path istarFile,
            Path iscnFile,
            Path bpmnFile) {
        List<StageResult> stages = new ArrayList<>();
        InputIssue inputIssue = validateInputs(aclFile, aolFile, istarFile, iscnFile, bpmnFile);
        if (inputIssue != null) {
            return failed(stages, inputIssue.stage(), "Input validation failed",
                    List.of(inputIssue.detail()), null, null);
        }

        AclCompiler.Result acl;
        try {
            acl = AclCompiler.compile(aclFile);
        } catch (Exception ex) {
            return failed(stages, Stage.ACL, "ACL compiler crashed",
                    List.of(message(ex)), null, null);
        }
        if (!acl.ok()) {
            return failed(stages, Stage.ACL, "ACL compilation failed", acl.errors(), null, null);
        }
        stages.add(passed(Stage.ACL, acl.model().name()));

        AolCompiler.Result aol;
        try {
            aol = AolCompiler.compile(aolFile);
        } catch (Exception ex) {
            return failed(stages, Stage.AOL, "AOL compiler crashed",
                    List.of(message(ex)), null, null);
        }
        if (!aol.ok()) {
            return failed(stages, Stage.AOL, "AOL compilation failed", aol.errors(), null, null);
        }
        try {
            if (!sameFile(aclFile, aol.aclFile())) {
                return failed(stages, Stage.AOL, "AOL references a different ACL",
                        List.of("selected ACL: " + aclFile.toAbsolutePath().normalize(),
                                "AOL ACL: " + aol.aclFile().toAbsolutePath().normalize()),
                        null, null);
            }
        } catch (Exception ex) {
            return failed(stages, Stage.AOL, "Cannot compare ACL references",
                    List.of(message(ex)), null, null);
        }
        stages.add(passed(Stage.AOL, aol.model().name() + " -> " + aol.aclFile()));

        IStarCompiler.Result istar;
        try {
            istar = IStarCompiler.compile(istarFile);
        } catch (Exception ex) {
            return failed(stages, Stage.ISTAR, "iStar compiler crashed",
                    List.of(message(ex)), null, null);
        }
        if (!istar.ok()) {
            return failed(stages, Stage.ISTAR, "iStar compilation failed",
                    istar.errors(), null, null);
        }
        stages.add(passed(Stage.ISTAR, istar.model().getName()));

        IStarScenarioCompiler.Result iscn;
        try {
            iscn = IStarScenarioCompiler.compile(iscnFile);
        } catch (Exception ex) {
            return failed(stages, Stage.ISCN, "ISCN compiler crashed",
                    List.of(message(ex)), null, null);
        }
        if (!iscn.ok()) {
            return failed(stages, Stage.ISCN, "ISCN compilation failed",
                    iscn.errors(), null, null);
        }
        try {
            if (!sameFile(istarFile, iscn.modelFile())) {
                return failed(stages, Stage.ISCN, "ISCN references a different iStar model",
                        List.of("selected iStar: " + istarFile.toAbsolutePath().normalize(),
                                "ISCN iStar: " + iscn.modelFile().toAbsolutePath().normalize()),
                        null, null);
            }
        } catch (Exception ex) {
            return failed(stages, Stage.ISCN, "Cannot compare iStar references",
                    List.of(message(ex)), null, null);
        }
        stages.add(passed(Stage.ISCN, iscn.scenario().name()));

        Bpmn2Compiler.Result bpmn;
        try {
            bpmn = Bpmn2Compiler.compile(bpmnFile);
        } catch (Exception ex) {
            return failed(stages, Stage.BPMN2, "BPMN2 compiler crashed",
                    List.of(message(ex)), null, null);
        }
        if (!bpmn.ok()) {
            return failed(stages, Stage.BPMN2, "BPMN2 compilation failed",
                    bpmn.errors(), null, null);
        }
        stages.add(passed(Stage.BPMN2, bpmn.model().name()));

        Path generatedInitialSoil = null;
        try {
            generatedInitialSoil = Files.createTempFile("aol-initial-", ".soil");
            generatedInitialSoil.toFile().deleteOnExit();
            Files.writeString(generatedInitialSoil,
                    AolToSoilTransformer.transform(acl.model(), aol.model()));
        } catch (Exception ex) {
            List<String> details = new ArrayList<>();
            details.add(message(ex));
            if (generatedInitialSoil != null) {
                try {
                    Files.deleteIfExists(generatedInitialSoil);
                } catch (Exception cleanupFailure) {
                    details.add("cannot delete failed-flow temporary SOIL: "
                            + message(cleanupFailure));
                }
            }
            return failed(stages, Stage.CONFORMANCE, "Flow execution crashed",
                    details, null, null);
        }

        BpmnExecutionSpaceEnumerator.Result executionSpace =
                BpmnExecutionSpaceEnumerator.enumerate(bpmn.model());
        if (!executionSpace.ok()) {
            return failed(stages, Stage.CONFORMANCE,
                    "Cannot prove complete BPMN execution-space coverage",
                    executionSpace.errors(), generatedInitialSoil, null);
        }

        List<TraceResult> traceResults = new ArrayList<>();
        AclBpmnIStarConformanceChecker.Result firstConformance = null;
        for (BpmnExecutionSpaceEnumerator.Trace executionTrace : executionSpace.traces()) {
            AclBpmnIStarConformanceChecker.Result traceConformance;
            try {
                traceConformance = AclBpmnIStarConformanceChecker.checkTrace(
                        aclFile, generatedInitialSoil, istarFile, bpmnFile,
                        executionTrace.activityIds());
            } catch (Exception ex) {
                return failed(stages, Stage.CONFORMANCE,
                        "Trace #" + executionTrace.index() + " execution crashed",
                        List.of(message(ex)), generatedInitialSoil, firstConformance);
            }
            if (firstConformance == null) firstConformance = traceConformance;
            if (!traceConformance.ok()) {
                return failed(stages, Stage.CONFORMANCE,
                        "Trace #" + executionTrace.index() + " execution failed",
                        traceConformance.errors(), generatedInitialSoil, traceConformance);
            }

            List<String> traceOracleFailures;
            try {
                IStarUseTraceCompiler.Result trace = IStarUseTraceCompiler.compile(
                        istarFile, traceConformance.generatedUse(), traceConformance.executionSoil());
                if (!trace.ok()) {
                    return failed(stages, Stage.CONFORMANCE,
                            "Cannot build oracle comparison trace #" + executionTrace.index(),
                            trace.errors(), generatedInitialSoil, traceConformance);
                }
                if (trace.checkpoints().isEmpty()) {
                    return failed(stages, Stage.CONFORMANCE,
                            "Cannot compare empty execution trace #" + executionTrace.index(),
                            List.of("the generated AOL/BPMN execution produced no checkpoint"),
                            generatedInitialSoil, traceConformance);
                }
                traceOracleFailures = IscnOracleComparator.compare(
                        iscn, aol.model(), trace.checkpoints().get(trace.checkpoints().size() - 1));
            } catch (Exception ex) {
                return failed(stages, Stage.CONFORMANCE,
                        "ISCN oracle comparison crashed for trace #" + executionTrace.index(),
                        List.of(message(ex)), generatedInitialSoil, traceConformance);
            }

            ConformanceVerdict traceVerdict =
                    traceConformance.conformant() && traceOracleFailures.isEmpty()
                            ? ConformanceVerdict.TRACE_CONFORMANT
                            : ConformanceVerdict.TRACE_NON_CONFORMANT;
            traceResults.add(new TraceResult(
                    executionTrace.index(),
                    executionTrace.flowElementIds(),
                    executionTrace.activityIds(),
                    traceConformance,
                    traceOracleFailures,
                    traceVerdict));
        }

        int conformantTraceCount = (int) traceResults.stream()
                .filter(TraceResult::conformant)
                .count();
        ConformanceVerdict verdict = traceResults.size() == 1
                ? traceResults.get(0).verdict()
                : ProcessConformanceClassifier.classify(
                        conformantTraceCount,
                        traceResults.size(),
                        executionSpace.completeExecutionSpace(),
                        executionSpace.traceUniquenessProven());
        stages.add(new StageResult(Stage.CONFORMANCE,
                verdict.isConformant() ? StageState.PASSED : StageState.FAILED,
                verdict.name() + " (" + conformantTraceCount + "/"
                        + traceResults.size() + " conformant traces)"));
        TraceResult firstTrace = traceResults.get(0);
        return new Result(stages, generatedInitialSoil, firstConformance,
                firstTrace.oracleFailures(), List.of(), verdict,
                traceResults, executionSpace.completeExecutionSpace());
    }

    private static InputIssue validateInputs(
            Path acl,
            Path aol,
            Path istar,
            Path iscn,
            Path bpmn) {
        InputIssue issue = require(Stage.ACL, acl, ".acl");
        if (issue != null) return issue;
        issue = require(Stage.AOL, aol, ".aol");
        if (issue != null) return issue;
        issue = require(Stage.ISTAR, istar, ".istar");
        if (issue != null) return issue;
        issue = require(Stage.ISCN, iscn, ".iscn");
        if (issue != null) return issue;
        return require(Stage.BPMN2, bpmn, ".bpmn2");
    }

    private static InputIssue require(Stage stage, Path file, String extension) {
        if (file == null || !Files.isRegularFile(file)) {
            return new InputIssue(stage, "input file does not exist: " + file);
        }
        String name = file.getFileName().toString().toLowerCase(Locale.ROOT);
        if (!name.endsWith(extension)) {
            return new InputIssue(stage, "expected " + extension + " file: " + file);
        }
        return null;
    }

    private static boolean sameFile(Path left, Path right) throws Exception {
        if (left == null || right == null) return false;
        return Files.isSameFile(left, right);
    }

    private static StageResult passed(Stage stage, String detail) {
        return new StageResult(stage, StageState.PASSED, detail);
    }

    private static Result failed(
            List<StageResult> stages,
            Stage stage,
            String summary,
            List<String> details,
            Path generatedInitialSoil,
            AclBpmnIStarConformanceChecker.Result conformance) {
        List<String> errors = new ArrayList<>();
        errors.add(stage.label() + ": " + summary);
        details.forEach(detail -> errors.add("  - " + detail));
        stages.add(new StageResult(stage, StageState.FAILED, summary));
        return new Result(stages, generatedInitialSoil, conformance, List.of(), errors,
                ConformanceVerdict.EXECUTION_ERROR, List.of(), false);
    }

    private static String message(Exception ex) {
        String message = ex.getMessage();
        return message == null || message.isBlank() ? ex.toString() : message;
    }
}
