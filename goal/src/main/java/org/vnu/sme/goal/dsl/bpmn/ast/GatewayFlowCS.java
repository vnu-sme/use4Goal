package org.vnu.sme.goal.dsl.bpmn.ast;

/**
 * One outgoing flow of a gateway. {@code postSource} is the formal
 * branch-specific postcondition. {@code guardSource}/{@code isDefault} are
 * retained for backward compatibility with the older guarded syntax.
 */
public record GatewayFlowCS(String target, String postSource,
                            String guardSource, boolean isDefault) {}
