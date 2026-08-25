package org.vnu.sme.goal.trace.usetrace;

import org.vnu.sme.goal.dsl.istar.mm.ContextResolution;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tzi.use.parser.soil.SoilCompiler;
import org.tzi.use.parser.use.USECompiler;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.ModelFactory;
import org.tzi.use.uml.ocl.expr.EvalContext;
import org.tzi.use.uml.ocl.expr.SimpleEvalContext;
import org.tzi.use.uml.ocl.value.BooleanValue;
import org.tzi.use.uml.ocl.value.VarBindings;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MLink;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.uml.sys.MSystemState;
import org.tzi.use.uml.sys.StatementEvaluationResult;
import org.tzi.use.uml.sys.soil.MStatement;
import org.tzi.use.uml.sys.soil.SoilEvaluationContext;

import org.vnu.sme.goal.verify.conformance.semantics.GoalTaskStatus;
import org.vnu.sme.goal.verify.conformance.semantics.GoalMarking;
import org.vnu.sme.goal.verify.conformance.semantics.QualityStatus;
import org.vnu.sme.goal.verify.conformance.semantics.IStarMarking;
import org.vnu.sme.goal.verify.conformance.semantics.IStarPropagation;
import org.vnu.sme.goal.verify.conformance.semantics.TaskMarking;
import org.vnu.sme.goal.dsl.istar.mm.Goal;
import org.vnu.sme.goal.dsl.istar.mm.GoalActivationGraph;
import org.vnu.sme.goal.dsl.istar.mm.GoalModel;
import org.vnu.sme.goal.dsl.istar.mm.Task;
import org.vnu.sme.goal.dsl.istar.parser.IStarCompiler;
import org.vnu.sme.goal.dsl.acl.mm.AclModel;
import org.vnu.sme.goal.trace.usetrace.IStarOclConstraintCompiler.ConstraintInfo;

/**
 * Compiles the trio {@code .istar} (goals, OCL guards) + {@code .use} (structure) +
 * {@code .soil} (one concrete execution) into a checkpoint-per-soil-line trace of
 * {@link IStarMarking}, replacing hand-authored {@code .iscn} fire/assign/aggregate
 * for scenarios where a real object graph already exists. See
 * doc/istarDesign/istar-scenario-action-architecture.md for how .iscn does the
 * equivalent job from a purpose-built scenario DSL instead of a real .use system.
 *
 * <p>Two-step-per-checkpoint evaluation:
 * <ol>
 *   <li>every element with a compiled OCL guard is evaluated directly, per instance of
 *       its own resolved actor type, and assigned into that instance's own private
 *       marking (state as input, exactly like .iscn's {@code assign}).</li>
 *   <li>elements that are themselves the quantified child of a forall/pick relation
 *       have their step-1 per-instance values aggregated (ALL for forall, ANY for
 *       pick) and the result is assigned into every instance of the relation's
 *       <em>owning</em> actor, so {@link IStarPropagation}'s generic AND/OR engine
 *       rolls it up to the parent exactly as it already does for {@code .iscn}.</li>
 * </ol>
 * Both steps end by re-saturating (assignGoalTask always does), so the final
 * per-instance markings are fully rolled up through AND/OR/contribution.
 */
public final class IStarUseTraceCompiler {

    private static final Pattern NEW_NAMED_OBJECT =
            Pattern.compile(".*:=\\s*new\\s+([A-Za-z_][A-Za-z0-9_]*)\\s*\\(\\s*'([^']+)'\\s*\\).*");
    private static final Pattern SHELL_CREATE =
            Pattern.compile("!create\\s+([A-Za-z_][A-Za-z0-9_]*)\\s*:\\s*([A-Za-z_][A-Za-z0-9_]*)");

    public record InstanceKey(String actorType, String objectName) {
        @Override public String toString() { return actorType + "#" + objectName; }
    }

    /** One contextual Goal/Task occurrence; objectNames follows ConstraintInfo.contextTypes. */
    public record OccurrenceKey(String elementId, List<String> objectNames) {
        public OccurrenceKey { objectNames = List.copyOf(objectNames); }
    }

    public record Checkpoint(int index, String soilLine, Map<InstanceKey, IStarMarking> markings,
                             Map<OccurrenceKey, GoalMarking> occurrenceGoals,
                             Map<OccurrenceKey, TaskMarking> occurrenceTasks,
                             MSystemState state, Map<String, ConstraintInfo> constraints,
                             Map<String, MClass> actorClasses, AclModel acl) {}

