package org.vnu.sme.goal.istar.ast;

/** Raw pre/post OCL attached to an iStar Goal or Task. */
public record IStarOclConstraintCS(Kind kind, String oclBody) {
    public enum Kind { PRE, POST }
}
