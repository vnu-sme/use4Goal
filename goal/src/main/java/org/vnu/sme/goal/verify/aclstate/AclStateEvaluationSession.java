package org.vnu.sme.goal.verify.aclstate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.vnu.sme.goal.dsl.acl.mm.AclInvariant;
import org.vnu.sme.goal.dsl.acl.mm.AclModel;
import org.vnu.sme.goal.dsl.acl.parser.AclCompiler;
import org.vnu.sme.goal.dsl.aol.state.AclSystemState;
import org.vnu.sme.goal.dsl.aol.state.AclSystemStateCompiler;
import org.vnu.sme.goal.dsl.istar.mm.GoalModel;
import org.vnu.sme.goal.dsl.istar.parser.IStarCompiler;
import org.vnu.sme.goal.trace.istartrace.nativeacl.NativeOclEvaluator;

/**
 * Evaluates OCL invariants directly over typed ACL/AOL states.
 *
 * <p>This class deliberately has no dependency on USE's {@code MModel},
 * {@code MSystem}, SOIL, or the ACL-to-USE translator. Entity, Role and Group
 * remain distinct ACL classifier kinds throughout compilation and evaluation.</p>
 */
public final class AclStateEvaluationSession {
    public enum TruthValue {
        TRUE, FALSE, UNDEFINED, ERROR
    }

    public record ConstraintResult(String name, String context, String expression,
                                   TruthValue result, String detail) {
        public ConstraintResult {
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(context, "context");
            Objects.requireNonNull(expression, "expression");
            Objects.requireNonNull(result, "result");
            detail = detail == null ? "" : detail;
        }
    }

    public record StateResult(int index, Path aolFile, AclSystemState state,
                              boolean structureValid, String structureReport,
                              List<String> inputDiagnostics,
                              List<ConstraintResult> constraints) {
        public StateResult {
            Objects.requireNonNull(aolFile, "aolFile");
            Objects.requireNonNull(state, "state");
            structureReport = structureReport == null ? "" : structureReport;
            inputDiagnostics = List.copyOf(inputDiagnostics);
            constraints = List.copyOf(constraints);
        }

        public int objectCount() { return state.objectCount(); }
        public int linkCount() { return state.linkCount(); }
        public long trueCount() { return count(TruthValue.TRUE); }
        public long falseCount() { return count(TruthValue.FALSE); }
        public long undefinedCount() { return count(TruthValue.UNDEFINED); }
        public long errorCount() { return count(TruthValue.ERROR); }

        private long count(TruthValue value) {
            return constraints.stream().filter(result -> result.result() == value).count();
        }
    }

    private final Path aclFile;
    private final AclModel aclModel;
    private final String aclSource;
    private final List<StateResult> states = new ArrayList<>();
    private AclBpmnStateTraceEvaluator bpmnEvaluator;
    private AclBpmnBoundary boundary;
    private Path istarFile;
    private GoalModel goalModel;
    private String istarSource;

    private AclStateEvaluationSession(Path aclFile, AclModel aclModel, String aclSource) {
        this.aclFile = aclFile;
        this.aclModel = aclModel;
        this.aclSource = aclSource;
    }

    public static AclStateEvaluationSession load(Path aclFile) throws IOException {
        Objects.requireNonNull(aclFile, "aclFile");
        Path source = aclFile.toAbsolutePath().normalize();
        AclCompiler.Result acl = AclCompiler.compile(source);
        if (!acl.ok()) {
            throw new IllegalArgumentException("ACL compilation failed:\n" + String.join("\n", acl.errors()));
        }
        return new AclStateEvaluationSession(source, acl.model(), Files.readString(source));
    }

    public Path aclFile() { return aclFile; }
    public AclModel aclModel() { return aclModel; }
    public String aclSource() { return aclSource; }
    public List<StateResult> states() { return List.copyOf(states); }
    public AclBpmnStateTraceEvaluator bpmnEvaluator() { return bpmnEvaluator; }
    public AclBpmnBoundary boundary() { return boundary; }
    public Path istarFile() { return istarFile; }
    public GoalModel goalModel() { return goalModel; }
    public String istarSource() { return istarSource; }

    /** Loads the BPMN specification whose execution is to be inferred from the AOL state chain. */
    public AclBpmnStateTraceEvaluator loadBpmn(Path bpmnFile) throws IOException {
        bpmnEvaluator = AclBpmnStateTraceEvaluator.load(bpmnFile, aclModel);
        return bpmnEvaluator;
    }

    /** Loads the finite symbolic scope used only by whole-process validation. */
    public AclBpmnBoundary loadBoundary(Path boundaryFile) throws IOException {
        boundary = AclBpmnBoundary.load(boundaryFile, aclModel);
        return boundary;
    }

