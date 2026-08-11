grammar TOCL;

options{
    language = Java;
}

@header {
package org.vnu.sme.tocl.parser;
import org.vnu.sme.tocl.ast.*;
import java.util.ArrayList;
}

//OCL EXPRESSION PRODUCTION RULES----------------------------------------
/**

*/
expressionInOcl
        @init{
                Environment env = new Environment(null);
                env.addElement("self", null);
        }
        : o1=oclExpression[env] EOF
        | p=packageDeclaration[env] EOF;

oclExpression[Environment env] returns [ASTOclExpression ast]
        : //v=varExp[$env] { $ast = $v.ast; }
        lit=literalExp[$env] { $ast = $lit.ast; }
        | let=letExp[$env] { $ast = $let.ast; }
        //| oclMessageExp
        //| i=ifExp[$env] { $ast = $i.ast; }
        //| t=toclOperatorExpression[$env]
        | op=binaryOperationExp[$env] { $ast = $op.ast; }
        ;

varExp[Environment env] returns [ASTVariableExp ast]
        : name=simpleName {
                Object obj = $env.lookup($name.str);
                ASTVariableDeclaration dec;
                if (obj instanceof ASTVariableDeclaration) {
                        dec = (ASTVariableDeclaration) obj;
                        $ast = new ASTVariableExp(dec);
                }
        }
        | SELF;

simpleName returns [String str]
        : n1=SIMPLE_NAME { $str = $n1.text; }
        | n1=UNDERSCORE STRING_CHARS {$str = $n1.text;}
        ;

restrictedKeyword returns [String str]
        : cTypeIdent=collectionTypeIdentifier { $str = $cTypeIdent.str; }
        | primType=primitiveType { $str = $primType.ast.getName(); }
        | oType=oclType { $str = $oType.ast.getName(); }
        | t=TUPLE { $str = $t.text; }
        ;

unreservedSimpleName returns [String str]
        : name=simpleName { $str = $name.str; }
        | restrKwrd=restrictedKeyword { $str = $restrKwrd.str; }
        ;

pathName returns [ArrayList<String> path]
        @init { $path = new ArrayList<String>(); }
        : n=simpleName { $path.add($n.str); }
        | pathName SCOPE uSimpleName=unreservedSimpleName
        { 
                $path = new ArrayList<String>();
                $path.add($uSimpleName.str); 
        }
        ;

literalExp[Environment env] returns [ASTLiteralExp ast]
        : e=enumLiteralExp[$env] { $ast = $e.ast; }
        | colLit=collectionLiteralExp[$env] { $ast = $colLit.ast; }
        | tupleLit=tupleLiteralExp[$env] { $ast = $tupleLit.ast; }
        | primLit=primitiveLiteralExp[$env] { $ast = $primLit.ast; }
        //| typeLiteralExp
        ;

enumLiteralExp[Environment env] returns [ASTEnumLiteralExp ast]
        : HASH lit1=simpleName { $ast = new ASTEnumLiteralExp(null, $lit1.str); }
        | name=pathName SCOPE lit2=simpleName { $ast = new ASTEnumLiteralExp($name.path.get(0), $lit2.str); } ;

collectionLiteralExp[Environment env] returns [ASTCollectionLiteralExp ast]
        : typeIdent=collectionTypeIdentifier LBRACK parts=collectionLiteralParts[$env]? RBRACK
        { $ast = new ASTCollectionLiteralExp($typeIdent.str, $parts.ast); };

collectionTypeIdentifier returns [String str]
        : set=SET { $str = $set.text; }
        | b=BAG { $str = $b.text; }
        | s=SEQUENCE { $str = $s.text; }
        | c=COLLECTION { $str = $c.text; }
        | o=ORDERED_SET { $str = $o.text; }
        ;

collectionLiteralParts[Environment env] returns [ArrayList<ASTCollectionLiteralPart> ast]
        @init { $ast = new ArrayList<ASTCollectionLiteralPart>(); }
        : cPart=collectionLiteralPart[$env] (COMMA cPartArrayList=collectionLiteralParts[$env])?
        {
                if ($cPartArrayList.ast != null) {
                        $ast = $cPartArrayList.ast; 
                }
                $ast.add($cPart.ast);
        };

collectionLiteralPart[Environment env] returns [ASTCollectionLiteralPart ast]
        : r=collectionRange[$env] { $ast = $r.ast; }
        | oclExpression[$env] { $ast = null; }
        ;

