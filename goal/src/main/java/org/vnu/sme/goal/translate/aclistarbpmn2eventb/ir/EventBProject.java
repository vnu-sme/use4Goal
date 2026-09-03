package org.vnu.sme.goal.translate.aclistarbpmn2eventb.ir;

import java.util.List;

/** Small typed IR; translation code never emits Rodin XML directly. */
public record EventBProject(String name, Context context, Machine machine, List<Trace> traces,
                            List<Property> properties) {
    public record Context(String name, List<String> sets, List<Constant> constants,
                          List<Predicate> axioms) {}
    public record Machine(String name, String contextName, List<String> variables,
                          List<Predicate> invariants, List<Event> events) {}
    public record Constant(String identifier) {}
    public record Predicate(String label, String formula, boolean theorem) {}
    public record Event(String label, List<String> parameters, List<Predicate> guards,
                        List<Assignment> actions) {}
    public record Assignment(String label, String formula) {}
    public record Trace(String sourceLanguage, String sourceElement, String targetElement) {}
    public record Property(String id, String kind, String formula, String source) {}
}
