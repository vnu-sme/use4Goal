package org.vnu.sme.goal.bpmn2scenario;

import java.nio.file.Path;

import org.vnu.sme.goal.bpmn2scenario.parser.Bpmn2ScenarioCompiler;

/** Small CLI demo for BPMN scenario files. */
public final class Bpmn2ScenarioDemoMain {

    private Bpmn2ScenarioDemoMain() {}

    public static void main(String[] args) throws Exception {
        Path file = Path.of(args.length > 0 ? args[0]
                : "goal/src/main/resources/examples/mtg/mtg_partial.bscn");

        Bpmn2ScenarioCompiler.Result result = Bpmn2ScenarioCompiler.compile(file);
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
