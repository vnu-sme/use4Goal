package org.vnu.sme.goal.scenarioverification;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.tzi.use.parser.Symtable;
import org.tzi.use.parser.ocl.OCLCompiler;
import org.tzi.use.uml.mm.MClass;
import org.vnu.sme.goal.bpmn2.ocl.Bpmn2OclConstraintCompiler.ConstraintInfo;
import org.vnu.sme.goal.bpmn2.ocl.Bpmn2OclEvaluationResult.Status;
import org.vnu.sme.goal.bpmn2.ocl.Bpmn2OclValidationCompiler;
import org.vnu.sme.goal.bpmn2scenario.mm.Bpmn2ScenarioSnapshot;
import org.vnu.sme.goal.bpmn2scenario.mm.NodeOccurrence;
import org.vnu.sme.goal.bpmn2scenario.mm.TokenMark;
import org.vnu.sme.goal.bpmn2scenario.mm.Value;

/** Plain Java unit test harness for {@link ScenarioTraceVerifier}. */
public final class ScenarioTraceVerifierTestMain {

    private ScenarioTraceVerifierTestMain() {}

    public static void main(String[] args) throws Exception {
        Path base = Path.of("goal/src/main/resources/examples/bpmn_ocl/audit");
        Bpmn2OclValidationCompiler.Result compiled = Bpmn2OclValidationCompiler.compile(
                base.resolve("order_process_valid.bpmn2"),
                base.resolve("order_domain.use"),
                base.resolve("order_context.bpmn2oclmap"));
        require(compiled.ok(), "audit BPMN OCL inputs should compile: " + compiled.errors());

        ScenarioRuntimeState runtime = approvedOrderRuntime(compiled);

        happyPath(runtime, compiled.constraints());
        bpmnFail(runtime, compiled.constraints());
        runtimeError(runtime, compiled);
        missingSelf(runtime, compiled.constraints());
        zeroConstraints(runtime);
        nullRuntimeState(compiled.constraints());
        nullConstraints(runtime);
        reportToText(runtime, compiled.constraints());

        System.out.println("ScenarioTraceVerifierTestMain OK");
    }

    private static void happyPath(
            ScenarioRuntimeState runtime,
            Map<String, ConstraintInfo> constraints) {
        ScenarioTraceVerificationReport report = ScenarioTraceVerifier.verifyBpmn(
                "OrderApproval",
                "OrderOffice",
                runtime,
                subset(constraints,
                        "order_received",
                        "review_order",
                        "decide_order",
                        "approve_order",
                        "order_closed",
                        "decide_order::approve_order"));

        require(report.bpmnResults().size() == 6, "happy path should evaluate six constraints");
        require(report.passCount() == 6, "happy path should pass all constraints: " + report.toText());
        require(report.failCount() == 0, "happy path should have no FAIL");
        require(report.errorCount() == 0, "happy path should have no ERROR");
        require(report.passed(), "happy path report should pass");
        require(report.overallStatus().equals("PASS"), "overall status should be PASS");
    }

    private static void bpmnFail(
            ScenarioRuntimeState runtime,
            Map<String, ConstraintInfo> constraints) {
        ScenarioTraceVerificationReport report = ScenarioTraceVerifier.verifyBpmn(
                "OrderApproval",
                "OrderOffice",
                runtime,
                subset(constraints, "reject_order"));

        require(report.failCount() == 1, "reject_order should FAIL for approved order: " + report.toText());
        require(!report.passed(), "FAIL report should not pass");
        require(report.overallStatus().equals("FAIL"), "overall status should be FAIL");
    }

    private static void runtimeError(
            ScenarioRuntimeState runtime,
            Bpmn2OclValidationCompiler.Result compiled) throws Exception {
        ConstraintInfo nonBoolean = compileAdHoc(compiled, "amount_value", "node", "review_order", "self.amount");
        ScenarioTraceVerificationReport report = ScenarioTraceVerifier.verifyBpmn(
                "OrderApproval",
                "OrderOffice",
                runtime,
                Map.of("amount_value", nonBoolean));

        require(report.errorCount() == 1, "non-Boolean OCL should be ERROR: " + report.toText());
        require(report.overallStatus().equals("ERROR"), "overall status should be ERROR");
    }

    private static void missingSelf(
            ScenarioRuntimeState runtime,
            Map<String, ConstraintInfo> constraints) {
        ScenarioTraceVerificationReport report = ScenarioTraceVerifier.verifyBpmn(
                "OrderApproval",
                "OrderOffice",
                runtime,
                subset(constraints, "decide_order::reject_order"));

        require(report.errorCount() == 1, "missing self should be ERROR: " + report.toText());
        require(report.bpmnResults().get(0).reason().contains("missing self binding"),
                "missing self reason should be explicit");
    }

