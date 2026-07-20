grammar ACL;

@parser::header { package org.vnu.sme.goal.acl.parser; }
@lexer::header { package org.vnu.sme.goal.acl.parser; }

model
    : 'acl' VERSION IDENT '{' topLevelDecl* '}' EOF
    ;

topLevelDecl
    : enumDecl
    | roleDecl
    | entityDecl
    | groupDecl
    ;

enumDecl
    : 'enum' IDENT '{' IDENT (',' IDENT)* ','? '}'
    ;

roleDecl
    : 'abstract'? 'role' IDENT extendsClause? (';' | attributeBlock)
    ;

entityDecl
    : 'entity' IDENT (';' | attributeBlock)
    ;

extendsClause
    : ('extends' | 'specializes') IDENT (',' IDENT)*
    ;

attributeBlock
    : '{' attributeDecl* '}'
    ;

attributeDecl
    : 'attribute'? IDENT ':' IDENT attributeModifier* defaultClause? ';'
    ;

attributeModifier
    : 'required'
    | 'mutable'
    ;

defaultClause
    : 'default' defaultValue
    ;

defaultValue
    : STRING_LITERAL
    | INT
    | SIGNED_NUMBER
    | BOOLEAN
    | IDENT
    ;

groupDecl
    : 'group' IDENT '{' groupItem* '}'
    ;

groupItem
    : roleMembership
    | entityMembership
    | subgroupMembership
    | linkDecl
    | compatibilityDecl
    | roleEntityRelationDecl
    | cardinalityConstraint
    ;

roleMembership
    : 'role' IDENT cardinality ';'
    ;

entityMembership
    : 'entity' IDENT cardinality ';'
    ;

subgroupMembership
    : 'subgroup' IDENT cardinality '{' groupItem* '}'
    ;

linkDecl
    : 'link' linkType IDENT linkArrow IDENT linkOption* ';'
    ;

linkType
    : IDENT
    | 'authority'
    | 'communication'
    | 'acquaintance'
    ;

linkArrow
    : '->'
    | '<->'
    ;

linkOption
    : 'scope' scopeValue
    | 'extends-subgroups' BOOLEAN
    | 'bidirectional' BOOLEAN
    ;

scopeValue
    : IDENT
    | 'intra-group'
    | 'inter-group'
    ;

compatibilityDecl
    : 'compatibility' IDENT linkArrow IDENT compatibilityOption* ';'
    ;

compatibilityOption
    : 'scope' scopeValue
    | 'extends-subgroups' BOOLEAN
    | 'bidirectional' BOOLEAN
    ;

// ACL extension: a dedicated Role -> Entity relation.  The optional first
// identifier is a stable relation name; when omitted the factory derives one
// from the relation type and endpoints.
roleEntityRelationDecl
    : ('relation' | 'role-entity' | 'entity-link')
      (relationType IDENT '->' IDENT
       | IDENT relationType IDENT '->' IDENT)
      relationOption* ';'
    ;

relationType
    : 'creates'
    | 'reads'
    | 'writes'
    | 'uses'
    | 'owns'
    | 'provides'
    | 'consumes'
    | 'participates-in'
    ;

relationOption
    : 'scope' scopeValue
    | 'extends-subgroups' BOOLEAN
    ;

cardinalityConstraint
    : 'cardinality' targetKind IDENT cardinality ';'
    ;

targetKind
    : IDENT
    | 'role'
    | 'entity'
    | 'subgroup'
    ;

cardinality
    : '[' INT '..' (INT | '*') ']'
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
