package org.vnu.sme.tocl.ast;

public class ASTBooleanLiteralExp extends ASTPrimitiveLiteralExp {
    public ASTBooleanLiteralExp(Boolean v) {
        super("Boolean");
        value =  v;
    }

    Boolean value;
}