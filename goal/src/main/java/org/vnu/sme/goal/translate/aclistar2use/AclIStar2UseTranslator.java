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
import org.vnu.sme.goal.dsl.acl.mm.AclOwner;
import org.vnu.sme.goal.dsl.acl.ocl.AclOclPropertyResolver;
import org.vnu.sme.goal.translate.acl2use.Acl2UseTranslator;
import org.vnu.sme.goal.dsl.istar.mm.Actor;
import org.vnu.sme.goal.dsl.istar.mm.Goal;
import org.vnu.sme.goal.dsl.istar.mm.GoalModel;
import org.vnu.sme.goal.dsl.istar.mm.GoalTaskElement;
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
 *   <li><b>Refinement structural invariants</b>: one {@code inv} per iStar
 *       AND/OR refinement, tying a parent goal's {@code _condition()} to its
 *       children's.</li>
 * </ol>
 * The companion {@code .tocl} file holds one formula per iStar Goal, encoding
 * ACHIEVE / MAINTAIN / SUSTAIN / RECUR semantics with the TOCL operators
 * {@code always}, {@code sometime}, {@code alwaysPast} -- see {@link Result}
 * for why that has to be a separate file.
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
     * sometime} / {@code alwaysPast} are not core OCL keywords -- they only
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
        public boolean ok() { return diagnostics.isEmpty(); }
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

        // ── Pass 0: collect per-actor goals/tasks and their activation/condition/
        // pre/post OCL bodies as USE 'operations' lines. USE has no context-level
        // 'def' clause (only 'inv'/'existential inv' and 'pre'/'post' are valid
        // inside 'context'; see USEBase.gpart invariant/prePost rules) -- derived
        // booleans must instead live in the class's own 'operations' block, so
        // these are attached to a class definition below rather than emitted as
        // a separate top-level 'def' statement.
        //
        // Which class hosts a goal/task's operation is NOT always the actor it is
        // textually declared under: iStar 'forall'/'pick' refinement can bind
        // 'self' to a DIFFERENT actor type (e.g. a goal declared under Initiator
        // with '> forall Participant ...' has self = Participant, because its
        // guard -- 'self.attended' -- is a Participant attribute). ContextResolution
        // already computes this correctly (it drives the live istarusebridge OCL
        // compiler); reusing it here is what makes 'self.attended' resolve to the
        // right class instead of "Undefined operation" on the declaring actor.
        Set<String> aclRoleNames = collectAclRoleNames(acl);
        ContextResolution resolution = ContextResolution.of(gm);
        Map<String, ActorMapping> actorMappings = new LinkedHashMap<>();
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

            List<Goal> goals = new ArrayList<>();
            List<Task> tasks = new ArrayList<>();
            for (IntentionalElement e : actor.elements()) {
                if (e instanceof Goal g) goals.add(g);
                if (e instanceof Task t) tasks.add(t);
            }

            for (Goal goal : goals) {
                String gId = sanitize(goal.id());
                String actorType = resolution.actorTypeOf(gm, goal.id());
                List<String> contextTypes = resolution.contextTypesOf(gm, goal.id());
                elementActorType.put(goal.id(), actorType);

                String activationOcl = oclBodyOf(goal, IStarOclConstraint.Kind.ACTIVATION);
                String activationExpr = activationOcl != null
                        ? resolveOclExpr(acl, actorType, contextTypes, activationOcl,
                                goal.id() + "::activation", diagnostics)
                        : "true";
                String conditionOcl = oclBodyOf(goal, IStarOclConstraint.Kind.CONDITION);
                String conditionExpr = conditionOcl != null
                        ? resolveOclExpr(acl, actorType, contextTypes, conditionOcl,
                                goal.id() + "::condition", diagnostics)
                        : "false";

                actorOperationLines.computeIfAbsent(actorType, k -> new ArrayList<>()).addAll(List.of(
                        "  " + gId + "_activation() : Boolean =\n    " + activationExpr,
                        "  " + gId + "_condition() : Boolean =\n    " + conditionExpr));
            }
            for (Task task : tasks) {
                String tId = sanitize(task.id());
                String actorType = resolution.actorTypeOf(gm, task.id());
                List<String> contextTypes = resolution.contextTypesOf(gm, task.id());
                elementActorType.put(task.id(), actorType);

                String preOcl = oclBodyOf(task, IStarOclConstraint.Kind.PRE);
                String preExpr = preOcl != null
                        ? resolveOclExpr(acl, actorType, contextTypes, preOcl, task.id() + "::pre", diagnostics)
                        : "true";
                String postOcl = oclBodyOf(task, IStarOclConstraint.Kind.POST);
                String postExpr = postOcl != null
                        ? resolveOclExpr(acl, actorType, contextTypes, postOcl, task.id() + "::post", diagnostics)
                        : "false";

                actorOperationLines.computeIfAbsent(actorType, k -> new ArrayList<>()).addAll(List.of(
                        "  " + tId + "_preHolds() : Boolean =\n    " + preExpr,
                        "  " + tId + "_postHolds() : Boolean =\n    " + postExpr));
            }

            actorMappings.put(actorName, new ActorMapping(safeActor));
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
            out.append("class ").append(safeActor).append("\n");
            List<String> ops = actorOperationLines.getOrDefault(actorName, List.of());
            if (!ops.isEmpty()) {
                out.append("operations\n");
                ops.forEach(line -> out.append(line).append("\n"));
            }
            out.append("end\n\n");
            consumedHostTypes.add(actorName);
        }

        // A goal/task can resolve (via forall/pick) to a host type that is
        // neither an ACL role (Layer 1) nor one of its own declaring actors
        // (Layer 3 above) -- e.g. a quantifier naming an actor type that never
        // got its own top-level iStar 'role' block. Give it a class too, instead
        // of silently dropping its operations.
        for (var entry : actorOperationLines.entrySet()) {
            String hostType = entry.getKey();
            if (consumedHostTypes.contains(hostType) || entry.getValue().isEmpty()) continue;
            diagnostics.add("Warning: resolved context type '" + hostType
                    + "' has no ACL role and no iStar actor block of its own — emitted as extra standalone class.");
            out.append("-- Extra standalone class (resolved forall/pick context type)\n");
            out.append("class ").append(sanitize(hostType)).append("\n")
               .append("operations\n");
            entry.getValue().forEach(line -> out.append(line).append("\n"));
            out.append("end\n\n");
        }

        out.append("constraints\n\n");
        out.append("-- ===== ACL compatibility constraints already included above =====\n\n");
        out.append("-- ===== iStar Goal/Task activation/condition/pre/post are class operations, ")
           .append("declared above on each host class (see USE 'operations' section) =====\n\n");

        // ── Layer 5: Refinement structural OCL (AND / OR soundness) ──────────
        out.append("-- ===== Refinement structural invariants =====\n\n");
        for (Map.Entry<String, ActorMapping> entry : actorMappings.entrySet()) {
            ActorMapping m = entry.getValue();
            emitRefinementInvariants(out, gm, acl, entry.getKey(), m, elementActorType, diagnostics);
        }

        // ── Layer 6: TOCL temporal constraints -- written to a SEPARATE file. ───
        // 'always'/'sometime'/'alwaysPast' are not core OCL keywords (they only
        // exist in the TOCL plugin's own grammar), so they cannot live inside
        // the .use file's 'constraints' section without that plugin extending
        // the OCL parser. Keeping them out of 'out' keeps the .use file valid
        // plain OCL on its own.
        out.append("-- ===== TOCL temporal goal properties are in the companion .tocl file =====\n");
        out.append("-- (load it via the TOCL plugin; see 'always'/'sometime'/'alwaysPast')\n\n");

        IStar2ToclTranslator.Result toclResult = IStar2ToclTranslator.generate(gm);
        diagnostics.addAll(toclResult.diagnostics());

        return new Result(out.toString(), toclResult.toclText(), diagnostics);
    }

    // ── Refinement structural invariants ──────────────────────────────────────

    /**
     * A child under a forall/pick-quantified ancestor (see {@link ContextResolution})
     * hosts its {@code _condition()} on a DIFFERENT class than its AND/OR parent --
     * e.g. TimetableCollected's condition lives on Participant, but its parent
     * SchedulingCompleted (on Organizer) needs "every Participant's TimetableCollected
     * holds". {@code self.child_condition()} would be a type error in that case
     * (self is Organizer, not Participant); the collection must be reached via the
     * shared owning Group and combined with {@code ->forAll}/{@code ->exists}.
     */
    private static void emitRefinementInvariants(StringBuilder out, GoalModel gm, AclModel acl,
                                                  String actorName, ActorMapping m,
                                                  Map<String, String> elementActorType, List<String> diagnostics) {
        Actor actor = gm.findActor(actorName).orElse(null);
        if (actor == null || actor.refinements().isEmpty()) return;

        for (var refinement : actor.refinements()) {
            if (refinement instanceof org.vnu.sme.goal.dsl.istar.mm.AndRefinement and) {
                // AND: parent condition ↔ all children condition
                String parentId = sanitize(and.parent());
                // The refinement's own resolved host class, NOT the declaring actor's class:
                // a goal/task keeps its textual '> parent' declaration under the actor whose
                // block it was written in, but forall/pick can still bind its OWN self to a
                // different actor type (see ContextResolution) -- 'context' must follow that.
                String parentType = elementActorType.getOrDefault(and.parent(), m.safeClassName());
                List<String> childExprs = new ArrayList<>();
                boolean unresolvable = false;
                for (String c : and.children()) {
                    String childType = elementActorType.getOrDefault(c, parentType);
                    String holds = holdsOperationCall(gm, c);
                    if (childType.equals(parentType)) {
                        childExprs.add("self." + holds);
                        continue;
                    }
                    String nav = groupSiblingNavigation(acl, parentType, childType,
                            "And_" + parentId + " (child " + c + ")", diagnostics);
                    if (nav == null) { unresolvable = true; break; }
                    String var = "x_" + sanitize(c);
                    childExprs.add(nav + "->forAll(" + var + " | " + var + "." + holds + ")");
                }
                if (unresolvable) {
                    diagnostics.add("Warning: skipped invariant 'And_" + parentId
                            + "' — a child's context type has no shared Group with the parent's.");
                } else if (!childExprs.isEmpty()) {
                    out.append("context ").append(sanitize(parentType)).append("\n")
                       .append("  inv And_").append(parentId).append(":\n")
                       .append("    self.").append(parentId).append("_condition()")
                       .append(" = (").append(String.join(" and ", childExprs)).append(")\n\n");
                }
            } else if (refinement instanceof org.vnu.sme.goal.dsl.istar.mm.OrRefinement or) {
                // OR: child condition → parent condition
                String parentId = sanitize(or.parent());
                String childId  = sanitize(or.child());
                String parentType = elementActorType.getOrDefault(or.parent(), m.safeClassName());
                String childType = elementActorType.getOrDefault(or.child(), parentType);
                String parentHolds = holdsOperationCall(gm, or.parent());
                String childHoldsOp = holdsOperationCall(gm, or.child());

                String childHolds;
                if (childType.equals(parentType)) {
                    childHolds = "self." + childHoldsOp;
                } else {
                    String nav = groupSiblingNavigation(acl, parentType, childType,
                            "Or_" + parentId + "_" + childId, diagnostics);
                    if (nav == null) {
                        diagnostics.add("Warning: skipped invariant 'Or_" + parentId + "_" + childId
                                + "' — child context type has no shared Group with the parent's.");
                        continue;
                    }
                    String var = "x_" + childId;
                    childHolds = nav + "->exists(" + var + " | " + var + "." + childHoldsOp + ")";
                }
                out.append("context ").append(sanitize(parentType)).append("\n")
                   .append("  inv Or_").append(parentId).append("_").append(childId).append(":\n")
                   .append("    ").append(childHolds)
                   .append(" implies self.").append(parentHolds).append("\n\n");
            }
        }
    }

    /**
     * Static-navigation equivalent of {@code self.group.<RoleName>} (see
     * {@link AclOclPropertyResolver}) for two DIFFERENT role types that are both
     * direct members of the same owning Group: {@code self.<source_.._in_G>.<target_.._in_G>},
     * as a collection of {@code targetType} reachable from a {@code selfType} instance.
     * Returns {@code null} (and records a diagnostic) when {@code selfType} and
     * {@code targetType} do not share a direct owning Group -- this does not walk
     * nested Group ancestry, matching {@code self.group}'s own single-hop scope.
     */
    private static String groupSiblingNavigation(AclModel acl, String selfType, String targetType,
                                                  String label, List<String> diagnostics) {
        String group = acl.owners().stream()
                .filter(o -> o.target().equals(selfType))
                .map(AclOwner::sourceGroup).findFirst().orElse(null);
        if (group == null) {
            diagnostics.add("Warning: '" + label + "': '" + selfType + "' is not a Group member in the ACL model.");
            return null;
        }
        AclOwner targetMembership = acl.owners().stream()
                .filter(o -> o.sourceGroup().equals(group) && o.target().equals(targetType))
                .findFirst().orElse(null);
        if (targetMembership == null) {
            diagnostics.add("Warning: '" + label + "': '" + targetType
                    + "' is not a member of '" + selfType + "'\\'s owning Group '" + group + "'.");
            return null;
        }
        String nav = AclOclPropertyResolver.rewrite(acl, selfType, Map.of(), "self.group." + targetType);
        boolean singular = targetMembership.multiplicity().max().isPresent()
                && targetMembership.multiplicity().max().getAsInt() == 1;
        if (singular) return nav;
        // targetType's multiplicity in the Group is not exactly 1 (e.g. Participant [2..*]):
        // there is no statically-determined single occurrence to navigate to -- which one
        // 'self.outer' means is a runtime/branch-specific fact this static class model
        // cannot express without turning every such operation into a parameterized one.
        // ->any(x|true) picks an arbitrary member so the expression still type-checks;
        // this is a best-effort simplification, not a claim that it is THE right occurrence.
        diagnostics.add("Warning: '" + label + "': '" + targetType + "' has multiplicity "
                + targetMembership.multiplicity() + " in Group '" + group
                + "' (not exactly 1) — using ->any(...) to pick an arbitrary occurrence.");
        String var = "any_" + sanitize(targetType);
        return nav + "->any(" + var + " | true)";
    }

    /** {@code _condition()} for a Goal, {@code _postHolds()} for a Task (Tasks have no
     *  {@code _condition()} operation) -- both are the element's "is satisfied" flag. */
    private static String holdsOperationCall(GoalModel gm, String elementId) {
        String id = sanitize(elementId);
        boolean isTask = gm.findElement(elementId).filter(Task.class::isInstance).isPresent();
        return id + (isTask ? "_postHolds()" : "_condition()");
    }

    // ── self.outer / self.group resolution ──────────────────────────────────

    private static final Pattern SELF_OUTER = Pattern.compile("\\bself((?:\\s*\\.\\s*outer)+)\\b");

    /**
     * Resolves one raw {@code .istar} OCL guard body into text valid as the body
     * of an {@code operations} entry hosted on {@code actorType}'s class.
     *
     * <p>Two rewrites happen, in order:
     * <ol>
     *   <li>{@code self.outer}, {@code self.outer.outer}, ... -- iStar's own
     *       "enclosing forall/pick binding" syntax (see the {@code Secretary} role
     *       comment in mtg.istar) -- are swapped for unique placeholder identifiers
     *       first, so {@link AclOclPropertyResolver} treats each one exactly like
     *       any other contextual role-typed variable (getting the same abstract
     *       profile / profile-association-includes() rewriting it already gives
     *       {@code participant}-style iterator variables). Each placeholder is
     *       then replaced by its REAL self-relative navigation path (through the
     *       Group all these roles share -- see {@link #groupSiblingNavigation}):
     *       a plain OCL operation has no ambient "participant"/"organizer"
     *       variable the way the live istarusebridge OCL compiler does, so the
     *       placeholder cannot simply become a bare name -- it must become an
     *       actual path from {@code self}.</li>
     *   <li>{@link AclOclPropertyResolver#rewrite} then resolves {@code self.group},
     *       {@code self.group.RoleName}, {@code self.agent} and transparent
     *       abstract-profile attribute access relative to {@code actorType}.</li>
     * </ol>
     */
    private static String resolveOclExpr(AclModel acl, String actorType, List<String> contextTypes,
                                         String rawExpr, String label, List<String> diagnostics) {
        Map<String, String> placeholderTypes = new LinkedHashMap<>();
        Matcher matcher = SELF_OUTER.matcher(rawExpr);
        StringBuilder placeholdered = new StringBuilder();
        int index = 0;
        while (matcher.find()) {
            int depth = (int) Pattern.compile("outer").matcher(matcher.group(1)).results().count();
            String replacement;
            if (depth >= contextTypes.size()) {
                diagnostics.add("Warning: '" + label + "': self.outer depth " + depth
                        + " exceeds context stack " + contextTypes + " — left as 'self'.");
                replacement = "self";
            } else {
                String placeholder = "outerCtx" + (index++) + "_";
                placeholderTypes.put(placeholder, contextTypes.get(depth));
                replacement = placeholder;
            }
            matcher.appendReplacement(placeholdered, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(placeholdered);

        String rewritten = AclOclPropertyResolver.rewrite(acl, actorType, placeholderTypes, placeholdered.toString());

        for (var entry : placeholderTypes.entrySet()) {
            String nav = groupSiblingNavigation(acl, actorType, entry.getValue(), label, diagnostics);
            if (nav == null) nav = "self"; // diagnostic already recorded; keep output syntactically valid
            rewritten = rewritten.replace(entry.getKey(), nav);
        }
        return rewritten;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private record ActorMapping(String safeClassName) {}

    private static Set<String> collectAclRoleNames(AclModel acl) {
        Set<String> names = new LinkedHashSet<>();
        acl.roles().forEach(r -> names.add(r.name()));
        return names;
    }

    private static String oclBodyOf(GoalTaskElement gte, IStarOclConstraint.Kind kind) {
        return gte.constraints().stream()
                .filter(c -> c.kind() == kind)
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
