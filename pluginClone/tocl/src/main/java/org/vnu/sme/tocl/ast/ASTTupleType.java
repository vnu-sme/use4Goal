package org.vnu.sme.tocl.ast;

import java.util.List;

public class ASTTupleType extends ASTType{
    public ASTTupleType(List<ASTVariableDeclaration> pList) {
        //pass "Tuple(" + p.name for p in pList ")"
        super(null);
        partsList = pList;
        //check init expression is null and type is NOT null
    }

    List<ASTVariableDeclaration> partsList;
}