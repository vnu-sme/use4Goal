package org.vnu.sme.tocl.ast;

public class ASTPropertyCallExp extends ASTCallExp{
    public ASTPropertyCallExp(ASTOclExpression src, String n, Boolean p) {
            super(src);
            attributeName = n;
            isPre = p;
    }

    String attributeName;
    Boolean isPre;
}