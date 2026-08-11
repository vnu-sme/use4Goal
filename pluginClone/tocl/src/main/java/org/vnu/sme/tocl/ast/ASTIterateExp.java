package org.vnu.sme.tocl.ast;

import java.util.List;

public class ASTIterateExp extends ASTCallExp {
    public ASTIterateExp(ASTOclExpression src, ASTVariableDeclaration dec1, ASTVariableDeclaration dec2, ASTOclExpression b) {
        super(src);
        kind = "iterate";
        if (dec1 == null) {
            dec1 = new ASTVariableDeclaration("ii", new ASTType("None"), null);
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
    List<ASTVariableDeclaration> varDeclarations;
    ASTOclExpression body;
}