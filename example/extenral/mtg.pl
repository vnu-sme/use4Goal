%:-consult("../golog/golog.pl").
%:-use_module("../axioms/structural_axioms.pl").
%:-use_module("../axioms/explanation_axioms.pl").
%:-use_module("../axioms/explanation_engine.pl").
%:-use_module("../axioms/explanation_axioms_helpers.pl").
:-consult("../axioms/explain_main.pl").
:-multifile primitive_action/1.
:-style_check(-discontiguous).
:-style_check(-singleton).
:-set_prolog_flag(toplevel_print_anon, false).

%Run with: do(main).

% #     #                                #####                                                        
% ##   ##  ####  #####  ###### #        #     # ##### #####  #    #  ####  ##### #    # #####  ###### 
% # # # # #    # #    # #      #        #         #   #    # #    # #    #   #   #    # #    # #      
% #  #  # #    # #    # #####  #         #####    #   #    # #    # #        #   #    # #    # #####  
% #     # #    # #    # #      #              #   #   #####  #    # #        #   #    # #####  #      
% #     # #    # #    # #      #        #     #   #   #   #  #    # #    #   #   #    # #   #  #      
% #     #  ####  #####  ###### ######    #####    #   #    #  ####   ####    #    ####  #    # ###### 
  

% Goals
%% Initiator
goal(rootGoal(_)). % RESERVED


goal(haveMeetingOrganized(_)).
  goal(haveSchedulingPerformed(_)).
    goal(haveSchedulingPerformedByOrganizer(_,_)).
  goal(meetingAttended(_)).
    goal(meetingAttendedByParticipant(_,_)).
    

goal(haveMeetingScheduled(_)).
	goal(timetablesCollected(_)).
		goal(timetableCollected(_,_)).
			goal(havePPCalled(_,_)).
				goal(haveSecretaryCallPP(_,_,_)).
	goal(meetingAnnounced(_)).

or_node(timetableCollected(_,_)).

% Tasks
task(foo).
task(delegate(_,_,_,_)). %RESERVED
task(decideMeetingDetails(_)).
task(collectConstraintsByPhone(_,_,_)).
task(collectFromCallendar(_,_)).
task(chooseTimeandDate(_)).
task(announceMeeting(_,_)).
task(participate(_)).

% Precedences
pre(decideMeetingDetails(X),haveSchedulingPerformed(X)).
pre(haveSchedulingPerformed(X),meetingAttended(X)).

pre(timetablesCollected(X),chooseTimeandDate(X)).
pre(chooseTimeandDate(X),meetingAnnounced(X)).


% Hierarchy
parent(rootGoal(haveMeetingOrganized),haveMeetingOrganized(X)).
parent(haveMeetingOrganized(X),decideMeetingDetails(X)).
parent(haveMeetingOrganized(X),haveSchedulingPerformed(X)).
parent(haveMeetingOrganized(X),meetingAttended(X)).
parent(haveSchedulingPerformed(I),haveSchedulingPerformedByOrganizer(I,O)) :- organizer(O).
parent(meetingAttended(X),meetingAttendedByParticipant(X,_)).

parent(haveMeetingScheduled(X),timetablesCollected(X)).
parent(haveMeetingScheduled(X),chooseTimeandDate(X)).
parent(haveMeetingScheduled(X),meetingAnnounced(X)).

parent(timetablesCollected(X),timetableCollected(X,P)) :- potential_participant(P).
parent(timetableCollected(X,P),havePPCalled(X,P)).
parent(timetableCollected(X,P),collectFromCallendar(X,P)).
parent(havePPCalled(X,P),haveSecretaryCallPP(X,T,P)):- secretary(T).
parent(meetingAnnounced(X),announceMeeting(X,_)).

%parent(haveSecretaryCallPP(O,T,P),collectConstraintsByPhone(O,T,P)).

% Contributions
contr(havePPCalled(_,_),inclusivity,plus).
contr(collectFromCallendar(_,_),quickScheduling,plus).


% Dependencies
dependency(I,O,
           haveSchedulingPerformedByOrganizer(I,O),haveMeetingScheduled(O)):-
		   initiator(I),organizer(O).

