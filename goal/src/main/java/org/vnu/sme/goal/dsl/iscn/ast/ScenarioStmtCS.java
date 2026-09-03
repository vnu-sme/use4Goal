package org.vnu.sme.goal.dsl.iscn.ast;

/** One statement inside a scenario body: fires/assigns an element's state, or asserts a cross-instance property. */
public sealed interface ScenarioStmtCS permits ScenarioStmtCS.FireCS, ScenarioStmtCS.AssignCS, ScenarioStmtCS.AggregateCS {

    /** {@code fire target;} — state as derived result (P_leaf + saturation). */
    record FireCS(QualifiedIdCS target, String objectInstanceId) implements ScenarioStmtCS {}

    /** {@code assign target = statusValue;} — state as direct input, then saturation. */
    record AssignCS(QualifiedIdCS target, String statusValue) implements ScenarioStmtCS {}

    /** {@code aggregate label : all|any [of actorType] over elementId;} — {@code actorType} is null when omitted. */
    record AggregateCS(String label, String mode, String actorType, String elementId) implements ScenarioStmtCS {}
}
