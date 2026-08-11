package org.vnu.sme.tocl.ast;

public class ASTIfExp extends ASTOclExpression{
    public ASTIfExp(ASTOclExpression c, ASTOclExpression t, ASTOclExpression e) {
        super((t != null) ? t.getType() : null);
        if (c != null && c.getType() != null && !c.getType().equals("Boolean")) {
            System.out.println("Warning: IfExp condition not of type \'Boolean\'");
        }
        if (t != null && e != null && t.getType() != null && e.getType() != null && !t.getType().equals(e.getType())) {
            System.out.println("Warning: IfExp returns two different types");
        }
        condition = c;
        thenExp = t;
        elseExp = e;
    }

    ASTOclExpression condition;
    ASTOclExpression thenExp;
    ASTOclExpression elseExp;
}