package org.vnu.sme.tocl.ast;

public class ASTUnlimitedNaturalLiteralExp extends ASTPrimitiveLiteralExp {
    public ASTUnlimitedNaturalLiteralExp(String v) {
        super("UnlimitedNatural");
        value = Integer.parseInt(v);
    }

    Integer value;
}