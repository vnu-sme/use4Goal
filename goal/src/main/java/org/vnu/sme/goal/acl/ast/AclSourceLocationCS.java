package org.vnu.sme.goal.acl.ast;

public record AclSourceLocationCS(int line, int column) {
    public AclSourceLocationCS {
        if (line < 1) throw new IllegalArgumentException("line must be positive");
        if (column < 0) throw new IllegalArgumentException("column must be non-negative");
    }
}
