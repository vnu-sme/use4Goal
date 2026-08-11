# Event-B translation: mtgBpmnEventB

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
|BPMN|process MeetingOrganization|p_MeetingOrganization / PI_MeetingOrganization / processScope_MeetingOrganization|
|BPMN|start_meeting -> decideMeetingDetails|f_MeetingOrganization_start_meeting_decideMeetingDetails_1 / tk_MeetingOrganization_start_meeting_decideMeetingDetails_1|
|BPMN|decideMeetingDetails -> checkCalendar|f_MeetingOrganization_decideMeetingDetails_checkCalendar_2 / tk_MeetingOrganization_decideMeetingDetails_checkCalendar_2|
|BPMN|checkCalendar -> phoneCollectionRequired|f_MeetingOrganization_checkCalendar_phoneCollectionRequired_3 / tk_MeetingOrganization_checkCalendar_phoneCollectionRequired_3|
|BPMN|phoneCollectionRequired -> requestSecretaryCall|f_MeetingOrganization_phoneCollectionRequired_requestSecretaryCall_4 / tk_MeetingOrganization_phoneCollectionRequired_requestSecretaryCall_4|
|BPMN|phoneCollectionRequired -> timetableCollectionComplete|f_MeetingOrganization_phoneCollectionRequired_timetableCollectionComplete_5 / tk_MeetingOrganization_phoneCollectionRequired_timetableCollectionComplete_5|
|BPMN|requestSecretaryCall -> collectConstraintsByPhone|f_MeetingOrganization_requestSecretaryCall_collectConstraintsByPhone_6 / tk_MeetingOrganization_requestSecretaryCall_collectConstraintsByPhone_6|
|BPMN|collectConstraintsByPhone -> timetableCollectionComplete|f_MeetingOrganization_collectConstraintsByPhone_timetableCollectionComplete_7 / tk_MeetingOrganization_collectConstraintsByPhone_timetableCollectionComplete_7|
|BPMN|timetableCollectionComplete -> chooseTimeAndDate|f_MeetingOrganization_timetableCollectionComplete_chooseTimeAndDate_8 / tk_MeetingOrganization_timetableCollectionComplete_chooseTimeAndDate_8|
|BPMN|chooseTimeAndDate -> announceMeeting|f_MeetingOrganization_chooseTimeAndDate_announceMeeting_9 / tk_MeetingOrganization_chooseTimeAndDate_announceMeeting_9|
|BPMN|announceMeeting -> participate|f_MeetingOrganization_announceMeeting_participate_10 / tk_MeetingOrganization_announceMeeting_participate_10|
|BPMN|participate -> end_meeting|f_MeetingOrganization_participate_end_meeting_11 / tk_MeetingOrganization_participate_end_meeting_11|
|BPMN|start_meeting|start_meeting / performer:R_Initiator|
|BPMN|decideMeetingDetails|decideMeetingDetails / performer:R_Initiator|
|BPMN|checkCalendar|checkCalendar / performer:R_Organizer|
|BPMN|phoneCollectionRequired|phoneCollectionRequired_to_requestSecretaryCall / performer:R_Organizer|
|BPMN|phoneCollectionRequired|phoneCollectionRequired_to_timetableCollectionComplete / performer:R_Organizer|
|BPMN|requestSecretaryCall|requestSecretaryCall / performer:R_Organizer|
|BPMN|collectConstraintsByPhone|collectConstraintsByPhone / performer:R_Secretary|
|BPMN|timetableCollectionComplete|timetableCollectionComplete_from_phoneCollectionRequired / performer:R_Organizer|
|BPMN|timetableCollectionComplete|timetableCollectionComplete_from_collectConstraintsByPhone / performer:R_Organizer|
|BPMN|chooseTimeAndDate|chooseTimeAndDate / performer:R_Organizer|
|BPMN|announceMeeting|announceMeeting / performer:R_Organizer|
|BPMN|participate|participate / performer:R_Participant|
|BPMN|end_meeting|end_meeting / performer:R_Organizer|

## Diagnostics

No diagnostics.
