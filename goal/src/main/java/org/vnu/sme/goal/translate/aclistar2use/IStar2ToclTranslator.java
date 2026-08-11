package org.vnu.sme.goal.translate.aclistar2use;

import java.util.ArrayList;
import java.util.List;

import org.vnu.sme.goal.dsl.istar.mm.Actor;
import org.vnu.sme.goal.dsl.istar.mm.Goal;
import org.vnu.sme.goal.dsl.istar.mm.GoalModel;
import org.vnu.sme.goal.dsl.istar.mm.GoalType;
import org.vnu.sme.goal.dsl.istar.mm.IStarOclConstraint;
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
 *   <tr><td>ACHIEVE</td>
 *       <td>{@code always (activation implies sometime condition)}</td></tr>
 *   <tr><td>MAINTAIN</td>
 *       <td>{@code always (activation implies always condition)}</td></tr>
 *   <tr><td>SUSTAIN</td>
 *       <td>{@code always (sometime condition implies alwaysPast condition)}</td></tr>
 *   <tr><td>RECUR</td>
 *       <td>{@code always (activation implies (sometime (condition and sometime (not condition implies sometime condition))))}</td></tr>
 * </table>
 *
 * <p>For Goals where the activation/condition OCL guards are known, the generated
 * TOCL directly references the per-actor operations produced by
 * {@link AclIStar2UseTranslator} (e.g. {@code self.MeetingOrganized_activation()}).
 * This lets the TOCL plugin evaluate the formula directly against a filmstrip
 * without a separate evaluation step.
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
        out.append("-- Operators used: always, sometime, alwaysPast, not, implies\n\n");

        for (Actor actor : gm.getActors()) {
            String actorName  = actor.name();
            String safeActor  = sanitize(actorName);
            boolean hasOutput = false;

            for (IntentionalElement element : actor.elements()) {
                if (!(element instanceof Goal goal)) continue;

                GoalType goalType = goal.goalType() != null ? goal.goalType() : GoalType.ACHIEVE;
                String gId        = sanitize(goal.id());

                // References to the per-actor operations emitted by AclIStar2UseTranslator
                // (declared in each actor's 'operations' section, so calls need '()')
                String activationRef = "self." + gId + "_activation()";
                String conditionRef  = "self." + gId + "_condition()";

                // Warn if activation/condition def will be constant (no real OCL)
                boolean hasActivation = hasConstraintOf(goal, IStarOclConstraint.Kind.ACTIVATION);
                boolean hasCondition  = hasConstraintOf(goal, IStarOclConstraint.Kind.CONDITION);
                if (!hasCondition) {
                    diagnostics.add("Info: goal '" + goal.id() + "' in actor '" + actorName
                            + "' has no condition OCL — TOCL will use 'false' placeholder.");
                }

                String invName = goalType.name() + "_" + gId;
                String toclBody = toToclBody(goalType, activationRef, conditionRef);

                out.append("-- ").append(actorName).append("::").append(goal.id())
                   .append(" [").append(goalType).append("]\n");
                out.append("context ").append(safeActor).append("\n");
                out.append("inv ").append(invName).append(":\n");
                out.append("  ").append(toclBody).append("\n\n");
                hasOutput = true;
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
     *   <li>ACHIEVE  → {@code always (A implies sometime C)}</li>
     *   <li>MAINTAIN → {@code always (A implies always C)}</li>
     *   <li>SUSTAIN  → {@code always (sometime C implies alwaysPast C)}</li>
     *   <li>RECUR    → {@code always (A implies (sometime (C and sometime (not C implies sometime C))))}</li>
     * </ul>
     */
    static String toToclBody(GoalType type, String activation, String condition) {
        return switch (type) {
            case ACHIEVE ->
                "always (\n" +
                "    " + activation + "\n" +
                "    implies\n" +
                "    sometime " + condition + "\n" +
                "  )";

            case MAINTAIN ->
                "always (\n" +
                "    " + activation + "\n" +
                "    implies\n" +
                "    always " + condition + "\n" +
                "  )";

            case SUSTAIN ->
                "always (\n" +
                "    sometime " + condition + "\n" +
                "    implies\n" +
                "    alwaysPast " + condition + "\n" +
                "  )";

            case RECUR ->
                "always (\n" +
                "    " + activation + "\n" +
                "    implies\n" +
                "    sometime (\n" +
                "      " + condition + " and\n" +
                "      sometime (\n" +
                "        not " + condition + " implies\n" +
                "        sometime " + condition + "\n" +
                "      )\n" +
                "    )\n" +
                "  )";
        };
    }

    private static boolean hasConstraintOf(Goal goal, IStarOclConstraint.Kind kind) {
        return goal.constraints().stream().anyMatch(c -> c.kind() == kind);
    }
}
