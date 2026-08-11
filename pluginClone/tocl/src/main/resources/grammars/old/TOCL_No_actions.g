grammar TOCL;

invariant
:
    'context'
    ( v=IDENT
       (',' v=IDENT )* COLON )?
    t=simpleType
    ( inv=invariantClause )*
    ;


invariantClause
:
    as = annotationSet
    'inv' ( name=IDENT )? COLON e=expression
    | 'existential' 'inv' ( name=IDENT )? COLON e=expression
    ;


prePost
:
    'context' classname=IDENT COLON_COLON opname=IDENT pl=paramList ( COLON rt=type )?
    ( ppc=prePostClause )+
    ;


prePostClause
:
    as = annotationSet
    ( 'pre' | 'post' )  ( name=IDENT )? COLON e=expression
    ;


annotationSet
:
    (an=annotation)*
    ;


annotation
:
    AT name=IDENT
    LPAREN
    values=annotationValues
    RPAREN
;


annotationValues
:
    (firstVal=annotationValue)?
    (COMMA val=annotationValue)*
    ;


annotationValue
:
    aName=IDENT
    EQUAL
    aValue=NON_OCL_STRING
;


expressionOnly
:
    nExp=expression EOF
    ;


expression
:
    (
      'let'
      name=IDENT ( COLON t=type )? EQUAL e1=expression
      (
      COMMA
        name=IDENT ( COLON t=type )? EQUAL e1=expression
      )*
      'in'
    )*
    nCndImplies=conditionalImpliesExpression
    ;


paramList
:
    LPAREN
    (
      v=variableDeclaration
      ( COMMA v=variableDeclaration )*
    )?
    RPAREN
    ;


idList
:
    id0=IDENT
    ( COMMA idn=IDENT )*
    ;


variableDeclaration
:
    name=IDENT COLON t=type
    ;


conditionalImpliesExpression
:
    nCndOrExp=conditionalOrExpression
    ( op='implies' n1=conditionalOrExpression )*
    ;


conditionalOrExpression
:
    nCndXorExp=conditionalXOrExpression
    ( op='or' n1=conditionalXOrExpression )*
    ;


conditionalXOrExpression
:
    nCndAndExp=conditionalAndExpression
    ( op='xor' n1=conditionalAndExpression )*
    ;


conditionalAndExpression
:
    nEqExp=equalityExpression
    ( op='and' n1=equalityExpression )*
    ;


equalityExpression
:
    nRelExp=relationalExpression
    ( (EQUAL | NOT_EQUAL) n1=relationalExpression )*
    ;


relationalExpression
:
    nAddiExp=additiveExpression
    ( (LESS | GREATER | LESS_EQUAL | GREATER_EQUAL) n1=additiveExpression )*
    ;


additiveExpression
:
    nMulExp=multiplicativeExpression
    ( (PLUS | MINUS) n1=multiplicativeExpression )*
    ;


multiplicativeExpression
:
    nUnExp=unaryExpression
    ( (STAR | SLASH | 'div') n1=unaryExpression )*
    ;


unaryExpression
:
      ( ('not' | MINUS | PLUS )
        nUnExp=unaryExpression
      )
    | nPosExp=postfixExpression
    ;


postfixExpression
:
    nPrimExp=primaryExpression
    (
     ( ARROW | DOT )
		nPc=propertyCall[$n, arrow]
    )*
    ;


primaryExpression
:
      nLit=literal
    | nOr=objectReference
    | nPc=propertyCall[null, false]
    | LPAREN nExp=expression RPAREN
    | nIfExp=ifExpression
    | id1=IDENT DOT 'allInstances'
        ( AT 'pre' )?
        ( LPAREN RPAREN )?
    | id2=IDENT DOT 'byUseId' ( LPAREN idExp=expression RPAREN )
      ( AT 'pre' )?
    ;


objectReference
:
  AT
  objectName = IDENT
;


