package org.vnu.sme.goal.conformance.flow;

import java.nio.file.Path;

/** Command-line entry point for repeatable end-to-end flow checks. */
public final class CompleteConformanceFlowMain {

    private CompleteConformanceFlowMain() {}

    public static void main(String[] args) {
        if (args.length != 5) {
            System.err.println("usage: CompleteConformanceFlowMain "
                    + "model.acl snapshot.aol model.istar expected.iscn process.bpmn2");
            System.exit(2);
        }

        ConformanceFlowRunner.Result result = ConformanceFlowRunner.run(
                Path.of(args[0]), Path.of(args[1]), Path.of(args[2]),
                Path.of(args[3]), Path.of(args[4]));

        result.stages().forEach(stage -> System.out.printf(
                "%-30s %-6s %s%n", stage.stage().label(), stage.state(), stage.detail()));
        result.errors().forEach(System.err::println);

        if (result.ok()) {
            for (ConformanceFlowRunner.TraceResult trace : result.traces()) {
                var conformance = trace.conformance();
                System.out.println();
                System.out.println("Trace #" + trace.index() + "   : " + trace.verdict());
                System.out.println("Activities     : " + String.join(" -> ", trace.activityIds()));
                printFailures("ACL invariants", conformance.aclFailures());
                printFailures("BPMN pre/post OCL", conformance.bpmnFailures());
                printFailures("iStar root goals", conformance.goalFailures());
                printFailures("ISCN oracle", trace.oracleFailures());
                System.out.println("Generated USE  : " + conformance.generatedUse());
                System.out.println("Execution SOIL : " + conformance.executionSoil());
            }
        }
        if (result.ok()) {
            System.out.println();
            System.out.println("Initial SOIL   : " + result.generatedInitialSoil());
            System.out.println("Trace coverage : " + result.conformantTraceCount() + "/"
                    + result.traces().size() + " conformant; complete="
                    + result.completeExecutionSpace());
        }
        System.out.println("Verdict       : " + result.verdict());
        if (result.ok()) {
            System.out.println("Scope         : "
                    + (result.verdict().isProcessLevel()
                            ? "COMPLETE_EXECUTION_SPACE" : "SINGLE_TRACE"));
        }
        if (!result.conformant()) System.exit(1);
    }

    private static void printFailures(String label, java.util.List<String> failures) {
        System.out.println(label + ": " + (failures.isEmpty() ? "PASS" : "FAIL"));
        failures.forEach(failure -> System.out.println("  - " + failure));
    }
}
