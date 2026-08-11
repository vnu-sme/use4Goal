package org.vnu.sme.goal.dsl.bpmn.ast;

/**
 * One outgoing flow of a gateway in concrete syntax: guarded ({@code when}),
 * the fallback ({@code default}), or plain when the gateway has only one
 * way out. At most one flow per gateway may be {@code isDefault}.
 */
public record GatewayFlowCS(String target, String guardSource, boolean isDefault) {}
