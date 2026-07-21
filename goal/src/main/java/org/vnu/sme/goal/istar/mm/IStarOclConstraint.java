package org.vnu.sme.goal.istar.mm;

/** A state contract attached to an iStar Goal or Task. */
public record IStarOclConstraint(Kind kind, String oclBody) {
    public enum Kind { PRE, POST }
}