collectionRange[Environment env] returns [ASTCollectionRange ast]
        : first=oclExpression[$env] DOUBLE_DOT last=oclExpression[$env]
        { $ast = new ASTCollectionRange($first.ast, $last.ast); };

primitiveLiteralExp[Environment env] returns [ASTPrimitiveLiteralExp ast]
        : i=integerLiteralExp { $ast = $i.ast; }
        | real=realLiteralExp { $ast = $real.ast; }
        | str=stringLiteralExp { $ast = $str.ast; }
        | b=booleanLiteralExp { $ast = $b.ast; }
        | uNatural=unlimitedNaturalLiteralExp { $ast = $uNatural.ast; }
        | n=nullLiteralExp { $ast = $n.ast; }
        | inv=invalidLiteralExp { $ast = $inv.ast; }
        ;

tupleLiteralExp[Environment env] returns [ASTTupleLiteralExp ast]
        : TUPLE LBRACK vDecArrayList=variableDeclarationList[$env] RBRACK
        { $ast = new ASTTupleLiteralExp($vDecArrayList.ast); };

unlimitedNaturalLiteralExp returns [ASTUnlimitedNaturalLiteralExp ast]
        : n=NATURAL_N { $ast = new ASTUnlimitedNaturalLiteralExp($n.text); }
        | ASTERISK { $ast = new ASTUnlimitedNaturalLiteralExp("-1"); }
        ;

integerLiteralExp returns [ASTIntegerLiteralExp ast]
        : i=INTEGER_N { $ast = new ASTIntegerLiteralExp($i.text); };

realLiteralExp returns [ASTRealLiteralExp ast]
        : r=REAL_N { $ast = new ASTRealLiteralExp($r.text); };

stringLiteralExp returns [ASTStringLiteralExp ast]
        : s=STRING_CHARS { $ast = new ASTStringLiteralExp($s.text); } ;

booleanLiteralExp returns [ASTBooleanLiteralExp ast]
        : TRUE { $ast = new ASTBooleanLiteralExp(true); }
        | FALSE { $ast = new ASTBooleanLiteralExp(false); }
        ;

//typeLiteralExp: ;

binaryOperationExp[Environment env] returns [ASTOclExpression ast]
        : or1=orExp[$env] { $ast = $or1.ast; }
                (op=IMPLIES or2=orExp[$env]
                { $ast = new ASTBinaryOperationExp($op.text, $or1.ast, $or2.ast); })*;

orExp[Environment env] returns [ASTOclExpression ast]
        : xor1=xorExp[$env] { $ast = $xor1.ast; }
                (op=OR xor2=xorExp[$env]
                { $ast = new ASTBinaryOperationExp($op.text, $xor1.ast, $xor2.ast); })*;

xorExp[Environment env] returns [ASTOclExpression ast]
        : and1=andExp[$env] { $ast = $and1.ast; }
                (op=XOR and2=andExp[$env]
                { $ast = new ASTBinaryOperationExp($op.text, $and1.ast, $and2.ast); })*;

andExp[Environment env] returns [ASTOclExpression ast]
        : eq1=equalityExp[$env] { $ast = $eq1.ast; }
                (op=AND eq2=equalityExp[$env]
                { $ast = new ASTBinaryOperationExp($op.text, $eq1.ast, $eq2.ast); })*;

equalityExp[Environment env] returns [ASTOclExpression ast]
        : rel1=relationalExp[$env] { $ast = $rel1.ast; }
                (op=(NOT_EQUAL | EQUALS) rel2=relationalExp[$env]
                { $ast = new ASTBinaryOperationExp($op.text, $rel1.ast, $rel2.ast); })*;

relationalExp[Environment env] returns [ASTOclExpression ast]
        : add1=additiveExp[$env] { $ast = $add1.ast; }
                (op=(GT | LT)(EQUALS)? add2=additiveExp[$env]
                { $ast = new ASTBinaryOperationExp($op.text, $add1.ast, $add2.ast); })*;

additiveExp[Environment env] returns [ASTOclExpression ast]
        : mul1=multiplicativeExp[$env] { $ast = $mul1.ast; }
                (op=(PLUS | MINUS) mul2=multiplicativeExp[$env]
                { $ast = new ASTBinaryOperationExp($op.text, $mul1.ast, $mul2.ast); })*;

