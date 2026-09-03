# Incident Response conformance example

The legacy ACL links and final-state SOIL were migrated to the current ACL→USE runtime model.
`incident_response.soil` now contains neutral response status, while BPMN activities produce
triage, evidence, repair and audit changes. The iStar model observes those changes and the
automated conformance test requires its root goal to be fulfilled at EndEvent.
