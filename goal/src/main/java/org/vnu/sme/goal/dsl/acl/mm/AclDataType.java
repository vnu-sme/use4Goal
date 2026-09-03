package org.vnu.sme.goal.dsl.acl.mm;

public sealed interface AclDataType permits AclPrimitiveType, AclEnum {
    String sourceName();
}
