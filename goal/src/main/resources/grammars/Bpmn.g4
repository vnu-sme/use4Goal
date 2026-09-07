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
//
// ─────────────────────────────────────────────────────────────────────
// CHANGE LOG — this grammar is now a FAITHFUL concrete syntax of
// docs/model/bpmn.ecore. Every production maps to an EClass/feature.
// ─────────────────────────────────────────────────────────────────────
// v2.1:
//  1. `name STRING` on start / end / gateway / event -> FlowElement.name
//     (it is on the abstract base, so every concrete element carries one).
//  2. `label STRING` on any `flow`/branch -> SequenceFlow.label.
//  3. `lang IDENT` before a pre / post / branch-post -> Condtion.condLang.
//  4. `name STRING` on `lane` -> Lane.name (the `lane` IDENT is Lane.roleName,
//     Lane.id is derived from it).
// v2.2 (this pass — bpmn.ecore was completed so the grammar has NO
//       remaining extensions):
//  5. `trigger <eventType>` on start/end/event is now backed by
//     Event.trigger : EventTrigger {none,message,timer,error,signal,
//     terminate,compensation,conditional} — added to bpmn.ecore.
//  6. gateway kind `event-based` is now GatewayKind::EVENT_BASED, and
//     Gateway::SupportedGatewayKind was widened to XOR/AND/OR/EVENT_BASED
//     so the enum and its own constraint agree.
//  7. `event` `direction` is now typed EventDirection {catching,throwing}
//     (the ecore feature previously had no eType).
//  `when` is kept only as an alias spelling of a branch `post` (guard).
// Java to update: regenerate dsl.bpmn.parser; builder reads element/lane
// `name`, flow `label`, `condLang`; EMF codegen from the updated
// bpmn.ecore (adds EventTrigger/EventDirection enums, Event.trigger).
model : 'model' IDENT '{' pool+ message* messageFlow* topElement* '}' EOF ;

// The optional `for <GroupClass>` names the ACL group/orgContext class one
// instance of this process is scoped to (e.g. `pool MeetingOrganization for
// MeetingUnit`): every activity/gateway declared for this pool evaluates
// its pre/effect/post with `self` bound to one concrete instance of that
// class, so two instances (two groups) run as two independent processes
// instead of being conflated through `X.allInstances()`.
pool : 'pool' IDENT ('for' IDENT)? '{' nameProperty? laneDecl* '}' ;
// `lane <RoleName>` — the identifier is Lane.roleName (and Lane.id).
// Optional `name` gives a distinct Lane.name display label.
laneDecl : 'lane' IDENT nameProperty? ';' ;

topElement
    : startDecl
    | endDecl
    | eventDecl
    | activityDecl
    | gatewayDecl
    ;

// start/end are process boundaries: a start has no incoming flow and always
// leads somewhere, an end has no outgoing flow at all — neither catches nor
// throws anything the way an intermediate event does. `name` is optional
// (FlowElement.name); `trigger` is a retained project extension.
startDecl
    : 'start' IDENT '{'
        nameProperty?
        laneProperty
        triggerProperty
        preProperty?
        flowProperty
      '}'
    ;
endDecl
    : 'end' IDENT '{'
        nameProperty?
        laneProperty
        triggerProperty
      '}'
    ;

// `event` is the metamodel's IntermediateEvent: the only flow element that
// actually catches or throws a trigger mid-process, hence mandatory
// `direction`.
eventDecl
    : 'event' IDENT '{'
        nameProperty?
        laneProperty
        triggerProperty
        directionProperty
        flowProperty?
      '}'
    ;

// An activity is Task, CallActivity, or SubProcess (the metamodel's three
// concrete Activity subtypes). Field order is fixed: name, type, lane, pre,
// post, flow.
//
// There is no separate `effect`: `post` is the only state-changing clause.
// When it is a conjunction of `self.attr = Expr` / `Coll->forAll(v | v.attr
// = Expr)` atoms (optionally `= true`/`= false` written as a bare/negated
// attribute, and Expr may reference `@pre` for a relative change, e.g.
// `self.num = self.num@pre + 5`), the runtime mechanically synthesizes the
// SOIL/Event-B action from it — see Bpmn2PostEffect. A `post` outside that
// shape is still legal but is verification-only.
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

// A gateway has no single node-level postcondition. Each outgoing flow may
// instead carry its own branch-specific `post`, matching the formal mapping
// Post_B((u,v)) = post_F((u,v)). `when` and `default` remain accepted for
// compatibility with older examples and execution adapters.
gatewayDecl
    : 'gateway' IDENT '{'
        nameProperty?
        laneProperty
        gatewayTypeProperty
        preProperty?
        gatewayFlow+
      '}'
    ;
gatewayTypeProperty : 'type' gwType ;
gatewayFlow : 'flow' IDENT labelProperty? gatewayFlowCondition? ;
gatewayFlowCondition : 'post' condLang? stateClause | 'when' condLang? stateClause | 'default' ;

laneProperty : 'lane' IDENT ;
flowProperty : 'flow' IDENT labelProperty? ;
labelProperty : 'label' STRING ;

message : 'message' IDENT ('{' nameProperty? '}')? ;
messageFlow : 'message-flow' IDENT '->' IDENT ('{' messageProperty? '}')? ;

nameProperty : 'name' STRING ;
triggerProperty : 'trigger' eventType ;
directionProperty : 'direction' eventDir ;
messageProperty : 'message' IDENT ;
preProperty : 'pre' condLang? stateClause ;
postProperty : 'post' condLang? stateClause ;
condLang : 'lang' IDENT ;
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