propertyCall[ASTExpression source, boolean followsArrow] returns [ASTExpression n]
:
      { org.tzi.use.parser.base.ParserHelper.isQueryIdent(input.LT(1)) }?
      { input.LA(2) == LPAREN }?
      queryExpression[source]
    | iterateExpression[source]
    | operationExpression[source, followsArrow]
    | typeExpression[source, followsArrow]
    | inStateExpression[source, followsArrow]
    ;


queryExpression[ASTExpression range]
:
    op=IDENT
    LPAREN
    ( decls=elemVarsDeclaration BAR )?
    nExp=expression
    RPAREN
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


operationExpression[ASTExpression source, boolean followsArrow]
:
    name=IDENT
    ( LBRACK
        rolename=expression
        (COMMA rolename=expression)*
      RBRACK

      ( LBRACK
          rolename=expression
          (COMMA rolename=expression)*
        RBRACK
      )?
    )?

    ( AT 'pre' ) ?

    (
      LPAREN
      (
	     e=expression
	     ( COMMA e=expression )*
	  )?
      RPAREN
    )?
    ;


inStateExpression[ASTExpression source, boolean followsArrow]
:
   ( 'oclIsInState' | 'oclInState' )
   LPAREN
   s = IDENT
   RPAREN
;


typeExpression[ASTExpression source, boolean followsArrow]
:
	( 'oclAsType' | 'oclIsKindOf' |  'oclIsTypeOf' | 'selectByType' | 'selectByKind' )
	LPAREN t=type RPAREN
    ;


elemVarsDeclaration
:
   var1 = IDENT (COLON t=type)?
   (COMMA varN = IDENT (COLON tN = type)? )*
;


variableInitialization
:
    name=IDENT COLON t=type EQUAL e=expression
    ;


ifExpression
:
    'if' cond=expression 'then' t=expression 'else' e=expression 'endif'
    ;


literal
:
      'true'
    | 'false'
    | INT
    | REAL
    | STRING
    | HASH IDENT
    | IDENT '::' IDENT
    | collectionLiteral
    | emptyCollectionLiteral
    | undefinedLiteral
    | tupleLiteral
    | STAR
    ;


collectionLiteral
:
    ( 'Set' | 'Sequence' | 'Bag' | 'OrderedSet' )
    LBRACE
    (
      collectionItem
      ( COMMA collectionItem )*
    )?
    RBRACE
    ;


collectionItem
:
    expression
    ( DOTDOT expression )?
    ;


emptyCollectionLiteral
:
    'oclEmpty' LPAREN collectionType RPAREN
|
    collectionType LBRACE RBRACE
    ;


undefinedLiteral
:
    'oclUndefined' LPAREN type RPAREN
|
    'Undefined'
|
    'null' LPAREN type RPAREN
|
    'null'
;


tupleLiteral
:
    'Tuple'
    LBRACE
    tupleItem
    ( COMMA tupleItem )*
    RBRACE
    ;


tupleItem
:
    IDENT
    (
      (COLON type EQUAL)
    |
      (COLON | EQUAL) expression
    )
    ;


type
:
    simpleType
    | collectionType
    | tupleType
    ;


typeOnly
:
    type EOF
    ;


simpleType
:
    IDENT
    ;


collectionType
:
    ( 'Collection' | 'Set' | 'Sequence' | 'Bag' | 'OrderedSet' )
    LPAREN type RPAREN
    ;


tupleType
:
    'Tuple' LPAREN
    tuplePart
    ( COMMA tuplePart )*
    RPAREN
    ;


tuplePart
:
    IDENT COLON type
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
    { $channel=HIDDEN; }
    ;

// Single-line comments
SL_COMMENT:
    ('//' | '--')
    (~(NEWLINE))*
    { $channel=HIDDEN; }
    ;

// multiple-line comments
ML_COMMENT:
    '/*' ( options {greedy=false;} : . )* '*/' { $channel=HIDDEN; };

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

fragment
INT:
    ('0'..'9')+
    ;

fragment
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
    '\U0003'..'\U0377'
    ;