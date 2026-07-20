grammar Bpmn2;

@header { package org.vnu.sme.goal.bpmn2.parser; }

// =====================================================================
//  BPMN 2.0 Model Language (Process + Collaboration, hợp nhất)
//  Khớp 1-1 với doc/05-bpmn2-metamodel.drawio (Unified Metamodel).
//
//  model ModelName {
//    pool PoolId "Pool Label" {
//      lane LaneId "Lane Label" {
//        start         EventId : none|message|timer|signal|conditional [ocl {[ raw-OCL ]}]
//        end           EventId : none|message|error|signal|terminate|compensation [ocl {[ raw-OCL ]}]
//        intermediate  EventId : message|timer|signal : catching|throwing [ocl {[ raw-OCL ]}]
//        task          TaskId "Task Label" [ocl {[ raw-OCL ]}]
//        call-activity CallId [ocl {[ raw-OCL ]}]
//        subprocess    SubId "Label" {
//          task ...
//          flow Src -> Tgt
//        } [ocl {[ raw-OCL ]}]
//        gateway GwId : xor|and|or|event-based [ocl {[ raw-OCL ]}]
//      }
//      flow Src -> Tgt : "condition label" [ocl {[ raw-OCL ]}]
//    }
//    message MsgId "Message label"
//    message-flow SrcId -> TgtId : MsgId
//  }
// =====================================================================

model : 'model' IDENT '{' pool+ message* messageFlow* '}' EOF ;

pool : 'pool' IDENT STRING? '{' lane* poolElement* sequenceFlow* '}' ;

lane : 'lane' IDENT STRING? '{' poolElement* '}' ;

poolElement
    : 'start'         IDENT (':' eventType)? oclClause?                                # elemStart
    | 'end'           IDENT (':' eventType)? oclClause?                                # elemEnd
    | 'intermediate'  IDENT (':' eventType)? (':' eventDir)? oclClause?                # elemIntermediate
    | 'task'          IDENT STRING? oclClause?                                         # elemTask
    | 'call-activity' IDENT oclClause?                                                 # elemCallActivity
    | 'subprocess'    IDENT STRING? '{' poolElement* sequenceFlow* '}' oclClause?      # elemSubProcess
    | 'gateway'       IDENT ':' gwType oclClause?                                      # elemGateway
    ;

sequenceFlow : 'flow' IDENT '->' IDENT (':' STRING)? oclClause? ;

message : 'message' IDENT STRING? ;

messageFlow : 'message-flow' IDENT '->' IDENT (':' IDENT)? ;

oclClause : OCL_CLAUSE ;

eventType
    : 'none'         # evtNone
    | 'message'      # evtMessage
    | 'timer'        # evtTimer
    | 'error'        # evtError
    | 'signal'       # evtSignal
    | 'terminate'    # evtTerminate
    | 'compensation' # evtCompensation
    | 'conditional'  # evtConditional
    ;

eventDir : 'catching' | 'throwing' ;

gwType
    : 'xor'          # gwXor
    | 'and'          # gwAnd
    | 'or'           # gwOr
    | 'event-based'  # gwEventBased
    ;

// ── Lexer ─────────────────────────────────────────────────────────────

IDENT  : [a-zA-Z_][a-zA-Z0-9_]* ;
STRING : '"' (~["\r\n\\] | '\\' .)* '"' ;
OCL_CLAUSE : 'ocl' [ \t\r\n\f]* '{[' .*? ']}' ;

WS            : [ \t\r\n\f]+ -> skip ;
LINE_COMMENT  : '//' ~[\r\n]*  -> skip ;
BLOCK_COMMENT : '/*' .*? '*/'  -> skip ;
