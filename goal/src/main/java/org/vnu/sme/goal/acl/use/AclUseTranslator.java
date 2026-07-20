package org.vnu.sme.goal.acl.use;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.vnu.sme.goal.acl.mm.AclActor;
import org.vnu.sme.goal.acl.mm.AclAttribute;
import org.vnu.sme.goal.acl.mm.AclEndpoint;
import org.vnu.sme.goal.acl.mm.AclEntity;
import org.vnu.sme.goal.acl.mm.AclGroup;
import org.vnu.sme.goal.acl.mm.AclInvariant;
import org.vnu.sme.goal.acl.mm.AclModel;
import org.vnu.sme.goal.acl.mm.AclRelation;

/**
 * Translates the ACL structural analysis model to a USE class model.
 *
 * <p>The current mapping intentionally follows the hand-written shadow models
 * used by the examples: roles/agents become association classes between a
 * context group and a shared identity class named {@code Person}.
 */
public final class AclUseTranslator {

    private static final String IDENTITY_CLASS = "Person";

    private AclUseTranslator() {}

    public static String translate(AclModel model) {
        StringBuilder out = new StringBuilder();
        Optional<AclGroup> context = model.groups().stream().findFirst();
        Map<String, AclActor> actors = model.actors().stream()
                .collect(Collectors.toMap(AclActor::name, a -> a, (a, b) -> a, LinkedHashMap::new));

        out.append("-- Generated from ACL. Edit the .acl source, then regenerate this .use file.\n\n");
        out.append("model ").append(model.name()).append("\n\n");

        model.enums().forEach(e -> out.append("enum ")
                .append(e.name())
                .append(" {")
                .append(String.join(", ", e.literals()))
                .append("}\n\n"));

        boolean hasPerson = model.entities().stream().anyMatch(e -> e.name().equals(IDENTITY_CLASS));
        if (!hasPerson && context.isPresent() && !model.actors().isEmpty()) {
            out.append("class ").append(IDENTITY_CLASS).append("\nend\n\n");
        }

        for (AclEntity entity : model.entities()) {
            renderClass(out, entity.name(), entity.attributes());
        }

        for (AclGroup group : model.groups()) {
            renderClass(out, group.name(), group.attributes());
        }

        if (context.isPresent() && !model.actors().isEmpty()) {
            AclGroup group = context.get();
            out.append("abstract associationclass Role between\n")
                    .append("  ").append(group.name()).append(" [*] role actor").append(group.name()).append("\n")
                    .append("  ").append(IDENTITY_CLASS).append(" [*] role actorPerson\n")
                    .append("end\n\n");
            out.append("abstract associationclass Agent between\n")
                    .append("  ").append(group.name()).append(" [*] role agent").append(group.name()).append("\n")
                    .append("  ").append(IDENTITY_CLASS).append(" [*] role agentPerson\n")
                    .append("end\n\n");

            for (AclActor actor : model.actors()) {
                renderActor(out, actor, actors);
            }

            for (AclGroup groupModel : model.groups()) {
                renderGroupMemberships(out, groupModel);
            }
        } else {
            for (AclActor actor : model.actors()) {
                renderClass(out, actor.name(), actor.attributes());
            }
        }

        for (AclRelation relation : model.relations()) {
            renderAssociation(out, relation);
        }

        if (!model.invariants().isEmpty()) {
            out.append("constraints\n\n");
            for (AclInvariant invariant : model.invariants()) {
                out.append("context ").append(invariant.contextType())
                        .append(" inv ").append(invariant.name()).append(":\n")
                        .append(indent(invariant.oclBody(), 2)).append("\n\n");
            }
        }

        if (!model.links().isEmpty()) {
            out.append("-- ACL links are preserved in the source model. Automatic OCL generation for\n")
                    .append("-- authority/communication/compatibility is intentionally left for the next\n")
                    .append("-- semantic step because it depends on the chosen identity/context policy.\n");
        }

        return out.toString();
    }

    private static void renderClass(StringBuilder out, String name, java.util.List<AclAttribute> attributes) {
        out.append("class ").append(name).append("\n");
        renderAttributes(out, attributes);
        out.append("end\n\n");
    }

    private static void renderActor(StringBuilder out, AclActor actor, Map<String, AclActor> actors) {
        String root = actor.kind().equals("agent") ? "Agent" : "Role";
        String parent = actor.specializes() != null && actors.containsKey(actor.specializes())
                ? actor.specializes()
                : root;

        if (actor.isAbstract()) out.append("abstract ");
        out.append("associationclass ").append(actor.name()).append(" < ").append(parent).append("\n");
        renderAttributes(out, actor.attributes());
        out.append("end\n\n");
    }

    private static void renderGroupMemberships(StringBuilder out, AclGroup group) {
        for (var member : group.members()) {
            if (member.type().equals(IDENTITY_CLASS)) continue;
            out.append("association ").append(group.name()).append(member.type()).append("Membership between\n")
                    .append("  ").append(group.name()).append(" [1] role ")
                    .append(lowerFirst(group.name())).append("\n")
                    .append("  ").append(member.type()).append(" ")
                    .append(useMultiplicity(member.multiplicity())).append(" role ")
                    .append(lowerFirst(member.type())).append("Members\n")
                    .append("end\n\n");
        }
    }

    private static void renderAssociation(StringBuilder out, AclRelation relation) {
        out.append(relation.kind().equals("partOf") ? "composition " : "association ")
                .append(relation.name()).append(" between\n");
        for (AclEndpoint endpoint : relation.endpoints()) {
            out.append("  ").append(endpoint.type()).append(" ")
                    .append(useMultiplicity(endpoint.multiplicity())).append(" role ")
                    .append(endpoint.roleName()).append("\n");
        }
        out.append("end\n\n");
    }

    private static void renderAttributes(StringBuilder out, java.util.List<AclAttribute> attributes) {
        if (attributes.isEmpty()) return;
        out.append("attributes\n");
        for (AclAttribute attribute : attributes) {
            out.append("  ").append(attribute.name()).append(" : ").append(attribute.type()).append("\n");
        }
    }

    private static String indent(String text, int spaces) {
        String prefix = " ".repeat(spaces);
        return text.lines().map(line -> prefix + line).collect(Collectors.joining(System.lineSeparator()));
    }

    private static String lowerFirst(String text) {
        if (text == null || text.isEmpty()) return text;
        return Character.toLowerCase(text.charAt(0)) + text.substring(1);
    }

    private static String useMultiplicity(String multiplicity) {
        return "[" + multiplicity + "]";
    }
}
