grammar TOCL;

options {
    superClass = BaseParser;
}

@header {
package org.vnu.sme.tocl.parser;

import org.tzi.use.parser.base.BaseParser;
import org.tzi.use.parser.ParseErrorHandler;
//import org.vnu.sme.tocl.ast.*;
import org.tzi.use.parser.ocl.*;
import org.tzi.use.parser.use.*;
import org.tzi.use.parser.use.statemachines.*;
import java.util.Set;
import java.util.Collections;
import java.util.HashSet;
}

@lexer::members {
    private ParseErrorHandler fParseErrorHandler;

    public String getFilename() {
        return fParseErrorHandler.getFileName();
    }

    @Override
    public String getErrorHeader(RecognitionException e) {
        return "line " + e.getLine() + ":" + e.getCharPositionInLine();
    }

    public void emitErrorMessage(String msg) {
           fParseErrorHandler.reportError(msg);
    }

    public void init(ParseErrorHandler handler) {
        fParseErrorHandler = handler;
    }
}

constraints
:
    (invariant | prePost)*
    EOF
    ;

invariant returns [ASTConstraintDefinition n]
:
    { $n = new ASTConstraintDefinition(); }
    'context'
    (
        v=IDENT { $n.addVarName($v); }
        (',' v=IDENT { $n.addVarName($v); } )*
        COLON
    )?
    t=simpleType { $n.setType($t.n); }
    (
        inv=invariantClause { $n.addInvariantClause($inv.n); }
        | temp=temporalClause
    )*
    ;

/* ----------------Temporal OCL starts---------------------*/

/* ------------------------
    temporalClause ::= "temp" [ id ] ":" temporalSpec
*/
temporalClause
:
    'temp' ( name=IDENT )? COLON ts=temporalSpec
    ;

/* ------------------------
    temporalSpec ::= quantif? pattern scope
*/
temporalSpec
:
    q=quantif? p=pattern s=scope
    ;

/* ------------------------
    quantif ::= 'let' variableDeclaration (',' variableDeclaration)* 'in'
*/
quantif
:
    'let' v=variableDeclaration ( COMMA v=variableDeclaration )* 'in'
    ;

/* ------------------------
    pattern ::= 'always' expression
            | 'never' event
            | 'eventually' event (('at least' | 'at most')? INT 'times')?
            | eventChain 'precedes' ('directly' | 'strictly')? eventChain
            | eventChain 'responds' ('directly' | 'strictly')? eventChain
*/
pattern
:
    'always' e=expression
    | 'never' ev=event
    | 'eventually' ev=event ( ('at least' | 'at most')? INT 'times' )?
    | ec=eventChain 'precedes' ( 'directly' | 'strictly' )? ec=eventChain
    | ec=eventChain 'responds' ( 'directly' | 'strictly' )? ec=eventChain
    ;

/* ------------------------
    scope ::= 'globally'
            | 'before' event ('[' | ']')?
            | 'after' ('[' | ']')? event
            | 'between' ('[' | ']')? 'last'? event 'and' event ('[' | ']')?
            | 'after' ('[' | ']')? 'last'? event 'until' event ('[' | ']')?
            | 'when' expression
*/
scope
:
    'globally'
    | 'before' ev=event ( LBRACK | RBRACK )?
    | 'after' ( LBRACK | RBRACK )? ev=event
    | 'between' ( LBRACK | RBRACK )? 'last'? ev=event 'and' ev=event ( LBRACK | RBRACK )?
    | 'after' ( LBRACK | RBRACK )? 'last'? ev=event 'until' ev=event ( LBRACK | RBRACK )?
    | 'when' e=expression
    ;

/* ------------------------
    event ::= callEvent ('|' event)?
            | changeEvent ('|' event)?
    *The '|' is used to support Disjunction operator.
*/
event
:
    ce=callEvent ( BAR ev=event )?
    | che=changeEvent ( BAR ev=event )?
    ;

/* ------------------------
    eventChain ::= event (',' event)*
                | event (';' event)*
*/
eventChain
:
    ev=event ( COMMA ev=event )*
    | ev=event ( SEMI ev=event )*
    ;

