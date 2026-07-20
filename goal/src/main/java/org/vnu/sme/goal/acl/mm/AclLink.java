package org.vnu.sme.goal.acl.mm;

public record AclLink(String kind,
                      String sourceRole,
                      String targetRole,
                      String scopeKind,
                      String scopeGroup) {}
