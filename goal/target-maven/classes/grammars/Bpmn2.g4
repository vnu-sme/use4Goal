grammar Bpmn2;

@header { package org.vnu.sme.goal.bpmn2.parser; }

// State-oriented BPMN concrete syntax. BPMN declares boolean state
// predicates only; state mutation belongs to an external execution adapter.
//
// A pool declares its lanes as a bare skeleton only (no nesting). Every
// flow element (event/activity/gateway) is a top-level declaration of the
// model, carrying its own lane membership and its own outgoing flow(s),
// Event-B-style: one self-contained block per element instead of a
// separate list of `flow A -> B` edges.
model : 'model' IDENT '{' pool+ message* messageFlow* topElement* '}' EOF ;

pool : 'pool' IDENT '{' nameProperty? laneDecl* '}' ;
laneDecl : 'lane' IDENT ';' ;

topElement
    : eventDecl
    | activityDecl
    | gatewayDecl
    ;

// An event has no name in the metamodel (Figure: StartEvent/EndEvent/
// IntermediateEvent carry only trigger/direction, never name).
eventDecl
    : 'event' IDENT '{'
        eventTypeProperty
        laneProperty
        triggerProperty
        directionProperty?
        flowProperty?
      '}'
    ;
eventTypeProperty : 'type' eventKind ;
eventKind : 'start' | 'end' | 'intermediate' ;

// An activity is Task, CallActivity, or SubProcess (the metamodel's three
// concrete Activity subtypes); `name` is optional since CallActivity
// carries none. Field order is fixed: name, type, lane, pre, effect,
// post, flow.
activityDecl
    : 'activity' IDENT '{'
        nameProperty?
        activityTypeProperty
        laneProperty
        preProperty?
        effectProperty?
        postProperty?
        flowProperty?
      '}'
    ;
activityTypeProperty : 'type' activityType ;
activityType : 'task' | 'call-activity' | 'subprocess' ;

// A gateway has no name in the metamodel (Figure: Gateway carries only
// kind) and no effect, so no `post`: routing never changes domain state.
// `pre` is kept — it is a real precondition the engine gates on before
// routing through the element, not a vestigial assertion. Each outgoing
// flow is either guarded (`when`), the fallback (`default`), or plain
// when the gateway has only one way out.
gatewayDecl
    : 'gateway' IDENT '{'
        laneProperty
        gatewayTypeProperty
        preProperty?
        gatewayFlow+
      '}'
    ;
gatewayTypeProperty : 'type' gwType ;
gatewayFlow : 'flow' IDENT gatewayFlowCondition? ;
gatewayFlowCondition : 'when' stateClause | 'default' ;

laneProperty : 'lane' IDENT ;
flowProperty : 'flow' IDENT ;

message : 'message' IDENT ('{' nameProperty? '}')? ;
messageFlow : 'message-flow' IDENT '->' IDENT ('{' messageProperty? '}')? ;

nameProperty : 'name' STRING ;
triggerProperty : 'trigger' eventType ;
directionProperty : 'direction' eventDir ;
messageProperty : 'message' IDENT ;
preProperty : 'pre' stateClause ;
postProperty : 'post' stateClause ;
effectProperty : 'effect' stateClause ;
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