/* ------------------------
    callEvent ::= 'isCalled' '(' ('anyOp' | 'op' ':' operationExpression)
                                (',' 'pre' ':' expression)?
                                (',' 'post' ':' expression)? ')'
*/
callEvent
:
    'isCalled' LPAREN ( 'anyOp' | 'op' COLON op=operation )
    ( COMMA 'pre' COLON e=expression )?
    ( COMMA 'post' COLON e=expression )? RPAREN
    ;

/* ------------------------
    changeEvent ::= 'becomesTrue' '(' expression ')'
*/
changeEvent
:
    'becomesTrue' LPAREN e=expression RPAREN
    ;

/* ------------------------
    operation ::= IDENT paramList
*/
operation
:
    name=IDENT pl=paramList
    ;


/* ----------------Temporal OCL ends---------------------*/

invariantClause returns [ASTInvariantClause n]
:
    as=annotationSet
    (
    'inv' ( name=IDENT )? COLON e=expression
    { $n = new ASTInvariantClause($name, $e.n); $n.setAnnotations( $as.annotations ); }
    | 'existential' 'inv' ( name=IDENT )? COLON e=expression
    { $n = new ASTExistentialInvariantClause($name, $e.n); $n.setAnnotations( $as.annotations ); }
    )
    ;


prePost returns [ASTPrePost n]
:
    'context' classname=IDENT COLON_COLON opname=IDENT pl=paramList ( COLON rt=type )?
    { $n = new ASTPrePost($classname, $opname, $pl.nparamList, $rt.n); }
    ( ppc=prePostClause { $n.addPrePostClause(ppc); } )+
    ;


prePostClause returns [ASTPrePostClause n]
@init { Token t = null; }
:
    as = annotationSet
    { t = input.LT(1); }
    ( 'pre' | 'post' )  ( name=IDENT )? COLON e=expression
    { $n = new ASTPrePostClause(t, $name, $e.n); $n.setAnnotations($as.annotations); }
    ;


annotationSet returns [Set<ASTAnnotation> annotations]
@init { $annotations = new HashSet<ASTAnnotation>(); }
:
    ( an=annotation { $annotations.add($an.n); } )*
    ;


annotation returns [ASTAnnotation n]
:
    AT name=IDENT { $n = new ASTAnnotation($name); }
    LPAREN
    values=annotationValues { $n.setValues($values.annoValues); }
    RPAREN
;


annotationValues returns [Map<Token, Token> annoValues]
@init{ $annoValues = new HashMap<Token, Token>(); }
:
    ( firstVal=annotationValue { $annoValues.put($firstVal.name, $firstVal.value); } )?
    ( COMMA val=annotationValue { $annoValues.put($val.name, $val.value); } )*
    ;


annotationValue returns [Token name, Token value]
:
    aName=IDENT { $name = $aName; }
    EQUAL
    aValue=NON_OCL_STRING { $value = $aValue; }
    ;


expressionOnly returns [ASTExpression n]
:
    nExp=expression EOF { $n = $nExp.n; }
    ;


expression returns [ASTExpression n]
@init{
    ASTLetExpression prevLet = null, firstLet = null;
    ASTExpression e2;
    Token tok = null;
}
:
    { tok = input.LT(1); /* remember start of expression */ }
    (
        'let'
        name=IDENT ( COLON t=type )? EQUAL e1=expression
        { ASTLetExpression nextLet = new ASTLetExpression($name, $t.n, $e1.n);
            if ( firstLet == null )
                firstLet = nextLet;
            if ( prevLet != null )
                prevLet.setInExpr(nextLet);
            prevLet = nextLet;
        }

        (
            COMMA
            name=IDENT ( COLON t=type )? EQUAL e1=expression
            { ASTLetExpression nextLet = new ASTLetExpression($name, $t.n, $e1.n);
                if ( firstLet == null )
                    firstLet = nextLet;
                if ( prevLet != null )
                    prevLet.setInExpr(nextLet);
                prevLet = nextLet;
            }
        )*
        'in'
    )*
    nCndImplies=conditionalImpliesExpression
    {
        if ( $nCndImplies.n != null ) {
            $n = $nCndImplies.n;
            $n.setStartToken(tok);
        }
        if ( prevLet != null ) {
            prevLet.setInExpr($n);
            $n = firstLet;
            $n.setStartToken(tok);
        }
    }
    ;