multiplicativeExp[Environment env] returns [ASTOclExpression ast]
        : un1=unaryExp[$env] { $ast = $un1.ast; }
                (op=(ASTERISK | DIVIDED_BY | DIV) un2=unaryExp[$env]
                { $ast = new ASTBinaryOperationExp($op.text, $un1.ast, $un2.ast); })*;

unaryExp[Environment env] returns [ASTOclExpression ast]
        : (NOT |MINUS) un=unaryExp[$env]
        { $ast = $un.ast; }
        | p=postfixExp[$env]
        { $ast = $p.ast; };

postfixExp[Environment env] returns [ASTOclExpression ast]
        : pExp=primaryExp[$env] { $ast = $pExp.ast; }
                (op=(DOT | ARROW) cExp=callExp[$env, $ast]
                { $ast = $cExp.ast; })*;

primaryExp[Environment env] returns [ASTOclExpression ast]
        : literalExp[$env]
        | varExp[$env]
        | callExp[$env, null]
        | ifExp[$env]
        | toclOperatorExpression[$env]
        | LPAREN oclExpression[$env] RPAREN
        | events[$env] { $ast = $events.ast; }
        ;


//---------------- Events --------------
events[Environment env] returns [ASTEvent ast]
        : isCalledEvent[$env] { $ast = $isCalledEvent.ast; }
        | becomesTrueEvent[$env] { $ast = $becomesTrueEvent.ast; }
        ;

isCalledEvent[Environment env] returns [ASTEvent ast]
        : 'isCalled' LPAREN eventOp[$env] RPAREN bounds[$env]?
        { $ast = new ASTEvent(); }
        ;

bounds[Environment env]: quantif=('at least' | 'at most')? n=NATURAL_N 'times';

eventOp[Environment env]
        : (receiver=postfixExp[$env] DOT)? name=simpleName LPAREN parameters[$env]? RPAREN
        ;

becomesTrueEvent[Environment env] returns [ASTEvent ast]
        : 'becomesTrue' LPAREN e=binaryOperationExp[$env] RPAREN
        { $ast = new ASTEvent(); }
        ;
// ------------------------------

callExp[Environment env, ASTOclExpression src] returns [ASTCallExp ast]
        : fCallExp=featureCallExp[$env, $src] { $ast = $fCallExp.ast; }
        | lExp=loopExp[$env, $src] { $ast = $lExp.ast; }
        | toclOpCallExp[$env] { $ast = null; }
        ;

loopExp[Environment env, ASTOclExpression src] returns [ASTCallExp ast]
        : qExp=oclQueryExp[$env, $src] { $ast = $qExp.ast; }
        | itExp=iterateExp[$env, $src] { $ast = $itExp.ast; }
        ;

//iteratorExp
/**
    Example:
        select(x | x > 5)
        reject(x | x > 5)
        card->exists(c:DebitCard | c.pay@pre())
*/
oclQueryExp[Environment env, ASTOclExpression src] returns [ASTOclQueryExp ast]
        : qIdent=oclOperationIdentifier LPAREN (fDec=variableDeclaration[$env] (COMMA sDec=variableDeclaration[$env])? (BAR
        exp=oclExpression[$env])? | exp=oclExpression[$env]) RPAREN
        {
                if ($exp.ctx != null) {
                        if ($fDec.ctx != null) {
                                if ($sDec.ctx != null) {
                                        $ast = new ASTOclQueryExp($src, $qIdent.str, $fDec.ast, $sDec.ast, $exp.ast);
                                }
                                else {
                                        $ast = new ASTOclQueryExp($src, $qIdent.str, $fDec.ast, null, $exp.ast);
                                }
                        }
                        else {
                                $ast = new ASTOclQueryExp($src, $qIdent.str, null, null, $exp.ast);
                        }
                }
                else {
                        if ($fDec.ctx != null) {
                                if ($sDec.ctx != null) {
                                        $ast = new ASTOclQueryExp($src, $qIdent.str, $fDec.ast, $sDec.ast, null);
                                }
                                else {
                                        $ast = new ASTOclQueryExp($src, $qIdent.str, $fDec.ast, null, null);
                                }
                        }
                        else {
                                $ast = new ASTOclQueryExp($src, $qIdent.str, null, null, null);
                        }
                }
        }
        ;