    public record Result(GoalModel model, MModel useModel, List<Checkpoint> checkpoints, List<String> errors) {
        public boolean ok() { return errors.isEmpty(); }
    }

    private IStarUseTraceCompiler() {}

    public static int countExecutableStatements(String soil) {
        int count = 0;
        for (String raw : soil.lines().toList()) {
            String line = raw.strip();
            if (!line.isEmpty() && !line.startsWith("--")) count++;
        }
        return count;
    }

    public static Result compile(Path istarFile, Path useFile, Path soilFile) throws IOException {
        return compile(istarFile, useFile, soilFile, null);
    }

    public static Result compile(Path istarFile, Path useFile, Path soilFile, AclModel acl) throws IOException {
        return compile(istarFile, useFile, soilFile, acl, 0);
    }

    /**
     * @param atomicInitializationStatements statements used only to construct s0; their
     *        intermediate snapshots must not contribute temporal Goal/Task history
     */
    public static Result compile(Path istarFile, Path useFile, Path soilFile, AclModel acl,
                                 int atomicInitializationStatements) throws IOException {
        List<String> errors = new ArrayList<>();

        IStarCompiler.Result istarResult = IStarCompiler.compile(istarFile);
        if (!istarResult.ok()) {
            errors.addAll(istarResult.errors());
            return new Result(null, null, List.of(), errors);
        }
        GoalModel gm = istarResult.model();

        StringWriter useErrBuf = new StringWriter();
        MModel useModel = USECompiler.compileSpecification(
                Files.readString(useFile), useFile.toString(), new PrintWriter(useErrBuf), new ModelFactory());
        if (useModel == null) {
            errors.add("errors in '" + useFile + "': " + useErrBuf);
            return new Result(gm, null, List.of(), errors);
        }

        ContextResolution resolution = ContextResolution.of(gm);
        IStarOclConstraintCompiler.Result constraintResult = IStarOclConstraintCompiler.compile(gm, useModel, resolution, acl);
        if (!constraintResult.ok()) {
            errors.addAll(constraintResult.errors());
            return new Result(gm, useModel, List.of(), errors);
        }
        Map<String, ConstraintInfo> constraints = constraintResult.constraints();

        Map<String, MClass> actorClasses = new LinkedHashMap<>();
        for (var actor : gm.getActors()) {
            MClass c = UseActorClasses.resolve(gm, useModel, actor.name(), errors);
            if (c != null) actorClasses.put(actor.name(), c);
        }
        if (!errors.isEmpty()) return new Result(gm, useModel, List.of(), errors);

        MSystem system = new MSystem(useModel);
        List<String> soilLines = new ArrayList<>();
        for (String raw : Files.readAllLines(soilFile)) {
            String line = raw.strip();
            if (!line.isEmpty() && !line.startsWith("--")) soilLines.add(line);
        }

        List<Checkpoint> checkpoints = new ArrayList<>();
        Map<String, Integer> objectOrder = new LinkedHashMap<>();
        int idx = 0;
        for (String shellLine : soilLines) {
            idx++;
            String line = normalizeShellSoil(shellLine);
            StringWriter stmtErr = new StringWriter();
            MStatement stmt = SoilCompiler.compileStatement(
                    useModel, system.state(), system.getVariableEnvironment(),
                    line, soilFile + ":" + idx, new PrintWriter(stmtErr), false);
            if (stmt == null) {
                errors.add("soil line " + idx + " '" + line + "': " + stmtErr);
                break;
            }
            try {
                stmt.execute(new SoilEvaluationContext(system), new StatementEvaluationResult(stmt));
                rememberCreatedObjectOrder(line, objectOrder);
            } catch (Exception ex) {
                errors.add("soil line " + idx + " '" + line + "': " + ex);
                break;
            }

            Checkpoint previous = idx <= atomicInitializationStatements || checkpoints.isEmpty()
                    ? null : checkpoints.get(checkpoints.size() - 1);
            Map<OccurrenceKey, GoalMarking> occurrenceGoals = new LinkedHashMap<>();
            Map<OccurrenceKey, TaskMarking> occurrenceTasks = new LinkedHashMap<>();
            Map<InstanceKey, IStarMarking> markings = computeCheckpoint(
                    gm, resolution, constraints, actorClasses, system.state(), objectOrder, acl, previous,
                    occurrenceGoals, occurrenceTasks);
            checkpoints.add(new Checkpoint(idx, line, markings, Map.copyOf(occurrenceGoals),
                    Map.copyOf(occurrenceTasks),
                    new MSystemState("checkpoint#" + idx, system.state()), constraints, actorClasses, acl));
        }

        return new Result(gm, useModel, checkpoints, errors);
    }