paramList returns [List<ASTVariableDeclaration> nparamList]
@init { $nparamList = new ArrayList<ASTVariableDeclaration>(); }
:
    LPAREN
    (
      v=variableDeclaration { nparamList.add($v.n); }
      ( COMMA v=variableDeclaration { nparamList.add($v.n); } )*
    )?
    RPAREN
    ;


idList1 returns [List idList]
@init { $idList = new ArrayList(); }
:
    id0=IDENT { $idList.add($id0); }
    ( COMMA idn=IDENT { $idList.add($idn); } )*
    ;


variableDeclaration returns [ASTVariableDeclaration n]
:
    name=IDENT COLON t=type
    { n = new ASTVariableDeclaration($name, $t.n); }
    ;


conditionalImpliesExpression returns [ASTExpression n]
:
    nCndOrExp=conditionalOrExpression { $n = $nCndOrExp.n; }
    (
        op='implies' n1=conditionalOrExpression
        { $n = new ASTBinaryExpression($op, $n, $n1.n); }
    )*
    ;


conditionalOrExpression returns [ASTExpression n]
:
    nCndXorExp=conditionalXOrExpression { $n = $nCndXorExp.n; }
    (
        op='or' n1=conditionalXOrExpression
        { $n = new ASTBinaryExpression($op, $n, $n1.n); }
    )*
    ;


conditionalXOrExpression returns [ASTExpression n]
:
    nCndAndExp=conditionalAndExpression { $n = $nCndAndExp.n; }
    (
        op='xor' n1=conditionalAndExpression
        { $n = new ASTBinaryExpression($op, $n, $n1.n); }
    )*
    ;


conditionalAndExpression returns [ASTExpression n]
:
    nEqExp=equalityExpression { $n = $nEqExp.n; }
    (
        op='and' n1=equalityExpression
        { $n = new ASTBinaryExpression($op, $n, $n1.n); }
    )*
    ;


equalityExpression returns [ASTExpression n]
@init { Token op = null; }
:
    nRelExp=relationalExpression { $n = $nRelExp.n; }
    (
        { op = input.LT(1); }
        (EQUAL | NOT_EQUAL) n1=relationalExpression
        { $n = new ASTBinaryExpression(op, $n, $n1.n); }
    )*
    ;


relationalExpression returns [ASTExpression n]
@init { Token op = null; }
:
    nAddiExp=additiveExpression { $n = $nAddiExp.n; }
    (
        { op = input.LT(1); }
        (LESS | GREATER | LESS_EQUAL | GREATER_EQUAL) n1=additiveExpression
        { $n = new ASTBinaryExpression(op, $n, $n1.n); }
    )*
    ;


additiveExpression returns [ASTExpression n]
@init { Token op = null; }
:
    nMulExp=multiplicativeExpression { $n = $nMulExp.n; }
    (
        { op = input.LT(1); }
        (PLUS | MINUS) n1=multiplicativeExpression
        { $n = new ASTBinaryExpression(op, $n, $n1.n); }
    )*
    ;


multiplicativeExpression returns [ASTExpression n]
@init { Token op = null; }
:
    nUnExp=unaryExpression { $n = $nUnExp.n; }
    (
        { op = input.LT(1); }
        (STAR | SLASH | 'div') n1=unaryExpression
        { $n = new ASTBinaryExpression(op, $n, $n1.n); }
    )*
    ;


unaryExpression returns [ASTExpression n]
@init { Token op = null; }
:
     (
        { op = input.LT(1); }
        ('not' | MINUS | PLUS )
        nUnExp=unaryExpression { $n = new ASTUnaryExpression(op, $nUnExp.n); }
     )
    | nPosExp=postfixExpression { $n = $nPosExp.n; }
    ;


postfixExpression returns [ASTExpression n]
@init{ boolean arrow = false; }
:
    nPrimExp=primaryExpression { $n = $nPrimExp.n; }
    (
        ( ARROW { arrow = true; } | DOT { arrow = false; } )
		nPc=propertyCall[$n, arrow] { $n = $nPc.n; }
    )*
    ;


