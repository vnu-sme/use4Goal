package org.vnu.sme.goal.feature.bpmnoclvalidation;

import java.nio.file.Path;

import org.vnu.sme.goal.trace.bpmn.BpmnOclValidationCompiler;
import org.vnu.sme.goal.trace.bpmn.BpmnOclConstraintCompiler.ConstraintInfo;

/**
 * Runnable demonstration that the BPMN2 Option 2 OCL compiler is wired into an
 * actual end-to-end flow.
 */
public final class BpmnOclValidationDemoMain {

    private BpmnOclValidationDemoMain() {}

    public static void main(String[] args) throws Exception {
        Path base = Path.of(args.length > 0 ? args[0] : "goal/src/main/resources/examples/bpmn_ocl");
        Path bpmn = args.length > 1 ? Path.of(args[1]) : base.resolve("claim_handling_ocl.bpmn2");
        Path use = args.length > 2 ? Path.of(args[2]) : base.resolve("claim_handling.use");
        Path map = args.length > 3 ? Path.of(args[3]) : base.resolve("claim_handling.bpmn2oclmap");

        BpmnOclValidationCompiler.Result result = BpmnOclValidationCompiler.compile(bpmn, use, map);
        if (!result.ok()) {
            result.errors().forEach(System.err::println);
            System.exit(1);
        }

        System.out.println("BPMN OCL validation OK");
        System.out.println("Compiled constraints: " + result.constraints().size());
        for (ConstraintInfo c : result.constraints().values()) {
            System.out.println("  " + c.constraintId() + " [" + c.ownerKind() + ", self : "
                    + c.contextType() + "]");
        }
    }
}
