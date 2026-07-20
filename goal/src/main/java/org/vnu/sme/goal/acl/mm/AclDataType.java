package org.vnu.sme.goal.acl.mm;

public sealed interface AclDataType permits AclPrimitiveType, AclEnum {
    String sourceName();
}
