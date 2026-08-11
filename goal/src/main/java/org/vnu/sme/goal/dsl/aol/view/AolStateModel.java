package org.vnu.sme.goal.dsl.aol.view;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.sys.MLink;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MSystemState;

import org.vnu.sme.goal.dsl.acl.mm.AclModel;
import org.vnu.sme.goal.dsl.acl.mm.AclOwner;
import org.vnu.sme.goal.dsl.acl.mm.AclRelation;
import org.vnu.sme.goal.dsl.aol.mm.AolGroupInstance;
import org.vnu.sme.goal.dsl.aol.mm.AolLink;
import org.vnu.sme.goal.dsl.aol.mm.AolModel;
import org.vnu.sme.goal.dsl.aol.mm.AolPlay;

/**
 * Rebuilds an {@link AolModel} snapshot from one live USE object-graph state, scoped to a
 * single group instance — the reverse direction of {@link
 * org.vnu.sme.goal.translate.aclaol2soil.AclAol2SoilTranslator}. This is what lets an arbitrary point
 * of a running BPMN trace be shown in the same {@link AolView} diagram/browser/snapshot the
 * static {@code .aol} file already uses, instead of a bespoke text dump. Follows the same
 * {@code Agent_plays_<Role>} / {@code <Role>_in_<Group>} association-naming convention {@link
 * org.vnu.sme.goal.translate.acl2use.Acl2UseTranslator} generates.
 */
public final class AolStateModel {

    private AolStateModel() {}

    public static AolModel build(AclModel acl, MSystemState state, String groupClass, MObject self) {
        if (self == null) {
            return new AolModel("v1.0", "state", "(unscoped)", List.of(), Map.of(), Map.of(),
                    List.of(), List.of(), List.of());
        }

        List<String> agents = new ArrayList<>();
        Map<String, Map<String, String>> agentAttributeValues = new LinkedHashMap<>();
        List<AolPlay> plays = new ArrayList<>();
        for (AclOwner owner : acl.owners()) {
            if (!owner.sourceGroup().equals(groupClass) || acl.findRole(owner.target()).isEmpty()) continue;
            String roleType = owner.target();
            for (MObject roleObject : linkedObjects(state, self, roleType + "_in_" + groupClass, 0, 1)) {
                MObject agent = linkedObject(state, roleObject, "Agent_plays_" + roleType, 1, 0);
                String agentId = agent == null ? "?" : agent.name();
                if (agent != null && !agentAttributeValues.containsKey(agentId)) {
                    agents.add(agentId);
                    agentAttributeValues.put(agentId, attributeValues(state, agent));
                }
                plays.add(new AolPlay(roleType, roleObject.name(), agentId, attributeValues(state, roleObject)));
            }
        }

        AolGroupInstance group = new AolGroupInstance(groupClass, self.name(),
                List.of(), plays, List.of(), attributeValues(state, self));

        // Agent-to-agent relations (e.g. "knowsPhoneOf") are declared directly on the
        // (abstract-role-realized) Agent class in ACL, not scoped to this group, but only
        // links whose source is one of this group's own agents are relevant to show here.
        List<AolLink> links = new ArrayList<>();
        for (AclRelation relation : acl.relations()) {
            for (String agentId : agents) {
                MObject agentObject = state.objectByName(agentId);
                List<MObject> targets = agentObject == null ? List.of()
                        : linkedObjects(state, agentObject, relation.name(), 0, 1);
                if (!targets.isEmpty()) {
                    links.add(new AolLink(relation.name(), agentId, targets.stream().map(MObject::name).toList()));
                }
            }
        }

        return new AolModel("v1.0", groupClass + "State", acl.name(), agents, Map.of(),
                agentAttributeValues, List.of(group), List.of(), links);
    }

    private static Map<String, String> attributeValues(MSystemState state, MObject object) {
        Map<String, String> values = new LinkedHashMap<>();
        object.state(state).attributeValueMap().forEach((attribute, value) ->
                // USE renders an enum value qualified ("TimetableChannel::calendar"); AOL's
                // own literal syntax is bare ("calendar"), so strip the "Type::" prefix to
                // match how the static .aol viewer already displays the same attribute.
                values.put(attribute.name(), String.valueOf(value).replaceFirst("^[A-Za-z_]\\w*::", "")));
        return values;
    }

    private static MObject linkedObject(MSystemState state, MObject known, String associationName,
            int knownIndex, int resultIndex) {
        List<MObject> found = linkedObjects(state, known, associationName, knownIndex, resultIndex);
        return found.isEmpty() ? null : found.get(0);
    }

    /** Association endpoint positions follow Acl2UseTranslator: source first, target second. */
    private static List<MObject> linkedObjects(MSystemState state, MObject known, String associationName,
            int knownIndex, int resultIndex) {
        MAssociation association = state.system().model().getAssociation(associationName);
        if (association == null || state.linksOfAssociation(association) == null) return List.of();
        List<MObject> result = new ArrayList<>();
        for (MLink link : state.linksOfAssociation(association).links()) {
            MObject[] objects = link.linkedObjectsAsArray();
            if (objects.length > Math.max(knownIndex, resultIndex) && objects[knownIndex].equals(known)) {
                result.add(objects[resultIndex]);
            }
        }
        return result;
    }
}
