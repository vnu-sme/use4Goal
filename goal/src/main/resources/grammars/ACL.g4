grammar ACL;

@header { package org.vnu.sme.goal.dsl.acl.parser; }

// Canonical ACL concrete syntax
// -----------------------------
// The surface mirrors docs/model/acl.ecore 1:1: AclModel owns Object
// declarations (Entity, Role, Group, Association), named DataType /
// Enumeration declarations, OrgCtx declarations, and Invariants. An OrgCtx
// (which now has a `name`) recursively contains Entity, Role, Association
// and child OrgCtx declarations. Entity, Role and Group are the
// state-bearing Objects. A Group is an Entity that also composes Role /
// Entity memberships with multiplicity. Both the top-level and the
// orgContext-nested placements are valid abstract syntax.
//
// ─────────────────────────────────────────────────────────────────────
// CHANGE LOG — this grammar is now a FAITHFUL concrete syntax of
// docs/model/acl.ecore. Every production maps to an EClass/feature; the
// ecore itself was completed this pass to remove its own gaps.
// ─────────────────────────────────────────────────────────────────────
//  v3.1:
//   1. `association | aggregation | composition` allowed INSIDE `orgContext`
//      -> OrgCtx.associations.
//   2. `endpointDecl endpointDecl+` (>= 2 ends) -> Association.ends [2..*].
//   3. `datatype IDENT ;` -> AclModel.dataTypes.
//  v3.2 (ecore completion — nothing below is an extension any more):
//   4. `enum IDENT { ... }` -> Enumeration (a DataType subclass with
//      `literals`), newly defined in acl.ecore. USE `enum` maps 1:1.
//   5. `orgContext IDENT` -> OrgCtx.name (added; a context now has an
//      identity that BPMN `pool ... for X` and `.aclboundary` can name).
//   6. `group IDENT { attrs; MemberName[card]; X compatible Y; }`
//      -> Group (Entity subclass) with roleMemberships / entityMemberships
//      (RoleMembership / EntityMembership carry lower/upper), added to
//      acl.ecore. `compatible` still fills Role.compatibility (symmetric).
//   7. `context X inv N: <ocl> ;` -> Invariant + AclModel.invariants
//      (contextName / name / oclBody), added to acl.ecore.
//   8. `aggregation` / `composition` -> Association.kind : AssociationRelKind
//      {PLAIN, AGGREGATION, COMPOSITION}, added to acl.ecore.
//   9. `*` in a cardinality maps to upper = -1 (AssociationEnd / *Membership).
// Java to update: regenerate dsl.acl.parser; builder reads the new
// classes; translate.acl2use / acl2eventb emit orgContext associations,
// datatypes, enums, group memberships, invariants; EMF codegen from the
// updated acl.ecore.
model
    : 'acl' VERSION IDENT '{' topLevelDecl* '}' EOF
    ;

topLevelDecl
    : enumDecl
    | datatypeDecl
    | entityDecl
    | roleDecl
    | orgContextDecl
    | groupDecl
    | entityRelationDecl
    | invariantDecl
    ;

// OCL is intentionally captured rather than interpreted by this grammar.
// NativeOclEvaluator is the single authority that validates/evaluates the
// expression.  Keeping punctuation here prevents the ACL parser from
// rejecting valid OCL text before it reaches that component.
invariantDecl
    : 'context' IDENT 'inv' IDENT ':' oclExpression ';'
    ;

oclExpression
    : oclToken+
    ;

oclToken
    : IDENT
    | oclKeyword
    | STRING_LITERAL
    | SIGNED_NUMBER
    | BOOLEAN
    | INT
    | '.' | '->' | '(' | ')' | '[' | ']' | '{' | '}'
    | ',' | '|' | '@' | '#' | '::' | ':'
    | '=' | '<>' | '<' | '<=' | '>' | '>='
    | '+' | '-' | '*' | '/'
    ;

