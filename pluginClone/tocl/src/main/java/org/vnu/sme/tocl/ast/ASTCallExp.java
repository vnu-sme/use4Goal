package org.vnu.sme.tocl.ast;

public class ASTCallExp extends ASTOclExpression {
    public ASTCallExp(ASTOclExpression s){
        super(null);
        src = s;
    }

    protected ASTOclExpression src;
}