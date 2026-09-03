package org.vnu.sme.goal.translate.aclistar2use;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vnu.sme.goal.dsl.acl.mm.AclModel;
import org.vnu.sme.goal.dsl.acl.ocl.AclOclPropertyResolver;
import org.vnu.sme.goal.translate.acl2use.Acl2UseTranslator;
import org.vnu.sme.goal.dsl.istar.mm.Actor;
import org.vnu.sme.goal.dsl.istar.mm.Goal;
import org.vnu.sme.goal.dsl.istar.mm.GoalModel;
import org.vnu.sme.goal.dsl.istar.mm.IStarOclConstraint;
import org.vnu.sme.goal.dsl.istar.mm.IntentionalElement;
import org.vnu.sme.goal.dsl.istar.mm.Task;
import org.vnu.sme.goal.dsl.istar.mm.ContextResolution;

/**
 * Translates the pair (AclModel, GoalModel) into a {@code .use} specification
 * (plain OCL) plus a companion {@code .tocl} specification (temporal properties).
 *
 * <p>OCL cannot exist on its own -- every {@code inv}/operation must be declared
 * {@code context} some class in a model -- so the {@code .use} file needs a class
 * diagram to attach to. That diagram is exactly the ACL-derived one; no separate
 * "Goal"/"Task" classes are introduced. The {@code .use} output has two sections:
 * <ol>
 *   <li><b>ACL domain</b>: class diagram from {@link Acl2UseTranslator}
 *       (Agent, Role, Group, Entity, associations, compatibility invariants),
 *       with derived {@code operations} entries spliced onto the class that
 *       {@code self} resolves to for each iStar goal/task (e.g. {@code
 *       DecideDetails_preHolds()}/{@code _postHolds()} on {@code Initiator}) --
 *       USE has no context-level {@code def} clause, so these live directly on
 *       the class instead.</li>
 *   <li><b>iStar structural queries</b>: each Goal exposes one
 *       {@code _condition()} operation. A parent condition combines its own
 *       OCL, when present, with its children's Goal conditions or Task posts.</li>
 * </ol>
 * The companion {@code .tocl} file holds one formula per supported iStar Goal,
 * encoding ACHIEVE / MAINTAIN / SUSTAIN semantics with {@code sometime} and
 * {@code always}.
 *
 * <p>Actors in iStar are matched to ACL roles by name (case-sensitive).
 * Unmatched actors are emitted as standalone classes with a warning.
 */
public final class AclIStar2UseTranslator {

    private AclIStar2UseTranslator() {}

    /**
     * Full translation result: the {@code .use} file content (class diagram +
     * OCL) and a separate {@code .tocl} file content (temporal properties).
     *
     * <p>These must stay two different strings/files: {@code always} / {@code
     * sometime} are not core OCL keywords -- they only
     * exist in the TOCL plugin's own grammar (see {@code pluginClone/tocl}).
     * Plain USE parses {@code inv} bodies with plain OCL, so embedding TOCL
     * operators inside the {@code .use} file's {@code constraints} section
     * fails to compile ("missing ) at 'self'"/similar) unless that plugin is
     * loaded and extends the OCL parser. Keeping them separate lets the
     * {@code .use} file always compile standalone; the {@code .tocl} file is
     * loaded through the TOCL plugin when temporal properties are needed --
     * mirroring the existing {@code mtg_shadow.use} + {@code mtg_goals.tocl}
     * example pair.
     */
    public record Result(String useText, String toclText, List<String> diagnostics) {
        public boolean ok() {
            return diagnostics.stream().noneMatch(message -> message.startsWith("Error:"));
        }
    }