dependency(O,S,
           haveSecretaryCallPP(O,S,P),collectConstraintsByPhone(O,S,P)):-
		   organizer(O),secretary(S).

dependency(I,P,
           meetingAttendedByParticipant(O,P),participate(P)):-
		   initiator(I),potential_participant(P).


%
% I N I T I A L I Z A T I O N
%

agent(xing).
agent(amr).
agent(naya).
potential_participant(xing).
potential_participant(amr).
potential_participant(naya).
important(xing).
important(amr).
initiator(abdul).
organizer(matilda).

hasPhoneNumber(alex,naya).
hasPhoneNumber(alex,amr).
hasPhoneNumber(alex,xing).

maintainsCallendar(amr).
maintainsCallendar(xing).

secretary(alex).

promote(quickScheduling).

ppJustification(amr,"needs to report on agenda items 3 and 4.").
ppJustification(xing,"is the recording secretary.").
ppJustification(naya,"is the chair of the meeting.").



%    #                                   #######                                   
%   # #    ####  ##### #  ####  #    #      #    #    # ######  ####  #####  #   # 
%  #   #  #    #   #   # #    # ##   #      #    #    # #      #    # #    #  # #  
% #     # #        #   # #    # # #  #      #    ###### #####  #    # #    #   #   
% ####### #        #   # #    # #  # #      #    #    # #      #    # #####    #   
% #     # #    #   #   # #    # #   ##      #    #    # #      #    # #   #    #   
% #     #  ####    #   #  ####  #    #      #    #    # ######  ####  #    #   #   
 



%
% PRIMITIVE ACTIONS LIST
%

primitive_action(X):-task(X).


%
% PROCEDURES, ATTEMPT, AND SATISFACTION FORMULAE
%

proc(main,
	pi(p, ?(initiator(p)) : haveMeetingOrganized(p))
).

% AND Decomposition
proc(haveMeetingOrganized(I),
	decideMeetingDetails(I) : haveSchedulingPerformed(I)  : meetingAttended(I)
).

% Conditinoal OR Decomposition
proc(haveSchedulingPerformed(I), 
	pi(o, ?(organizer(o)) : haveSchedulingPerformedByOrganizer(I,o))
).

% Delegation
proc(haveSchedulingPerformedByOrganizer(I,O),
	delegate(I,O, haveSchedulingPerformedByOrganizer(I,O),haveMeetingScheduled(O)) : haveMeetingScheduled(O)
).

% AND Decomposition
proc(haveMeetingScheduled(O),
							timetablesCollected(O) :
							chooseTimeandDate(O) : 
							meetingAnnounced(O)).


% Conditional AND Decomposition
proc(meetingAttended(I), 
	while( some(p, invited(p) & (-sat_meetingAttendedByParticipant(I,p))),
		  pi(p, ?(invited(p) & -sat_meetingAttendedByParticipant(I,p)) : meetingAttendedByParticipant(I,p))
		  )
).
	% Termination condition
	sat_meetingAttendedByParticipant(_,P,S) :- perf_participate(P,S).


% Delegation
proc(meetingAttendedByParticipant(I,P),
	delegate(I,P,meetingAttendedByParticipant(I,P),participate(P)) : participate(P)
).

% Conditional AND Decomposition
proc(timetablesCollected(O), 
	while( some(p, potential_participant(p) & (-sat_timetableCollected(O,p))),
		  pi(p, ?(potential_participant(p) & -sat_timetableCollected(O,p)) : timetableCollected(O,p))
		  )
).


% OR Decomposition
proc(timetableCollected(O,P), 
	havePPCalled(O,P) # collectFromCallendar(O,P)
).


% Conditional OR Decomposition
proc(havePPCalled(O,P), 
	pi(t, ?(secretary(t)) : haveSecretaryCallPP(O,t,P))
).


% Delegation
proc(haveSecretaryCallPP(O,T,P),
	delegate(O,T,haveSecretaryCallPP(O,T,P),collectConstraintsByPhone(O,T,P)) : collectConstraintsByPhone(O,T,P)
).