oclOperationIdentifier returns [String str]
        : SELECT { $str = "select"; }
        | REJECT { $str = "reject"; }
        | COLLECT { $str = "collect"; }
        | EXISTS { $str = "exists"; }
        | FOR_ALL { $str = "forAll"; }
        | IS_UNIQUE { $str = "isUnique"; }
        | SORTED_BY { $str = "sortedby"; }
        | INCLUDES { $str = "includes"; }
        | EXCLUDES { $str = "excludes"; }
        | INCLUDES_ALL { $str = "includesAll"; }
        | EXCLUDES_ALL { $str = "excludesAll"; }
        ;

iterateExp[Environment env, ASTOclExpression src] returns [ASTIterateExp ast]
        : ITERATE LPAREN (fDec=variableDeclaration[$env] SEMI)? sDec=variableDeclaration[$env]
        BAR exp=oclExpression[$env] RPAREN
        { $ast = new ASTIterateExp($src, $fDec.ast, $sDec.ast, $exp.ast); };

variableDeclaration[Environment env] returns [ASTVariableDeclaration ast]
        : n=simpleName (COLON t=type[$env])? (EQUALS v=oclExpression[$env])?
        {
                if ($t.ctx != null) {
                        if ($v.ctx != null) {
                                $ast = new ASTVariableDeclaration($n.str, $t.ast, $v.ast); 
                        }
                        else {
                                $ast = new ASTVariableDeclaration($n.str, $t.ast, null);
                        }
                }
                else {
                        $ast = new ASTVariableDeclaration($n.str, null, null);
                }
        };

type[Environment env] returns [ASTType ast]
        : p=pathName { $ast = new ASTType($p.path.get($p.path.size() - 1)); }
        | cType=collectionType[$env] { $ast = $cType.ast; }
        | tType=tupleType[$env] { $ast = $tType.ast; }
        | pType=primitiveType { $ast = $pType.ast; }
        | oType=oclType { $ast = $oType.ast; }
        ;

primitiveType returns [ASTType ast]
        : b=BOOLEAN { $ast = new ASTType("Boolean"); }
        | i=INTEGER { $ast = new ASTType("Integer"); }
        | r=REAL { $ast = new ASTType("Real"); }
        | s=STRING { $ast = new ASTType("String"); }
        | u=UNLIMITED_NATURAL { $ast = new ASTType("UnlimitedNatural"); }
        ;

oclType returns [ASTType ast]
        : OCL_ANY { $ast = new ASTType("OclAny"); }
        | OCL_INVALID { $ast = new ASTType("OclInvalid"); }
        | OCL_MESSAGE { $ast = new ASTType("OclMessage"); }
        | OCL_VOID { $ast = new ASTType("OclVoid"); }
        ;

collectionType[Environment env] returns [ASTType ast]
        : cIdent=collectionTypeIdentifier LPAREN t=type[$env] RPAREN
        { $ast = new ASTCollectionType($cIdent.str, $t.ast); };

tupleType[Environment env] returns [ASTType ast]:
         TUPLE LPAREN varDecs=variableDeclarationList[$env] RPAREN
         { $ast = new ASTTupleType($varDecs.ast); };

variableDeclarationList[Environment env] returns [ArrayList<ASTVariableDeclaration> ast]
        @init { $ast = new ArrayList<ASTVariableDeclaration>(); }
        : vDec=variableDeclaration[$env] (COMMA vDecArrayList=variableDeclarationList[$env])?
        {
                if ($vDecArrayList.ctx != null) {
                        $ast = $vDecArrayList.ast; 
                }
                $ast.add($vDec.ast);
        };

//first production joins operationCallExp, porpertyCallExp, and navigationCallExp
featureCallExp[Environment env, ASTOclExpression src] returns [ASTCallExp ast]
        @init { Boolean isPre = false; }
        : n1=simpleName
        (
                (L_SQ_BRACK n2=simpleName R_SQ_BRACK)
                {
                        if ($n2.str == null) {
                                $ast = new ASTPropertyCallExp($src, $n1.str, false);
                        }
                        else {
                                $ast = new ASTAssociationClassCallExp($src, $n1.str, $n2.str);
                        }
                }
                | (p=isMarkedPre { isPre = true; })?
                {
                       $ast = new ASTPropertyCallExp($src, $n1.str, isPre);
                }
                | l=LPAREN a=arguments[$env]? RPAREN
                {
                        if ($a.ctx == null) {
                                $ast = new ASTOperationCallExp($src, $n1.str, null);
                        }
                        else {
                                $ast = new ASTOperationCallExp($src, $n1.str, $a.ast);
                        }
                }
        )
        | ident=oclTypeExpIdentifier LPAREN t=type[$env] RPAREN
        {
                ArrayList l = new ArrayList<ASTOclExpression>();
                l.add($t.ast);
                $ast = new ASTOperationCallExp($src, $ident.str, l);
        }
        | ALL_INSTANCES LPAREN RPAREN
        { $ast = new ASTOperationCallExp($src, "allInstances", null); }
        ;

