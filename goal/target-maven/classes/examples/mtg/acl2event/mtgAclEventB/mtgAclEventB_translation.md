# Event-B translation: mtgAclEventB

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

## Diagnostics

No diagnostics.