% Conditional AND Decomposition
proc(meetingAnnounced(C), 
	while( some(p, potential_participant(p) & (-perf_announceMeeting(C,p))),
		  pi(p, ?(potential_participant(p) & -perf_announceMeeting(C,p)) : announceMeeting(C,p))
		  )
).


%
% ACTION PRECONDITION AXIOMS
%

poss(foo,S).

poss(delegate(_,_,_,_),S).

poss(collectConstraintsByPhone(O,T,P),S) :- hasPhoneNumber(T,P).

poss(collectFromCallendar(_,P),S) :- maintainsCallendar(P).

poss(decideMeetingDetails(_),S).

poss(chooseTimeandDate(C),S) :- sat_timetablesCollected(C,S).

poss(announceMeeting(C,P),S) :- perf_chooseTimeandDate(C,S).

poss(participate(P),S).


%
% SATISFACTION AXIOMS
%

sat_haveMeetingOrganized(I,S) :- perf_decideMeetingDetails(I,S),
								sat_haveSchedulingPerformed(I,S),
								sat_meetingAttended(I,S).
								
	sat_haveSchedulingPerformed(I,S) :- organizer(O),sat_haveSchedulingPerformedByOrganizer(I,O,S).
	sat_meetingAttended(I,S) :- \+ (invited(P,S), \+ sat_meetingAttendedByParticipant(I,P,S)).

sat_haveSchedulingPerformedByOrganizer(I,O,S):- 
				hasDelegated(I,O,haveSchedulingPerformedByOrganizer(I,O),haveMeetingScheduled(O),S),
				sat_haveMeetingScheduled(O,S).

sat_haveMeetingScheduled(O,S) :- sat_timetablesCollected(O,S),perf_chooseTimeandDate(O,S),sat_meetingAnnounced(A,S).

sat_timetablesCollected(O,S) :- \+ (potential_participant(P),\+ sat_timetableCollected(O,P,S)).

sat_timetableCollected(O,P,S) :- perf_collectFromCallendar(O,P,S).

sat_timetableCollected(O,P,S) :- sat_havePPCalled(O,P,S).

sat_havePPCalled(O,P,S):- secretary(T),sat_haveSecretaryCallPP(O,T,P,S).

sat_haveSecretaryCallPP(O,T,P,S) :- 
						hasDelegated(O,T,haveSecretaryCallPP(O,T,P),collectConstraintsByPhone(O,T,P),S), 
						perf_collectConstraintsByPhone(O,T,P,S).

sat_meetingAnnounced(O,S) :- \+ (potential_participant(P),\+ perf_announceMeeting(O,P,S)).

sat_meetingAttended(N,S):- \+ (invited(P,S),\+ sat_meetingAttendedByParticipant(N,P,S)).

sat_meetingAttendedByParticipant(_,P,S) :- perf_participate(P,S).




%
% FEASIBILITY AXIOMS
%

feas(havePPCalled(O,P),S):- secretary(T),feas(haveSecretaryCallPP(O,T,P),S).

% In case of richer decompositions, simply form the disjunction of all tasks.
feas(haveSecretaryCallPP(O,T,P),S) :- poss(collectConstraintsByPhone(O,T,P),S). 

% In case of richer decompositions, simply form the disjunction of all tasks.
feas(collectFromCallendar(O,P),S):- poss(collectFromCallendar(O,P),S).





%
% TASKS SUCCESSOR STATE AXIOMS
%

hasDelegated(Delegator,Delegatee,DelegatorGoal,DelegateeGoal,do(A,S)):- 
									hasDelegated(Delegator,Delegatee,DelegatorGoal,DelegateeGoal,S);
									A = delegate(Delegator,Delegatee,DelegatorGoal,DelegateeGoal).

perf_decideMeetingDetails(Agent,do(A,S)):-perf_decideMeetingDetails(Agent,S);
									A = decideMeetingDetails(Agent).

perf_collectFromCallendar(Agent,P,do(A,S)) :- perf_collectFromCallendar(Agent,P,S);
									A = collectFromCallendar(Agent,P).

perf_collectConstraintsByPhone(O,T,P,do(A,S)) :- perf_collectConstraintsByPhone(O,T,P,S);
									A = collectConstraintsByPhone(O,T,P).