oclTypeExpIdentifier returns [String str]
        : OCL_AS_TYPE { $str = "oclAsType"; }
        | OCL_IS_KIND_OF { $str = "oclIsKindOf"; }
        | OCL_IS_TYPE_OF { $str = "oclIsTypeOf"; }
        | SELECT_BY_KIND { $str = "selectByKind"; }
        | SELECT_BY_TYPE { $str = "selectByType"; }
        ;

/*navigationCallExp: propertyCallExp
                | associationClassCallExp
                ;*/

/*associationClassCallExp: oclExpression[$env]'.' simpleName ('[' arguments ']')? isMarkedPre?
                        | simpleName ('[' arguments ']')? isMarkedPre?
                        ;*/

isMarkedPre: AT_PRE;

arguments[Environment env] returns [ArrayList<ASTOclExpression> ast]
        @init { $ast = new ArrayList<ASTOclExpression>(); }
        : e=oclExpression[$env] (COMMA args=arguments[$env])?
        {
                if ($args.ctx != null) {
                        $ast = $args.ast;
                }
                $ast.add($e.ast);
        };

letExp[Environment env] returns [ASTLetExp ast]
        : LET vDec=variableDeclaration[$env] sub=letExpSub[$env.nestedEnvironment().addElement($vDec.ast.getVarName(), $vDec.ast)]
        { $ast = new ASTLetExp($vDec.ast, $sub.ast); };

letExpSub[Environment env] returns [ASTOclExpression ast]
        : COMMA vDec=variableDeclaration[$env]
        sub=letExpSub[$env.nestedEnvironment().addElement($vDec.ast.getVarName(), $vDec.ast)]
        { $ast = new ASTLetExp($vDec.ast, $sub.ast); }
        | IN e=oclExpression[$env] { $ast = $e.ast; }
        ;

/*oclMessageExp: oclExpression[$env]'^^' simpleName '(' oclMessageArguments? ')'
                | oclExpression[$env]'^' simpleName '(' oclMessageArguments? ')'
                ;

oclMessageArguments[Environment env]: oclMessageArg[$env] (COMMA oclMessageArguments[$env])?;

oclMessageArg[Environment env]: QUESTION_MARK (COLON type[$env])?
                | oclExpression[$env]
                ;
*/

ifExp[Environment env] returns [ASTIfExp ast]
        : IF e1=oclExpression[$env] THEN e2=oclExpression[$env] ELSE e3=oclExpression[$env] ENDIF
        { $ast = new ASTIfExp($e1.ast, $e2.ast, $e3.ast); };

nullLiteralExp returns [ASTNullLiteralExp ast]
        : NULL { $ast = new ASTNullLiteralExp(); };

invalidLiteralExp returns [ASTInvalidLiteralExp ast]
        : INVALID { $ast = new ASTInvalidLiteralExp(); };

//OCL CONSTRAINT PRODUCTION RULES--------------------------------------------
packageDeclaration[Environment env]: PACKAGE pathName contextDeclaration[$env]* ENDPACKAGE
                | contextDeclaration[$env]+
                ;

contextDeclaration[Environment env]: propertyContextDecl[$env]
                | classifierContextDecl[$env]
                | operationContextDecl[$env]
                ;

propertyContextDecl[Environment env]: CONTEXT pathName SCOPE simpleName COLON type[$env] initOrDerValue[$env];

initOrDerValue[Environment env]: (INIT | DERIVE) COLON oclExpression[$env] initOrDerValue[$env]?;

classifierContextDecl[Environment env]: CONTEXT (simpleName (COMMA simpleName)* COLON)? pathName invOrDef[$env];

invOrDef[Environment env]: INV (simpleName)? COLON oclExpression[$env] invOrDef[$env]?
        | (STATIC)? DEF (simpleName)? COLON defExpression[$env] invOrDef[$env]?
        ;

