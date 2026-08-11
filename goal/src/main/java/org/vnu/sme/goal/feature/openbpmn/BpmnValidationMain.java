package org.vnu.sme.goal.feature.openbpmn;

import java.nio.file.Path;

import org.vnu.sme.goal.dsl.bpmn.mm.Activity;
import org.vnu.sme.goal.dsl.bpmn.mm.FlowElement;
import org.vnu.sme.goal.dsl.bpmn.parser.BpmnCompiler;

/** Small command-line validator for BPMN concrete-syntax files. */
public final class BpmnValidationMain {

    private BpmnValidationMain() {}

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("usage: BpmnValidationMain model.bpmn2");
            System.exit(2);
        }
        BpmnCompiler.Result result = BpmnCompiler.compile(Path.of(args[0]));
        if (!result.ok()) {
            result.errors().forEach(System.err::println);
            System.exit(1);
        }

        int activities = 0;
        int constraints = 0;
        int guards = 0;
        for (var process : result.model().processes()) {
            for (FlowElement element : process.flowElements()) {
                if (element instanceof Activity activity) {
                    activities++;
                    constraints += activity.constraints().size();
                }
            }
            guards += (int) process.sequenceFlows().stream()
                    .filter(flow -> flow.guardSource() != null && !flow.guardSource().isBlank())
                    .count();
        }
        System.out.println("BPMN validation OK");
        System.out.println("Processes: " + result.model().processes().size());
        System.out.println("Activities: " + activities);
        System.out.println("Pre/post constraints: " + constraints);
        System.out.println("Flow guards: " + guards);
    }
}
