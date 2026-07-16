package org.vnu.sme.goal.acl.view;

public record AclEdge(String fromId, String toId, AclEdgeKind kind, String label) {}
