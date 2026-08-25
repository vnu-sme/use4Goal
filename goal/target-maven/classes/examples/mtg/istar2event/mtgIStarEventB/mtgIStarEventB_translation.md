# Event-B translation: mtgIStarEventB

## Traceability

| Source | Element | Event-B |
|---|---|---|
|ACL|synthetic Agent class|AGENT_ID / AGENTS|
|ACL|group MeetingUnit|G_MeetingUnit|
|ACL|role MeetingParty|R_MeetingParty|
|ACL|role Initiator|R_Initiator|
|ACL|role Organizer|R_Organizer|
|ACL|role Secretary|R_Secretary|
|ACL|role Participant|R_Participant|
|ACL|enum TimetableChannel|TIMETABLECHANNEL|
|ACL|MeetingUnit.detailsDecided|MeetingUnit_detailsDecided|
|ACL|MeetingUnit.timeChosen|MeetingUnit_timeChosen|
|ACL|MeetingParty.name|MeetingParty_name|
|ACL|MeetingParty.phone|MeetingParty_phone|
|ACL|MeetingParty.hasCalendar|MeetingParty_hasCalendar|
|ACL|Participant.timetableCollected|Participant_timetableCollected|
|ACL|Participant.timetableChannel|Participant_timetableChannel|
|ACL|Participant.notified|Participant_notified|
|ACL|Participant.attended|Participant_attended|
|ACL|association knowsPhoneOf|knowsPhoneOf|
|ACL|owner MeetingUnit -> Initiator|owns_Initiator|
|ACL|owner MeetingUnit -> Organizer|owns_Organizer|
|ACL|owner MeetingUnit -> Secretary|owns_Secretary|
|ACL|owner MeetingUnit -> Participant|owns_Participant|
|ACL|plays MeetingParty|plays_MeetingParty|
|ACL|plays Initiator|plays_Initiator|
|ACL|plays Organizer|plays_Organizer|
|ACL|plays Secretary|plays_Secretary|
|ACL|plays Participant|plays_Participant|
|iStar|actor Initiator|R_Initiator|
|iStar|actor Organizer|R_Organizer|
|iStar|actor Secretary|R_Secretary|
|iStar|actor Participant|R_Participant|
|iStar|Initiator.MeetingOrganized|g_MeetingOrganized / G_MeetingOrganized_{A,P,S}|
|iStar|Initiator.DecideDetails|t_DecideDetails / T_DecideDetails_{Q,R}|
|iStar|Initiator.MeetingScheduled|g_MeetingScheduled / G_MeetingScheduled_{A,P,S}|
|iStar|Initiator.ParticipantsAttended|g_ParticipantsAttended / G_ParticipantsAttended_{A,P,S}|
|iStar|Initiator.OrganizerScheduledMeeting|g_OrganizerScheduledMeeting / G_OrganizerScheduledMeeting_{A,P,S}|
|iStar|Initiator.ParticipantAttended|g_ParticipantAttended / G_ParticipantAttended_{A,P,S}|
|iStar|Organizer.ChosenTimeHasDetails|g_ChosenTimeHasDetails / G_ChosenTimeHasDetails_{A,P,S}|
|iStar|Organizer.SchedulingCompleted|g_SchedulingCompleted / G_SchedulingCompleted_{A,P,S}|
|iStar|Organizer.TimetablesCollected|g_TimetablesCollected / G_TimetablesCollected_{A,P,S}|
|iStar|Organizer.ChooseMeetingTime|t_ChooseMeetingTime / T_ChooseMeetingTime_{Q,R}|
|iStar|Organizer.ParticipantsNotified|g_ParticipantsNotified / G_ParticipantsNotified_{A,P,S}|
|iStar|Organizer.TimetableCollected|g_TimetableCollected / G_TimetableCollected_{A,P,S}|
|iStar|Organizer.ContactedByPhone|g_ContactedByPhone / G_ContactedByPhone_{A,P,S}|
|iStar|Organizer.CollectFromCalendar|t_CollectFromCalendar / T_CollectFromCalendar_{Q,R}|
|iStar|Organizer.SecretaryRequested|g_SecretaryRequested / G_SecretaryRequested_{A,P,S}|
|iStar|Organizer.NotifyParticipant|t_NotifyParticipant / T_NotifyParticipant_{Q,R}|
|iStar|Organizer.InclusiveCollection|q_InclusiveCollection / Q_InclusiveCollection_{I,TRUE,FALSE}|
|iStar|Organizer.FastCollection|q_FastCollection / Q_FastCollection_{I,TRUE,FALSE}|
|iStar|Secretary.CollectByPhone|t_CollectByPhone / T_CollectByPhone_{Q,R}|
|iStar|Participant.AttendMeeting|t_AttendMeeting / T_AttendMeeting_{Q,R}|

## Diagnostics

No diagnostics.
