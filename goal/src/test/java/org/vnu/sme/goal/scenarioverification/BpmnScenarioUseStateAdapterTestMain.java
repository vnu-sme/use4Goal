package org.vnu.sme.goal.scenarioverification;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import org.tzi.use.parser.use.USECompiler;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.ModelFactory;
import org.tzi.use.uml.ocl.value.EnumValue;
import org.tzi.use.uml.ocl.value.IntegerValue;
import org.tzi.use.uml.ocl.value.ObjectValue;
import org.tzi.use.uml.ocl.value.StringValue;
import org.tzi.use.uml.sys.MObject;
import org.vnu.sme.goal.bpmn2scenario.mm.Bpmn2ScenarioSnapshot;
import org.vnu.sme.goal.bpmn2scenario.mm.NodeOccurrence;
import org.vnu.sme.goal.bpmn2scenario.mm.TokenMark;
import org.vnu.sme.goal.bpmn2scenario.mm.Value;

/** Plain Java unit test harness for {@link BpmnScenarioUseStateAdapter}. */
public final class BpmnScenarioUseStateAdapterTestMain {

    private BpmnScenarioUseStateAdapterTestMain() {}

    public static void main(String[] args) {
        MModel model = compileUseModel();

        happyPath(model);
        unknownUseClass(model);
        unknownAttribute(model);
        wrongAttributeType(model);
        unknownObject(model);
        missingSelfBinding(model);
        multipleObjects(model);
        multipleProcessInstances(model);
        enumAttribute(model);
        objectReferenceAttribute(model);

        System.out.println("BpmnScenarioUseStateAdapterTestMain OK");
    }

    private static void happyPath(MModel model) {
        Bpmn2ScenarioSnapshot snapshot = snapshot(
                Map.of("p1", "OrderProcess"),
                Map.of("order1", "Order", "customer1", "Customer"),
                Map.of(
                        "order1.created", new Value.Atom("true"),
                        "order1.amount", new Value.Atom("42"),
                        "order1.total", new Value.Atom("10.5"),
                        "order1.status", new Value.Atom("#approved"),
                        "order1.note", new Value.Atom("\"ready\""),
                        "order1.customer", new Value.Atom("customer1"),
                        "customer1.vip", new Value.Atom("false")),
                List.of(new NodeOccurrence("p1", "review_order", "order1", "customer1")),
                List.of(new NodeOccurrence("p1", "approve_order", "order1", "customer1")),
                List.of(new NodeOccurrence("p1", "ship_order", "order1", "customer1")),
                List.of(new TokenMark("p1", "decide_order", "approve_order", "order1")));

        var result = BpmnScenarioUseStateAdapter.materialize(snapshot, model);
        require(result.ok(), "happy path should materialize: " + result.errors());

        ScenarioRuntimeState runtime = result.runtimeState();
        require(runtime.snapshot() == snapshot, "runtime state should keep the source snapshot");
        MObject order = runtime.state().objectByName("order1");
        MObject customer = runtime.state().objectByName("customer1");
        require(order != null, "order object should be materialized");
        require(customer != null, "customer object should be materialized");
        require(runtime.selfForBpmn("review_order") == order, "node self binding should point to order");
        require(runtime.selfForBpmn("decide_order::approve_order") == order,
                "sequence-flow self binding should point to order");
        require(order.state(runtime.state()).attributeValue("amount").equals(IntegerValue.valueOf(42)),
                "integer attribute should be set");
        require(order.state(runtime.state()).attributeValue("note").equals(new StringValue("ready")),
                "string attribute should be unquoted and set");
        require(order.state(runtime.state()).attributeValue("customer").equals(customer.value()),
                "object reference attribute should be set");
    }

    private static void unknownUseClass(MModel model) {
        var result = BpmnScenarioUseStateAdapter.materialize(snapshot(
                Map.of("p1", "OrderProcess"),
                Map.of("order1", "MissingClass"),
                Map.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of()), model);

        require(!result.ok(), "unknown class should fail");
        require(contains(result, "Unknown USE class 'MissingClass'"), "unknown class message should be clear");
    }

    private static void unknownAttribute(MModel model) {
        var result = BpmnScenarioUseStateAdapter.materialize(snapshot(
                Map.of("p1", "OrderProcess"),
                Map.of("order1", "Order"),
                Map.of("order1.unknown", new Value.Atom("true")),
                List.of(),
                List.of(),
                List.of(),
                List.of()), model);

        require(!result.ok(), "unknown attribute should fail");
        require(contains(result, "Unknown attribute 'unknown'"), "unknown attribute message should be clear");
    }

    private static void wrongAttributeType(MModel model) {
        var result = BpmnScenarioUseStateAdapter.materialize(snapshot(
                Map.of("p1", "OrderProcess"),
                Map.of("order1", "Order"),
                Map.of("order1.amount", new Value.Atom("abc")),
                List.of(),
                List.of(),
                List.of(),
                List.of()), model);

        require(!result.ok(), "wrong attribute type should fail");
        require(contains(result, "expected Integer"), "wrong type message should mention expected type");
    }