    /**
     * Translate the (ACL, iStar) pair into a single USE specification string.
     *
     * @param acl  parsed ACL model; must not be {@code null}
     * @param gm   parsed iStar GoalModel; must not be {@code null}
     * @return {@link Result} holding the generated text and any warnings/errors
     */
    public static Result translate(AclModel acl, GoalModel gm) {
        List<String> diagnostics = new ArrayList<>();
        StringBuilder out = new StringBuilder();

        // Each Goal becomes one side-effect-free condition() query. Its body is
        // the declared OCL condition, the AND/OR refinement, or their conjunction
        // when both exist. Activation is intentionally not emitted because the
        // current TOCL semantics checks sometime/always condition() directly.
        Set<String> aclRoleNames = collectAclRoleNames(acl);
        ContextResolution resolution = ContextResolution.of(gm);
        Map<String, List<String>> actorOperationLines = new LinkedHashMap<>();
        Map<String, String> elementActorType = new LinkedHashMap<>();

        for (Actor actor : gm.getActors()) {
            String actorName = actor.name();
            String safeActor = sanitize(actorName);
            boolean inAcl = aclRoleNames.contains(actorName);
            if (!inAcl) {
                diagnostics.add("Warning: iStar actor '" + actorName
                        + "' has no matching ACL role — emitted as standalone class.");
            }

            for (IntentionalElement e : actor.elements()) {
                // The class-attached mapping is deliberately simple: an element
                // is hosted by the class corresponding to its declaring actor.
                elementActorType.put(e.id(), actor.name());
            }
        }

        for (Actor actor : gm.getActors()) {
            for (IntentionalElement element : actor.elements()) {
                if (element instanceof Goal goal) {
                    String gId = sanitize(goal.id());
                    String actorType = elementActorType.get(goal.id());
                    List<String> contextTypes = resolution.contextTypesOf(gm, goal.id());

                    String conditionOcl = oclBodyOf(goal.conditions());
                    String localExpr = conditionOcl != null
                            ? resolveOclExpr(acl, actorType, contextTypes, conditionOcl,
                                    goal.id() + "::condition", diagnostics)
                            : null;
                    String refinementExpr = refinementExpression(gm, actor, goal.id(), actorType,
                            elementActorType, diagnostics);
                    String conditionExpr = combineLocalAndRefinement(
                            localExpr, refinementExpr, hasOrRefinement(actor, goal.id()));
                    if (conditionExpr == null) {
                        diagnostics.add("Warning: leaf goal '" + goal.id()
                                + "' has no condition — generated condition() is false.");
                        conditionExpr = "false";
                    }

                    actorOperationLines.computeIfAbsent(actorType, k -> new ArrayList<>()).add(
                            "  " + gId + "_condition() : Boolean =\n    " + conditionExpr);
                } else if (element instanceof Task task) {
                    String tId = sanitize(task.id());
                    String actorType = elementActorType.get(task.id());
                    List<String> contextTypes = resolution.contextTypesOf(gm, task.id());

                    String preOcl = oclBodyOf(task.preconditions());
                    String preExpr = preOcl != null
                            ? resolveOclExpr(acl, actorType, contextTypes, preOcl, task.id() + "::pre", diagnostics)
                            : "true";
                    String postOcl = oclBodyOf(task.postconditions());
                    String localPostExpr = postOcl != null
                            ? resolveOclExpr(acl, actorType, contextTypes, postOcl, task.id() + "::post", diagnostics)
                            : null;
                    String refinementExpr = refinementExpression(gm, actor, task.id(), actorType,
                            elementActorType, diagnostics);
                    String postExpr = combineLocalAndRefinement(
                            localPostExpr, refinementExpr, hasOrRefinement(actor, task.id()));
                    if (postExpr == null) postExpr = "false";

                    actorOperationLines.computeIfAbsent(actorType, k -> new ArrayList<>()).addAll(List.of(
                            "  " + tId + "_preHolds() : Boolean =\n    " + preExpr,
                            "  " + tId + "_postHolds() : Boolean =\n    " + postExpr));
                }
            }
        }

        // ── Layer 1: ACL domain (reuse Acl2UseTranslator), with each resolved host
        // class's operations spliced into its already-rendered class block. ────
        String aclPart = Acl2UseTranslator.translate(acl);
        // Replace 'model <Name>' header with a unified model name
        String unifiedName = sanitize(gm.getName()) + "_Verification";
        String aclFixed = aclPart.replaceFirst("model\\s+\\S+", "model " + unifiedName);
        Set<String> consumedHostTypes = new LinkedHashSet<>();
        for (String hostType : actorOperationLines.keySet()) {
            if (!aclRoleNames.contains(hostType)) continue;
            List<String> ops = actorOperationLines.get(hostType);
            if (ops.isEmpty()) continue;
            aclFixed = Acl2UseTranslator.spliceOperations(aclFixed, sanitize(hostType), ops);
            consumedHostTypes.add(hostType);
        }
        out.append(aclFixed);

        // ── Layer 2: standalone classes for actors that iStar declares but ACL
        // doesn't (their operations were computed in Pass 0 like everyone else's,
        // just never had an ACL class to be spliced into).
        for (Actor actor : gm.getActors()) {
            String actorName = actor.name();
            String safeActor = sanitize(actorName);
            if (aclRoleNames.contains(actorName)) continue;

            out.append("-- Standalone iStar actor (no ACL role match)\n");
            out.append("class ").append(safeActor).append("\n")
                    .append("attributes\n")
                    .append("  id : Integer\n");
            List<String> ops = actorOperationLines.getOrDefault(actorName, List.of());
            if (!ops.isEmpty()) {
                out.append("operations\n");
                ops.forEach(line -> out.append(line).append("\n"));
            }
            out.append("end\n\n");
            consumedHostTypes.add(actorName);
        }

        // Defensive fallback for malformed/unmatched context types.
        for (var entry : actorOperationLines.entrySet()) {
            String hostType = entry.getKey();
            if (consumedHostTypes.contains(hostType) || entry.getValue().isEmpty()) continue;
            diagnostics.add("Warning: resolved context type '" + hostType
                    + "' has no ACL role and no iStar actor block of its own — emitted as extra standalone class.");
            out.append("-- Extra standalone class (unmatched context type)\n");
            out.append("class ").append(sanitize(hostType)).append("\n")
               .append("attributes\n")
               .append("  id : Integer\n")
               .append("operations\n");
            entry.getValue().forEach(line -> out.append(line).append("\n"));
            out.append("end\n\n");
        }

        out.append("constraints\n\n");
        out.append("-- ===== ACL compatibility constraints already included above =====\n\n");
        out.append("-- ===== iStar Goal conditions and Task pre/post predicates are class operations, ")
           .append("declared above on each host class (see USE 'operations' section) =====\n\n");

        // ── Layer 6: TOCL temporal constraints -- written to a SEPARATE file. ───
        // 'always'/'sometime' are not core OCL keywords (they only
        // exist in the TOCL plugin's own grammar), so they cannot live inside
        // the .use file's 'constraints' section without that plugin extending
        // the OCL parser. Keeping them out of 'out' keeps the .use file valid
        // plain OCL on its own.
        out.append("-- ===== TOCL temporal goal properties are in the companion .tocl file =====\n");
        out.append("-- (load it via the TOCL plugin; see 'always'/'sometime')\n\n");

        IStar2ToclTranslator.Result toclResult = IStar2ToclTranslator.generate(gm);
        diagnostics.addAll(toclResult.diagnostics());

        return new Result(out.toString(), toclResult.toclText(), diagnostics);
    }

