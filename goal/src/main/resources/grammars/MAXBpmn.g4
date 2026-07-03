grammar MAXBpmn;

@header { package org.vnu.sme.goal.maxbpmn.parser; }

// ====================================================================
//  MAXBpmn — BPMN Notation produced by the MAXGoal transformer
//
//  process Name {
//    lane LaneId "Actor Label" {
//      start  StartId
//      end    EndId
//      task   TaskId "label"
//      gateway GwId : XOR|PAR|IOR fork|join
//    }
//    flow   SourceId -> TargetId : "label"
//    message SourceId -> TargetId
//  }
// ====================================================================

bpmnFile : processDecl+ EOF ;

processDecl : 'process' IDENT '{' processStmt* '}' ;

processStmt
    : laneDecl
    | flowStmt
    | messageStmt
    ;

laneDecl : 'lane' IDENT STRING? '{' nodeDecl* '}' ;

nodeDecl
    : 'start'   IDENT              # startNode
    | 'end'     IDENT              # endNode
    | 'task'    IDENT STRING?      # taskNode
    | 'gateway' IDENT ':' gwType gwRole # gwNode
    ;

gwType : 'XOR' | 'PAR' | 'IOR' ;
gwRole : 'fork' | 'join' ;

flowStmt    : 'flow'    IDENT '->' IDENT (':' STRING)? ;
messageStmt : 'message' IDENT '->' IDENT               ;

IDENT  : [a-zA-Z_][a-zA-Z0-9_]* ;
STRING : '"' (~["\r\n\\] | '\\' .)* '"' ;

WS            : [ \t\r\n\f]+ -> skip ;
LINE_COMMENT  : '//' ~[\r\n]* -> skip ;
BLOCK_COMMENT : '/*' .*? '*/' -> skip ;
