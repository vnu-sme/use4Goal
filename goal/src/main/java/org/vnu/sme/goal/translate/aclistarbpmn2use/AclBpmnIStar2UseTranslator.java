package org.vnu.sme.goal.translate.aclistarbpmn2use;

import java.util.ArrayList;
import java.util.List;

import org.vnu.sme.goal.dsl.acl.mm.AclModel;
import org.vnu.sme.goal.dsl.bpmn.mm.BpmnModel;
import org.vnu.sme.goal.dsl.istar.mm.GoalModel;
import org.vnu.sme.goal.translate.aclbpmn2use.AclBpmn2UseTranslator;
import org.vnu.sme.goal.translate.aclistar2use.AclIStar2UseTranslator;

/** Composes the ACL+iStar and ACL+BPMN translations into one USE/TOCL model. */
public final class AclBpmnIStar2UseTranslator {
    private AclBpmnIStar2UseTranslator() {}

    public record Result(String useText, String toclText, List<String> diagnostics) {
        public boolean ok() {
            return diagnostics.stream().noneMatch(message -> message.startsWith("Error:"));
        }
    }

    public static Result translate(AclModel acl, BpmnModel bpmn, GoalModel istar) {
        AclIStar2UseTranslator.Result goalResult = AclIStar2UseTranslator.translate(acl, istar);
        String modelName = sanitize(istar.getName()) + "_" + sanitize(bpmn.name()) + "_Verification";
        AclBpmn2UseTranslator.Result processResult = AclBpmn2UseTranslator.translateOnto(
                acl, bpmn, goalResult.useText(), modelName);

        List<String> diagnostics = new ArrayList<>(goalResult.diagnostics());
        diagnostics.addAll(processResult.diagnostics());
        String tocl = joinTemporalSections(goalResult.toclText(), processResult.toclText());
        return new Result(processResult.useText(), tocl, List.copyOf(diagnostics));
    }

    public static String outputModelName(GoalModel istar, BpmnModel bpmn) {
        return sanitize(istar.getName()) + "_" + sanitize(bpmn.name()) + "_Verification";
    }

    private static String joinTemporalSections(String goals, String process) {
        StringBuilder out = new StringBuilder();
        if (goals != null && !goals.isBlank()) {
            out.append("-- ===== iStar temporal goal properties =====\n")
                    .append(goals.strip()).append("\n\n");
        }
        if (process != null && !process.isBlank()) {
            out.append("-- ===== BPMN temporal sequence-flow properties =====\n")
                    .append(process.strip()).append('\n');
        }
        return out.toString();
    }

    private static String sanitize(String value) {
        if (value == null || value.isBlank()) return "unnamed";
        String clean = value.replaceAll("[^A-Za-z0-9_]", "_");
        return Character.isDigit(clean.charAt(0)) ? "_" + clean : clean;
    }
}