    // ── Structural holds() expressions ───────────────────────────────────────

    private static String combineLocalAndRefinement(
            String localExpr, String refinementExpr, boolean orRefinement) {
        if (localExpr == null) return refinementExpr;
        if (refinementExpr == null) return localExpr;
        return "(" + localExpr + ") " + (orRefinement ? "or" : "and")
                + " (" + refinementExpr + ")";
    }

    private static boolean hasOrRefinement(Actor actor, String parentId) {
        return actor.refinements().stream().anyMatch(refinement ->
                refinement.parent().equals(parentId)
                        && refinement instanceof org.vnu.sme.goal.dsl.istar.mm.OrRefinement);
    }

    /**
     * Builds the structural part of one intentional element's condition query.
     * AND/OR refinements remain inside one actor/class and are plain calls to
     * the child condition/post queries.
     */
    private static String refinementExpression(GoalModel gm, Actor actor, String parentId, String parentType,
                                               Map<String, String> elementActorType,
                                               List<String> diagnostics) {
        List<String> andChildren = new ArrayList<>();
        List<String> orChildren = new ArrayList<>();

        for (var refinement : actor.refinements()) {
            if (!refinement.parent().equals(parentId)) continue;
            if (refinement instanceof org.vnu.sme.goal.dsl.istar.mm.AndRefinement and) {
                andChildren.addAll(and.children());
            } else if (refinement instanceof org.vnu.sme.goal.dsl.istar.mm.OrRefinement or) {
                orChildren.add(or.child());
            }
        }

        List<String> children = !andChildren.isEmpty() ? andChildren : orChildren;
        if (children.isEmpty()) return null;

        List<String> calls = new ArrayList<>();
        for (String child : children) {
            String childType = elementActorType.getOrDefault(child, parentType);
            if (!childType.equals(parentType)) {
                diagnostics.add("Error: unsupported cross-class refinement '" + child + " -> " + parentId
                        + "' (" + childType + " -> " + parentType
                        + "). Attach the child goal to its own actor class and connect actors through domain OCL/dependency semantics.");
                calls.add("false");
            } else {
                calls.add(elementConditionCall(gm, child));
            }
        }
        return String.join(andChildren.isEmpty() ? " or " : " and ", calls);
    }

