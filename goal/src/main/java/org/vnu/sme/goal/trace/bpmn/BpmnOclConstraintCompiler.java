package org.vnu.sme.goal.trace.bpmn;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.tzi.use.parser.Symtable;
import org.tzi.use.parser.ocl.OCLCompiler;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.ocl.expr.Expression;
import org.vnu.sme.goal.dsl.bpmn.mm.BpmnModel;
import org.vnu.sme.goal.dsl.bpmn.mm.FlowElement;
import org.vnu.sme.goal.dsl.bpmn.mm.SequenceFlow;
import org.vnu.sme.goal.dsl.bpmn.mm.SubProcess;
import org.vnu.sme.goal.dsl.acl.mm.AclModel;
import org.vnu.sme.goal.dsl.acl.ocl.AclOclPropertyResolver;

/**
 * Compiles raw OCL clauses embedded in BPMN2 flow elements and sequence flows
 * against a USE domain model. The BPMN model stays domain-agnostic; callers
 * provide the USE context class for each BPMN node/flow that has OCL.
 */
public final class BpmnOclConstraintCompiler {

    public record ConstraintInfo(
            String constraintId,
            String ownerKind,
            String ownerId,
            String contextType,
            MClass contextClass,
            Expression expr) {}

    public record Result(Map<String, ConstraintInfo> constraints, List<String> errors) {
        public boolean ok() { return errors.isEmpty(); }
    }

    private BpmnOclConstraintCompiler() {}

    /**
     * Compile every embedded BPMN OCL clause.
     *
     * <p>Keys in {@code contextTypes}:
     * <ul>
     *   <li>FlowElement OCL: the BPMN element id, e.g. {@code approveTask}</li>
     *   <li>SequenceFlow OCL: {@code sourceId::targetId}, e.g. {@code check::approve}</li>
     * </ul>
     */
    public static Result compile(BpmnModel model, MModel useModel, Map<String, String> contextTypes) {
        return compile(model, useModel, contextTypes, null);
    }

    public static Result compile(BpmnModel model, MModel useModel,
                                 Map<String, String> contextTypes, AclModel acl) {
        List<String> errors = new ArrayList<>();
        Map<String, ConstraintInfo> constraints = new LinkedHashMap<>();

        for (org.vnu.sme.goal.dsl.bpmn.mm.Process process : model.processes()) {
            compileFlowElements(process.flowElements(), useModel, contextTypes, constraints, errors, acl);
            compileSequenceFlows(process.sequenceFlows(), useModel, contextTypes, constraints, errors, acl);
        }

        return new Result(constraints, errors);
    }

    private static void compileFlowElements(List<FlowElement> elements, MModel useModel,
            Map<String, String> contextTypes, Map<String, ConstraintInfo> constraints, List<String> errors,
            AclModel acl) {
        for (FlowElement element : elements) {
            int index = 1;
            for (var condition : element.constraints()) {
                String id = element.id() + "::" + condition.kind().name().toLowerCase() + "#" + index++;
                compileOne(id, "node-" + condition.kind().name().toLowerCase(), element.id(),
                        condition.oclBody(), useModel, contextTypes, constraints, errors, acl);
            }
            if (element instanceof SubProcess sp) {
                compileFlowElements(sp.flowElements(), useModel, contextTypes, constraints, errors, acl);
                compileSequenceFlows(sp.sequenceFlows(), useModel, contextTypes, constraints, errors, acl);
            }
        }
    }

    private static void compileSequenceFlows(List<SequenceFlow> flows, MModel useModel,
            Map<String, String> contextTypes, Map<String, ConstraintInfo> constraints, List<String> errors,
            AclModel acl) {
        for (SequenceFlow flow : flows) {
            String id = sequenceFlowId(flow);
            compileOne(id, "sequenceFlow-guard", id, flow.guardSource(), useModel,
                    contextTypes, constraints, errors, acl);
        }
    }

    public static String sequenceFlowId(SequenceFlow flow) {
        return flow.source().id() + "::" + flow.target().id();
    }

    private static void compileOne(String constraintId, String ownerKind, String ownerId, String oclSource,
            MModel useModel, Map<String, String> contextTypes,
            Map<String, ConstraintInfo> constraints, List<String> errors, AclModel acl) {
        if (oclSource == null || oclSource.isBlank()) return;

        String contextType = contextTypes.get(constraintId);
        if (contextType == null) contextType = contextTypes.get(ownerId);
        if (contextType == null || contextType.isBlank()) {
            errors.add("bpmn ocl '" + constraintId + "': missing USE context type mapping");
            return;
        }

        MClass contextClass = useModel.getClass(contextType);
        if (contextClass == null) {
            errors.add("bpmn ocl '" + constraintId + "': no .use class named '" + contextType + "'");
            return;
        }

        StringWriter sw = new StringWriter();
        PrintWriter err = new PrintWriter(sw);
        Symtable vars = new Symtable();
        Expression expr;
        try {
            vars.add("self", contextClass, null);
            String resolvedSource = AclOclPropertyResolver.rewrite(acl, contextType, oclSource);
            expr = OCLCompiler.compileExpression(useModel, resolvedSource, constraintId, err, vars);
        } catch (org.tzi.use.parser.SemanticException ex) {
            errors.add("bpmn ocl '" + constraintId + "' (self : " + contextType + "): " + ex.getMessage());
            return;
        }
        err.flush();

        if (expr == null) {
            errors.add("bpmn ocl '" + constraintId + "' (self : " + contextType + "): " + sw);
        } else {
            constraints.put(constraintId, new ConstraintInfo(
                    constraintId, ownerKind, ownerId, contextType, contextClass, expr));
        }
    }
}
