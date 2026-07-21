package org.vnu.sme.goal.conformance;

import java.nio.file.Path;

/**
 * First end-to-end checker for the four-file workflow:
 * ACL -> generated USE, SOIL initial/execution state, i* goals, BPMN+OCL solution.
 */
public final class AclBpmnIStarConformanceMain {

    private AclBpmnIStarConformanceMain() {}

    public static void main(String[] args) throws Exception {
        if (args.length != 4) {
            System.err.println("usage: AclBpmnIStarConformanceMain model.acl init.soil model.istar model.bpmn2");
            System.exit(2);
        }

        Path aclFile = Path.of(args[0]);
        Path soilFile = Path.of(args[1]);
        Path istarFile = Path.of(args[2]);
        Path bpmnFile = Path.of(args[3]);

        AclBpmnIStarConformanceChecker.Result result =
                AclBpmnIStarConformanceChecker.check(aclFile, soilFile, istarFile, bpmnFile);
        if (!result.ok()) {
            result.errors().forEach(System.err::println);
            System.exit(1);
        }
        System.out.println("Generated USE : " + result.generatedUse());
        System.out.println("Execution SOIL: " + result.executionSoil());
        System.out.println("Checkpoints   : " + result.checkpoints());
        System.out.println("ACL invariants: " + (result.aclFailures().isEmpty() ? "PASS" : "FAIL"));
        result.aclFailures().forEach(f -> System.out.println("  - " + f));
        System.out.println("BPMN OCL      : " + (result.bpmnFailures().isEmpty() ? "PASS" : "FAIL"));
        result.bpmnFailures().forEach(f -> System.out.println("  - " + f));
        System.out.println("i* root goals : " + (result.goalFailures().isEmpty() ? "PASS" : "FAIL"));
        result.goalFailures().forEach(f -> System.out.println("  - " + f));
        System.out.println("Verdict       : " + (result.conformant() ? "CONFORMANT" : "NOT CONFORMANT"));
    }
}
