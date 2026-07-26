package org.vnu.sme.goal.bpmn2.ocl;

import org.tzi.use.uml.ocl.value.Value;

/** Result of evaluating one compiled BPMN OCL clause against one runtime self object. */
public record Bpmn2OclEvaluationResult(
        String constraintId,
        String ownerKind,
        String ownerId,
        String contextType,
        String selfObjectName,
        Status status,
        Value value,
        String reason) {

    public enum Status {
        PASS,
        FAIL,
        ERROR
    }

    public boolean pass() {
        return status == Status.PASS;
    }
}
