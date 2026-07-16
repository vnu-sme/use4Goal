package org.vnu.sme.goal.acl.ast;

public record AclLinkCS(String kind,
                        String sourceRole,
                        String targetRole,
                        String scopeKind,
                        String scopeGroup) {}
