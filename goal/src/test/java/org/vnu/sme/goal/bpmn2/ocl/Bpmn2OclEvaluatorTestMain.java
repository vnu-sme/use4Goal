package org.vnu.sme.goal.bpmn2.ocl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.tzi.use.parser.Symtable;
import org.tzi.use.parser.ocl.OCLCompiler;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.ocl.value.BooleanValue;
import org.tzi.use.uml.ocl.value.EnumValue;
import org.tzi.use.uml.ocl.value.IntegerValue;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MSystem;
import org.vnu.sme.goal.bpmn2.ocl.Bpmn2OclConstraintCompiler.ConstraintInfo;
import org.vnu.sme.goal.bpmn2.ocl.Bpmn2OclEvaluationResult.Status;

/** Plain Java unit test harness for {@link Bpmn2OclEvaluator}. */
public final class Bpmn2OclEvaluatorTestMain {

    private Bpmn2OclEvaluatorTestMain() {}

    public static void main(String[] args) throws Exception {
        Path base = Path.of("goal/src/main/resources/examples/bpmn_ocl/audit");
        Bpmn2OclValidationCompiler.Result compiled = Bpmn2OclValidationCompiler.compile(
                base.resolve("order_process_valid.bpmn2"),
                base.resolve("order_domain.use"),
                base.resolve("order_context.bpmn2oclmap"));
        require(compiled.ok(), "expected BPMN OCL audit inputs to compile: " + compiled.errors());

        MClass orderClass = compiled.useModel().getClass("Order");
        MSystem system = new MSystem(compiled.useModel());
        MObject order = system.state().createObject(orderClass, "order1");
        order.state(system.state()).setAttributeValue(orderClass.attribute("created", true), BooleanValue.TRUE);
        order.state(system.state()).setAttributeValue(orderClass.attribute("reviewed", true), BooleanValue.TRUE);
        order.state(system.state()).setAttributeValue(orderClass.attribute("approved", true), BooleanValue.TRUE);
        order.state(system.state()).setAttributeValue(orderClass.attribute("rejected", true), BooleanValue.FALSE);
        order.state(system.state()).setAttributeValue(orderClass.attribute("priority", true), BooleanValue.TRUE);
        order.state(system.state()).setAttributeValue(orderClass.attribute("amount", true), IntegerValue.valueOf(100));
        order.state(system.state()).setAttributeValue(orderClass.attribute("status", true),
                new EnumValue(compiled.useModel().enumType("OrderStatus"), "approved"));

        ConstraintInfo approve = compiled.constraints().get("approve_order");
        var approveResult = Bpmn2OclEvaluator.evaluate(approve, system.state(), order);
        require(approveResult.status() == Status.PASS, "approve_order should pass: " + approveResult);

        ConstraintInfo reject = compiled.constraints().get("reject_order");
        var rejectResult = Bpmn2OclEvaluator.evaluate(reject, system.state(), order);
        require(rejectResult.status() == Status.FAIL, "reject_order should fail: " + rejectResult);

        var missingSelf = Bpmn2OclEvaluator.evaluate(approve, system.state(), null);
        require(missingSelf.status() == Status.ERROR, "missing self should be ERROR: " + missingSelf);
        require(missingSelf.reason().contains("missing self binding"), "missing self reason should be explicit");

        var nullConstraint = Bpmn2OclEvaluator.evaluate(null, system.state(), order);
        require(nullConstraint.status() == Status.ERROR, "null constraint should be ERROR: " + nullConstraint);

        var nullState = Bpmn2OclEvaluator.evaluate(approve, null, order);
        require(nullState.status() == Status.ERROR, "null MSystemState should be ERROR: " + nullState);

        ConstraintInfo nonBoolean = compileAdHoc(compiled, "amount_value", "node", "amount_value", "self.amount");
        var nonBooleanResult = Bpmn2OclEvaluator.evaluate(nonBoolean, system.state(), order);
        require(nonBooleanResult.status() == Status.ERROR, "non-Boolean expression should be ERROR: " + nonBooleanResult);
        require(nonBooleanResult.reason().contains("Boolean"), "non-Boolean reason should mention Boolean");

        Map<String, MObject> bindings = new LinkedHashMap<>();
        compiled.constraints().keySet().forEach(id -> bindings.put(id, order));
        List<Bpmn2OclEvaluationResult> all = Bpmn2OclEvaluator.evaluateAll(
                compiled.constraints(), system.state(), bindings);
        require(all.size() == compiled.constraints().size(), "evaluateAll should return one result per constraint");
        require(all.stream().anyMatch(r -> r.constraintId().equals("approve_order") && r.status() == Status.PASS),
                "evaluateAll should include approve_order PASS");
        require(all.stream().anyMatch(r -> r.constraintId().equals("reject_order") && r.status() == Status.FAIL),
                "evaluateAll should include reject_order FAIL");

        List<Bpmn2OclEvaluationResult> nullBindings = Bpmn2OclEvaluator.evaluateAll(
                compiled.constraints(), system.state(), null);
        require(nullBindings.stream().allMatch(r -> r.status() == Status.ERROR),
                "null selfBindings should produce ERROR results");

        List<Bpmn2OclEvaluationResult> nullConstraints = Bpmn2OclEvaluator.evaluateAll(
                null, system.state(), bindings);
        require(nullConstraints.size() == 1 && nullConstraints.get(0).status() == Status.ERROR,
                "null constraints map should produce one ERROR result");

        System.out.println("Bpmn2OclEvaluatorTestMain OK");
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