primaryExpression returns [ASTExpression n]
:
      nLit=literal          { $n = $nLit.n; }
    | nOr=objectReference   { $n = $nOr.n; }
    | nPc=propertyCall[null, false] { $n = $nPc.n; }
    | LPAREN nExp=expression RPAREN { $n = $nExp.n; }
    | nIfExp=ifExpression   { $n = $nIfExp.n; }
    | id1=IDENT DOT 'allInstances' { $n = new ASTAllInstancesExpression($id1); }
        ( AT 'pre' { $n.setIsPre(); } )?
        ( LPAREN RPAREN )?
    | id2=IDENT DOT 'byUseId' ( LPAREN idExp=expression RPAREN )
      { $n = new ASTObjectByUseIdExpression($id2, $idExp.n); }
      ( AT 'pre' { $n.setIsPre(); } )?
    ;


objectReference returns [ASTExpression n]
:
    AT
    objectName=IDENT
    { $n = new ASTObjectReferenceExpression(objectName); }
    ;


propertyCall[ASTExpression source, boolean followsArrow] returns [ASTExpression n]
:
      { org.tzi.use.parser.base.ParserHelper.isQueryIdent(input.LT(1)) }?
      { input.LA(2) == LPAREN }?
      nExpQuery=queryExpression[source]         { $n = $nExpQuery.n; }
    | nExpIterate=iterateExpression[source]     { $n = $nExpIterate.n; }
    | nExpOperation=operationExpression[source, followsArrow] { $n = $nExpOperation.n; }
    | nExpType=typeExpression[source, followsArrow] { $n = $nExpType.n; }
    | nExpInState=inStateExpression[source, followsArrow] { $n = $nExpInState.n; }
    ;


queryExpression[ASTExpression range] returns [ASTExpression n]
@init { ASTElemVarsDeclaration decl = new ASTElemVarsDeclaration(); }
:
    op=IDENT
    LPAREN
    ( decls=elemVarsDeclaration { decl = $decls.n; } BAR )?
    nExp=expression
    RPAREN
    { $n = new ASTQueryExpression($op, $range, decl, $nExp.n); }
    ;


iterateExpression[ASTExpression range] returns [ASTExpression n]:
    i='iterate'
    LPAREN
    decls=elemVarsDeclaration SEMI
    init=variableInitialization BAR
    nExp=expression
    RPAREN
    { $n = new ASTIterateExpression($i, $range, $decls.n, $init.n, $nExp.n); }
    ;


operationExpression[ASTExpression source, boolean followsArrow] returns [ASTOperationExpression n]
:
    name=IDENT { $n = new ASTOperationExpression($name, $source, $followsArrow); }
    ( LBRACK
        rolename=expression { $n.addExplicitRolenameOrQualifier($rolename.n); }
        (COMMA rolename=expression { $n.addExplicitRolenameOrQualifier($rolename.n); } )*
      RBRACK

      ( LBRACK
          rolename=expression { $n.addQualifier($rolename.n); }
          (COMMA rolename=expression { $n.addQualifier($rolename.n); } )*
        RBRACK
      )?
    )?

    ( AT 'pre' { $n.setIsPre(); } )?

    (
      LPAREN { $n.hasParentheses(); }
      (
	     e=expression { $n.addArg($e.n); }
	     ( COMMA e=expression { $n.addArg($e.n); } )*
	  )?
      RPAREN
    )?
    { $n.setStartToken($start); }
    ;


inStateExpression[ASTExpression source, boolean followsArrow] returns [ASTInStateExpression n]
@init { Token opToken = null; }
:
    { opToken = input.LT(1); }
    ( 'oclIsInState' | 'oclInState' )
    LPAREN
    s=IDENT
    RPAREN
    { $n = new ASTInStateExpression(opToken, $source, $s, $followsArrow); }
    ;


typeExpression[ASTExpression source, boolean followsArrow] returns [ASTTypeArgExpression n]
@init { Token opToken = null; }
:
    { opToken = input.LT(1); }
	( 'oclAsType' | 'oclIsKindOf' |  'oclIsTypeOf' | 'selectByType' | 'selectByKind' )
	LPAREN t=type RPAREN
    { $n = new ASTTypeArgExpression(opToken, $source, $t.n, $followsArrow); }
    ;


elemVarsDeclaration returns [ASTElemVarsDeclaration n]
:
    { $n = new ASTElemVarsDeclaration(); }
    var1=IDENT (COLON t=type)? { $n.addDeclaration($var1, $t.n); }
    ( COMMA varN=IDENT (COLON tN=type)? { $n.addDeclaration($varN, $tN.n); } )*
    ;


