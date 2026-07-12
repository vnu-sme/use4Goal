package org.vnu.sme.goal.istarusebridge;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.tzi.use.parser.soil.SoilCompiler;
import org.tzi.use.parser.use.USECompiler;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.ModelFactory;
import org.tzi.use.uml.ocl.expr.EvalContext;
import org.tzi.use.uml.ocl.expr.SimpleEvalContext;
import org.tzi.use.uml.ocl.value.BooleanValue;
import org.tzi.use.uml.ocl.value.VarBindings;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.uml.sys.MSystemState;
import org.tzi.use.uml.sys.StatementEvaluationResult;
import org.tzi.use.uml.sys.soil.MStatement;
import org.tzi.use.uml.sys.soil.SoilEvaluationContext;

import org.vnu.sme.goal.conformance.semantics.GoalTaskStatus;
import org.vnu.sme.goal.conformance.semantics.QualityStatus;
import org.vnu.sme.goal.conformance.semantics.IStarMarking;
import org.vnu.sme.goal.conformance.semantics.IStarPropagation;
import org.vnu.sme.goal.istar.mm.ForRefinement;
import org.vnu.sme.goal.istar.mm.GoalModel;
import org.vnu.sme.goal.istar.parser.IStarCompiler;
import org.vnu.sme.goal.istarusebridge.IStarOclConstraintCompiler.ConstraintInfo;

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

    public record InstanceKey(String actorType, String objectName) {
        @Override public String toString() { return actorType + "#" + objectName; }
    }

    public record Checkpoint(int index, String soilLine, Map<InstanceKey, IStarMarking> markings) {}

    public record Result(GoalModel model, MModel useModel, List<Checkpoint> checkpoints, List<String> errors) {
        public boolean ok() { return errors.isEmpty(); }
    }

    private IStarUseTraceCompiler() {}

    public static Result compile(Path istarFile, Path useFile, Path soilFile) throws IOException {
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
        IStarOclConstraintCompiler.Result constraintResult = IStarOclConstraintCompiler.compile(gm, useModel, resolution);
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
        int idx = 0;
        for (String line : soilLines) {
            idx++;
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
            } catch (Exception ex) {
                errors.add("soil line " + idx + " '" + line + "': " + ex);
                break;
            }

            Map<InstanceKey, IStarMarking> markings =
                    computeCheckpoint(gm, resolution, constraints, actorClasses, system.state());
            checkpoints.add(new Checkpoint(idx, line, markings));
        }

        return new Result(gm, useModel, checkpoints, errors);
    }

    private static Map<InstanceKey, IStarMarking> computeCheckpoint(
            GoalModel gm, ContextResolution resolution, Map<String, ConstraintInfo> constraints,
            Map<String, MClass> actorClasses, MSystemState state) {

        Map<InstanceKey, IStarMarking> markings = new LinkedHashMap<>();
        Map<String, List<MObject>> instancesByActorType = new LinkedHashMap<>();
        for (var e : actorClasses.entrySet()) {
            List<MObject> objs = new ArrayList<>(state.objectsOfClass(e.getValue()));
            instancesByActorType.put(e.getKey(), objs);
            for (MObject o : objs) {
                markings.put(new InstanceKey(e.getKey(), o.name()), IStarMarking.initial(gm));
            }
        }

        for (ConstraintInfo c : constraints.values()) {
            for (MObject o : instancesByActorType.getOrDefault(c.actorType(), List.of())) {
                boolean holds = evalBoolean(c, state, o);
                InstanceKey key = new InstanceKey(c.actorType(), o.name());
                markings.put(key, IStarPropagation.assignGoalTask(gm, markings.get(key), c.elementId(),
                        holds ? GoalTaskStatus.FULFILLED : GoalTaskStatus.PENDING));
            }
        }

        for (ConstraintInfo c : constraints.values()) {
            var quantified = resolution.quantifiedEdge(c.elementId());
            if (quantified.isEmpty()) continue;

            List<MObject> universe = instancesByActorType.getOrDefault(c.actorType(), List.of());
            boolean isForall = quantified.get().quantifier() instanceof ForRefinement;
            // An empty universe must not vacuously satisfy forall (nobody's condition has
            // actually been checked yet) -- only genuinely universal satisfaction counts.
            boolean aggregate = !universe.isEmpty() && (isForall
                    ? universe.stream().allMatch(o -> markings.get(new InstanceKey(c.actorType(), o.name()))
                            .goalTaskStatus(c.elementId()) == GoalTaskStatus.FULFILLED)
                    : universe.stream().anyMatch(o -> markings.get(new InstanceKey(c.actorType(), o.name()))
                            .goalTaskStatus(c.elementId()) == GoalTaskStatus.FULFILLED));

            String ownerActorType = gm.ownerOf(c.elementId()).orElseThrow();
            for (MObject owner : instancesByActorType.getOrDefault(ownerActorType, List.of())) {
                InstanceKey key = new InstanceKey(ownerActorType, owner.name());
                markings.put(key, IStarPropagation.assignGoalTask(gm, markings.get(key), c.elementId(),
                        aggregate ? GoalTaskStatus.FULFILLED : GoalTaskStatus.PENDING));
            }
        }

        broadcastSingletonActorFacts(gm, resolution, markings, instancesByActorType);
        markings.replaceAll((key, marking) -> IStarPropagation.closePending(gm, marking));
        return markings;
    }

    /**
     * Mirrors .iscn's private-trace convention (org.vnu.sme.goal.iscn.parser.IStarScenarioCompiler.appliesTo): an
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
        VarBindings vars = new VarBindings();
        EvalContext ctx = new SimpleEvalContext(state, state, vars);
        ctx.pushVarBinding("self", self.value());
        var v = c.expr().eval(ctx);
        return v instanceof BooleanValue bv && bv.isTrue();
    }
}
