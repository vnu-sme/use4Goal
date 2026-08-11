package org.vnu.sme.goal.trace.istartrace;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tzi.use.uml.sys.MSystemState;
import org.vnu.sme.goal.dsl.acl.mm.AclModel;
import org.vnu.sme.goal.dsl.acl.parser.AclCompiler;
import org.vnu.sme.goal.translate.acl2use.Acl2UseTranslator;
import org.vnu.sme.goal.trace.usetrace.IStarUseTraceCompiler;
import org.vnu.sme.goal.trace.usetrace.IStarUseTraceCompiler.Checkpoint;
import org.vnu.sme.goal.trace.usetrace.IStarUseTraceEvaluator;
import org.vnu.sme.goal.trace.usetrace.IStarUseTraceEvaluator.Instantiation;

/**
 * Application service behind the interactive trace-stepper action. It accepts the three
 * domain-level inputs directly: ACL structure, iStar goals, and a SOIL execution trace.
 * The generated USE model is an internal adapter and is never an input exposed to the user.
 */
public final class IStarTraceStepper {

    public record Step(int index, Checkpoint checkpoint, Instantiation instantiation,
                       List<String> stateDelta, List<String> goalDelta) {}

    public record Result(AclModel acl, IStarUseTraceCompiler.Result execution,
                         List<Step> steps, List<String> errors) {
        public Result {
            steps = List.copyOf(steps);
            errors = List.copyOf(errors);
        }
        public boolean ok() { return errors.isEmpty(); }
    }

    public Result load(Path aclFile, Path istarFile, Path traceFile) {
        List<String> errors = new ArrayList<>();
        AclCompiler.Result acl;
        try {
            acl = AclCompiler.compile(aclFile);
        } catch (IOException ex) {
            return failed("Cannot read ACL file: " + message(ex));
        }
        if (!acl.ok()) return new Result(null, null, List.of(), acl.errors());

        Path temporaryDirectory = null;
        try {
            temporaryDirectory = Files.createTempDirectory("istar-trace-stepper-");
            Path useFile = temporaryDirectory.resolve("acl-shadow.use");
            Files.writeString(useFile, Acl2UseTranslator.translate(acl.model()));
            int initializationStatements = initializationStatementCount(traceFile);
            IStarUseTraceCompiler.Result execution = IStarUseTraceCompiler.compile(
                    istarFile, useFile, traceFile, acl.model(), initializationStatements);
            if (!execution.ok()) {
                return new Result(acl.model(), execution, List.of(), execution.errors());
            }
            List<Step> steps = initializationStatements > 0
                    ? buildStepsAfterInitialization(execution, initializationStatements, traceFile)
                    : buildSteps(execution);
            if (steps.isEmpty()) errors.add("The execution trace contains no executable SOIL statement.");
            return new Result(acl.model(), execution, steps, errors);
        } catch (IOException ex) {
            return new Result(acl.model(), null, List.of(),
                    List.of("Cannot load execution trace: " + message(ex)));
        } finally {
            if (temporaryDirectory != null) {
                try {
                    Files.deleteIfExists(temporaryDirectory.resolve("acl-shadow.use"));
                    Files.deleteIfExists(temporaryDirectory);
                } catch (IOException ignored) {
                    // The compiled model and snapshots are already memory-resident. A failed
                    // best-effort cleanup must not invalidate an otherwise usable trace.
                }
            }
        }
    }

    private static List<Step> buildSteps(IStarUseTraceCompiler.Result execution) {
        List<Step> steps = new ArrayList<>();
        Checkpoint previous = null;
        Instantiation previousInstantiation = null;
        for (int i = 0; i < execution.checkpoints().size(); i++) {
            Checkpoint current = execution.checkpoints().get(i);
            Instantiation instantiation = IStarUseTraceEvaluator.evaluate(execution.model(), current);
            steps.add(new Step(i, current, instantiation,
                    stateDelta(previous == null ? null : previous.state(), current.state()),
                    goalDelta(previousInstantiation, instantiation)));
            previous = current;
            previousInstantiation = instantiation;
        }
        return List.copyOf(steps);
    }