perf_chooseTimeandDate(Agent,do(A,S)) :- perf_chooseTimeandDate(Agent,S);
									A = chooseTimeandDate(Agent).

perf_announceMeeting(Agent,P,do(A,S)) :- perf_announceMeeting(Agent,P,S);
									A = announceMeeting(Agent,P).

perf_participate(Agent,do(A,S)) :- perf_participate(Agent,S);
									A = participate(Agent).



%
% ORDINARY SUCCESSOR STATE AXIOMS
%

hasTimeTable(Agent,Participant,do(A,S)) :- hasTimeTable(Agent,Participant,S); 
									A=collectFromCallendar(Agent,Participant);
									A=collectConstraintsByPhone(Secretary,Participant),
										hasDelegated(Agent,Secretary,haveSecretaryCallPP(Agent,Secretary,P),S).

invited(P,do(A,S)) :- invited(P,S); A=announceMeeting(_,P).

participated(P,do(A,S)):- participated(P,S); A=participate(P).



%
% Argument Restoration - Helpers
%


% Reserved
restoreSitArg(hasDelegated(Delegator,Delegatee,DelegatorGoal,DelegateeGoal),S,hasDelegated(Delegator,Delegatee,DelegatorGoal,DelegateeGoal,S)).

% Ordinary Fluents
restoreSitArg(hasTimeTable(C,P),S,hasTimeTable(C,P,S)).
restoreSitArg(invited(P),S,invited(P,S)).
restoreSitArg(participated(P),S,participated(P,S)).

% Task Fluents
restoreSitArg(perf_decideMeetingDetails(A),S,perf_decideMeetingDetails(A,S)).
restoreSitArg(perf_collectFromCallendar(A,P),S,perf_collectFromCallendar(A,P,S)).
restoreSitArg(perf_collectConstraintsByPhone(A,P),S,perf_collectConstraintsByPhone(A,P,S)).
restoreSitArg(perf_chooseTimeandDate(A),S,perf_chooseTimeandDateSucc(A,S)).
restoreSitArg(perf_announceMeeting(A,P),S,perf_announceMeeting(A,P,S)).
restoreSitArg(perf_participateSucc(A),S,perf_participate(A,S)).

% Goal satisfaction Fluents
restoreSitArg(sat_haveMeetingOrganized(A),S,sat_haveMeetingOrganized(A,S)).
restoreSitArg(sat_haveSchedulingPerformed(I),S,sat_haveSchedulingPerformed(I,S)).
restoreSitArg(sat_meetingAttended(I),S,sat_meetingAttended(I,S)).
restoreSitArg(sat_haveSchedulingPerformedByOrganizer(I,O),S,sat_haveSchedulingPerformedByOrganizer(I,O,S)).
restoreSitArg(sat_haveMeetingScheduled(O),S,sat_haveMeetingScheduled(O,S)).
restoreSitArg(sat_timetablesCollected(O),S,sat_timetablesCollected(O,S)).
restoreSitArg(sat_timetableCollected(O,P),S,sat_timetableCollected(O,P,S)).
restoreSitArg(sat_havePPCalled(A,P),S,sat_havePPCalled(A,P,S)).
restoreSitArg(sat_havePPCalled(O,P),S,sat_havePPCalled(O,P,S)).
restoreSitArg(sat_haveSecretaryCallPP(O,T,P),S,sat_haveSecretaryCallPP(O,T,P,S)).
restoreSitArg(sat_meetingAnnounced(O),S,sat_meetingAnnounced(O,S)).
restoreSitArg(sat_meetingAttendedByParticipant(I,P),S,sat_meetingAttendedByParticipant(I,P,S)).



%
% User friendly descriptions
%

descr2(X,Y):- descr(X,D),term_string(X,Xt),atomic_list_concat([D,' (',Xt,')'],Y).


%descr(decideMeetingDetails(X),Y):- 
%		term_string(X,Xs),string_concat(Xs,' wanted to decide meeting details.',Y).
descr(haveMeetingOrganized(I),Y):-
		atomic_list_concat(["initiator ",I," wanted to have the meeting organized."],Y),!.