    private static String elementConditionCall(GoalModel gm, String elementId) {
        String suffix = gm.findElement(elementId).filter(Task.class::isInstance).isPresent()
                ? "_postHolds()"
                : "_condition()";
        return "self." + sanitize(elementId) + suffix;
    }

    // ── self.outer / self.group resolution ──────────────────────────────────

    private static final Pattern SELF_OUTER = Pattern.compile("\\bself((?:\\s*\\.\\s*outer)+)\\b");

    /**
     * Resolves one raw {@code .istar} OCL guard body into text valid as the body
     * of an {@code operations} entry hosted on {@code actorType}'s class.
     *
     * <p>{@code self.outer}, {@code self.outer.outer}, ... had meaning only under
     * iStar's former quantified-refinement (forall/pick) contexts, which no longer
     * exist -- every element's context stack is now a singleton (its own owning
     * actor), so any remaining {@code self.outer} in a legacy model is left as
     * {@code self} with a diagnostic. {@link AclOclPropertyResolver#rewrite} then
     * resolves {@code self.group}, {@code self.group.RoleName}, {@code self.agent}
     * and transparent abstract-profile attribute access relative to
     * {@code actorType}.
     */
    private static String resolveOclExpr(AclModel acl, String actorType, List<String> contextTypes,
                                         String rawExpr, String label, List<String> diagnostics) {
        Matcher matcher = SELF_OUTER.matcher(rawExpr);
        StringBuilder placeholdered = new StringBuilder();
        while (matcher.find()) {
            diagnostics.add("Warning: '" + label + "': self.outer has no meaning without "
                    + "quantified refinement — left as 'self'.");
            matcher.appendReplacement(placeholdered, Matcher.quoteReplacement("self"));
        }
        matcher.appendTail(placeholdered);

        return AclOclPropertyResolver.rewrite(acl, actorType, Map.of(), placeholdered.toString());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static Set<String> collectAclRoleNames(AclModel acl) {
        Set<String> names = new LinkedHashSet<>();
        acl.roles().forEach(r -> names.add(r.name()));
        return names;
    }

    private static String oclBodyOf(List<IStarOclConstraint> contracts) {
        return contracts.stream()
                .map(IStarOclConstraint::oclBody)
                .reduce((a, b) -> "(" + a + ") and (" + b + ")")
                .orElse(null);
    }

    /** Convert arbitrary name to a valid USE identifier. */
    static String sanitize(String name) {
        if (name == null || name.isBlank()) return "unnamed";
        String clean = name.replaceAll("[^A-Za-z0-9_]", "_");
        return Character.isDigit(clean.charAt(0)) ? "_" + clean : clean;
    }
}
