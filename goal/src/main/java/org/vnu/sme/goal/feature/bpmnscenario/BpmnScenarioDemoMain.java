package org.vnu.sme.goal.feature.bpmnscenario;

import java.nio.file.Path;

import org.vnu.sme.goal.dsl.bpmnscenario.parser.BpmnScenarioCompiler;

/** Small CLI demo for BPMN scenario files. */
public final class BpmnScenarioDemoMain {

    private BpmnScenarioDemoMain() {}

    public static void main(String[] args) throws Exception {
        Path file = Path.of(args.length > 0 ? args[0]
                : "goal/src/main/resources/examples/mtg/mtg_partial.bscn");

        BpmnScenarioCompiler.Result result = BpmnScenarioCompiler.compile(file);
        if (!result.ok()) {
            result.errors().forEach(System.err::println);
            System.exit(1);
        }

        System.out.println("Scenario  : " + result.scenario().name());
        System.out.println("Model     : " + result.modelFile());
        System.out.println("Processes : " + result.snapshot().processInstances().size());
        System.out.println("Actors    : " + result.snapshot().actors().size());
        System.out.println();

        System.out.println("Completed:");
        result.snapshot().completed().forEach(o -> System.out.println("  " + o.display()));
        System.out.println("Active:");
        result.snapshot().active().forEach(o -> System.out.println("  " + o.display()));
        System.out.println("Tokens:");
        result.snapshot().tokens().forEach(t -> System.out.println("  " + t.display()));
        System.out.println("Assertions:");
        result.assertions().forEach(a -> System.out.println("  " + a.expression()
                + " -> " + (a.holds() ? "HOLDS" : "FAILS") + " (" + a.detail() + ")"));
    }
}
