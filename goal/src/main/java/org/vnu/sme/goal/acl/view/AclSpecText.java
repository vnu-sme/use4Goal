package org.vnu.sme.goal.acl.view;

import java.util.List;
import java.util.stream.Collectors;

import org.vnu.sme.goal.acl.mm.AclActor;
import org.vnu.sme.goal.acl.mm.AclAttribute;
import org.vnu.sme.goal.acl.mm.AclEndpoint;
import org.vnu.sme.goal.acl.mm.AclEntity;
import org.vnu.sme.goal.acl.mm.AclGroup;
import org.vnu.sme.goal.acl.mm.AclModel;
import org.vnu.sme.goal.acl.mm.AclRelation;

public final class AclSpecText {

    private AclSpecText() {}

    public static String render(AclModel model) {
        StringBuilder sb = new StringBuilder();
        sb.append("ACL ").append(model.version()).append(" ").append(model.name()).append("\n");

        section(sb, "Enums");
        if (model.enums().isEmpty()) {
            none(sb);
        } else {
            model.enums().forEach(e -> sb.append("  enum ").append(e.name()).append(" {")
                    .append(String.join(", ", e.literals())).append("}\n"));
        }

        section(sb, "Entities");
        if (model.entities().isEmpty()) {
            none(sb);
        } else {
            model.entities().forEach(e -> writeTypedBlock(sb, "entity", e.name(), null, e.attributes()));
        }

        section(sb, "Actors");
        if (model.actors().isEmpty()) {
            none(sb);
        } else {
            model.actors().forEach(a -> writeActor(sb, a));
        }

        section(sb, "Relations");
        if (model.relations().isEmpty()) {
            none(sb);
        } else {
            model.relations().forEach(r -> writeRelation(sb, r));
        }

        section(sb, "Groups");
        if (model.groups().isEmpty()) {
            none(sb);
        } else {
            model.groups().forEach(g -> writeGroup(sb, g));
        }

        section(sb, "Links");
        if (model.links().isEmpty()) {
            none(sb);
        } else {
            model.links().forEach(l -> {
                sb.append("  link ").append(l.kind()).append(" ")
                        .append(l.sourceRole()).append(" -> ").append(l.targetRole());
                if (l.scopeKind() != null) sb.append(" ").append(l.scopeKind()).append(" ").append(l.scopeGroup());
                sb.append("\n");
            });
        }

        section(sb, "Invariants");
        if (model.invariants().isEmpty()) {
            none(sb);
        } else {
            model.invariants().forEach(i -> sb.append("  invariant ").append(i.name())
                    .append(" context ").append(i.contextType()).append("\n")
                    .append(indent(i.oclBody(), 4)).append("\n"));
        }
        return sb.toString();
    }

    private static void writeActor(StringBuilder sb, AclActor actor) {
        String keyword = actor.isAbstract() ? "abstract " + actor.kind() : actor.kind();
        writeTypedBlock(sb, keyword, actor.name(), actor.specializes(), actor.attributes());
    }

    private static void writeTypedBlock(StringBuilder sb, String keyword, String name, String specializes,
                                        List<AclAttribute> attributes) {
        sb.append("  ").append(keyword).append(" ").append(name);
        if (specializes != null) sb.append(" specializes ").append(specializes);
        sb.append("\n");
        attributes.forEach(a -> sb.append("    ").append(a.name()).append(" : ").append(a.type()).append("\n"));
    }

    private static void writeRelation(StringBuilder sb, AclRelation relation) {
        sb.append("  ").append(relation.kind()).append(" ").append(relation.name()).append("\n");
        relation.endpoints().forEach(e -> sb.append("    ").append(endpoint(e)).append("\n"));
    }

    private static String endpoint(AclEndpoint endpoint) {
        return endpoint.type() + " [" + endpoint.multiplicity() + "] " + endpoint.roleName();
    }

    private static void writeGroup(StringBuilder sb, AclGroup group) {
        sb.append("  group ").append(group.name());
        if (group.specializes() != null) sb.append(" specializes ").append(group.specializes());
        sb.append("\n");
        group.attributes().forEach(a -> sb.append("    ").append(a.name()).append(" : ").append(a.type()).append("\n"));
        group.members().forEach(m -> sb.append("    ").append(m.type()).append(" [").append(m.multiplicity()).append("]\n"));
    }

    private static void section(StringBuilder sb, String title) {
        sb.append("\n").append(title).append("\n");
    }

    private static void none(StringBuilder sb) {
        sb.append("  (none)\n");
    }

    private static String indent(String text, int spaces) {
        String prefix = " ".repeat(spaces);
        return text.lines().map(line -> prefix + line).collect(Collectors.joining(System.lineSeparator()));
    }
}
