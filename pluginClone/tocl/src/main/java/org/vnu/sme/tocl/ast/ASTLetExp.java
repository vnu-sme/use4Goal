package org.vnu.sme.tocl.ast;

public class ASTLetExp extends ASTOclExpression {
    public ASTLetExp(ASTVariableDeclaration v, ASTOclExpression i) {
        super(i.getType());
        variable = v;
        in = i;
    }

    ASTVariableDeclaration variable;
    ASTOclExpression in;
}