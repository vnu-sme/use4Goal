package org.vnu.sme.goal.translate.aclistar2use;

import java.util.ArrayList;
import java.util.List;

import org.vnu.sme.goal.dsl.istar.mm.Actor;
import org.vnu.sme.goal.dsl.istar.mm.Goal;
import org.vnu.sme.goal.dsl.istar.mm.GoalModel;
import org.vnu.sme.goal.dsl.istar.mm.GoalType;
import org.vnu.sme.goal.dsl.istar.mm.IntentionalElement;
import org.vnu.sme.goal.dsl.istar.mm.Task;

import static org.vnu.sme.goal.translate.aclistar2use.AclIStar2UseTranslator.sanitize;

/**
 * Generates TOCL constraint text from an iStar {@link GoalModel}.
 *
 * <p>Each iStar Goal with a {@link GoalType} is translated to a TOCL {@code inv}
 * on the context class (the ACL role that owns the goal). Goals without an explicit
 * GoalType default to ACHIEVE semantics.
 *
 * <h2>GoalType → TOCL mapping</h2>
 * <table>
 *   <tr><th>GoalType</th><th>TOCL pattern</th></tr>
 *   <tr><td>ACHIEVE</td><td>{@code sometime holds}</td></tr>
 *   <tr><td>MAINTAIN</td><td>{@code always holds}</td></tr>
 *   <tr><td>SUSTAIN</td><td>{@code sometime (always holds)}</td></tr>
 *   <tr><td>RECUR</td><td>not generated yet</td></tr>
 * </table>
 *
 * <p>The generated TOCL references the structural {@code condition()} query
 * produced by {@link AclIStar2UseTranslator}. That query already combines the goal's own
 * OCL predicate with its AND/OR children, so temporal constraints and
 * refinement propagation use exactly the same Boolean definition.
 *
 * <p>Tasks are not given TOCL constraints (they are atomic actions, not temporal
 * properties); instead, a comment is emitted to document where pre/post OCL
 * can be found in the companion {@code .use} file.
 */
public final class IStar2ToclTranslator {

    private IStar2ToclTranslator() {}

    /** Full generation result. */
    public record Result(String toclText, List<String> diagnostics) {
        public boolean ok() { return diagnostics.isEmpty(); }
    }

    /**
     * Generate TOCL constraints for every goal in the model.
     *
     * @param gm the parsed iStar GoalModel
     * @return {@link Result} with the full TOCL text and any warnings
     */
    public static Result generate(GoalModel gm) {
        List<String> diagnostics = new ArrayList<>();
        StringBuilder out = new StringBuilder();

        out.append("-- TOCL constraints generated from ").append(gm.getName()).append(".istar\n");
        out.append("-- Each 'context C inv Name:' block encodes one iStar GoalType property.\n");
        out.append("-- Mapping: Achieve=eventually, Maintain=always, Sustain=eventually-always.\n\n");

        for (Actor actor : gm.getActors()) {
            String actorName  = actor.name();
            String safeActor  = sanitize(actorName);
            for (IntentionalElement element : actor.elements()) {
                if (!(element instanceof Goal goal)) continue;

                GoalType goalType = goal.goalType() != null ? goal.goalType() : GoalType.ACHIEVE;
                String gId        = sanitize(goal.id());

                String conditionRef = "self." + gId + "_condition()";

                // Warn when neither local OCL nor a refinement defines the condition.
                boolean hasCondition  = !goal.conditions().isEmpty();
                boolean hasRefinement = actor.refinements().stream()
                        .anyMatch(r -> r.parent().equals(goal.id()));
                if (!hasCondition && !hasRefinement) {
                    diagnostics.add("Info: goal '" + goal.id() + "' in actor '" + actorName
                            + "' is a leaf without condition OCL — condition() is false.");
                }

                if (goalType == GoalType.RECUR) {
                    diagnostics.add("Info: Recur goal '" + goal.id()
                            + "' is omitted from TOCL generation in the current translation.");
                    out.append("-- OMITTED Recur goal ").append(actorName).append("::")
                       .append(goal.id()).append("\n\n");
                    continue;
                }

                String invName = goalType.name() + "_" + gId;
                String toclBody = toToclBody(goalType, conditionRef);

                out.append("-- ").append(actorName).append("::").append(goal.id())
                   .append(" [").append(goalType).append("]\n");
                out.append("context ").append(safeActor).append("\n");
                out.append("inv ").append(invName).append(":\n");
                out.append("  ").append(toclBody).append("\n\n");
            }

            // Document tasks in a comment block (no TOCL, only OCL pre/post in .use)
            List<Task> tasks = actor.elements().stream()
                    .filter(Task.class::isInstance)
                    .map(Task.class::cast)
                    .toList();
            if (!tasks.isEmpty()) {
                out.append("-- Tasks of ").append(actorName)
                   .append(" — pre/post OCL is in the .use file as class operations:\n");
                for (Task task : tasks) {
                    String tId = sanitize(task.id());
                    out.append("--   self.").append(tId).append("_preHolds() : Boolean\n");
                    out.append("--   self.").append(tId).append("_postHolds() : Boolean\n");
                }
                out.append("\n");
            }
        }

        return new Result(out.toString(), diagnostics);
    }

    // ── GoalType → TOCL body ──────────────────────────────────────────────────

    /**
     * Produces the TOCL expression body (without leading "inv Name:") for a given GoalType.
     *
     * <ul>
     *   <li>ACHIEVE  → {@code sometime H}</li>
     *   <li>MAINTAIN → {@code always H}</li>
     *   <li>SUSTAIN  → {@code sometime (always H)}</li>
     *   <li>RECUR    → omitted by {@link #generate(GoalModel)}</li>
     * </ul>
     */
    static String toToclBody(GoalType type, String holds) {
        return switch (type) {
            case ACHIEVE -> "sometime " + holds;
            case MAINTAIN -> "always " + holds;
            case SUSTAIN -> "sometime (always " + holds + ")";
            case RECUR -> throw new IllegalArgumentException("RECUR TOCL mapping is not defined yet");
        };
    }

}
