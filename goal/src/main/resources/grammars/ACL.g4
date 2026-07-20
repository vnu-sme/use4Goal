grammar ACL;

@header { package org.vnu.sme.goal.acl.parser; }

model : 'acl' VERSION? IDENT '{' decl* '}' EOF ;

decl
    : enumDecl
    | entityDecl
    | actorDecl
    | relationshipDecl
    | partOfDecl
    | groupDecl
    | linkDecl
    | invariantDecl
    ;

enumDecl : 'enum' IDENT '{' IDENT (',' IDENT)* '}' ;

entityDecl : 'entity' IDENT attributeBlock? ;

actorDecl : actorKind IDENT specializes? attributeBlock? ;

actorKind
    : 'agent'
    | 'role'
    | 'abstract' 'role'
    ;

specializes : 'specializes' IDENT ;

attributeBlock : '{' attribute* '}' ;

attribute : IDENT ':' typeRef ';' ;

typeRef : IDENT ;

relationshipDecl : 'relationship' IDENT endpointBlock ;

partOfDecl : 'partOf' IDENT endpointBlock ;

endpointBlock : '{' endpoint+ '}' ;

endpoint : IDENT multiplicity IDENT ';' ;

groupDecl : 'group' IDENT specializes? '{' groupItem* '}' ;

groupItem
    : attribute
    | groupMember
    ;

groupMember : IDENT multiplicity ';' ;

linkDecl : 'link' linkKind IDENT '->' IDENT linkScope? ';' ;

linkKind
    : 'authority'
    | 'communication'
    | 'acquaintance'
    | 'compatibility'
    | IDENT
    ;

linkScope : ('intra' | 'inter') IDENT ;

invariantDecl : 'invariant' IDENT 'context' IDENT oclClause ;

multiplicity : '[' bound ('..' bound)? ']' ;

bound : INT | '*' ;

oclClause : OCL_CLAUSE ;

OCL_CLAUSE : 'ocl' [ \t\r\n\f]* '{[' .*? ']}' ;
VERSION    : 'v' [0-9]+ '.' [0-9]+ ;
IDENT      : [a-zA-Z_][a-zA-Z0-9_]* ;
INT        : [0-9]+ ;

WS            : [ \t\r\n\f]+ -> skip ;
LINE_COMMENT  : '//' ~[\r\n]*  -> skip ;
BLOCK_COMMENT : '/*' .*? '*/'  -> skip ;
