package org.vnu.sme.goal.bpmn2.ast;

/** Raw OCL condition attached to a BPMN activity in concrete syntax. */
public record ActivityConstraintCS(Kind kind, String oclBody) {
    public enum Kind {
        PRE,
        POST
    }
}
