package org.vnu.sme.goal.dsl.bpmn.mm;

/** Raw OCL condition attached to a BPMN activity. */
public record ActivityConstraint(Kind kind, String oclBody) {
    public enum Kind {
        PRE,
        POST
    }
}
