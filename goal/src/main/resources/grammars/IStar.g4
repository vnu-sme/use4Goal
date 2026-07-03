grammar IStar;

@header { package org.vnu.sme.goal.istar.parser; }

// =====================================================================
//  iStar 2.0 — Strategic Rationale (SR) view language
//
//  istar ModelName {
//    actor|role|agent ActorName {
//      goal     GoalId
//      task     TaskId
//      resource ResourceId
//      quality  QualityId
//      and-refine ParentId : Child1, Child2, ...
//      or-refine  ParentId : ChildId
//      needed-by  ResourceId for TaskId
//      contributes ElementId make|help|hurt|break|unknown|some+|some- to QualityId
//      qualifies   QualityId ElementId
//      ActorId is-a           SuperActorId
//      ActorId participates-in RoleId
//    }
//    depend DependerId -> DependumId -> DependeeId
//  }
// =====================================================================

model : 'istar' IDENT '{' actorDef* dependency* '}' EOF ;

actorDef : actorKind IDENT '{' actorBody* '}' ;

actorKind : 'actor' | 'role' | 'agent' ;

actorBody
    : 'goal'             IDENT                         # bodyGoal
    | 'task'             IDENT                         # bodyTask
    | 'resource'         IDENT                         # bodyResource
    | 'quality'          IDENT                         # bodyQuality
    | 'and-refine'       IDENT ':' IDENT (',' IDENT)*  # bodyAndRefine
    | 'or-refine'        IDENT ':' IDENT               # bodyOrRefine
    | 'needed-by'        IDENT 'for' IDENT             # bodyNeededBy
    | 'contributes'      IDENT contribType 'to' IDENT  # bodyContrib
    | 'qualifies'        IDENT IDENT                   # bodyQualify
    | IDENT 'is-a'           IDENT                     # bodyIsA
    | IDENT 'participates-in' IDENT                    # bodyParticipates
    ;

dependency : 'depend' IDENT '->' IDENT '->' IDENT ;

contribType
    : 'make'    # ctMake
    | 'help'    # ctHelp
    | 'hurt'    # ctHurt
    | 'break'   # ctBreak
    | 'unknown' # ctUnknown
    | SOME_PLUS # ctSomePlus
    | SOME_MINUS # ctSomeMinus
    ;

// ── Lexer ─────────────────────────────────────────────────────────────

SOME_PLUS  : 'some+' ;
SOME_MINUS : 'some-' ;

IDENT  : [a-zA-Z_][a-zA-Z0-9_]* ;

WS            : [ \t\r\n\f]+ -> skip ;
LINE_COMMENT  : '//' ~[\r\n]*  -> skip ;
BLOCK_COMMENT : '/*' .*? '*/'  -> skip ;
