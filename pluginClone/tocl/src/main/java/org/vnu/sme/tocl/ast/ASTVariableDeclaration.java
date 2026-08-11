package org.vnu.sme.tocl.ast;

public class ASTVariableDeclaration{
    public ASTVariableDeclaration(String n, ASTType t, ASTOclExpression e){
        varName = n;
        type = t;
        initExpr = e;
    }

    public String getVarName(){
        return varName;
    }

    public ASTType getType() {
        return type;
    }

    String varName;
    ASTType type;
    ASTOclExpression initExpr;
}