grammar Bpmn2;

@header { package org.vnu.sme.goal.bpmn2.parser; }

// State-oriented BPMN concrete syntax. BPMN declares boolean state
// predicates only; state mutation belongs to an external execution adapter.
model : 'model' IDENT '{' pool+ message* messageFlow* '}' EOF ;

pool : 'pool' IDENT '{' nameProperty? lane* poolElement* sequenceFlow* '}' ;
lane : 'lane' IDENT '{' nameProperty? poolElement* '}' ;

poolElement
    : 'start' IDENT '{' nameProperty? triggerProperty? stateCondition* '}'                    # elemStart
    | 'end' IDENT '{' nameProperty? triggerProperty? stateCondition* '}'                      # elemEnd
    | 'intermediate' IDENT '{' nameProperty? triggerProperty? directionProperty? stateCondition* '}' # elemIntermediate
    | 'task' IDENT '{' nameProperty? stateCondition* effectProperty? '}'                       # elemTask
    | 'call-activity' IDENT '{' nameProperty? stateCondition* effectProperty? '}'              # elemCallActivity
    | 'subprocess' IDENT '{' nameProperty? stateCondition* effectProperty? poolElement* sequenceFlow* '}' # elemSubProcess
    | 'gateway' IDENT '{' nameProperty? typeProperty stateCondition* '}'                      # elemGateway
    ;

sequenceFlow : 'flow' IDENT '->' IDENT flowBody? ;
flowBody : '{' nameProperty? guardProperty? '}' ;

message : 'message' IDENT ('{' nameProperty? '}')? ;
messageFlow : 'message-flow' IDENT '->' IDENT ('{' messageProperty? '}')? ;

nameProperty : 'name' STRING ;
triggerProperty : 'trigger' eventType ;
directionProperty : 'direction' eventDir ;
typeProperty : 'type' gwType ;
messageProperty : 'message' IDENT ;
stateCondition : ('pre' | 'post') stateClause ;
effectProperty : 'effect' stateClause ;
guardProperty : 'guard' stateClause ;
stateClause : STATE_CLAUSE ;

eventType
    : 'none' | 'message' | 'timer' | 'error' | 'signal'
    | 'terminate' | 'compensation' | 'conditional'
    ;
eventDir : 'catching' | 'throwing' ;
gwType : 'xor' | 'and' | 'or' | 'event-based' ;

IDENT  : [a-zA-Z_][a-zA-Z0-9_]* ;
STRING : '"' (~["\r\n\\] | '\\' .)* '"' ;
STATE_CLAUSE : '{[' .*? ']}' ;
WS            : [ \t\r\n\f]+ -> skip ;
LINE_COMMENT  : '//' ~[\r\n]*  -> skip ;
BLOCK_COMMENT : '/*' .*? '*/'  -> skip ;