// Literal keywords receive their own implicit lexer tokens.  Listing them
// here also permits an OCL property/operation to have one of these names.
oclKeyword
    : 'acl'
    | 'enum'
    | 'datatype'
    | 'entity'
    | 'role'
    | 'specializes'
    | 'extends'
    | 'orgContext'
    | 'group'
    | 'association'
    | 'aggregation'
    | 'composition'
    | 'compatible'
    | 'context'
    | 'inv'
    | 'attribute'
    | 'optional'
    | 'required'
    | 'mutable'
    | 'default'
    ;

// acl.ecore Enumeration (a DataType subclass): name + ordered literals.
enumDecl : 'enum' IDENT '{' IDENT (',' IDENT)* ','? '}' ;

// acl.ecore DataType (bare, name only). Primitive names (Boolean, Integer,
// Real, String) remain usable without declaration; this is for a named
// domain type a downstream translator maps explicitly.
datatypeDecl : 'datatype' IDENT ';' ;

entityDecl : 'entity' IDENT specializesClause? (';' | attributeBlock) ;
roleDecl : 'role' IDENT specializesClause? (';' | attributeBlock) ;
specializesClause : ('specializes' | 'extends') IDENT ;

// acl.ecore OrgCtx: a NAMED structural container that owns Entity / Role /
// Association / child-OrgCtx declarations directly and carries no state
// attributes of its own.
orgContextDecl
    : 'orgContext' IDENT '{' orgContextItem* '}'
    ;

orgContextItem
    : entityDecl
    | roleDecl
    | orgContextDecl
    | entityRelationDecl
    | compatibilityDecl
    ;

attributeBlock : '{' attributeDecl* '}' ;
attributeDecl : 'attribute'? IDENT ':' IDENT attributeModifier* defaultClause? ';' ;
// UML-B default: a scalar Property has multiplicity [1].  `optional` changes
// it to [0..1].  `required` remains accepted as an explicit/legacy spelling
// of the default so existing ACL models continue to parse.
attributeModifier : 'optional' | 'required' | 'mutable' ;
defaultClause : 'default' defaultValue ;
defaultValue : STRING_LITERAL | INT | SIGNED_NUMBER | BOOLEAN | IDENT ;

// Group members may resolve only to Role or Group classifiers. Entity
// participation must be declared as an explicit relationship outside Group.
groupDecl : 'group' IDENT specializesClause? '{' groupItem* '}' ;
groupItem
    : attributeDecl
    | groupMemberDecl
    | entityRelationDecl
    | compatibilityDecl
    ;
groupMemberDecl : IDENT cardinality ';' ;

// acl.ecore Association::ends [2..*]. Two ends are the common case; extra
// ends are accepted for n-ary associations. Each end is a USE-style member
// end: `Classifier [multiplicity] role navigationName;` (the `role`
// keyword is optional, legacy).
entityRelationDecl : relationKind IDENT '{' endpointDecl endpointDecl+ '}' ;
relationKind
    : 'association'
    | 'aggregation'
    | 'composition'
    ;
endpointDecl : IDENT cardinality ('role'? IDENT)? ';' ;

compatibilityDecl
    : IDENT 'compatible' IDENT ';'
    ;

cardinality
    : '[' INT ']'
    | '[' INT '..' (INT | '*') ']'
    | '[' '*' ']'
    ;

VERSION        : 'v' [0-9]+ '.' [0-9]+ ;
BOOLEAN        : 'true' | 'false' ;
INT            : [0-9]+ ;
SIGNED_NUMBER  : '-'? [0-9]+ ('.' [0-9]+)? ;
STRING_LITERAL : '"' ('\\' . | ~["\\\r\n])* '"' ;
IDENT          : [a-zA-Z_] [a-zA-Z0-9_]* ;
WS            : [ \t\r\n\f]+ -> skip ;
LINE_COMMENT  : '//' ~[\r\n]* -> skip ;
BLOCK_COMMENT : '/*' .*? '*/' -> skip ;
