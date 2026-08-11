grammar TOCL;

options {
    output = AST;
}

invariant
    : 'context'
      ( IDENT (',' IDENT )* COLON )? simpleType
      ( invariantClause )*
    ;

invariantClause
    : annotationSet
      'inv' (IDENT)? COLON expression
    | 'existential' 'inv' (IDENT)? COLON expression
    ;

prePost
    : 'context' IDENT COLON_COLON IDENT paramList (COLON type)?
      (prePostClause)+
    ;

prePostClause
    : annotationSet
      ('pre' | 'post') (IDENT)? COLON expression
    ;

annotationSet
    : (annotation)*
    ;

annotation
    : AT IDENT LPAREN annotationValues RPAREN
    ;

annotationValues
    : (annotationValue
       (COMMA annotationValue)*)?
    ;

annotationValue
    : IDENT EQUAL NON_OCL_STRING
    ;

stateMachine
    : 'psm'
      IDENT
      'states' (stateDefinition)+
      'transitions' (transitionDefinition)+
      'end'
    ;

stateDefinition
    : IDENT (COLON IDENT)? (LBRACK expression RBRACK)?
    ;

transitionDefinition
    : IDENT ARROW IDENT
      (LBRACE
        (LBRACK expression RBRACK)?
        (event | IDENT LPAREN paramList? RPAREN)
        (LBRACK expression RBRACK)?
      RBRACE)?
    ;

event
    : 'create'
    ;

signalDefinition[boolean isAbstract]
    : keySignal name=IDENT
      (LESS idListRes=idList)?
      ('attributes' (a=attributeDefinition)*)?
      ('constraints' (inv=invariantClause)*)?
      'end'
    ;

keyUnion : {input.LT(1).getText().equals("union")}? IDENT;
keyAssociation : {input.LT(1).getText().equals("association")}? IDENT;
keyRole : {input.LT(1).getText().equals("role")}? IDENT;
keyComposition : {input.LT(1).getText().equals("composition")}? IDENT;
keyAggregation : {input.LT(1).getText().equals("aggregation")}? IDENT;
keyDataType : {input.LT(1).getText().equals("dataType")}? IDENT;
keyClass : {input.LT(1).getText().equals("class")}? IDENT;
keySignal : {input.LT(1).getText().equals("signal")}? IDENT;
keyDerived : {input.LT(1).getText().equals("derived")}? IDENT;
keyDerive : {input.LT(1).getText().equals("derive")}? IDENT;
keyInit : {input.LT(1).getText().equals("init")}? IDENT;
keyQualifier : {input.LT(1).getText().equals("qualifier")}? IDENT;

expressionOnly
    : nExp=expression EOF
    ;

expression
    : ('let' IDENT (COLON type)? EQUAL expression
       (COMMA IDENT (COLON type)? EQUAL expression)*
       'in')*
      conditionalImpliesExpression
    ;

paramList
    : LPAREN
      (variableDeclaration (COMMA variableDeclaration)*)?
      RPAREN
    ;

idList
    : IDENT (COMMA IDENT)*
    ;

variableDeclaration
    : IDENT COLON type
    ;

conditionalImpliesExpression
    : conditionalOrExpression ('implies' conditionalOrExpression)*
    ;

conditionalOrExpression
    : conditionalXOrExpression ('or' conditionalXOrExpression)*
    ;

conditionalXOrExpression
    : conditionalAndExpression ('xor' conditionalAndExpression)*
    ;

conditionalAndExpression
    : equalityExpression ('and' equalityExpression)*
    ;

equalityExpression
    : relationalExpression ((EQUAL | NOT_EQUAL) relationalExpression)*
    ;

relationalExpression
    : additiveExpression ((LESS | GREATER | LESS_EQUAL | GREATER_EQUAL) additiveExpression)*
    ;

additiveExpression
    : multiplicativeExpression ((PLUS | MINUS) multiplicativeExpression)*
    ;

multiplicativeExpression
    : unaryExpression ((STAR | SLASH | 'div') unaryExpression)*
    ;

unaryExpression
    : ('not' | MINUS | PLUS) unaryExpression
    | postfixExpression
    ;

postfixExpression
    : primaryExpression ((ARROW | DOT) propertyCall)*
    ;

primaryExpression
    : literal
    | objectReference
    | propertyCall
    | LPAREN expression RPAREN
    | ifExpression
    | IDENT DOT 'allInstances' (AT 'pre')? (LPAREN RPAREN)?
    | IDENT DOT 'byUseId' (LPAREN expression RPAREN) (AT 'pre')?
    ;

objectReference
    : AT IDENT
    ;

propertyCall
    : queryExpression
    | iterateExpression
    | operationExpression
    | typeExpression
    | inStateExpression
    ;

