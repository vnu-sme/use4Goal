package org.vnu.sme.tocl.ast;

public class ASTInvalidLiteralExp extends ASTPrimitiveLiteralExp {
    public ASTInvalidLiteralExp () {
        super("OclInvalid");
    }

    String value = "invalid";
}