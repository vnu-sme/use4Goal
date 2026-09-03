package org.vnu.sme.goal.dsl.acl.view;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

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

        Set<String> nested = new LinkedHashSet<>();
        Set<String> nestedContexts = new LinkedHashSet<>();
        model.orgContexts().forEach(context -> context.members().forEach(member -> {
            nested.add(member.type());
            if (model.findOrgContext(member.type()).isPresent()) nestedContexts.add(member.type());
        }));

        model.entities().stream().filter(value -> !nested.contains(value.name()))
                .forEach(value -> renderClassifier(out, "entity", value.name(),
                        value.specializes(), value.attributes(), 1));
        model.roles().stream().filter(value -> !nested.contains(value.name()))
                .forEach(value -> renderClassifier(out, "role", value.name(),
                        value.parentRoles().stream().findFirst(), value.attributes(), 1));
        model.orgContexts().stream().filter(value -> !nestedContexts.contains(value.name()))
                .forEach(value -> renderOrgContext(out, model, value, 1, new LinkedHashSet<>()));
        model.groups().stream().filter(value -> !value.isOrganizationalContext())
                .forEach(value -> renderLegacyGroup(out, value));

        Set<String> derivedContainments = new LinkedHashSet<>();
        model.groups().forEach(context -> context.members().forEach(member ->
                derivedContainments.add(AclContainment.relationName(context.name(), member.type()))));
        model.relations().stream().filter(value -> !derivedContainments.contains(value.name()))
                .forEach(value -> renderRelation(out, value));
        model.invariants().forEach(value -> out.append('\n').append(INDENT)
                .append("context ").append(value.contextType()).append(" inv ")
                .append(value.name()).append(":\n")
                .append(INDENT.repeat(2)).append(value.expression()).append(";\n"));
        return out.append("}\n").toString();
    }

    private static void renderOrgContext(StringBuilder out, AclModel model, AclGroup context,
                                         int depth, Set<String> path) {
        if (!path.add(context.name())) return;
        out.append('\n').append(INDENT.repeat(depth)).append("orgContext ")
                .append(context.name()).append(" {\n");
        for (AclGroupMember member : context.members()) {
            model.findRole(member.type()).ifPresent(value -> renderClassifier(out, "role", value.name(),
                    value.parentRoles().stream().findFirst(), value.attributes(), depth + 1));
            model.findEntity(member.type()).ifPresent(value -> renderClassifier(out, "entity", value.name(),
                    value.specializes(), value.attributes(), depth + 1));
            model.findOrgContext(member.type()).ifPresent(value ->
                    renderOrgContext(out, model, value, depth + 1, path));
        }
        context.compatibilities().forEach(value -> out.append(INDENT.repeat(depth + 1))
                .append(value.fromRole()).append(" compatible ").append(value.toRole()).append(";\n"));
        out.append(INDENT.repeat(depth)).append("}\n");
        path.remove(context.name());
    }

    private static void renderClassifier(StringBuilder out, String keyword, String name,
                                         Optional<String> specializes,
                                         java.util.List<AclAttribute> attributes, int depth) {
        out.append('\n').append(INDENT.repeat(depth)).append(keyword).append(' ').append(name);
        specializes.ifPresent(parent -> out.append(" specializes ").append(parent));
        if (attributes.isEmpty()) { out.append(";\n"); return; }
        out.append(" {\n");
        attributes.forEach(value -> renderAttribute(out, value, depth + 1));
        out.append(INDENT.repeat(depth)).append("}\n");
    }

    private static void renderLegacyGroup(StringBuilder out, AclGroup group) {
        out.append('\n').append(INDENT).append("group ").append(group.name());
        group.specializes().ifPresent(parent -> out.append(" specializes ").append(parent));
        out.append(" {\n");
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
