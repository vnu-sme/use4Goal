grammar ACL;

@header { package org.vnu.sme.goal.dsl.acl.parser; }

model : 'acl' VERSION IDENT '{' topLevelDecl* '}' EOF ;

topLevelDecl
    : enumDecl
    | entityDecl
    | roleDecl
    | groupDecl
    | entityRelationDecl
    ;

enumDecl : 'enum' IDENT '{' IDENT (',' IDENT)* ','? '}' ;

entityDecl : 'entity' IDENT specializesClause? (';' | attributeBlock) ;
roleDecl : 'role' IDENT specializesClause? (';' | attributeBlock) ;
specializesClause : ('specializes' | 'extends') IDENT ;

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
    | compatibilityDecl
    ;
groupMemberDecl : IDENT cardinality ';' ;

entityRelationDecl : relationKind IDENT '{' endpointDecl endpointDecl '}' ;
relationKind
    : 'association'
    | 'aggregation'
    | 'composition'
    ;
// USE-style member end: `Classifier [multiplicity] role navigationName;`.
// The `role` keyword remains optional only for legacy ACL files.
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