    /** Accept the same !create/!set/!insert command file that USE's `read` command accepts. */
    private static String normalizeShellSoil(String line) {
        Matcher create = SHELL_CREATE.matcher(line);
        if (create.matches()) {
            return create.group(1) + " := new " + create.group(2) + "('" + create.group(1) + "')";
        }
        if (line.startsWith("!set ")) return line.substring("!set ".length()).stripLeading();
        return line.startsWith("!") ? line.substring(1).stripLeading() : line;
    }

    private static Map<InstanceKey, IStarMarking> computeCheckpoint(
            GoalModel gm, ContextResolution resolution, Map<String, ConstraintInfo> constraints,
            Map<String, MClass> actorClasses, MSystemState state, Map<String, Integer> objectOrder,
            AclModel acl, Checkpoint previous,
            Map<OccurrenceKey, GoalMarking> occurrenceGoals,
            Map<OccurrenceKey, TaskMarking> occurrenceTasks) {

        Map<InstanceKey, IStarMarking> markings = new LinkedHashMap<>();
        GoalActivationGraph activationGraph = GoalActivationGraph.of(gm);
        Map<String, List<MObject>> instancesByActorType = new LinkedHashMap<>();
        for (var e : actorClasses.entrySet()) {
            List<MObject> objs = new ArrayList<>(state.objectsOfClass(e.getValue()));
            objs.sort(Comparator.comparingInt((MObject o) -> objectOrder.getOrDefault(o.name(), Integer.MAX_VALUE))
                    .thenComparing(MObject::name));
            instancesByActorType.put(e.getKey(), objs);
            for (MObject o : objs) {
                markings.put(new InstanceKey(e.getKey(), o.name()), IStarMarking.initial(gm));
            }
        }

        for (ConstraintInfo c : constraints.values()) {
            if (c.contextTypes().size() > 1) continue;
            for (MObject o : instancesByActorType.getOrDefault(c.actorType(), List.of())) {
                InstanceKey key = new InstanceKey(c.actorType(), o.name());
                IStarMarking oldMarking = previous == null
                        ? IStarMarking.initial(gm)
                        : previous.markings().getOrDefault(key, IStarMarking.initial(gm));
                var element = gm.findElement(c.elementId()).orElse(null);
                if (element instanceof Task) {
                    TaskMarking old = oldMarking.taskMarking(c.elementId());
                    if (old == null) old = TaskMarking.initial();
                    boolean preHolds = !c.preconditions().isEmpty()
                            && c.preconditions().stream().allMatch(expr -> evalBoolean(expr, state, o));
                    boolean postHolds = c.expr() != null && evalBoolean(c.expr(), state, o);
                    boolean required = isDemanded(c.elementId(), activationGraph, constraints,
                            Map.of(c.actorType(), o), markings, occurrenceGoals, occurrenceTasks)
                            || old.preSeen();
                    markings.put(key, markings.get(key).withTaskMarking(
                            c.elementId(), old.update(required, preHolds, postHolds)));
                } else if (element instanceof Goal goal) {
                    GoalMarking old = oldMarking.goalMarking(c.elementId());
                    if (old == null) old = GoalMarking.initial(goal.goalType());
                    boolean active = isDemanded(c.elementId(), activationGraph, constraints,
                            Map.of(c.actorType(), o), markings, occurrenceGoals, occurrenceTasks);
                    // A structural goal without a predicate is derived from its children.
                    // Its demand is retained by the child-demand propagation below.
                    boolean predicate = c.expr() != null && evalBoolean(c.expr(), state, o);
                    markings.put(key, markings.get(key).withGoalMarking(
                            c.elementId(), old.update(active, predicate)));
                }
            }
        }

        // Contextual occurrences must retain the complete binding. A flat marking indexed
        // only by Secretary#s1 would merge s1's work for Participant#p1 and Participant#p2.
        for (ConstraintInfo c : constraints.values()) {
            if (c.contextTypes().size() <= 1) continue;
            for (List<MObject> objects : contextCombinations(c.contextTypes(), instancesByActorType)) {
                if (!sameAclContext(acl, state, c.contextTypes(), objects)) continue;
                List<String> names = objects.stream().map(MObject::name).toList();
                OccurrenceKey key = new OccurrenceKey(c.elementId(), names);
                Map<String, MObject> bindings = new LinkedHashMap<>();
                for (int i = 0; i < c.contextTypes().size(); i++) {
                    bindings.put(c.contextTypes().get(i), objects.get(i));
                }
                var element = gm.findElement(c.elementId()).orElse(null);
                if (element instanceof Task) {
                    TaskMarking old = previous == null
                            ? TaskMarking.initial()
                            : previous.occurrenceTasks().getOrDefault(key, TaskMarking.initial());
                    boolean pre = !c.preconditions().isEmpty() && c.preconditions().stream()
                            .allMatch(expr -> evalBoolean(expr, state, c.actorType(), bindings));
                    boolean post = c.expr() != null
                            && evalBoolean(c.expr(), state, c.actorType(), bindings);
                    boolean required = isDemanded(c.elementId(), activationGraph, constraints,
                            bindings, markings, occurrenceGoals, occurrenceTasks) || old.preSeen();
                    occurrenceTasks.put(key, old.update(required, pre, post));
                } else if (element instanceof Goal goal) {
                    GoalMarking old = previous == null
                            ? GoalMarking.initial(goal.goalType())
                            : previous.occurrenceGoals().getOrDefault(
                                    key, GoalMarking.initial(goal.goalType()));
                    boolean active = isDemanded(c.elementId(), activationGraph, constraints,
                            bindings, markings, occurrenceGoals, occurrenceTasks);
                    boolean predicate = c.expr() != null
                            && evalBoolean(c.expr(), state, c.actorType(), bindings);
                    occurrenceGoals.put(key, old.update(active, predicate));
                }
            }
        }

        broadcastSingletonActorFacts(gm, resolution, markings, instancesByActorType);
        markings.replaceAll((key, marking) -> IStarPropagation.closePending(gm, marking));
        return markings;
    }