queryExpression
    : IDENT
      LPAREN
      (elemVarsDeclaration BAR)?
      expression
      RPAREN
    ;

iterateExpression
    : 'iterate'
      LPAREN
      elemVarsDeclaration SEMI
      variableInitialization BAR
      expression
      RPAREN
    ;

operationExpression
    : IDENT
      (LBRACK expression (COMMA expression)* RBRACK
        (LBRACK expression (COMMA expression)* RBRACK)?
      )?
      (AT 'pre')?
      (LPAREN (expression (COMMA expression)*)? RPAREN)?
    ;

inStateExpression
    : ('oclIsInState' | 'oclInState')
      LPAREN
      IDENT
      RPAREN
    ;

typeExpression
    : ('oclAsType' | 'oclIsKindOf' | 'oclIsTypeOf' | 'selectByType' | 'selectByKind')
      LPAREN type RPAREN
    ;

elemVarsDeclaration
    : IDENT (COLON type)?
      (COMMA IDENT (COLON type)?)*
    ;

variableInitialization
    : IDENT COLON type EQUAL expression
    ;

ifExpression
    : 'if' expression 'then' expression 'else' expression 'endif'
    ;

literal
    : 'true'
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
    : ('Set' | 'Sequence' | 'Bag' | 'OrderedSet')
      LBRACE
      (collectionItem (COMMA collectionItem)*)?
      RBRACE
    ;

collectionItem
    : expression (DOTDOT expression)?
    ;

emptyCollectionLiteral
    : 'oclEmpty' LPAREN collectionType RPAREN
    | collectionType LBRACE RBRACE
    ;

undefinedLiteral
    : 'oclUndefined' LPAREN type RPAREN
    | 'Undefined'
    | 'null' LPAREN type RPAREN
    | 'null'
    ;

tupleLiteral
    : 'Tuple'
      LBRACE
      tupleItem (COMMA tupleItem)*
      RBRACE
    ;

tupleItem
    : IDENT
      ((COLON type EQUAL) => COLON type EQUAL expression
      |(COLON | EQUAL) expression)
    ;

type
    : simpleType
    | collectionType
    | tupleType
    ;

typeOnly
    : type EOF
    ;

simpleType
    : IDENT
    ;

collectionType
    : ('Collection' | 'Set' | 'Sequence' | 'Bag' | 'OrderedSet')
      LPAREN type RPAREN
    ;

tupleType
    : 'Tuple' LPAREN
      tuplePart (COMMA tuplePart)*
      RPAREN
    ;

tuplePart
    : IDENT COLON type
    ;

// Lexer Rules
WS
    : (' ' | '\t' | '\f' | NEWLINE)
      -> channel(HIDDEN)
    ;

SL_COMMENT
    : ('//' | '--') ~(NEWLINE)*
      -> channel(HIDDEN)
    ;

ML_COMMENT:
    '/*' ( options {greedy=false;} : . )* '*/';

fragment
NEWLINE:
    '\r\n' | '\r' | '\n';

ARROW: '->';
AT: '@';
BAR: '|';
COLON: ':';
COLON_COLON: '::';
COLON_EQUAL: ':=';
COMMA: ',';
DOT: '.';
DOTDOT: '..';
EQUAL: '=';
GREATER: '>';
GREATER_EQUAL: '>=';
HASH: '#';
LBRACE: '{';
LBRACK: '[';
LESS: '<';
LESS_EQUAL: '<=';
LPAREN: '(';
MINUS: '-';
NOT_EQUAL: '<>';
PLUS: '+';
RBRACE: '}';
RBRACK: ']';
RPAREN: ')';
SEMI: ';';
SLASH: '/';
STAR: '*';

fragment
INT: ('0'..'9')+;

fragment
REAL: INT ('.' INT (('e' | 'E') ('+' | '-')? INT)? | ('e' | 'E') ('+' | '-')? INT);

RANGE_OR_INT:
    ( INT '..' ) => INT
    | ( REAL ) => REAL
    | INT;

STRING: '\'' ( ~('\''|'\\') | ESC)* '\'';

NON_OCL_STRING: '"' ( ~('"'|'\\') | ESC)* '"';

fragment
ESC: '\\'
    ( 'n'
    | 'r'
    | 't'
    | 'b'
    | 'f'
    | '"'
    | '\''
    | '\\'
    | 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
    | '0'..'3' ('0'..'7' ('0'..'7')? )?
    | '4'..'7' ('0'..'7')?
    );

fragment
HEX_DIGIT: ('0'..'9' | 'A'..'F' | 'a'..'f');

IDENT: ('$' | 'a'..'z' | 'A'..'Z' | '_')
       ('a'..'z' | 'A'..'Z' | '_' | '0'..'9')*;

fragment
VOCAB: '\U0003'..'\U0377';