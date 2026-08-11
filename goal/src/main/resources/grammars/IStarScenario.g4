grammar IStarScenario;

@header { package org.vnu.sme.goal.dsl.iscn.parser; }

// =====================================================================
//  i* Scenario — a SOIL-style instance script for an i* 2.0 goal model.
//
//  The goal model (.istar) is the "class diagram" — a type-level SR/SD
//  structure, where each actor block is (like a UML class) a TYPE, not
//  an instance. This scenario (.iscn) is its "object diagram": an
//  'instance' block declares N objects of one actor type — like USE's
//  '!new Candidate(...)' — and each fire is qualified 'instanceId.id'.
//  With >=1 instance declared, each instance gets its OWN independent
//  trace through the *whole* goal model (its private copy of whatever
//  singleton actors it depends on), because a shared actor like HR
//  processes each candidate's application separately — one shared
//  ScreenApplication value could not tell "screened c1" from "screened
//  c2" apart. Satisfaction saturates per-instance through the same
//  AND/OR-refinement and Make/Break-contribution rules the conformance
//  checker already uses (see IStarPropagation, Fig. 7 of
//  Caballero-Villalobos). 'aggregate' then checks a property across all
//  declared instances at once ("does EVERY / does ANY instance satisfy
//  X"), which is what a single marking could never answer.
//
//  Setting an instance's state has two equivalent forms (see
//  doc/concept-domain-model-vs-scenario.md) — neither requires the other,
//  pick whichever describes the state more directly:
//
//   - 'fire' (state as DERIVED result): the element really was performed
//     (rule P_leaf) and the rest of its status is inferred by
//     AND/OR-refinement + Make/Break-contribution saturation (see
//     IStarPropagation, Fig. 7 of Caballero-Villalobos).
//   - 'assign' (state as INPUT): set a Goal/Task/Quality's status directly,
//     then saturate from there. Use this for a goal whose satisfaction
//     comes from a Dependency, not the depender's own action (the
//     depender didn't "do" anything — the dependee's completion is what
//     satisfies it), or for describing a partial/mid-way state with no
//     need to replay how it was reached.
//
//  Without any 'instance' declaration, a bare (unqualified) 'fire'/'assign'
//  behaves exactly as the original single-instance scenario (fully
//  backward compatible). With instances declared, an unqualified
//  statement broadcasts to every instance's private trace.
//
//    scenario ScenarioName for "model.istar" {
//      instance c1, c2 : Candidate;
//
//      fire c1.SubmitApplication;
//      assign c1.ReviewDelegatedToManager = Fulfilled;
//      assign c1.NoCommonSlot = Active;
//      assign c1.NoCommonSlot = Resolved;
//
//      aggregate AllHired : all of Candidate over TeamQuality;
//    }
//
//  'aggregate ... of ActorType over ...' restricts which declared instances the check ranges
//  over (only those of that one actor type) — needed once a scenario declares instances of
//  several actor types (e.g. both the singleton Organizer and the many-valued Candidate):
//  without it, "all over X" would range over every declared instance regardless of role,
//  diluting a check that's only meaningful for one of them. 'of ActorType' is optional; when
//  omitted, the check ranges over every declared instance (or the one implicit default
//  instance, if none were declared) — unchanged from before.
// =====================================================================

scenario : 'scenario' IDENT 'for' STRING '{' instanceDecl* stmt* '}' EOF ;

instanceDecl : 'instance' IDENT (',' IDENT)* ':' IDENT ';' ;

stmt
    : fireStmt      # stmtFire
    | assignStmt    # stmtAssign
    | aggregateStmt # stmtAggregate
    ;

fireStmt : 'fire' qualifiedId ('for' IDENT)? ';' ;

assignStmt : 'assign' qualifiedId '=' statusValue ';' ;

statusValue
    : 'Fulfilled' # svFulfilled
    | 'Pending'   # svPending
    | 'True'      # svTrue
    | 'False'     # svFalse
    | 'Active'    # svActive
    | 'Resolved'  # svResolved
    ;

qualifiedId : (IDENT '.')? IDENT ;

aggregateStmt : 'aggregate' IDENT ':' aggMode ('of' IDENT)? 'over' IDENT ';' ;

aggMode
    : 'all' # aggAll
    | 'any' # aggAny
    ;

// ── Lexer ─────────────────────────────────────────────────────────────

STRING : '"' (~["\r\n])* '"' ;
IDENT  : [a-zA-Z_][a-zA-Z0-9_]* ;

WS            : [ \t\r\n\f]+ -> skip ;
LINE_COMMENT  : '//' ~[\r\n]*  -> skip ;
BLOCK_COMMENT : '/*' .*? '*/'  -> skip ;