    /** Loads the iStar requirements whose markings are derived from generated ACL states. */
    public GoalModel loadIStar(Path file) throws IOException {
        Objects.requireNonNull(file, "file");
        Path source = file.toAbsolutePath().normalize();
        IStarCompiler.Result compiled = IStarCompiler.compile(source);
        if (!compiled.ok()) {
            throw new IllegalArgumentException("iStar compilation failed:\n"
                    + String.join("\n", compiled.errors()));
        }
        istarFile = source;
        goalModel = compiled.model();
        istarSource = Files.readString(source);
        return goalModel;
    }

    public void clearStates() {
        states.clear();
    }

    public AclBpmnStateTraceEvaluator.TraceResult evaluateBpmnTrace() {
        if (bpmnEvaluator == null) throw new IllegalStateException("Load a BPMN specification first");
        return bpmnEvaluator.evaluate(states);
    }

    /**
     * Kodkod realization of bounded ACL/BPMN/iStar consistency. iStar is
     * evaluated over generated ACL paths; AOL snapshots are deliberately not used.
     */
    public AclBpmnWholeProcessValidator.ValidationResult validateWholeBpmnProcess() {
        if (bpmnEvaluator == null) throw new IllegalStateException("Load a BPMN specification first");
        if (boundary == null) throw new IllegalStateException("Load an ACL/BPMN boundary first");
        if (goalModel == null) throw new IllegalStateException("Load an iStar requirement model first");
        return new AclBpmnWholeProcessValidator().validate(bpmnEvaluator, aclModel, boundary, goalModel);
    }

    /** Backward-compatible BPMN-only bounded validation used by focused BPMN clients. */
    public AclBpmnWholeProcessValidator.ValidationResult validateWholeBpmnOnly() {
        if (bpmnEvaluator == null) throw new IllegalStateException("Load a BPMN specification first");
        if (boundary == null) throw new IllegalStateException("Load an ACL/BPMN boundary first");
        return new AclBpmnWholeProcessValidator().validate(bpmnEvaluator, aclModel, boundary);
    }

    /** Adds one complete AOL snapshot and evaluates ACL invariants without a USE conversion. */
    public StateResult addState(Path aolFile) throws IOException {
        Objects.requireNonNull(aolFile, "aolFile");
        Path source = aolFile.toAbsolutePath().normalize();
        AclSystemStateCompiler.Result aol = AclSystemStateCompiler.compile(source, aclFile, aclModel);
        if (aol.state() == null) {
            throw new IllegalArgumentException("AOL v2 compilation failed:\n"
                    + String.join("\n", aol.diagnostics()));
        }
        AclSystemState snapshot = aol.state();
        List<ConstraintResult> constraints = evaluateConstraints(snapshot);
        boolean structureValid = aol.diagnostics().isEmpty();
        String structureReport = structureValid ? "ACL structure valid"
                : String.join("\n", aol.diagnostics());
        StateResult result = new StateResult(states.size(), source, snapshot,
                structureValid, structureReport, aol.diagnostics(), constraints);
        states.add(result);
        return result;
    }

    private List<ConstraintResult> evaluateConstraints(AclSystemState snapshot) {
        List<ConstraintResult> results = new ArrayList<>(aclModel.invariants().size());
        for (AclInvariant invariant : aclModel.invariants()) {
            try {
                List<AclSystemState.ObjectValue> contextObjects = snapshot.objectsOfType(invariant.contextType());
                List<String> violations = new ArrayList<>();
                for (AclSystemState.ObjectValue self : contextObjects) {
                    boolean value = NativeOclEvaluator.evaluate(
                            invariant.expression(), snapshot, List.of(self));
                    if (!value) violations.add(self.id());
                }
                boolean holds = violations.isEmpty();
                String detail = contextObjects.isEmpty()
                        ? "true (no instances of context " + invariant.contextType() + ")"
                        : holds ? "true for " + contextObjects.size() + " instance(s)"
                        : "false for: " + String.join(", ", violations);
                results.add(new ConstraintResult(invariant.qualifiedName(), invariant.contextType(),
                        invariant.expression(), holds ? TruthValue.TRUE : TruthValue.FALSE, detail));
            } catch (RuntimeException ex) {
                results.add(new ConstraintResult(invariant.qualifiedName(), invariant.contextType(),
                        invariant.expression(), TruthValue.ERROR, message(ex)));
            }
        }
        return List.copyOf(results);
    }

    private static String message(Throwable throwable) {
        String value = throwable.getMessage();
        return value == null || value.isBlank() ? throwable.getClass().getSimpleName() : value;
    }
}