variableInitialization returns [ASTVariableInitialization n]
:
    name=IDENT COLON t=type EQUAL e=expression
    { $n = new ASTVariableInitialization($name, $t.n, $e.n); }
    ;


ifExpression returns [ASTExpression n]
:
    i='if' cond=expression 'then' t=expression 'else' e=expression 'endif'
    { $n = new ASTIfExpression($i, $cond.n, $t.n, $e.n); }
    ;


literal returns [ASTExpression n]
:
      t='true'      { $n = new ASTBooleanLiteral(true); }
    | f='false'     { $n = new ASTBooleanLiteral(false); }
    | i=INT         { $n = new ASTIntegerLiteral($i); }
    | r=REAL        { $n = new ASTRealLiteral($r); }
    | s=STRING      { $n = new ASTStringLiteral($s); }
    | HASH enumLit=IDENT { $n = new ASTEnumLiteral($enumLit); }
    | enumName=IDENT '::' enumLit=IDENT { $n = new ASTEnumLiteral($enumName, $enumLit); }
    | nColIt=collectionLiteral { $n = $nColIt.n; }
    | nEColIt=emptyCollectionLiteral { $n = $nEColIt.n; }
    | nUndLit=undefinedLiteral { $n = $nUndLit.n; }
    | nTupleLit=tupleLiteral { $n = $nTupleLit.n; }
    | un=STAR       { $n = new ASTUnlimitedNaturalLiteral($un); }
    ;


collectionLiteral returns [ASTCollectionLiteral n]
@init { Token op = null; }
:
    { op = input.LT(1); }
    ( 'Set' | 'Sequence' | 'Bag' | 'OrderedSet' )
    { $n = new ASTCollectionLiteral(op); }
    LBRACE
    (
      ci=collectionItem { $n.addItem($ci.n); }
      ( COMMA ci=collectionItem { $n.addItem($ci.n); } )*
    )?
    RBRACE
    ;


collectionItem returns [ASTCollectionItem n]
@init{ $n = new ASTCollectionItem(); }
:
    e=expression { $n.setFirst($e.n); }
    ( DOTDOT e=expression { $n.setSecond($e.n); } )?
    ;


emptyCollectionLiteral returns [ASTEmptyCollectionLiteral n]
:
    'oclEmpty' LPAREN t=collectionType RPAREN
    { $n = new ASTEmptyCollectionLiteral($t.n); }
|
    t=collectionType LBRACE RBRACE
    { $n = new ASTEmptyCollectionLiteral($t.n); }
    ;


undefinedLiteral returns [ASTUndefinedLiteral n]
:
    'oclUndefined' LPAREN t=type RPAREN
    { $n = new ASTUndefinedLiteral($t.n); }
|
    'Undefined'
    { $n = new ASTUndefinedLiteral(); }
|
    'null' LPAREN t=type RPAREN
    { $n = new ASTUndefinedLiteral(t); }
|
    'null'
    { $n = new ASTUndefinedLiteral(); }
;


tupleLiteral returns [ASTTupleLiteral n]
@init { List tiList = new ArrayList(); }
:
    'Tuple'
    LBRACE
    ti=tupleItem { tiList.add($ti.n); }
    ( COMMA ti=tupleItem { tiList.add($ti.n); } )*
    RBRACE
    { $n = new ASTTupleLiteral(tiList); }
    ;


tupleItem returns [ASTTupleItem n]
:
    name=IDENT
    (
      { input.LA(2) == COLON }? COLON t=type EQUAL e=expression
      { $n = new ASTTupleItem($name, $t.n, $e.n); }
    |
      (COLON | EQUAL) e=expression
      { $n = new ASTTupleItem($name, $e.n); }
    )
    ;


type returns [ASTType n]
@init { Token tok = null; }
:
    { tok = input.LT(1); }
    (
    nTsimple=simpleType { $n = $nTsimple.n; if ($n != null) $n.setStartToken(tok); }
    | nTCollection=collectionType { $n = $nTCollection.n; if ($n != null) $n.setStartToken(tok); }
    | nTTuple=tupleType { $n = $nTTuple.n; if ($n != null) $n.setStartToken(tok); }
    )
    ;


typeOnly returns [ASTType n]
:
    nT=type EOF { $n = $nT.n; }
    ;


simpleType returns [ASTSimpleType n]
:
    name=IDENT { $n = new ASTSimpleType($name); }
    ;


