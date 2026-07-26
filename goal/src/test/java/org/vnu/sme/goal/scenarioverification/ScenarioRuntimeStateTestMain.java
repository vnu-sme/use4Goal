package org.vnu.sme.goal.scenarioverification;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.tzi.use.parser.use.USECompiler;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.ModelFactory;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MSystem;
import org.vnu.sme.goal.bpmn2scenario.mm.Bpmn2ScenarioSnapshot;
import org.vnu.sme.goal.bpmn2scenario.mm.NodeOccurrence;
import org.vnu.sme.goal.bpmn2scenario.mm.TokenMark;
import org.vnu.sme.goal.bpmn2scenario.mm.Value;

/** Plain Java unit test harness for {@link ScenarioRuntimeState}. */
public final class ScenarioRuntimeStateTestMain {

    private ScenarioRuntimeStateTestMain() {}

    public static void main(String[] args) throws Exception {
        MModel model = compileUseModel(Path.of("goal/src/main/resources/examples/bpmn_ocl/audit/order_domain.use"));
        MSystem system = new MSystem(model);
        MObject order = system.state().createObject(model.getClass("Order"), "order1");

        Bpmn2ScenarioSnapshot snapshot = new Bpmn2ScenarioSnapshot(
                Map.of("p1", "OrderOffice"),
                Map.of("order1", "Order"),
                Map.of("order1.amount", new Value.Atom("100")),
                List.of(new NodeOccurrence("p1", "review_order", "order1", null)),
                List.of(new NodeOccurrence("p1", "review_order", "order1", null)),
                List.of(),
                List.of(new TokenMark("p1", "review_order", "decide_order", "order1")),
                List.of());

        Map<String, MObject> bindings = new LinkedHashMap<>();
        bindings.put("review_order", order);
        ScenarioRuntimeState runtimeState = new ScenarioRuntimeState(snapshot, system.state(), bindings);

        require(runtimeState.snapshot() == snapshot, "snapshot should be the provided snapshot");
        require(runtimeState.state() == system.state(), "state should be the provided MSystemState");
        require(runtimeState.selfForBpmn("review_order") == order, "self binding should be returned");
        require(runtimeState.selfForBpmn("missing") == null, "missing self binding should return null");

        bindings.clear();
        require(runtimeState.selfForBpmn("review_order") == order,
                "self bindings should be defensively copied");

        boolean unmodifiable = false;
        try {
            runtimeState.selfBindings().put("other", order);
        } catch (UnsupportedOperationException ex) {
            unmodifiable = true;
        }
        require(unmodifiable, "selfBindings should be immutable");

        ScenarioRuntimeState emptyBindings = new ScenarioRuntimeState(snapshot, system.state(), null);
        require(emptyBindings.selfBindings().isEmpty(), "null selfBindings should become an empty immutable map");
        require(emptyBindings.selfForBpmn("review_order") == null,
                "missing self binding in empty map should return null");

        System.out.println("ScenarioRuntimeStateTestMain OK");
    }

    private static MModel compileUseModel(Path file) throws Exception {
        StringWriter err = new StringWriter();
        MModel model = USECompiler.compileSpecification(
                Files.readString(file), file.toString(), new PrintWriter(err), new ModelFactory());
        require(model != null, "USE model should compile: " + err);
        return model;
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
