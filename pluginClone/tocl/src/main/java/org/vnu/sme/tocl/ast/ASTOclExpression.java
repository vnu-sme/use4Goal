package org.vnu.sme.tocl.ast;

public class ASTOclExpression {
    public ASTOclExpression(String t) {
        type = t;
    }
    
    public String getType() {
        return type;
    }

    String type;
}