descr(haveSchedulingPerformed(I),Y):-
		atomic_list_concat(["initiator ",I," wanted to have the scheduling of the meeting performed."],Y),!.
descr(haveSchedulingPerformedByOrganizer(I,O),Y):-
		atomic_list_concat(["initiator ",I," wanted to have the scheduling of the meeting performed by organizer ",O,"."],Y),!.
descr(meetingAttended(I),Y):-
		atomic_list_concat(["initiator ",I," wanted to have the meeting attended by participants."],Y),!.
descr(meetingAttendedByParticipant(I,P),Y):-
		atomic_list_concat(["initiator ",I," wanted to have the meeting attended by participant ",P,"."],Y),!.
		descr(decideMeetingDetails(I),Y):- 
		atomic_list_concat(["initiator ",I," wanted to decide meeting details (purpose, potential participants, quality preferences etc.)."],Y),!.
		
descr(timetableCollected(O,P),Y):- 
		atomic_list_concat(["organizer ",O," wanted to collect ",P,"'s timetable."],Y),!.
descr(chooseTimeandDate(O),Y):- 
		atomic_list_concat(["organizer ",O," wanted to choose time and date for the meeting."],Y),!.
descr(haveSecretaryCallPP(O,S,P),Y):-
		atomic_list_concat(["organizer ",O," wanted to have secretary ",S," call participant ",P," to collect their constraints."],Y),!.
descr(meetingAttended(O),Y):-
		atomic_list_concat(["organizer ",O," wanted to have the meeting attended."],Y),!.
descr(haveMeetingScheduled(O),Y):-
		atomic_list_concat(["organizer ",O," wanted to have the meeting that was assigned to them scheduled."],Y),!.
descr(timetablesCollected(O),Y):- 
		atomic_list_concat(["organizer ",O," wanted to collect timetables of all potential participants."],Y),!.
descr(havePPCalled(O,P),Y):- 
		atomic_list_concat(["organizer ",O," wanted to have ", P,"'s constraints collected by phone."],Y),!.
descr(meetingAnnounced(O),Y):- 
		atomic_list_concat(["organizer ",O," wanted to have the meeting announced."],Y),!.
descr(collectFromCallendar(O,P),Y):- 
		atomic_list_concat(["organizer ",O," wanted to collect ",P ,"'s constraints by looking at their online callendars."],Y),!.
descr(announceMeeting(O,P),Y):- 
		atomic_list_concat(["organizer ",O," wanted to announce the meeting to participant ", P, "."],Y),!.

descr(collectConstraintsByPhone(O,T,P),Y):- 
		atomic_list_concat(["secretary ",T," collected ",P,"'s constraints by phone on behalf of ",O,"."],Y),!.

descr(participate(P),Y):- 
		atomic_list_concat(["intitee ",P," wanted to participate to the meeting."],Y),!.

descr(X,X).



 %#######                                                                    #######                                                        
 %#       #    # #####  #        ##   #    #   ##   ##### #  ####  #    #       #    ###### #    # #####  #        ##   ##### ######  ####  
 %#        #  #  #    # #       #  #  ##   #  #  #    #   # #    # ##   #       #    #      ##  ## #    # #       #  #    #   #      #      
 %#####     ##   #    # #      #    # # #  # #    #   #   # #    # # #  #       #    #####  # ## # #    # #      #    #   #   #####   ####  
 %#         ##   #####  #      ###### #  # # ######   #   # #    # #  # #       #    #      #    # #####  #      ######   #   #           # 
 %#        #  #  #      #      #    # #   ## #    #   #   # #    # #   ##       #    #      #    # #      #      #    #   #   #      #    # 
 %####### #    # #      ###### #    # #    # #    #   #   #  ####  #    #       #    ###### #    # #      ###### #    #   #   ######  ####  


explain(whyisPP(X)) :- potential_participant(X), ppJustification(X,U), atomic_list_concat(["Because ",X," ",U],Y),write(Y),!.

% TODO: Custom templates need to be provided in the following format:
explains_templ(Y,added_for_compatibility,_,J).

