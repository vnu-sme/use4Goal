grammar AOL;

@header { package org.vnu.sme.goal.dsl.aol.parser; }

// AOL v2 is the concrete syntax of the formal ACL state
// (sigma_Class, sigma_Att, sigma_Assoc, sigma_Play):
//
//   group Classroom as classroom1;
//   role Person as person1;
//   role Teacher as teacher1;
//   play person1 -> teacher1;
//   link Classroom_contains_Teacher : classroom1 -> teacher1;
//
// Entity, Role and Group instances are the only objects.  `play` links a
// parent Role object to a direct child Role object.  Group containment is an
// ordinary composition link in sigma_Assoc.  There is no Agent classifier.
//
// The agent declaration and `play Role as id by agent` productions below are
// retained solely so legacy AOL v1 tools can still parse their old fixtures.
// AclSystemStateCompiler, used by ACL State Evaluator, requires v2.0 and
// rejects those legacy constructs.

model : 'aol' VERSION IDENT 'for' STRING_LITERAL '{' topLevelDecl* '}' EOF ;

topLevelDecl
    : agentDecl
    | groupInstanceDecl
    | entityInstanceDecl
    | roleInstanceDecl
    | playLinkDecl
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

// AOL v2 formal-state declarations.  A Role is an object in its own oid(r)
// domain.  `play parent -> child` instantiates sigma_Play between two Role
// objects; it is deliberately not an Agent--Role binding.
roleInstanceDecl
    : 'role' IDENT 'as' IDENT (';' | attributeValueBlock)
    ;

playLinkDecl
    : 'play' IDENT '->' IDENT ';'
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
