# Monitoring trace-stepper example

Open **GoalModel Plugin → iStar Execution Trace Stepper**, then select:

- ACL: `monitoring.acl`
- iStar: `monitoring.istar`
- execution trace: `monitoring.soil`

Every non-comment SOIL statement is one checkpoint. Near the end of the trace,
`ServiceChecked : Recur` changes as follows:

```text
UNKNOWN -> PENDING -> FULFILLED -> PENDING -> FULFILLED
```

The right-hand panel displays the current raw goal marking `(A,P,S)`, its derived
status, ACL attribute changes, and iStar status changes. The diagram on the left
uses the same status at the same checkpoint.
