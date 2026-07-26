package org.vnu.sme.goal.bpmn2.ocl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.tzi.use.uml.ocl.expr.EvalContext;
import org.tzi.use.uml.ocl.expr.SimpleEvalContext;
import org.tzi.use.uml.ocl.value.BooleanValue;
import org.tzi.use.uml.ocl.value.Value;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MSystemState;
import org.vnu.sme.goal.bpmn2.ocl.Bpmn2OclConstraintCompiler.ConstraintInfo;
import org.vnu.sme.goal.bpmn2.ocl.Bpmn2OclEvaluationResult.Status;

/**
 * Evaluates already-compiled BPMN OCL clauses against a provided USE runtime state.
 *
 * <p>This class deliberately does not execute BPMN. Callers provide the state and the
 * per-constraint {@code self} bindings, typically from a scenario/trace layer.
 */
public final class Bpmn2OclEvaluator {

    private Bpmn2OclEvaluator() {}

    public static List<Bpmn2OclEvaluationResult> evaluateAll(
            Map<String, ConstraintInfo> constraints,
            MSystemState state,
            Map<String, MObject> selfBindings) {
        if (constraints == null) {
            return List.of(error(null, null, "constraints map is null"));
        }
        List<Bpmn2OclEvaluationResult> results = new ArrayList<>();
        for (ConstraintInfo constraint : constraints.values()) {
            MObject self = null;
            if (constraint != null && selfBindings != null) {
                self = selfBindings.get(constraint.constraintId());
            }
            results.add(evaluate(constraint, state, self));
        }
        return List.copyOf(results);
    }

    public static Bpmn2OclEvaluationResult evaluate(
            ConstraintInfo constraint,
            MSystemState state,
            MObject self) {
        if (constraint == null) {
            return error(null, self == null ? null : self.name(), "constraint is null");
        }
        if (state == null) {
            return error(constraint, self == null ? null : self.name(),
                    "MSystemState is null for BPMN OCL '" + constraint.constraintId() + "'");
        }
        if (self == null) {
            return error(constraint, null, "missing self binding for BPMN OCL '" + constraint.constraintId() + "'");
        }
        if (!self.cls().conformsTo(constraint.contextClass())) {
            return error(constraint, self.name(), "BPMN OCL '" + constraint.constraintId()
                    + "' expects self : " + constraint.contextType()
                    + ", but object '" + self.name() + "' has type " + self.cls().name());
        }

        try {
            EvalContext ctx = new SimpleEvalContext(state, state, state.system().varBindings());
            ctx.pushVarBinding("self", self.value());
            Value value = constraint.expr().eval(ctx);
            if (value == null || value.isUndefined()) {
                return new Bpmn2OclEvaluationResult(constraint.constraintId(), constraint.ownerKind(),
                        constraint.ownerId(), constraint.contextType(), self.name(), Status.ERROR, value,
                        "expression evaluated to undefined");
            }
            if (!(value instanceof BooleanValue booleanValue)) {
                return new Bpmn2OclEvaluationResult(constraint.constraintId(), constraint.ownerKind(),
                        constraint.ownerId(), constraint.contextType(), self.name(), Status.ERROR, value,
                        "expression did not evaluate to Boolean: " + value.type());
            }
            Status status = booleanValue.isTrue() ? Status.PASS : Status.FAIL;
            String reason = booleanValue.isTrue() ? "expression evaluated to true" : "expression evaluated to false";
            return new Bpmn2OclEvaluationResult(constraint.constraintId(), constraint.ownerKind(),
                    constraint.ownerId(), constraint.contextType(), self.name(), status, value, reason);
        } catch (RuntimeException ex) {
            return error(constraint, self.name(), "runtime evaluation error: " + ex.getMessage());
        }
    }

    private static Bpmn2OclEvaluationResult error(ConstraintInfo constraint, String selfObjectName, String reason) {
        return new Bpmn2OclEvaluationResult(
                constraint == null ? null : constraint.constraintId(),
                constraint == null ? null : constraint.ownerKind(),
                constraint == null ? null : constraint.ownerId(),
                constraint == null ? null : constraint.contextType(),
                selfObjectName, Status.ERROR, null, reason);
    }
}