defExpression[Environment env]: (variableDeclaration[$env] | operation[$env]) EQUALS oclExpression[$env];

operationContextDecl[Environment env]: CONTEXT operation[$env] prePostOrBodyDecl[$env];

prePostOrBodyDecl[Environment env]: (PRE | POST | BODY) (simpleName)? COLON oclExpression[$env] prePostOrBodyDecl[$env]?;

operation[Environment env]: (pathName SCOPE)? simpleName LPAREN parameters[$env]? RPAREN COLON type[$env];

parameters[Environment env]: variableDeclaration[$env] (COMMA parameters[$env])?;

// TOCL EXPRESSION PRODUCTION RULES
toclOperatorExpression[Environment env]
:                 nextExp[$env]
                | alwaysExp[$env]
                | sometimeExp[$env]
                | previousExp[$env]
                | alwaysPastExp[$env]
                | sometimePastExp[$env]
                ;

// Rule 7. If e ∈ ExprBoolean then next e ∈ ExprBoolean and free(next e):= free(e).
nextExp[Environment env]: NEXT (e=binaryOperationExp[$env] | ev=events[$env])
    {
        if ($e.ctx != null && $e.ast != null && $e.ast.getType() != null) {
            if (!$e.ast.getType().equals("Boolean")) {
                System.out.println("Warning: 'Next' operator applied to non-boolean expression");
            }
        }
    }
    ;

//Rule 8 an
alwaysExp[Environment env]: ALWAYS e1=binaryOperationExp[$env] (op=(SINCE | UNTIL) e2=binaryOperationExp[$env])?
        {
                if ($e1.ctx != null && $e1.ast != null && $e1.ast.getType() != null) {
                        if (!$e1.ast.getType().equals("Boolean")) {
                                System.out.println("Warning: 'Always' operator applied to non-boolean expression");
                        }
                        if ($e2.ctx != null && $e2.ast != null && $e2.ast.getType() != null) {
                                if (!$e2.ast.getType().equals("Boolean")) {
                                        System.out.println("Warning: " + $op.text + " operator applied to non-boolean expression");
                                }
                        }
                } 
        };

//Rule 9
sometimeExp[Environment env]: SOMETIME e1=binaryOperationExp[$env] (op=(SINCE | BEFORE) e2=binaryOperationExp[$env])?
        {
                if ($e1.ctx != null && $e1.ast != null && $e1.ast.getType() != null) {
                        if (!$e1.ast.getType().equals("Boolean")) {
                                System.out.println("Warning: 'Always' operator applied to non-boolean expression");
                        }
                        if ($e2.ctx != null && $e2.ast != null && $e2.ast.getType() != null) {
                                if (!$e2.ast.getType().equals("Boolean")) {
                                        System.out.println("Warning: " + $op.text + " operator applied to non-boolean expression");
                                }
                        }
                } 
        };

previousExp[Environment env]: PREVIOUS e=binaryOperationExp[$env]
        {
                if ($e.ctx != null && $e.ast != null && $e.ast.getType() != null) {
                        if (!$e.ast.getType().equals("Boolean")) {
                                System.out.println("Warning: 'Previous' operator applied to non-boolean expression");
                        }
                } 
        };

alwaysPastExp[Environment env]: ALWAYS_PAST e=binaryOperationExp[$env]
        {
                if ($e.ctx != null && $e.ast != null && $e.ast.getType() != null) {
                        if (!$e.ast.getType().equals("Boolean")) {
                                System.out.println("Warning: 'Next' operator applied to non-boolean expression");
                        }
                } 
        };

sometimePastExp[Environment env]: SOMETIME_PAST e=binaryOperationExp[$env]
        {
                if ($e.ctx != null && $e.ast != null && $e.ast.getType() != null) {
                        if (!$e.ast.getType().equals("Boolean")) {
                                System.out.println("Warning: 'Next' operator applied to non-boolean expression");
                        }
                } 
        };

isMarkedNext: AT_NEXT;

/**
    Example:

*/
toclOpCallExp[Environment env]: simpleName (isMarkedPre | isMarkedNext) LPAREN arguments[$env]? RPAREN;


