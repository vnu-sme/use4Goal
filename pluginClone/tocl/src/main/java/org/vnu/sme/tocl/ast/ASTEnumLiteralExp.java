package org.vnu.sme.tocl.ast;

public class ASTEnumLiteralExp extends ASTLiteralExp {
    public ASTEnumLiteralExp(String t, String l) {
            super("Enumeration");
            type = t;
            literal = l;
    }

    String type;
    String literal;
}