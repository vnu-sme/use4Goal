grammar AOL;

@header { package org.vnu.sme.goal.dsl.aol.parser; }

// =====================================================================
//  AOL — ACL Object Language: an object-diagram-style instance snapshot
//  for an ACL structural specification.
//
//  The .acl file is the "class diagram" -- type-level: which Role/Entity/
//  Group types exist, their cardinality bounds, and which Role pairs may
//  be jointly held by one agent (compatibility). This .aol file is its
//  "object diagram": concrete Agent identities, concrete Group instances
//  (nested exactly like the .acl subgroup tree), concrete Entity
//  instances with attribute values, and 'play' statements binding one
//  Agent to one Role inside one Group instance.
//
//  Central point (see Metamodel/tmp/01-ACL-metamodel.drawio): a Role
//  instance is never an object with its own identity across occasions --
//  the Agent is the persistent identity. 'play' is the (Agent, Role,
//  GroupInstance) triple that makes a role concrete, exactly why the
//  eventual .use translation needs Role modeled as an associationclass
//  over (Agent, GroupInstance) rather than a bare class: an Agent, unlike
//  a plain UML Object, is not single-classified -- it may play several
//  Roles at once, gated by CompatLink (default: not allowed).
//
//  Example (against Metamodel's Company/Manager/Employee/Task showcase):
//
//    aol v1.0 CompanySnapshot for "full_showcase_diagram_notations.acl" {
//      agent alice, bob, carol;
//
//      group Company as company1 {
//        play Manager as m1 by alice;
//        play Employee as e1 by bob;
//
//        entity Task as t1 { done = true; }
//
//        group Team as team1 {
//          play Employee as e2 by carol;
//        }
//      }
//    }
//
//  Validation (see AolModelFactory) checks, against the referenced .acl
//  StructuralSpecification: every group/role/entity instance's declared
//  type actually exists at that nesting point; abstract roles are never
//  played directly; per-type-per-group-instance counts respect the
//  declared AclCardinality bounds; attribute values type-check against
//  AclDataType (including inherited role attributes); and -- the whole
//  point of this language -- for every Agent, every pair of *distinct*
//  Role types it plays within the same Group instance (propagated into
//  nested subgroup instances when the compatibility declares
//  extends-subgroups true) must be declared 'compatible' at the type
//  level, or it is a semantic error (default: incompatible).
// =====================================================================

model : 'aol' VERSION IDENT 'for' STRING_LITERAL '{' topLevelDecl* '}' EOF ;

topLevelDecl
    : agentDecl
    | groupInstanceDecl
    | entityInstanceDecl
    | linkDecl
    ;

agentDecl
    : 'agent' IDENT 'as' IDENT attributeValueBlock
    | 'agent' IDENT (',' IDENT)* ';'
    ;

groupInstanceDecl
    : 'group' IDENT 'as' IDENT (';' | '{' groupItemDecl* '}')
    ;

groupItemDecl
    : groupInstanceDecl
    | playDecl
    | entityInstanceDecl
    | attributeValue
    ;

playDecl
    : 'play' IDENT 'as' IDENT 'by' IDENT (';' | attributeValueBlock)
    ;

entityInstanceDecl
    : 'entity' IDENT 'as' IDENT (';' | attributeValueBlock)
    ;

// Instantiates an ACL entityRelationDecl (association/aggregation/composition) between one
// source instance (agent, play or entity instance id) and one-or-more target instance ids --
// independent of any Group nesting, since ACL Entity--Role/Entity--Entity/Entity--Group
// relations are never Group-owned (see ACL_doc.md section 4 and 9).
linkDecl : 'link' IDENT ':' IDENT '->' IDENT (',' IDENT)* ';' ;

attributeValueBlock : '{' attributeValue* '}' ;

attributeValue : IDENT '=' value ';' ;

value
    : STRING_LITERAL
    | SIGNED_NUMBER
    | BOOLEAN
    | IDENT
    ;

VERSION        : 'v' [0-9]+ '.' [0-9]+ ;
BOOLEAN        : 'true' | 'false' ;
SIGNED_NUMBER  : '-'? [0-9]+ ('.' [0-9]+)? ;
STRING_LITERAL : '"' ('\\' . | ~["\\\r\n])* '"' ;
IDENT          : [a-zA-Z_] [a-zA-Z0-9_]* ;

WS            : [ \t\r\n\f]+ -> skip ;
LINE_COMMENT  : '//' ~[\r\n]* -> skip ;
BLOCK_COMMENT : '/*' .*? '*/' -> skip ;