//OCL EXPRESSIONS LEXER TOKENS
//HEX: ([0-9] | [A-F] | [a-f])+;
NATURAL_N: '0' | [1-9] [0-9]*;
INTEGER_N: '-'? [1-9] [0-9]*;
REAL_N: INTEGER_N '.' [0-9]+;
PLUS: '+';
MINUS: '-';
ASTERISK: '*';
DIVIDED_BY: '/';
SELF: 'self';
UNDERSCORE: '_';
SINGLE_QUOTE: '\'';
TUPLE: 'Tuple';
SCOPE: '::';
HASH: '#';
LBRACK: '{';
RBRACK: '}';
SET: 'Set';
BAG: 'Bag';
SEQUENCE: 'Sequence';
COLLECTION: 'Collection';
ORDERED_SET: 'OrderedSet';
COMMA: ',';
DOT: '.';
DOUBLE_DOT: '..';
TRUE: 'true';
FALSE: 'false';
BAR: '|';
ARROW: '->';
SELECT: 'select';
REJECT: 'reject';
COLLECT: 'collect';
EXISTS: 'exists';
FOR_ALL: 'forAll';
IS_UNIQUE: 'isUnique';
SORTED_BY: 'sortedBy';
INCLUDES: 'includes';
EXCLUDES: 'excludes';
INCLUDES_ALL: 'includesAll';
EXCLUDES_ALL: 'excludesAll';
DIV: 'div';
L_SQ_BRACK: '[';
R_SQ_BRACK: ']';
ITERATE: 'iterate';
COLON: ':';
EQUALS: '=';
NOT_EQUAL: '<>';
BOOLEAN: 'Boolean';
INTEGER: 'Integer';
REAL: 'Real';
STRING: 'String';
UNLIMITED_NATURAL: 'UnlimitedNatural';
OCL_ANY: 'OclAny';
OCL_INVALID: 'OclInvalid';
OCL_MESSAGE: 'OclMessage';
OCL_VOID: 'OclVoid';
LPAREN: '(';
RPAREN: ')';
IMPLIES: 'implies';
OR: 'or';
XOR: 'xor';
AND: 'and';
NOT: 'not';
GT: '>';
LT: '<';
OCL_AS_TYPE: 'oclAsType';
OCL_IS_KIND_OF: 'oclIsKindOf';
OCL_IS_TYPE_OF: 'oclIsTypeOf';
SELECT_BY_KIND: 'selectByKind';
SELECT_BY_TYPE: 'selectByType';
ALL_INSTANCES: 'allInstances';
AT_PRE: '@pre';
LET: 'let';
IN: 'in';
SEMI: ';';
QUESTION_MARK: '?';
IF: 'if';
THEN: 'then';
ELSE: 'else';
ENDIF: 'endif';
INVALID: 'invalid';
NULL: 'null';

//OCL CONSTRAINTS LEXER TOKENS
PACKAGE: 'package';
ENDPACKAGE: 'endpackage';
CONTEXT: 'context';
INIT: 'init';
DERIVE: 'derive';
INV: 'inv';
STATIC: 'static';
DEF: 'def';
PRE: 'pre';
POST: 'post';
BODY: 'body';

//TOCL EXPRESSIONS LEXER TOKENS
NEXT: 'next';
ALWAYS: 'always';
SOMETIME: 'sometime';
UNTIL: 'until';
BEFORE: 'before';
PREVIOUS: 'previous';
ALWAYS_PAST: 'alwaysPast';
SOMETIME_PAST: 'sometimePast';
SINCE: 'since';
AT_NEXT: '@next';

SIMPLE_NAME: [A-Z_$a-z] [A-Z_$a-z0-9]*;

WhiteSpaceChar: (' ' | '\t' | '\n' | '\r' | '\n\r') -> channel(HIDDEN);

SL_COMMENT:
    ('--')(~('\n'))* -> channel(HIDDEN);

// multiple-line comments
ML_COMMENT:
    '/*' (.)*? '*/' ->channel(HIDDEN);

Char: [A-Z] | UNDERSCORE | '$' | [a-z] | MINUS | [0-9];

EscapeSequence: '\\' 'b'
                | '\\' 't'
                | '\\' 'n'
                | '\\' 'f'
                | '\\' 'r'
                | '\\' '"'
                | '\\' '\''
                | '\\' '\\'
                | '\\' 'x'
                | '\\' 'u'
                ;
STRING_CHARS: '\''(Char | WhiteSpaceChar | EscapeSequence)+ '\'';
