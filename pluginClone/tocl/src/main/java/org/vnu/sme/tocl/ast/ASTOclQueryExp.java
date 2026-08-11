package org.vnu.sme.tocl.ast;

import java.util.ArrayList;

public class ASTOclQueryExp extends ASTCallExp {
    public ASTOclQueryExp(ASTOclExpression src, String k, ASTVariableDeclaration dec1, ASTVariableDeclaration dec2, ASTOclExpression b) {
        super(src);
        kind = k;
        if (dec1 == null) {
            dec1 = new ASTVariableDeclaration(null, new ASTType("None"), null);
        }
        else {
            varDeclarations.add(dec1);
        }
        if (dec2 != null) {
            varDeclarations.add(dec2);
        }
        body = b;
    }

    String kind;
    ArrayList<ASTVariableDeclaration> varDeclarations = new ArrayList<ASTVariableDeclaration>();
    ASTOclExpression body;
}