    private static void zeroConstraints(ScenarioRuntimeState runtime) {
        ScenarioTraceVerificationReport report = ScenarioTraceVerifier.verifyBpmn(
                "OrderApproval", "OrderOffice", runtime, Map.of());

        require(report.bpmnResults().isEmpty(), "zero constraints should evaluate no results");
        require(report.passCount() == 0, "zero constraints should have zero PASS");
        require(report.failCount() == 0, "zero constraints should have zero FAIL");
        require(report.errorCount() == 0, "zero constraints should have zero ERROR");
        require(report.passed(), "zero constraints should count as PASS");
        require(report.overallStatus().equals("PASS"), "zero constraints overall status should be PASS");
    }

    private static void nullRuntimeState(Map<String, ConstraintInfo> constraints) {
        ScenarioTraceVerificationReport report = ScenarioTraceVerifier.verifyBpmn(
                "OrderApproval", "OrderOffice", null, constraints);

        require(report.errorCount() == 1, "null runtime state should produce report ERROR");
        require(report.overallStatus().equals("ERROR"), "null runtime state overall status should be ERROR");
    }

    private static void nullConstraints(ScenarioRuntimeState runtime) {
        ScenarioTraceVerificationReport report = ScenarioTraceVerifier.verifyBpmn(
                "OrderApproval", "OrderOffice", runtime, null);

        require(report.errorCount() == 1, "null constraints should produce report ERROR");
        require(report.overallStatus().equals("ERROR"), "null constraints overall status should be ERROR");
    }

    private static void reportToText(
            ScenarioRuntimeState runtime,
            Map<String, ConstraintInfo> constraints) {
        ScenarioTraceVerificationReport report = ScenarioTraceVerifier.verifyBpmn(
                "OrderApproval",
                "OrderOffice",
                runtime,
                subset(constraints, "review_order"));
        String text = report.toText();

        require(text.contains("Scenario: OrderApproval"), "report text should include scenario");
        require(text.contains("Process: OrderOffice"), "report text should include process");
        require(text.contains("Objects"), "report text should include objects section");
        require(text.contains("order1 : Order"), "report text should list materialized object");
        require(text.contains("Self bindings"), "report text should include self bindings section");
        require(text.contains("review_order -> order1"), "report text should list self binding");
        require(text.contains("Evaluated BPMN constraints"), "report text should include constraints section");
        require(text.contains("PASS review_order"), "report text should include evaluation result");
        require(text.contains("Summary"), "report text should include summary");
        require(text.contains("Overall: PASS"), "report text should include overall status");
    }

    private static ScenarioRuntimeState approvedOrderRuntime(Bpmn2OclValidationCompiler.Result compiled) {
        Bpmn2ScenarioSnapshot snapshot = new Bpmn2ScenarioSnapshot(
                Map.of("p1", "OrderOffice"),
                Map.of("order1", "Order"),
                Map.of(
                        "order1.created", new Value.Atom("true"),
                        "order1.reviewed", new Value.Atom("true"),
                        "order1.approved", new Value.Atom("true"),
                        "order1.rejected", new Value.Atom("false"),
                        "order1.priority", new Value.Atom("true"),
                        "order1.amount", new Value.Atom("100"),
                        "order1.status", new Value.Atom("#approved")),
                List.of(
                        new NodeOccurrence("p1", "order_received", "order1", null),
                        new NodeOccurrence("p1", "review_order", "order1", null),
                        new NodeOccurrence("p1", "decide_order", "order1", null),
                        new NodeOccurrence("p1", "approve_order", "order1", null),
                        new NodeOccurrence("p1", "order_closed", "order1", null),
                        new NodeOccurrence("p1", "reject_order", "order1", null)),
                List.of(),
                List.of(),
                List.of(new TokenMark("p1", "decide_order", "approve_order", "order1")),
                List.of());

        BpmnScenarioUseStateAdapter.Result result =
                BpmnScenarioUseStateAdapter.materialize(snapshot, compiled.useModel());
        require(result.ok(), "approved order runtime should materialize: " + result.errors());
        return result.runtimeState();
    }

    private static Map<String, ConstraintInfo> subset(
            Map<String, ConstraintInfo> constraints,
            String... ids) {
        Map<String, ConstraintInfo> selected = new LinkedHashMap<>();
        for (String id : ids) {
            ConstraintInfo constraint = constraints.get(id);
            require(constraint != null, "missing compiled constraint: " + id);
            selected.put(id, constraint);
        }
        return selected;
    }

    private static ConstraintInfo compileAdHoc(Bpmn2OclValidationCompiler.Result compiled,
            String id, String ownerKind, String ownerId, String source) throws Exception {
        MClass orderClass = compiled.useModel().getClass("Order");
        Symtable vars = new Symtable();
        vars.add("self", orderClass, null);
        StringWriter sw = new StringWriter();
        var expr = OCLCompiler.compileExpression(compiled.useModel(), source, id, new PrintWriter(sw), vars);
        require(expr != null, "ad-hoc OCL should compile: " + sw);
        return new ConstraintInfo(id, ownerKind, ownerId, "Order", orderClass, expr);
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
