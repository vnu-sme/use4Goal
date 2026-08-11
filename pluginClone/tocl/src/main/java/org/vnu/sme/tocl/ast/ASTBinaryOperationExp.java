package org.vnu.sme.tocl.ast;

public class ASTBinaryOperationExp extends ASTOclExpression {
    public ASTBinaryOperationExp(String op, ASTOclExpression l, ASTOclExpression r) {
        super("Boolean");
        operation = op;
        left = l;
        right = r;
    }

    ASTOclExpression left;
    ASTOclExpression right;
    String operation;
}