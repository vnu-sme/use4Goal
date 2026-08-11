package org.vnu.sme.goal.gui;

import java.awt.BorderLayout;
import java.util.List;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.*;

import org.vnu.sme.goal.dsl.acl.mm.*;
import org.vnu.sme.goal.dsl.bpmn.mm.Activity;
import org.vnu.sme.goal.dsl.bpmn.mm.BpmnModel;
import org.vnu.sme.goal.dsl.bpmn.mm.FlowElement;
import org.vnu.sme.goal.dsl.bpmn.mm.SubProcess;
import org.vnu.sme.goal.dsl.istar.mm.Actor;
import org.vnu.sme.goal.dsl.istar.mm.Goal;
import org.vnu.sme.goal.dsl.istar.mm.GoalModel;
import org.vnu.sme.goal.dsl.istar.mm.IntentionalElement;
import org.vnu.sme.goal.dsl.istar.mm.Obstacle;

/** Read-only tree browser for the Goal plugin's diagram models. */
@SuppressWarnings("serial")
public final class DiagramModelBrowser extends JPanel {
    private final JTree tree = new JTree();
    private final JEditorPane details = new JEditorPane("text/html", "");

    private DiagramModelBrowser(DefaultMutableTreeNode root) {
        super(new BorderLayout());
        tree.setModel(new DefaultTreeModel(root));
        tree.setRootVisible(true);
        tree.setShowsRootHandles(true);
        tree.putClientProperty("JTree.lineStyle", "Angled");
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(this::selectionChanged);
        ToolTipManager.sharedInstance().registerComponent(tree);
        details.setEditable(false);
        details.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(tree), new JScrollPane(details));
        split.setResizeWeight(0.72);
        split.setDividerLocation(360);
        split.setOneTouchExpandable(true);
        add(split, BorderLayout.CENTER);
        expand(root.getChildCount() < 12 ? 3 : 1);
        tree.setSelectionRow(0);
    }

    public static DiagramModelBrowser forBpmn(BpmnModel model) {
        DefaultMutableTreeNode root = node("BPMN: " + model.name(), "BPMN collaboration model",
                "Model", model.name());
        DefaultMutableTreeNode processes = group(root, "Processes", model.processes().size());
        for (var process : model.processes()) {
            DefaultMutableTreeNode processNode = node("Process: " + named(process.id(), process.name()),
                    "BPMN process", "Id", process.id(), "Name", process.name());
            processes.add(processNode);
            DefaultMutableTreeNode lanes = group(processNode, "Lanes", process.lanes().size());
            process.lanes().forEach(lane -> lanes.add(node("Lane: " + named(lane.id(), lane.name()),
                    "Lane", "Referenced elements", lane.flowElements().size())));
            DefaultMutableTreeNode elements = group(processNode, "Flow elements", process.flowElements().size());
            process.flowElements().forEach(element -> elements.add(bpmnElement(element)));
            DefaultMutableTreeNode flows = group(processNode, "Sequence flows", process.sequenceFlows().size());
            process.sequenceFlows().forEach(flow -> flows.add(node(flow.source().id() + " -> " + flow.target().id(),
                    "Sequence flow", "Label", flow.label(), "Guard", flow.guardSource())));
        }
        DefaultMutableTreeNode messages = group(root, "Messages", model.messages().size());
        model.messages().forEach(message -> messages.add(node("Message: " + named(message.id(), message.name()),
                "BPMN message")));
        DefaultMutableTreeNode messageFlows = group(root, "Message flows", model.messageFlows().size());
        model.messageFlows().forEach(flow -> messageFlows.add(node(flow.source().id() + " -> " + flow.target().id(),
                "Message flow", "Message", flow.message() == null ? null : flow.message().id())));
        return new DiagramModelBrowser(root);
    }

    public static DiagramModelBrowser forAcl(AclModel model) {
        DefaultMutableTreeNode root = node("ACL: " + model.name(), "ACL structural specification",
                "Version", model.version(), "Root group", model.rootGroup().name());
        DefaultMutableTreeNode enums = group(root, "Enums", model.enums().size());
        model.enums().forEach(value -> enums.add(node("Enum: " + value.name(), "Enumeration",
                "Literals", String.join(", ", value.literals()))));
        DefaultMutableTreeNode roles = group(root, "Roles", model.roles().size());
        model.roles().forEach(role -> {
            DefaultMutableTreeNode roleNode = node((role.isAbstract() ? "Abstract role: " : "Role: ") + role.name(),
                    "Role definition", "Parents", String.join(", ", role.parentRoles()));
            roles.add(roleNode);
            role.attributes().forEach(a -> roleNode.add(attribute(a)));
        });
        DefaultMutableTreeNode entities = group(root, "Entities", model.entities().size());
        model.entities().forEach(entity -> {
            DefaultMutableTreeNode entityNode = node("Entity: " + entity.name(), "Entity definition");
            entities.add(entityNode);
            entity.attributes().forEach(a -> entityNode.add(attribute(a)));
        });
        root.add(aclGroup(model.rootGroup(), true));
        return new DiagramModelBrowser(root);
    }

    public static DiagramModelBrowser forAol(org.vnu.sme.goal.dsl.aol.mm.AolModel model) {
        DefaultMutableTreeNode root = node("AOL: " + model.name(), "AOL object snapshot",
                "Version", model.version(), "ACL file", model.aclFile());
        DefaultMutableTreeNode agents = group(root, "Agents", model.agents().size());
        model.agents().forEach(a -> agents.add(node(a, "Agent identity",
                "Profile role", model.agentProfileRoles().get(a),
                "Attributes", model.agentAttributeValues().getOrDefault(a, java.util.Map.of()))));
        DefaultMutableTreeNode groups = group(root, "Group instances", model.groupInstances().size());
        model.groupInstances().forEach(g -> groups.add(aolGroup(g)));
        return new DiagramModelBrowser(root);
    }

    public static DiagramModelBrowser forIStar(GoalModel model) {
        DefaultMutableTreeNode root = node("iStar: " + model.getName(), "iStar goal model",
                "Actors", model.getActors().size(), "Dependencies", model.getDependencies().size());
        DefaultMutableTreeNode actors = group(root, "Actors", model.getActors().size());
        for (Actor actor : model.getActors()) {
            DefaultMutableTreeNode actorNode = node(actor.getClass().getSimpleName() + ": " + actor.name(),
                    "Intentional actor", "Elements", actor.elements().size());
            actors.add(actorNode);
            DefaultMutableTreeNode elements = group(actorNode, "Intentional elements", actor.elements().size());
            actor.elements().forEach(element -> elements.add(iStarElement(element)));
            addRelationGroup(actorNode, "Refinements", actor.refinements());
            addRelationGroup(actorNode, "Contributions", actor.contributions());
            addRelationGroup(actorNode, "Qualifications", actor.qualifications());
            addRelationGroup(actorNode, "Needed-by", actor.neededBys());
            addRelationGroup(actorNode, "Obstructions", actor.obstructions());
            addRelationGroup(actorNode, "Resolutions", actor.resolutions());
            addRelationGroup(actorNode, "Associations", actor.associations());
        }
        DefaultMutableTreeNode dependencies = group(root, "Dependencies", model.getDependencies().size());
        model.getDependencies().forEach(d -> dependencies.add(node(d.depender() + " -> " + d.dependee(),
                "iStar dependency", "Depender element", d.dependerElmt(), "Dependum kind", d.dependumKind(),
                "Dependum", d.dependum(), "Dependee element", d.dependeeElmt())));
        return new DiagramModelBrowser(root);
    }

    private static DefaultMutableTreeNode bpmnElement(FlowElement element) {
        DefaultMutableTreeNode result = node(element.getClass().getSimpleName() + ": "
                + named(element.id(), element.name()), "BPMN flow element",
                "Id", element.id(), "Name", element.name());
        if (!element.constraints().isEmpty()) {
            DefaultMutableTreeNode constraints = group(result, "State conditions", element.constraints().size());
            element.constraints().forEach(v -> constraints.add(node(
                    v.kind().name() + " OCL: " + preview(v.oclBody()),
                    v.kind().name() + " state condition", "Owner", element.id(), "Body", v.oclBody())));
        }
        if (element instanceof SubProcess subprocess) {
            DefaultMutableTreeNode children = group(result, "Nested elements", subprocess.flowElements().size());
            subprocess.flowElements().forEach(child -> children.add(bpmnElement(child)));
        }
        return result;
    }

    private static DefaultMutableTreeNode aclGroup(AclGroup value, boolean rootGroup) {
        DefaultMutableTreeNode result = node((rootGroup ? "Root group: " : "Subgroup: ") + value.name(),
                "ACL group specification");
        DefaultMutableTreeNode roles = group(result, "Role memberships", value.roles().size());
        value.roles().forEach(v -> roles.add(node(v.roleName() + " " + cardinality(v.cardinality()), "Role membership")));
        DefaultMutableTreeNode entities = group(result, "Entity memberships", value.entities().size());
        value.entities().forEach(v -> entities.add(node(v.entityName() + " " + cardinality(v.cardinality()), "Entity membership")));
        DefaultMutableTreeNode subgroups = group(result, "Subgroups", value.subgroups().size());
        value.subgroups().forEach(v -> subgroups.add(aclGroup(v.group(), false)));
        DefaultMutableTreeNode compatibility = group(result, "Compatibility", value.compatibilities().size());
        value.compatibilities().forEach(v -> compatibility.add(node(v.fromRole() + " <-> " + v.toRole(),
                "Role compatibility", "Type", v.type().sourceName(), "Scope", v.scope().sourceName())));
        DefaultMutableTreeNode relations = group(result, "Role-entity relations", value.roleEntityRelations().size());
        value.roleEntityRelations().forEach(v -> relations.add(node(v.name(), "Role-entity relation",
                "Type", v.type().sourceName(), "Source role", v.sourceRole().name(),
                "Target entity", v.targetEntity().name(), "Scope", v.scope().sourceName())));
        DefaultMutableTreeNode cardinalities = group(result, "Cardinality constraints", value.cardinalityConstraints().size());
        value.cardinalityConstraints().forEach(v -> cardinalities.add(node(v.targetKind().sourceName() + ": "
                + v.targetName() + " " + cardinality(v.cardinality()), "Cardinality constraint")));
        return result;
    }

    private static DefaultMutableTreeNode aolGroup(org.vnu.sme.goal.dsl.aol.mm.AolGroupInstance value) {
        DefaultMutableTreeNode result = node(value.typeName() + " as " + value.instanceId(), "AOL group instance");
        DefaultMutableTreeNode plays = group(result, "Plays", value.plays().size());
        value.plays().forEach(p -> plays.add(node(p.roleType() + " as " + p.instanceId() + " by " + p.agentId(),
                "Role play", joinValues(p.attributeValues()))));
        DefaultMutableTreeNode entities = group(result, "Entity instances", value.entities().size());
        value.entities().forEach(e -> entities.add(node(e.entityType() + " as " + e.instanceId(),
                "Entity instance", joinValues(e.attributeValues()))));
        DefaultMutableTreeNode subgroups = group(result, "Subgroup instances", value.subgroups().size());
        value.subgroups().forEach(s -> subgroups.add(aolGroup(s)));
        return result;
    }

    private static Object[] joinValues(java.util.Map<String, String> values) {
        return new Object[] {"Attributes", values.isEmpty() ? null
                : values.entrySet().stream().map(e -> e.getKey() + " = " + e.getValue())
                        .reduce((a, b) -> a + ", " + b).orElse(null)};
    }

    private static DefaultMutableTreeNode attribute(AclAttribute value) {
        return node("Attribute: " + value.name(), "Attribute", "Type", value.type().sourceName(),
                "Optional", value.optional(), "Mutable", value.mutable(), "Default", value.defaultValue().orElse(null));
    }

    private static DefaultMutableTreeNode iStarElement(IntentionalElement element) {
        String type = element.getClass().getSimpleName();
        if (element instanceof Goal goal) type += " (" + goal.goalType() + ")";
        if (element instanceof Obstacle obstacle) type += " (" + obstacle.type() + ")";
        var contract = element instanceof org.vnu.sme.goal.dsl.istar.mm.GoalTaskElement value ? value : null;
        return node(type + ": " + element.id(), "iStar intentional element",
                "Pre OCL", contract == null ? null : contract.preconditions().stream()
                        .map(org.vnu.sme.goal.dsl.istar.mm.IStarOclConstraint::oclBody).reduce((a, b) -> a + " and " + b).orElse(null),
                "Post OCL", contract == null ? null : contract.postconditions().stream()
                        .map(org.vnu.sme.goal.dsl.istar.mm.IStarOclConstraint::oclBody).reduce((a, b) -> a + " and " + b).orElse(null));
    }

    private static void addRelationGroup(DefaultMutableTreeNode parent, String label, List<?> values) {
        DefaultMutableTreeNode result = group(parent, label, values.size());
        values.forEach(value -> result.add(node(relationLabel(value), value.getClass().getSimpleName())));
    }

    private static String relationLabel(Object value) {
        if (value instanceof org.vnu.sme.goal.dsl.istar.mm.AndRefinement v)
            return "AND: " + String.join(", ", v.children()) + " -> " + v.parent();
        if (value instanceof org.vnu.sme.goal.dsl.istar.mm.OrRefinement v)
            return "OR: " + v.child() + " -> " + v.parent();
        if (value instanceof org.vnu.sme.goal.dsl.istar.mm.ParameterRefinement v)
            return value.getClass().getSimpleName() + ": " + v.child() + " -> " + v.parent()
                    + " [" + v.actorType() + "]";
        if (value instanceof org.vnu.sme.goal.dsl.istar.mm.Contribution v)
            return v.element() + " -" + v.type().label() + "-> " + v.quality();
        if (value instanceof org.vnu.sme.goal.dsl.istar.mm.Qualification v)
            return v.quality() + " -> " + v.element();
        if (value instanceof org.vnu.sme.goal.dsl.istar.mm.NeededBy v)
            return v.resource() + " -> " + v.task();
        if (value instanceof org.vnu.sme.goal.dsl.istar.mm.Obstruction v)
            return v.obstacle() + " -> " + v.element();
        if (value instanceof org.vnu.sme.goal.dsl.istar.mm.Resolution v)
            return v.element() + " -> " + v.obstacle();
        if (value instanceof org.vnu.sme.goal.dsl.istar.mm.Association v)
            return v.actor() + " -" + v.kind() + "-> " + v.target();
        return String.valueOf(value);
    }

    private static DefaultMutableTreeNode group(DefaultMutableTreeNode parent, String label, int size) {
        DefaultMutableTreeNode result = node(label + " (" + size + ")", label);
        parent.add(result);
        return result;
    }

    private static DefaultMutableTreeNode node(String label, String description, Object... fields) {
        return new DefaultMutableTreeNode(new Entry(label, html(description, fields)));
    }

    private static String html(String description, Object... fields) {
        StringBuilder out = new StringBuilder("<html><body><b>").append(escape(description)).append("</b>");
        for (int i = 0; i + 1 < fields.length; i += 2) {
            Object value = fields[i + 1];
            if (value == null || String.valueOf(value).isBlank()) continue;
            out.append("<br><b>").append(escape(String.valueOf(fields[i]))).append(":</b> ")
                    .append(escape(String.valueOf(value)).replace("\n", "<br>"));
        }
        return out.append("</body></html>").toString();
    }

    private static String escape(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static String named(String id, String name) {
        return name == null || name.isBlank() ? id : id + " (" + name + ")";
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static String preview(String value) {
        if (!hasText(value)) return "";
        String oneLine = value.replaceAll("\\s+", " ").trim();
        return oneLine.length() <= 72 ? oneLine : oneLine.substring(0, 69) + "...";
    }

    private static String cardinality(AclCardinality value) {
        return "[" + value.min() + ".." + (value.max().isPresent() ? value.max().getAsInt() : "*") + "]";
    }

    private void selectionChanged(TreeSelectionEvent event) {
        Object selected = tree.getLastSelectedPathComponent();
        if (selected instanceof DefaultMutableTreeNode node && node.getUserObject() instanceof Entry entry) {
            details.setText(entry.details());
            details.setCaretPosition(0);
        }
    }

    private void expand(int depth) {
        for (int row = 0; row < tree.getRowCount(); row++) {
            if (tree.getPathForRow(row).getPathCount() <= depth) tree.expandRow(row);
        }
    }

    private record Entry(String label, String details) {
        @Override public String toString() { return label; }
    }
}
