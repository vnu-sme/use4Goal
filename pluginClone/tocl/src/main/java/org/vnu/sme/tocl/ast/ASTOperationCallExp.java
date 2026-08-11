package org.vnu.sme.tocl.ast;

import java.util.List;

public class ASTOperationCallExp extends ASTCallExp {
    public ASTOperationCallExp(ASTOclExpression src, String n, List<ASTOclExpression> a) {
            super(src);
            operationName = n;
            args = a;
    }

    String operationName;
    List<ASTOclExpression> args;
}