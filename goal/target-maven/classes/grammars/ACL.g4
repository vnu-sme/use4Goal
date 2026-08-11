grammar ACL;

@header { package org.vnu.sme.goal.acl.parser; }

model : 'acl' VERSION IDENT '{' topLevelDecl* '}' EOF ;

topLevelDecl
    : enumDecl
    | entityDecl
    | roleDecl
    | groupDecl
    | entityRelationDecl
    | compatibilityDecl
    ;

enumDecl : 'enum' IDENT '{' IDENT (',' IDENT)* ','? '}' ;

entityDecl : 'entity' IDENT specializesClause? (';' | attributeBlock) ;
roleDecl : 'abstract'? 'role' IDENT specializesClause? (';' | attributeBlock)? ;
specializesClause : ('specializes' | 'extends') IDENT ;

attributeBlock : '{' attributeDecl* '}' ;
attributeDecl : 'attribute'? IDENT ':' IDENT attributeModifier* defaultClause? ';' ;
attributeModifier : 'required' | 'mutable' ;
defaultClause : 'default' defaultValue ;
defaultValue : STRING_LITERAL | INT | SIGNED_NUMBER | BOOLEAN | IDENT ;

// Group -> Role/Group members become Owner relations. Group -> Entity members
// become Entity composition relations, because Owner never targets Entity.
groupDecl : 'group' IDENT '{' groupItem* '}' ;
groupItem
    : attributeDecl
    | groupMemberDecl
    | legacyTypedMemberDecl
    | legacySubgroupDecl
    | compatibilityDecl
    ;
groupMemberDecl : IDENT cardinality ';' ;
legacyTypedMemberDecl : ('role' | 'entity') IDENT cardinality ';' ;
legacySubgroupDecl : 'subgroup' IDENT cardinality '{' groupItem* '}' ;

// relationship and partOf remain accepted aliases for association and
// composition so existing ACL v2 files can be migrated without ambiguity.
entityRelationDecl : relationKind IDENT '{' endpointDecl endpointDecl '}' ;
relationKind
    : 'association'
    | 'aggregation'
    | 'composition'
    | 'relationship'
    | 'partOf'
    ;
// USE-style member end: `Classifier [multiplicity] role navigationName;`.
// The `role` keyword remains optional only for legacy ACL files.
endpointDecl : IDENT cardinality ('role'? IDENT)? ';' ;

compatibilityDecl
    : 'compatibility' IDENT linkArrow IDENT compatibilityOption* ';'
    | 'link' 'compatibility' IDENT linkArrow IDENT linkScope? ';'
    | IDENT 'compatible' IDENT 'intra'? ';'
    ;
linkArrow : '->' | '<->' ;
linkScope : ('intra' | 'inter') IDENT ;
compatibilityOption
    : 'scope' scopeValue
    | 'extends-subgroups' BOOLEAN
    | 'bidirectional' BOOLEAN
    | 'type' compatibilityType
    ;
compatibilityType : 'compatible' | 'incompatible' ;
scopeValue : IDENT | 'intra-group' | 'inter-group' ;

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
