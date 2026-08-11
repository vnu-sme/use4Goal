package org.vnu.sme.tocl.ast;

public class ASTRealLiteralExp extends ASTPrimitiveLiteralExp {
    public ASTRealLiteralExp(String v) {
        super("Real");
        value =  Float.parseFloat(v);
    }

    Float value;
}