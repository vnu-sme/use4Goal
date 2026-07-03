grammar MAXGoal;

@header { package org.vnu.sme.goal.maxgoal.parser; }

// ====================================================================
//  MAXGoal — Multi-Actor Extended Goal Language
//
//  model Name {
//    ActorName : agent|role|position {
//      goal GoalName {
//        achieve: condition
//        refine:  SEQ|PAR|XOR|IOR|ITER [...]
//      }
//      task TaskName {
//        pre:    condition
//        post:   condition
//        needby: ResourceName
//        refine: SEQ|PAR|...
//      }
//      resource ResName : virtual|physical
//    }
//    depend {
//      NodeA -> NodeB
//    }
//  }
// ====================================================================

model : 'model' IDENT '{' actorDef* dependBlock? '}' EOF ;

actorDef : IDENT ':' actorKind '{' intentional* '}' ;

actorKind : 'agent' | 'role' | 'position' ;

intentional : goalDecl | taskDecl | resourceDecl ;

goalDecl     : 'goal'     IDENT '{' goalAttr* '}' ;
taskDecl     : 'task'     IDENT '{' taskAttr* '}' ;
resourceDecl : 'resource' IDENT ':' resourceKind  ;

goalAttr
    : 'achieve'  ':' condition   # gaAchieve
    | 'maintain' ':' condition   # gaMaintain
    | 'avoid'    ':' condition   # gaAvoid
    | 'refine'   ':' refineSpec  # gaRefine
    ;

taskAttr
    : 'pre'    ':' condition   # taPre
    | 'post'   ':' condition   # taPost
    | 'refine' ':' refineSpec  # taRefine
    | 'needby' ':' IDENT       # taNeedby
    ;

refineSpec
    : 'SEQ'  '[' IDENT (',' IDENT)* ']'                     # seqRefine
    | 'PAR'  '[' IDENT (',' IDENT)* ']'                     # parRefine
    | 'XOR'  '[' guardedChild (',' guardedChild)* ']'       # xorRefine
    | 'IOR'  '[' guardedChild (',' guardedChild)* ']'       # iorRefine
    | 'ITER' '[' IDENT (',' IDENT)* ']' 'until' condition   # iterRefine
    ;

guardedChild : '(' condition ',' IDENT ')' ;

dependBlock : 'depend' '{' depStmt* '}' ;

depStmt : IDENT '->' IDENT ;

resourceKind : 'virtual' | 'physical' ;

// ── Condition grammar ──────────────────────────────────────────────

condition : orExpr ;
orExpr    : andExpr  ( 'or'  andExpr  )* ;
andExpr   : notExpr  ( 'and' notExpr  )* ;
notExpr   : 'not' notExpr | atom ;

atom
    : '(' condition ')'     # atomParen
    | pathExpr relOp atom2  # atomRelExpr
    | pathExpr              # atomPath
    | literal               # atomLiteral
    ;

atom2    : pathExpr | literal ;
relOp    : EQ | NEQ | LT | GT | LE | GE ;
pathExpr : IDENT ( '.' IDENT )* ;
literal  : INT | REAL | STRING | TRUE | FALSE | NULL_LIT ;

// ── Lexer ──────────────────────────────────────────────────────────

TRUE     : 'true'  ;
FALSE    : 'false' ;
NULL_LIT : 'null'  ;

ARROW : '->' ;

LE  : '<=' ;
GE  : '>=' ;
NEQ : '<>' ;
LT  : '<'  ;
GT  : '>'  ;
EQ  : '='  ;

IDENT  : [a-zA-Z_] [a-zA-Z0-9_]* ;
INT    : [0-9]+                    ;
REAL   : [0-9]+ '.' [0-9]+        ;
STRING : '"' ( ~["\\\r\n] | '\\' . )* '"' ;

WS            : [ \t\r\n\f]+ -> skip ;
LINE_COMMENT  : '//' ~[\r\n]* -> skip ;
BLOCK_COMMENT : '/*' .*? '*/' -> skip ;
