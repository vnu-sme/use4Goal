package org.vnu.sme.goal.dsl.acl.mm;

/** Naming convention for the composition denoted by a Group member declaration. */
public final class AclContainment {
    private AclContainment() {}

    public static String relationName(String groupType, String memberType) {
        return groupType + "_contains_" + memberType;
    }

    public static String wholeRoleName() {
        return "group";
    }

    public static String partRoleName(String memberType) {
        return memberType;
    }
}
