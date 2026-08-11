grammar Bpmn;

@header { package org.vnu.sme.goal.dsl.bpmn.parser; }

// State-oriented BPMN concrete syntax. BPMN declares boolean state
// predicates only; state mutation belongs to an external execution adapter.
//
// A pool declares its lanes as a bare skeleton only (no nesting). Every
// flow element (event/activity/gateway) is a top-level declaration of the
// model, carrying its own lane membership and its own outgoing flow(s),
// Event-B-style: one self-contained block per element instead of a
// separate list of `flow A -> B` edges.
model : 'model' IDENT '{' pool+ message* messageFlow* topElement* '}' EOF ;

// The optional `for <GroupClass>` names the ACL group class one instance
// of this process is scoped to (e.g. `pool MeetingOrganization for
// MeetingUnit`): every activity/gateway declared for this pool evaluates
// its pre/effect/post with `self` bound to one concrete instance of that
// class, so two instances (two groups) run as two independent processes
// instead of being conflated through `X.allInstances()`.
pool : 'pool' IDENT ('for' IDENT)? '{' nameProperty? laneDecl* '}' ;
laneDecl : 'lane' IDENT ';' ;

topElement
    : startDecl
    | endDecl
    | eventDecl
    | activityDecl
    | gatewayDecl
    ;

// start/end are process boundaries, not events: a start has no incoming
// flow and always leads somewhere, an end has no outgoing flow at all —
// neither catches nor throws anything the way an intermediate event does.
// None of the three carries a name in the metamodel (StartEvent/EndEvent/
// IntermediateEvent list only trigger/direction, never name).
startDecl
    : 'start' IDENT '{'
        laneProperty
        triggerProperty
        preProperty?
        flowProperty
      '}'
    ;
endDecl
    : 'end' IDENT '{'
        laneProperty
        triggerProperty
      '}'
    ;

// `event` now means exactly what the metamodel's IntermediateEvent is:
// the only flow element that actually catches or throws a trigger
// mid-process, hence the mandatory `direction`.
eventDecl
    : 'event' IDENT '{'
        laneProperty
        triggerProperty
        directionProperty
        flowProperty?
      '}'
    ;

// An activity is Task, CallActivity, or SubProcess (the metamodel's three
// concrete Activity subtypes); `name` is optional since CallActivity
// carries none. Field order is fixed: name, type, lane, pre, post, flow.
//
// There is no separate `effect`: `post` is the only state-changing clause.
// When it is a conjunction of `self.attr = Expr` / `Coll->forAll(v | v.attr
// = Expr)` atoms (optionally `= true`/`= false` written as a bare/negated
// attribute, and Expr may reference `@pre` for a relative change, e.g.
// `self.num = self.num@pre + 5`), the runtime mechanically synthesizes the
// SOIL/Event-B action from it — see Bpmn2PostEffect. A `post` outside that
// shape is still legal but is verification-only: nothing executes it, and
// the real state change is expected to come from an external adapter.
activityDecl
    : 'activity' IDENT '{'
        nameProperty?
        activityTypeProperty
        laneProperty
        preProperty?
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
