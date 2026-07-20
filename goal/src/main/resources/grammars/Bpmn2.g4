grammar Bpmn2;

@header { package org.vnu.sme.goal.bpmn2.parser; }

// =====================================================================
//  BPMN 2.0 Model Language (Process + Collaboration, hợp nhất)
//  Khớp 1-1 với doc/05-bpmn2-metamodel.drawio (Unified Metamodel).
//
//  model ModelName {
//    pool PoolId "Pool Label" {
//      lane LaneId "Lane Label" {
//        start         EventId : none|message|timer|signal|conditional
//        end           EventId : none|message|error|signal|terminate|compensation
//        intermediate  EventId : message|timer|signal : catching|throwing
//        task          TaskId "Task Label" pre ocl {[ ... ]} do soil {[ ... ]} post ocl {[ ... ]}
//        call-activity CallId pre ocl {[ ... ]} do soil {[ ... ]} post ocl {[ ... ]}
//        subprocess    SubId "Label" pre ocl {[ ... ]} do soil {[ ... ]} post ocl {[ ... ]} {
//          task ...
//          flow Src -> Tgt
//        }
//        gateway GwId : xor|and|or|event-based
//      }
//      flow Src -> Tgt : "condition label"
//    }
//    message MsgId "Message label"
//    message-flow SrcId -> TgtId : MsgId
//  }
// =====================================================================

model : 'model' IDENT '{' pool+ message* messageFlow* '}' EOF ;

pool : 'pool' IDENT STRING? '{' lane* poolElement* sequenceFlow* '}' ;

lane : 'lane' IDENT STRING? '{' poolElement* '}' ;

poolElement
    : 'start'         IDENT (':' eventType)?                                 # elemStart
    | 'end'           IDENT (':' eventType)?                                 # elemEnd
    | 'intermediate'  IDENT (':' eventType)? (':' eventDir)?                # elemIntermediate
    | 'task'          IDENT STRING? activityBehavior*                         # elemTask
    | 'call-activity' IDENT activityBehavior*                                 # elemCallActivity
    | 'subprocess'    IDENT STRING? activityBehavior* '{' poolElement* sequenceFlow* '}' # elemSubProcess
    | 'gateway'       IDENT ':' gwType                                       # elemGateway
    ;

sequenceFlow : 'flow' IDENT '->' IDENT (':' STRING)? ;

activityBehavior
    : activityConstraint
    | activityEffect
    ;

activityConstraint
    : ('pre' | 'post') oclClause
    ;

activityEffect
    : 'do' soilClause
    ;

oclClause : OCL_CLAUSE ;
soilClause : SOIL_CLAUSE ;

message : 'message' IDENT STRING? ;

messageFlow : 'message-flow' IDENT '->' IDENT (':' IDENT)? ;

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
SOIL_CLAUSE : 'soil' [ \t\r\n\f]* '{[' .*? ']}' ;

WS            : [ \t\r\n\f]+ -> skip ;
LINE_COMMENT  : '//' ~[\r\n]*  -> skip ;
BLOCK_COMMENT : '/*' .*? '*/'  -> skip ;
