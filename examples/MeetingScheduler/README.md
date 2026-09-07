# MeetingScheduler case study

Van Lamsweerde's classic Meeting Scheduler, recast in this project's three
DSLs (ACL structure from `incident_response_bridge/mtg.acl` +
`docs/model/mtg.*.xmi`; i* and BPMN authored here in the supported
grammars).

| File | Language | Role |
| ---- | -------- | ---- |
| `meeting-scheduler.acl` | ACL v3.1 | `MeetingParty -> Initiator / Organizer / Secretary / Participant` role specialization; `Participant` carries `CollectionStatus` / `TimetableChannel` / `NotificationStatus` / `AttendanceStatus` enums; `Meeting` entity with `MeetingPhase`; `knowsPhoneOf` + `meetingParticipants` + `meetingOrganizedBy` associations |
| `meeting-scheduler.aclboundary` | acl-bpmn-boundary v1.0 | 24 snapshots, 2 participants, one replan |
| `meeting-scheduler.istar` | iStar 2.1 | SR view; `MeetingScheduled` AND-refined into `ConstraintsKnown` / `DateDetermined` / `ParticipantsInformed`; phone collection `hurt`s `LowSchedulingEffort` |
| `meeting-scheduler.bpmn2` | state BPMN | AS-IS process: calendar vs. phone constraint collection, one replan then cancel |

Exercises the metamodel-coverage grammar surface: element `description`, a
`lang OCL` tag on one condition, model-level `is-a` (`Initiator is-a
MeetingParty`, ...), and BPMN `name` + branch `label` on start / gateway /
end elements.

Consistency: BPMN lanes = ACL roles, `pool ... for MeetingUnit`, i* actors
= ACL roles; participant-level effects are written as
`self.meeting.invitee->forAll(p | ...)` over the association end.