    private static void unknownObject(MModel model) {
        var result = BpmnScenarioUseStateAdapter.materialize(snapshot(
                Map.of("p1", "OrderProcess"),
                Map.of("order1", "Order"),
                Map.of(),
                List.of(new NodeOccurrence("p1", "review_order", "ghost", null)),
                List.of(),
                List.of(),
                List.of()), model);

        require(!result.ok(), "unknown object in self binding should fail");
        require(contains(result, "Unknown object 'ghost'"), "unknown object message should be clear");
    }

    private static void missingSelfBinding(MModel model) {
        var result = BpmnScenarioUseStateAdapter.materialize(snapshot(
                Map.of("p1", "OrderProcess"),
                Map.of("order1", "Order"),
                Map.of(),
                List.of(new NodeOccurrence("p1", "review_order", null, null)),
                List.of(),
                List.of(),
                List.of()), model);

        require(!result.ok(), "missing self binding should fail");
        require(contains(result, "Missing self binding"), "missing self binding message should be clear");
    }

    private static void multipleObjects(MModel model) {
        var result = BpmnScenarioUseStateAdapter.materialize(snapshot(
                Map.of("p1", "OrderProcess"),
                Map.of("order1", "Order", "order2", "Order"),
                Map.of("order1.amount", new Value.Atom("10"), "order2.amount", new Value.Atom("20")),
                List.of(new NodeOccurrence("p1", "review_order", "order1", null)),
                List.of(new NodeOccurrence("p1", "approve_order", "order2", null)),
                List.of(),
                List.of()), model);

        require(result.ok(), "multiple objects should materialize: " + result.errors());
        require(result.runtimeState().state().objectByName("order1") != null, "first object should exist");
        require(result.runtimeState().state().objectByName("order2") != null, "second object should exist");
        require(result.runtimeState().selfForBpmn("review_order").name().equals("order1"),
                "first owner should bind first object");
        require(result.runtimeState().selfForBpmn("approve_order").name().equals("order2"),
                "second owner should bind second object");
    }

    private static void multipleProcessInstances(MModel model) {
        var result = BpmnScenarioUseStateAdapter.materialize(snapshot(
                Map.of("p1", "OrderProcess", "p2", "OrderProcess"),
                Map.of("order1", "Order", "order2", "Order"),
                Map.of(),
                List.of(
                        new NodeOccurrence("p1", "review_order", "order1", null),
                        new NodeOccurrence("p2", "approve_order", "order2", null)),
                List.of(),
                List.of(),
                List.of()), model);

        require(result.ok(), "multiple process instances should materialize when owner ids differ: "
                + result.errors());
        require(result.runtimeState().selfForBpmn("review_order").name().equals("order1"),
                "p1 owner should bind order1");
        require(result.runtimeState().selfForBpmn("approve_order").name().equals("order2"),
                "p2 owner should bind order2");
    }

    private static void enumAttribute(MModel model) {
        var result = BpmnScenarioUseStateAdapter.materialize(snapshot(
                Map.of("p1", "OrderProcess"),
                Map.of("order1", "Order"),
                Map.of("order1.status", new Value.Atom("OrderStatus::approved")),
                List.of(),
                List.of(),
                List.of(),
                List.of()), model);

        require(result.ok(), "enum attribute should materialize: " + result.errors());
        var value = result.runtimeState().state().objectByName("order1")
                .state(result.runtimeState().state()).attributeValue("status");
        require(value.equals(new EnumValue(model.enumType("OrderStatus"), "approved")),
                "enum attribute should be set");
    }

    private static void objectReferenceAttribute(MModel model) {
        var result = BpmnScenarioUseStateAdapter.materialize(snapshot(
                Map.of("p1", "OrderProcess"),
                Map.of("order1", "Order", "customer1", "Customer"),
                Map.of("order1.customer", new Value.Atom("customer1")),
                List.of(),
                List.of(),
                List.of(),
                List.of()), model);

        require(result.ok(), "object reference attribute should materialize: " + result.errors());
        var value = result.runtimeState().state().objectByName("order1")
                .state(result.runtimeState().state()).attributeValue("customer");
        require(value instanceof ObjectValue, "object reference value should be ObjectValue");
        require(((ObjectValue) value).value().name().equals("customer1"), "object reference should point to customer1");
    }

    private static Bpmn2ScenarioSnapshot snapshot(
            Map<String, String> processInstances,
            Map<String, String> objects,
            Map<String, Value> values,
            List<NodeOccurrence> fired,
            List<NodeOccurrence> completed,
            List<NodeOccurrence> active,
            List<TokenMark> tokens) {
        return new Bpmn2ScenarioSnapshot(
                processInstances, objects, values, fired, completed, active, tokens, List.of());
    }

    private static boolean contains(BpmnScenarioUseStateAdapter.Result result, String text) {
        return result.errors().stream().anyMatch(error -> error.contains(text));
    }

    private static MModel compileUseModel() {
        String source = """
                model AdapterTest

                enum OrderStatus {newOrder, approved, rejected}

                class Customer
                attributes
                  vip : Boolean
                end

                class Order
                attributes
                  created  : Boolean
                  amount   : Integer
                  total    : Real
                  status   : OrderStatus
                  note     : String
                  customer : Customer
                end
                """;
        StringWriter err = new StringWriter();
        MModel model = USECompiler.compileSpecification(source, "adapter-test.use", new PrintWriter(err),
                new ModelFactory());
        require(model != null, "USE model should compile: " + err);
        return model;
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