    private static List<List<MObject>> contextCombinations(
            List<String> contextTypes, Map<String, List<MObject>> instancesByActorType) {
        List<List<MObject>> result = new ArrayList<>();
        buildContextCombinations(contextTypes, instancesByActorType, 0, new ArrayList<>(), result);
        return result;
    }

    private static void buildContextCombinations(List<String> types,
            Map<String, List<MObject>> instances, int index, List<MObject> current,
            List<List<MObject>> result) {
        if (index == types.size()) {
            result.add(List.copyOf(current));
            return;
        }
        for (MObject object : instances.getOrDefault(types.get(index), List.of())) {
            current.add(object);
            buildContextCombinations(types, instances, index + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    private static boolean sameAclContext(AclModel acl, MSystemState state,
            List<String> types, List<MObject> objects) {
        if (acl == null) return true;
        for (int i = 0; i < types.size(); i++) {
            for (int j = i + 1; j < types.size(); j++) {
                if (!sharesAclGroupContext(acl, state,
                        types.get(i), objects.get(i), types.get(j), objects.get(j))) return false;
            }
        }
        return true;
    }

    private static boolean isDemanded(String elementId, GoalActivationGraph graph,
            Map<String, ConstraintInfo> constraints, Map<String, MObject> bindings,
            Map<InstanceKey, IStarMarking> flat,
            Map<OccurrenceKey, GoalMarking> occurrenceGoals,
            Map<OccurrenceKey, TaskMarking> occurrenceTasks) {
        return isDemanded(elementId, graph, constraints, bindings, flat,
                occurrenceGoals, occurrenceTasks, new LinkedHashSet<>());
    }

    private static boolean isDemanded(String elementId, GoalActivationGraph graph,
            Map<String, ConstraintInfo> constraints, Map<String, MObject> bindings,
            Map<InstanceKey, IStarMarking> flat,
            Map<OccurrenceKey, GoalMarking> occurrenceGoals,
            Map<OccurrenceKey, TaskMarking> occurrenceTasks, Set<String> seen) {
        if (!seen.add(elementId)) return false;
        String parentId = graph.parentOf(elementId).orElse(null);
        if (parentId == null) return true; // Goal activation root, or root Task enabled by pre.
        ConstraintInfo parent = constraints.get(parentId);
        if (parent != null && parent.contextTypes().size() > 1) {
            List<String> names = new ArrayList<>();
            for (String type : parent.contextTypes()) {
                MObject object = bindings.get(type);
                if (object == null) return false;
                names.add(object.name());
            }
            OccurrenceKey key = new OccurrenceKey(parentId, names);
            GoalMarking goal = occurrenceGoals.get(key);
            if (goal != null && goal.status() != GoalTaskStatus.UNKNOWN) {
                return goal.status() == GoalTaskStatus.PENDING;
            }
            TaskMarking task = occurrenceTasks.get(key);
            if (task != null && task.status() != GoalTaskStatus.UNKNOWN) {
                return task.status() == GoalTaskStatus.PENDING;
            }
            return isDemanded(parentId, graph, constraints, bindings, flat,
                    occurrenceGoals, occurrenceTasks, seen);
        }
        String parentType = parent == null ? null : parent.actorType();
        if (parentType != null && bindings.get(parentType) != null) {
            IStarMarking marking = flat.get(new InstanceKey(parentType, bindings.get(parentType).name()));
            if (marking != null && marking.goalTaskStatus(parentId) != GoalTaskStatus.UNKNOWN) {
                return marking.goalTaskStatus(parentId) == GoalTaskStatus.PENDING;
            }
            return isDemanded(parentId, graph, constraints, bindings, flat,
                    occurrenceGoals, occurrenceTasks, seen);
        }
        boolean pending = flat.values().stream().anyMatch(marking ->
                marking.goalTaskStatus(parentId) == GoalTaskStatus.PENDING);
        if (pending) return true;
        boolean known = flat.values().stream().anyMatch(marking ->
                marking.goalTaskStatus(parentId) != GoalTaskStatus.UNKNOWN);
        return !known && isDemanded(parentId, graph, constraints, bindings, flat,
                occurrenceGoals, occurrenceTasks, seen);
    }

    /**
     * Quantified iStar refinements are contextual: an owner occurrence quantifies only over
     * role occurrences in the same ACL Group-instance ancestry. Two roles in sibling Groups,
     * or in two different instances of the same Group type, never share a quantifier universe.
     */
    static boolean sharesAclGroupContext(AclModel acl, MSystemState state,
            String leftRoleType, MObject left, String rightRoleType, MObject right) {
        Map<String, MObject> leftContexts = groupContexts(acl, state, leftRoleType, left);
        Map<String, MObject> rightContexts = groupContexts(acl, state, rightRoleType, right);
        for (var entry : leftContexts.entrySet()) {
            MObject rightContext = rightContexts.get(entry.getKey());
            // The first common Group type is the nearest shared scope. If its instances
            // differ, the roles are in sibling contexts and a higher common ancestor must
            // not collapse their quantified universes.
            if (rightContext != null) return entry.getValue().equals(rightContext);
        }
        return false;
    }

    private static Map<String, MObject> groupContexts(
            AclModel acl, MSystemState state, String roleType, MObject roleObject) {
        Map<String, MObject> contexts = new LinkedHashMap<>();
        String groupType = acl.owners().stream()
                .filter(owner -> owner.target().equals(roleType))
                .map(org.vnu.sme.goal.dsl.acl.mm.AclOwner::sourceGroup)
                .findFirst().orElse(null);
        if (groupType == null) return contexts;
        MObject groupObject = linkedObject(state, roleObject, roleType + "_in_" + groupType, 1, 0);
        if (groupObject == null) return contexts;
        contexts.put(groupType, groupObject);

        Set<String> seen = new LinkedHashSet<>();
        while (seen.add(groupType)) {
            String childType = groupType;
            String parentType = acl.owners().stream()
                    .filter(owner -> owner.target().equals(childType))
                    .filter(owner -> acl.groups().stream().anyMatch(group -> group.name().equals(owner.sourceGroup())))
                    .map(org.vnu.sme.goal.dsl.acl.mm.AclOwner::sourceGroup)
                    .findFirst().orElse(null);
            if (parentType == null) break;
            MObject parentObject = linkedObject(
                    state, groupObject, "Owner_" + parentType + "_" + childType, 1, 0);
            if (parentObject == null) break;
            contexts.put(parentType, parentObject);
            groupType = parentType;
            groupObject = parentObject;
        }
        return contexts;
    }

    /** Association endpoint positions follow Acl2UseTranslator: source first, target second. */
    private static MObject linkedObject(MSystemState state, MObject known, String associationName,
                                        int knownIndex, int resultIndex) {
        MAssociation association = state.system().model().getAssociation(associationName);
        if (association == null || state.linksOfAssociation(association) == null) return null;
        for (MLink link : state.linksOfAssociation(association).links()) {
            MObject[] objects = link.linkedObjectsAsArray();
            if (objects.length > Math.max(knownIndex, resultIndex) && objects[knownIndex].equals(known)) {
                return objects[resultIndex];
            }
        }
        return null;
    }

    private static void rememberCreatedObjectOrder(String soilLine, Map<String, Integer> objectOrder) {
        Matcher matcher = NEW_NAMED_OBJECT.matcher(soilLine);
        if (!matcher.matches()) return;
        objectOrder.putIfAbsent(matcher.group(2), objectOrder.size());
    }

    /**
     * Mirrors .iscn's private-trace convention (org.vnu.sme.goal.dsl.iscn.parser.IStarScenarioCompiler.appliesTo): an
     * unqualified fire/assign is written into every declared instance's own trace, not just
     * the actor it structurally belongs to -- "a fact shared by the whole scenario, not
     * specific to one instance". There is no "unqualified" statement in this pipeline (every
     * OCL evaluation is inherently tied to one self object), but an actor type with exactly one
     * real .use instance IS, in effect, the same thing an .iscn author would naturally leave
     * unqualified: there is no other instance to distinguish it from. So every fact already
     * settled for a singleton actor's own instance is copied into every other declared
     * instance's own marking too; a multi-instance actor's facts (e.g. each Participant's own
     * CollectFromCalendar) stay scoped to that one instance, exactly as a qualified
     * "fire xing.CollectFromCalendar" would in .iscn.
     */
    private static void broadcastSingletonActorFacts(GoalModel gm, ContextResolution resolution,
                                                      Map<InstanceKey, IStarMarking> markings,
                                                      Map<String, List<MObject>> instancesByActorType) {
        for (var singletonEntry : instancesByActorType.entrySet()) {
            List<MObject> singletonInstances = singletonEntry.getValue();
            if (singletonInstances.size() != 1) continue;
            String singletonType = singletonEntry.getKey();
            InstanceKey singletonKey = new InstanceKey(singletonType, singletonInstances.get(0).name());
            IStarMarking singletonMarking = markings.get(singletonKey);

            for (var recipientEntry : instancesByActorType.entrySet()) {
                if (recipientEntry.getKey().equals(singletonType)) continue;
                for (MObject recipient : recipientEntry.getValue()) {
                    InstanceKey recipientKey = new InstanceKey(recipientEntry.getKey(), recipient.name());
                    IStarMarking recipientMarking = markings.get(recipientKey);
                    for (var e : singletonMarking.goalTaskStatuses().entrySet()) {
                        if (e.getValue() == GoalTaskStatus.UNKNOWN) continue;
                        if (!singletonType.equals(resolution.actorTypeOf(gm, e.getKey()))) continue;
                        recipientMarking = IStarPropagation.assignGoalTask(gm, recipientMarking, e.getKey(), e.getValue());
                    }
                    for (var e : singletonMarking.qualityStatuses().entrySet()) {
                        if (e.getValue() == QualityStatus.UNKNOWN) continue;
                        recipientMarking = IStarPropagation.assignQuality(gm, recipientMarking, e.getKey(), e.getValue());
                    }
                    markings.put(recipientKey, recipientMarking);
                }
            }
        }
    }

    private static boolean evalBoolean(ConstraintInfo c, MSystemState state, MObject self) {
        return evalBoolean(c.expr(), state, self);
    }

    private static boolean evalBoolean(org.tzi.use.uml.ocl.expr.Expression expression,
                                       MSystemState state, MObject self) {
        VarBindings vars = new VarBindings();
        EvalContext ctx = new SimpleEvalContext(state, state, vars);
        ctx.pushVarBinding("self", self.value());
        try {
            var v = expression.eval(ctx);
            return v instanceof BooleanValue bv && bv.isTrue();
        } catch (RuntimeException ex) {
            return false;
        }
    }

    private static boolean evalBoolean(org.tzi.use.uml.ocl.expr.Expression expression,
            MSystemState state, String selfType, Map<String, MObject> bindings) {
        if (expression == null || bindings.get(selfType) == null) return false;
        VarBindings vars = new VarBindings();
        EvalContext ctx = new SimpleEvalContext(state, state, vars);
        ctx.pushVarBinding("self", bindings.get(selfType).value());
        bindings.forEach((type, object) -> ctx.pushVarBinding(lowerFirst(type), object.value()));
        try {
            var value = expression.eval(ctx);
            return value instanceof BooleanValue booleanValue && booleanValue.isTrue();
        } catch (RuntimeException ex) {
            return false;
        }
    }

    private static String lowerFirst(String value) {
        if (value == null || value.isEmpty()) return value;
        return Character.toLowerCase(value.charAt(0)) + value.substring(1);
    }
}