collectionType returns [ASTCollectionType n]
@init { Token op = null; }
:
    { op = input.LT(1); }
    ( 'Collection' | 'Set' | 'Sequence' | 'Bag' | 'OrderedSet' )
    LPAREN eleType=type RPAREN
    { $n = new ASTCollectionType(op, $eleType.n); $n.setStartToken(op); }
    ;


tupleType returns [ASTTupleType n]
@init { List tpList = new ArrayList(); }
:
    'Tuple' LPAREN
    tp=tuplePart { tpList.add($tp.n); }
    ( COMMA tp=tuplePart { tpList.add($tp.n); } )*
    RPAREN
    { $n = new ASTTupleType(tpList); }
    ;


/* ------------------------------------
  tuplePart ::= id ":" type
  Example:
    a: Integer
    b: String
*/
tuplePart returns [ASTTuplePart n]
:
    name=IDENT COLON t=type { $n = new ASTTuplePart($name.text, $t.n); }
    ;

/*
--------- Start of file OCLLexerRules.gpart --------------------
*/

// Whitespace -- ignored
WS:
    ( ' '
    | '\t'
    | '\f'
    | NEWLINE
    )
    -> skip
    ;

// Single-line comments
SL_COMMENT:
    ('//' | '--')
    ~('\r' | '\n')*
    -> skip
    ;

// multiple-line comments
ML_COMMENT:
    '/*' .*? '*/' -> skip
    ;

fragment
NEWLINE	:
    '\r\n' | '\r' | '\n';

// Use paraphrases for nice error messages
ARROW 		 : '->';
AT     		 : '@';
BAR 		 : '|';
COLON 		 : ':';
COLON_COLON	 : '::';
COLON_EQUAL	 : ':=';
COMMA 		 : ',';
DOT 		 : '.';
DOTDOT 		 : '..';
EQUAL 		 : '=';
GREATER 	 : '>';
GREATER_EQUAL : '>=';
HASH 		 : '#';
LBRACE 		 : '{';
LBRACK 		 : '[';
LESS 		 : '<';
LESS_EQUAL 	 : '<=';
LPAREN 		 : '(';
MINUS 		 : '-';
NOT_EQUAL 	 : '<>';
PLUS 		 : '+';
RBRACE 		 : '}';
RBRACK 		 : ']';
RPAREN		 : ')';
SEMI		 : ';';
SLASH 		 : '/';
STAR 		 : '*';

INT:
    ('0'..'9')+
    ;

REAL:
    INT ('.' INT (('e' | 'E') ('+' | '-')? INT)? | ('e' | 'E') ('+' | '-')? INT)
    ;

RANGE_OR_INT
:
      INT '..'
    | REAL
    | INT
    ;

// String literals
STRING:
    '\'' ( ~('\''|'\\') | ESC)* '\'';

NON_OCL_STRING:
    '"' ( ~('"'|'\\') | ESC)* '"';

// escape sequence -- note that this is protected; it can only be called
//   from another lexer rule -- it will not ever directly return a token to
//   the parser
// There are various ambiguities hushed in this rule.  The optional
// '0'...'7' digit matches should be matched here rather than letting
// them go back to STRING_LITERAL to be matched.  ANTLR does the
// right thing by matching immediately; hence, it's ok to shut off
// the FOLLOW ambig warnings.
fragment
ESC
:
    '\\'
     ( 'n'
     | 'r'
     | 't'
     | 'b'
     | 'f'
     | '"'
     | '\''
     | '\\'
     | 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
     | '0'..'3' ('0'..'7' ('0'..'7')? )?  | '4'..'7' ('0'..'7')?
     )
     ;

// hexadecimal digit (again, note it's protected!)
fragment
HEX_DIGIT:
    ( '0'..'9' | 'A'..'F' | 'a'..'f' );


// An identifier.  Note that testLiterals is set to true!  This means
// that after we match the rule, we look in the literals table to see
// if it's a literal or really an identifier.

IDENT:
    ('$' | 'a'..'z' | 'A'..'Z' | '_') ('a'..'z' | 'A'..'Z' | '_' | '0'..'9')*
    ;

// A dummy rule to force vocabulary to be all characters (except
// special ones that ANTLR uses internally (0 to 2)

fragment
VOCAB:
    '\u0003'..'\u0377'
    ;