    private static List<Step> buildStepsAfterInitialization(IStarUseTraceCompiler.Result execution,
                                                             int initializationStatements,
                                                             Path traceFile) {
        List<Step> steps = new ArrayList<>();
        Checkpoint initial = execution.checkpoints().get(initializationStatements - 1);
        Checkpoint visibleInitial = copyCheckpoint(initial, 0,
                "[initial state from " + traceFile.getFileName() + "]");
        Instantiation previousInstantiation = IStarUseTraceEvaluator.evaluate(
                execution.model(), initial);
        steps.add(new Step(0, visibleInitial, previousInstantiation,
                List.of("Loaded " + initial.state().allObjects().size() + " objects from trace initialization."),
                goalDelta(null, previousInstantiation)));

        Checkpoint previous = initial;
        int visibleIndex = 1;
        for (int i = initializationStatements; i < execution.checkpoints().size(); i++) {
            Checkpoint current = execution.checkpoints().get(i);
            Instantiation currentInstantiation = IStarUseTraceEvaluator.evaluate(execution.model(), current);
            Checkpoint visible = copyCheckpoint(current, visibleIndex, current.soilLine());
            steps.add(new Step(visibleIndex, visible, currentInstantiation,
                    stateDelta(previous.state(), current.state()),
                    goalDelta(previousInstantiation, currentInstantiation)));
            previous = current;
            previousInstantiation = currentInstantiation;
            visibleIndex++;
        }
        return List.copyOf(steps);
    }

    private static Checkpoint copyCheckpoint(Checkpoint source, int index, String soilLine) {
        return new Checkpoint(index, soilLine, source.markings(), source.occurrenceGoals(),
                source.occurrenceTasks(), source.state(), source.constraints(),
                source.actorClasses(), source.acl());
    }

    private static int initializationStatementCount(Path traceFile) throws IOException {
        int count = 0;
        boolean markerFound = false;
        for (String raw : Files.readAllLines(traceFile)) {
            String line = raw.strip();
            if (line.equals("-- @trace")) {
                markerFound = true;
                break;
            }
            if (!line.isEmpty() && !line.startsWith("--")) count++;
        }
        return markerFound ? count : 0;
    }

    static List<String> stateDelta(MSystemState before, MSystemState after) {
        Map<String, String> left = before == null ? Map.of() : attributeValues(before);
        Map<String, String> right = attributeValues(after);
        Set<String> keys = new LinkedHashSet<>(left.keySet());
        keys.addAll(right.keySet());
        List<String> result = new ArrayList<>();
        for (String key : keys) {
            if (!java.util.Objects.equals(left.get(key), right.get(key))) {
                result.add(key + ": " + left.getOrDefault(key, "<absent>")
                        + " -> " + right.getOrDefault(key, "<absent>"));
            }
        }
        return List.copyOf(result);
    }

    private static Map<String, String> attributeValues(MSystemState state) {
        Map<String, String> values = new LinkedHashMap<>();
        state.allObjects().stream().sorted(Comparator.comparing(object -> object.name()))
                .forEach(object -> object.state(state).attributeValueMap().forEach((attribute, value) ->
                        values.put(object.name() + "." + attribute.name(), String.valueOf(value))));
        return values;
    }

    private static List<String> goalDelta(Instantiation before, Instantiation after) {
        Map<String, org.vnu.sme.goal.verify.conformance.semantics.GoalTaskStatus> left = before == null
                ? Map.of() : before.instanceMarking().goalTaskStatuses();
        Map<String, org.vnu.sme.goal.verify.conformance.semantics.GoalTaskStatus> right =
                after.instanceMarking().goalTaskStatuses();
        Set<String> ids = new LinkedHashSet<>(left.keySet());
        ids.addAll(right.keySet());
        List<String> result = new ArrayList<>();
        for (String id : ids) {
            var oldStatus = left.getOrDefault(id,
                    org.vnu.sme.goal.verify.conformance.semantics.GoalTaskStatus.UNKNOWN);
            var newStatus = right.getOrDefault(id,
                    org.vnu.sme.goal.verify.conformance.semantics.GoalTaskStatus.UNKNOWN);
            if (oldStatus != newStatus) {
                result.add(after.nodeLabels().getOrDefault(id, id)
                        + ": " + oldStatus + " -> " + newStatus);
            }
        }
        return List.copyOf(result);
    }

    private static Result failed(String error) {
        return new Result(null, null, List.of(), List.of(error));
    }

    private static String message(Exception ex) {
        String value = ex.getMessage();
        return value == null || value.isBlank() ? ex.toString() : value;
    }
}
