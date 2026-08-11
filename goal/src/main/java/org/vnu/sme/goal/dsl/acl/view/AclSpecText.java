package org.vnu.sme.goal.dsl.acl.view;

import java.util.Optional;

import org.vnu.sme.goal.dsl.acl.mm.*;

/** Canonical concrete-syntax renderer for the ACL metamodel. */
public final class AclSpecText {
    private static final String INDENT = "    ";
    private AclSpecText() {}

    public static String render(AclModel model) {
        StringBuilder out = new StringBuilder("acl ").append(model.version()).append(' ')
                .append(model.name()).append(" {\n");
        model.enums().forEach(value -> out.append('\n').append(INDENT).append("enum ")
                .append(value.name()).append(" { ").append(String.join(", ", value.literals())).append(" }\n"));
        model.entities().forEach(value -> renderClassifier(out, "entity", value.name(),
                value.specializes(), false, value.attributes()));
        model.roles().forEach(value -> renderClassifier(out, "role", value.name(),
                value.parentRoles().stream().findFirst(), false, value.attributes()));
        model.groups().forEach(value -> renderGroup(out, value));
        model.relations().forEach(value -> renderRelation(out, value));
        return out.append("}\n").toString();
    }

    private static void renderClassifier(StringBuilder out, String keyword, String name,
                                         Optional<String> specializes, boolean isAbstract,
                                         java.util.List<AclAttribute> attributes) {
        out.append('\n').append(INDENT);
        if (isAbstract) out.append("abstract ");
        out.append(keyword).append(' ').append(name);
        specializes.ifPresent(parent -> out.append(" specializes ").append(parent));
        if (attributes.isEmpty()) { out.append(";\n"); return; }
        out.append(" {\n");
        attributes.forEach(value -> renderAttribute(out, value, 2));
        out.append(INDENT).append("}\n");
    }

    private static void renderGroup(StringBuilder out, AclGroup group) {
        out.append('\n').append(INDENT).append("group ").append(group.name()).append(" {\n");
        group.attributes().forEach(value -> renderAttribute(out, value, 2));
        group.members().forEach(value -> out.append(INDENT.repeat(2)).append(value.type()).append(' ')
                .append(cardinality(value.multiplicity())).append(";\n"));
        group.compatibilities().forEach(value -> out.append(INDENT.repeat(2))
                .append(value.fromRole()).append(" compatible ").append(value.toRole()).append(";\n"));
        out.append(INDENT).append("}\n");
    }

    private static void renderRelation(StringBuilder out, AclRelation relation) {
        out.append('\n').append(INDENT).append(relation.kind().sourceName()).append(' ')
                .append(relation.name()).append(" {\n");
        relation.endpoints().forEach(value -> out.append(INDENT.repeat(2)).append(value.type()).append(' ')
                .append(cardinality(value.multiplicity()))
                .append(value.roleName().map(role -> " role " + role).orElse(""))
                .append(";\n"));
        out.append(INDENT).append("}\n");
    }

    private static void renderAttribute(StringBuilder out, AclAttribute attribute, int depth) {
        out.append(INDENT.repeat(depth)).append(attribute.name()).append(" : ")
                .append(attribute.type().sourceName());
        if (attribute.optional()) out.append(" optional");
        if (attribute.mutable()) out.append(" mutable");
        attribute.defaultValue().ifPresent(value -> out.append(" default ").append(value));
        out.append(";\n");
    }

    private static String cardinality(AclCardinality value) {
        if (value.min() == 0 && value.max().isEmpty()) return "[*]";
        if (value.max().isPresent() && value.min() == value.max().getAsInt()) return "[" + value.min() + "]";
        return "[" + value.min() + ".." + (value.max().isPresent() ? value.max().getAsInt() : "*") + "]";
    }
}
