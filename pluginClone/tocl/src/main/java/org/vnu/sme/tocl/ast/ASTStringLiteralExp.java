package org.vnu.sme.tocl.ast;

public class ASTStringLiteralExp extends ASTPrimitiveLiteralExp {
    public ASTStringLiteralExp(String v) {
        super("String");
        value = v;
    }

    String value;
}