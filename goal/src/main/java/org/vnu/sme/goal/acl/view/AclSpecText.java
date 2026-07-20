package org.vnu.sme.goal.acl.view;

import org.vnu.sme.goal.acl.mm.AclAttribute;
import org.vnu.sme.goal.acl.mm.AclCardinality;
import org.vnu.sme.goal.acl.mm.AclCompatibility;
import org.vnu.sme.goal.acl.mm.AclGroup;
import org.vnu.sme.goal.acl.mm.AclLink;
import org.vnu.sme.goal.acl.mm.AclModel;
import org.vnu.sme.goal.acl.mm.AclRoleEntityRelation;

public final class AclSpecText {
    private static final String INDENT = "    ";

    private AclSpecText() {}

    public static String render(AclModel model) {
        StringBuilder out = new StringBuilder();
        out.append("acl ").append(model.version()).append(' ').append(model.name()).append(" {\n");

        model.enums().forEach(value -> {
            out.append('\n').append(INDENT).append("enum ").append(value.name()).append(" {\n");
            for (int index = 0; index < value.literals().size(); index++) {
                out.append(INDENT.repeat(2)).append(value.literals().get(index));
                if (index + 1 < value.literals().size()) out.append(',');
                out.append('\n');
            }
            out.append(INDENT).append("}\n");
        });

        model.roles().forEach(role -> {
            out.append('\n').append(INDENT).append(role.isAbstract() ? "abstract role " : "role ")
                    .append(role.name());
            if (!role.parentRoles().isEmpty()) out.append(" extends ").append(String.join(", ", role.parentRoles()));
            if (role.attributes().isEmpty()) {
                out.append(";\n");
            } else {
                out.append(" {\n");
                role.attributes().forEach(attribute -> renderAttribute(out, attribute, 2));
                out.append(INDENT).append("}\n");
            }
        });

        model.entities().forEach(entity -> {
            out.append('\n').append(INDENT).append("entity ").append(entity.name());
            if (entity.attributes().isEmpty()) {
                out.append(";\n");
            } else {
                out.append(" {\n");
                entity.attributes().forEach(attribute -> renderAttribute(out, attribute, 2));
                out.append(INDENT).append("}\n");
            }
        });

        out.append('\n');
        renderRootGroup(out, model.rootGroup());
        out.append("}\n");
        return out.toString();
    }

    private static void renderAttribute(StringBuilder out, AclAttribute attribute, int depth) {
        out.append(INDENT.repeat(depth)).append("attribute ").append(attribute.name()).append(" : ")
                .append(attribute.type().sourceName());
        if (attribute.required()) out.append(" required");
        if (attribute.mutable()) out.append(" mutable");
        attribute.defaultValue().ifPresent(value -> out.append(" default ").append(value));
        out.append(";\n");
    }

    private static void renderRootGroup(StringBuilder out, AclGroup group) {
        out.append(INDENT).append("group ").append(group.name()).append(" {\n");
        renderGroupBody(out, group, 2);
        out.append(INDENT).append("}\n");
    }

    private static void renderSubgroup(StringBuilder out, AclGroup group, AclCardinality cardinality, int depth) {
        out.append(INDENT.repeat(depth)).append("subgroup ").append(group.name()).append(' ')
                .append(cardinality(cardinality)).append(" {\n");
        renderGroupBody(out, group, depth + 1);
        out.append(INDENT.repeat(depth)).append("}\n");
    }

    private static void renderGroupBody(StringBuilder out, AclGroup group, int depth) {
        group.roles().forEach(value -> out.append(INDENT.repeat(depth)).append("role ")
                .append(value.roleName()).append(' ').append(cardinality(value.cardinality())).append(";\n"));
        group.entities().forEach(value -> out.append(INDENT.repeat(depth)).append("entity ")
                .append(value.entityName()).append(' ').append(cardinality(value.cardinality())).append(";\n"));

        group.links().forEach(value -> renderLink(out, value, depth));
        group.compatibilities().forEach(value -> renderCompatibility(out, value, depth));
        group.roleEntityRelations().forEach(value -> renderRoleEntityRelation(out, value, depth));
        group.subgroups().forEach(value -> renderSubgroup(out, value.group(), value.cardinality(), depth));
        group.cardinalityConstraints().forEach(value -> out.append(INDENT.repeat(depth)).append("cardinality ")
                .append(value.targetKind().sourceName()).append(' ').append(value.targetName()).append(' ')
                .append(cardinality(value.cardinality())).append(";\n"));
    }

    private static void renderLink(StringBuilder out, AclLink link, int depth) {
        out.append(INDENT.repeat(depth)).append("link ").append(link.type().sourceName()).append(' ')
                .append(link.fromRole()).append(" -> ").append(link.toRole()).append('\n')
                .append(INDENT.repeat(depth + 1)).append("scope ").append(link.scope().sourceName()).append('\n')
                .append(INDENT.repeat(depth + 1)).append("extends-subgroups ").append(link.extendsSubgroups()).append('\n')
                .append(INDENT.repeat(depth + 1)).append("bidirectional ").append(link.bidirectional()).append(";\n");
    }

    private static void renderCompatibility(StringBuilder out, AclCompatibility compatibility, int depth) {
        String arrow = compatibility.bidirectional() ? " <-> " : " -> ";
        out.append(INDENT.repeat(depth)).append("compatibility ").append(compatibility.fromRole())
                .append(arrow).append(compatibility.toRole()).append('\n')
                .append(INDENT.repeat(depth + 1)).append("scope ").append(compatibility.scope().sourceName()).append('\n')
                .append(INDENT.repeat(depth + 1)).append("extends-subgroups ")
                .append(compatibility.extendsSubgroups()).append(";\n");
    }

    private static void renderRoleEntityRelation(StringBuilder out, AclRoleEntityRelation relation, int depth) {
        out.append(INDENT.repeat(depth)).append("relation ").append(relation.name()).append(' ')
                .append(relation.type().sourceName()).append(' ').append(relation.sourceRole().name()).append(" -> ")
                .append(relation.targetEntity().name()).append(" scope ").append(relation.scope().sourceName())
                .append(" extends-subgroups ").append(relation.extendsSubgroups()).append(";\n");
    }

    private static String cardinality(AclCardinality cardinality) {
        String max = cardinality.max().isPresent() ? Integer.toString(cardinality.max().getAsInt()) : "*";
        return "[" + cardinality.min() + ".." + max + "]";
    }
}
