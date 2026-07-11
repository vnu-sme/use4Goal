package org.vnu.sme.goal.bpmn2.view;

/**
 * PROCESS   — one Process's own lanes/flowElements/sequenceFlows (no MessageFlow).
 * COLLABORATION — all Processes side by side (pools) + MessageFlows between them.
 * CHOREOGRAPHY  — MessageFlows only, as a sequence diagram (1 column per Participant),
 *                 ordered by declaration order (no internal Process detail).
 * SCENARIO_EXECUTION  — concrete scenario occurrences, usually one lane per actor/object.
 * SCENARIO_AGGREGATE  — the BPMN process with scenario counts/details attached to activities.
 */
public enum Bpmn2ViewMode { PROCESS, COLLABORATION, CHOREOGRAPHY, SCENARIO_EXECUTION, SCENARIO_AGGREGATE }
