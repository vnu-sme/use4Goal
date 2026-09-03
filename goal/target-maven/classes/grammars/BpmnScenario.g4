grammar BpmnScenario;

@header { package org.vnu.sme.goal.dsl.bpmnscenario.parser; }

// =====================================================================
//  BPMN Scenario — an instance-level script/snapshot for a BPMN process.
//
//  The .bpmn2 model is the type-level process schema. This scenario file
//  captures one concrete process situation: actors/objects, bindings, data
//  values, completed activities, active activities, and explicit token marks.
// =====================================================================

scenario : 'scenario' IDENT 'for' STRING '{' stmt* '}' EOF ;

stmt
    : processDecl   # stmtProcess
    | actorDecl     # stmtActor
    | bindStmt      # stmtBind
    | fireStmt      # stmtFire
    | completedStmt # stmtCompleted
    | activeStmt    # stmtActive
    | tokenStmt     # stmtToken
    | valueStmt     # stmtValue
    | assertStmt    # stmtAssert
    ;

processDecl : 'process' IDENT ':' IDENT ';' ;
actorDecl   : 'actor' IDENT (',' IDENT)* ':' IDENT ';' ;

bindStmt  : 'bind' ref '=' value ';' ;
fireStmt  : 'fire' qualifiedId forClause? byClause? ';' ;
completedStmt : 'completed' qualifiedId forClause? byClause? ';' ;
activeStmt    : 'active' qualifiedId forClause? byClause? ';' ;
tokenStmt     : 'token' IDENT '.' IDENT '::' IDENT forClause? ';' ;
valueStmt     : 'value' ref '=' value ';' ;
assertStmt    : 'assert' expr ';' ;

forClause : 'for' IDENT ;
byClause  : 'by' IDENT ;

qualifiedId : IDENT '.' IDENT ;
ref         : IDENT ('.' IDENT)* ;

value
    : listValue
    | STRING
    | NUMBER
    | IDENT
    ;

listValue : '[' (IDENT (',' IDENT)*)? ']' ;

expr
    : 'count' '(' IDENT 'where' IDENT '=' IDENT ')' compOp NUMBER # countExpr
    | ref compOp value                                             # compareExpr
    ;

compOp : '==' | '!=' | '>=' | '<=' | '>' | '<' ;

// ── Lexer ─────────────────────────────────────────────────────────────

STRING : '"' (~["\r\n\\] | '\\' .)* '"' ;
NUMBER : [0-9]+ ;
IDENT  : [a-zA-Z_][a-zA-Z0-9_]* ;

WS            : [ \t\r\n\f]+ -> skip ;
LINE_COMMENT  : '//' ~[\r\n]*  -> skip ;
BLOCK_COMMENT : '/*' .*? '*/'  -> skip ;
