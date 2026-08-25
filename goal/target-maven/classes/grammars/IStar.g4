grammar IStar;

@header { package org.vnu.sme.goal.dsl.istar.parser; }

// =====================================================================
//  iStar 2.0 — SD (Strategic Dependency) + SR (Strategic Rationale) views
//
//  Declaration-only body: only intentional elements (goal/task/resource/
//  quality) are declared. Every element-to-element relation is
//  written inline at the child's own declaration — '>' relation target —
//  the same way Java writes 'class Foo extends Bar' at Foo's declaration
//  instead of as a separate statement. AND-refinement is the only relation
//  with no keyword (the default): multiple children written '> SameParent'
//  are grouped into one AND-refinement by the factory. Every other relation
//  (or, make/help/hurt/break, qualifies, needed-by)
//  requires its keyword — see doc/04-istar-metamodel.drawio.
//
//  Actor is abstract — only Role and Agent are concrete, both generalizing
//  to Actor. is-a/participates-in (actor-to-actor) and depend (cross-actor
//  SD dependency) are not intentional-element relations, so they keep their
//  own statement form.
//
//  istar ModelName {
//    role|agent ActorName {
//      goal     GoalId [: Achieve|Maintain|Sustain|Recur] rel* [ocl {[ raw-OCL ]}]
//      task     TaskId                            rel* [ocl {[ raw-OCL ]}]
//      resource ResourceId                        rel*
//      quality  QualityId                         rel*
//      ActorId is-a           SuperActorId
//      ActorId participates-in RoleId
//    }
//    // dependency composition-owns one newly declared dependum intentional element;
//    // goal|task|resource|quality selects its concrete EClass. ".Elmt"
//    // on either end is the optional SD "boundary
//    // opening" (Dependency.dependerElmt/dependeeElmt) — the SR element inside that
//    // actor's boundary the dependency arrow visually attaches to.
//    depend DependerId['.'DependerElmt] -> goal|task|resource|quality DependumId -> DependeeId['.'DependeeElmt]
//  }
//
//  Optional OCL clauses are captured as raw text. IStar.g4 does not parse OCL
//  grammar; the body is passed later to USE's OCLCompiler with a shadow
//  MModel/MSystemState. Prefer the readable block form:
//
//      ocl {[
//        self.someCondition()
//      ]}
//
//  The old single-line form 'ocl: raw ;' is still accepted for compatibility.
//
//  rel : '>' relation target
//    '> Parent'                                      AND-refinement
//    '> or Parent'                                   OR-refinement
//    '> make|help|hurt|break Quality'                contributes
//    '> qualifies Element'                           qualifies
//    '> needed-by Task'                              needed-by (from a resource)
// =====================================================================

model : 'istar' IDENT '{' actorDef* dependency* '}' EOF ;

actorDef : actorKind IDENT '{' actorBody* '}' ;

actorKind : 'role' | 'agent' ;

actorBody
    : 'goal'     IDENT goalType?     rel* goalCondition* # bodyGoal
    | 'task'     IDENT                rel* oclCondition*  # bodyTask
    | 'resource' IDENT                rel*  # bodyResource
    | 'quality'  IDENT                rel*  # bodyQuality
    | IDENT 'is-a'           IDENT         # bodyIsA
    | IDENT 'participates-in' IDENT        # bodyParticipates
    ;

goalType : ':' goalTypeName ;
goalTypeName : 'Achieve' | 'Maintain' | 'Sustain' | 'Recur' ;

rel
    : '>' target=IDENT                                                                    # relAnd
    | '>' 'or' target=IDENT                                                               # relOr
    | '>' contribType target=IDENT                                                        # relContribute
    | '>' 'qualifies' target=IDENT                                                        # relQualifies
    | '>' 'needed-by' target=IDENT                                                        # relNeededBy
    ;

dependency : 'depend' depEnd '->' dependumRef '->' depEnd ;

dependumRef : dependumKind IDENT ;

dependumKind : 'goal' | 'task' | 'resource' | 'quality' ;

depEnd : IDENT ('.' IDENT)? ;

contribType
    : 'make'  # ctMake
    | 'help'  # ctHelp
    | 'hurt'  # ctHurt
    | 'break' # ctBreak
    ;

oclCondition
    : ('pre' | 'post') OCL_BLOCK
    | OCL_CLAUSE
    ;

// Goal owns a condition contract. Task owns pre/post contracts.
// The older satisfy/ensure spellings remain accepted aliases.
goalCondition
    : ('condition' | 'satisfy' | 'ensure') OCL_BLOCK
    | OCL_CLAUSE
    ;

// ── Lexer ─────────────────────────────────────────────────────────────

OCL_CLAUSE
    : 'ocl' [ \t\r\n\f]* '{[' .*? ']}'
    | 'ocl:' (OCL_DQ_STRING | OCL_SQ_STRING | ~[;"'])* ';'
    ;

OCL_BLOCK : '{[' .*? ']}' ;

fragment OCL_DQ_STRING
    : '"' ('\\' . | ~["\\\r\n])* '"'
    ;

fragment OCL_SQ_STRING
    : '\'' ('\\' . | ~['\\\r\n])* '\''
    ;

IDENT  : [a-zA-Z_][a-zA-Z0-9_]* ;

WS            : [ \t\r\n\f]+ -> skip ;
LINE_COMMENT  : '//' ~[\r\n]*  -> skip ;
BLOCK_COMMENT : '/*' .*? '*/'  -> skip ;
