grammar DCR;

@header { package org.vnu.sme.goal.dsl.dcr.parser; }

model : 'dcr' IDENT '{' statement* '}' EOF ;

statement
    : 'event' IDENT STRING? ';'                                      # eventStmt
    | 'marking' IDENT ':' (markItem (',' markItem)*)? ';'            # markingStmt
    | relKind IDENT relTime? '->' IDENT ';'                          # relationStmt
    ;

markItem
    : 'executed' INT?       # markExecuted
    | 'included'            # markIncluded
    | 'pending' deadline?   # markPending
    ;

relKind
    : 'condition' # relCondition
    | 'response'  # relResponse
    | 'include'   # relInclude
    | 'exclude'   # relExclude
    | 'milestone' # relMilestone
    ;

relTime
    : 'after' INT          # conditionTime
    | 'within' deadline    # responseTime
    ;

deadline
    : INT
    | 'omega'
    ;

IDENT  : [a-zA-Z_][a-zA-Z0-9_]* ;
INT    : [0-9]+ ;
STRING : '"' (~["\r\n\\] | '\\' .)* '"' ;

WS            : [ \t\r\n\f]+ -> skip ;
LINE_COMMENT  : '//' ~[\r\n]*  -> skip ;
BLOCK_COMMENT : '/*' .*? '*/'  -> skip ;
