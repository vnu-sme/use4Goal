package org.vnu.sme.goal.verify.conformance.oracle;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vnu.sme.goal.dsl.aol.mm.AolGroupInstance;
import org.vnu.sme.goal.dsl.aol.mm.AolModel;
import org.vnu.sme.goal.dsl.aol.mm.AolPlay;
import org.vnu.sme.goal.verify.conformance.semantics.IStarMarking;
import org.vnu.sme.goal.dsl.iscn.mm.ScenarioInstance;
import org.vnu.sme.goal.dsl.iscn.mm.ScenarioStmt;
import org.vnu.sme.goal.dsl.iscn.parser.IStarScenarioCompiler;
import org.vnu.sme.goal.trace.usetrace.IStarUseTraceCompiler.Checkpoint;
import org.vnu.sme.goal.trace.usetrace.IStarUseTraceCompiler.InstanceKey;

/**
 * Compares the final iStar marking derived from the ACL/AOL/BPMN/OCL execution
 * with the facts explicitly observed by ISCN fire/assign/aggregate statements.
 * Derived values on unrelated per-actor markings are intentionally not treated
 * as assertions.
 *
 * <p>ISCN names are bound deterministically to AOL role objects. An exact AOL
 * role-instance id wins; otherwise a unique AOL agent id playing the requested
 * actor type is accepted. This keeps the binding explicit without introducing
 * a second mapping file.
 */
public final class IscnOracleComparator {

    private record PlayBinding(String actorType, String instanceId, String agentId) {}

    private IscnOracleComparator() {}

    public static List<String> compare(
            IStarScenarioCompiler.Result oracle,
            AolModel snapshot,
            Checkpoint actualCheckpoint) {
        List<String> failures = new ArrayList<>();
        Map<String, ScenarioInstance> declarations = new LinkedHashMap<>();
        oracle.scenario().instances().forEach(instance -> declarations.put(instance.name(), instance));
        Map<String, Set<String>> observedElements = observations(oracle, declarations);

        List<PlayBinding> plays = new ArrayList<>();
        snapshot.groupInstances().forEach(group -> collectPlays(group, plays));

        if (oracle.markings().containsKey(IStarScenarioCompiler.DEFAULT_INSTANCE)) {
            compareDefault(oracle.markings().get(IStarScenarioCompiler.DEFAULT_INSTANCE),
                    observedElements.getOrDefault(IStarScenarioCompiler.DEFAULT_INSTANCE, Set.of()),
                    actualCheckpoint, failures);
            return List.copyOf(failures);
        }

        Set<InstanceKey> matchedActualInstances = new LinkedHashSet<>();
        for (var entry : oracle.markings().entrySet()) {
            String scenarioInstance = entry.getKey();
            ScenarioInstance declaration = declarations.get(scenarioInstance);
            if (declaration == null) {
                failures.add("ISCN instance '" + scenarioInstance + "' has no actor declaration");
                continue;
            }

            InstanceKey actualKey = resolve(
                    declaration.actorType(), scenarioInstance, plays, actualCheckpoint, failures);
            if (actualKey == null) continue;

            IStarMarking actual = actualCheckpoint.markings().get(actualKey);
            if (actual == null) {
                failures.add("ISCN instance '" + scenarioInstance + " : " + declaration.actorType()
                        + "' resolves to missing execution object '" + actualKey.objectName() + "'");
                continue;
            }
            if (!matchedActualInstances.add(actualKey)) {
                failures.add("ISCN instance '" + scenarioInstance + " : " + declaration.actorType()
                        + "' resolves to an execution object already covered by another ISCN instance: "
                        + actualKey.objectName());
                continue;
            }
            compareObserved(scenarioInstance, observedElements.getOrDefault(scenarioInstance, Set.of()),
                    entry.getValue(), actual, failures);
        }
        for (InstanceKey actualKey : actualCheckpoint.markings().keySet()) {
            if (!matchedActualInstances.contains(actualKey)) {
                failures.add("execution actor '" + actualKey.objectName() + " : "
                        + actualKey.actorType() + "' is not covered by any ISCN instance");
            }
        }
        return List.copyOf(failures);
    }

    private static void compareDefault(
            IStarMarking expected,
            Set<String> observedElements,
            Checkpoint checkpoint,
            List<String> failures) {
        if (checkpoint.markings().size() != 1) {
            failures.add("ISCN has an implicit singleton marking, but the AOL execution produced "
                    + checkpoint.markings().size() + " iStar actor instances");
            return;
        }
        IStarMarking actual = checkpoint.markings().values().iterator().next();
        compareObserved("<default>", observedElements, expected, actual, failures);
    }

    private static InstanceKey resolve(
            String actorType,
            String scenarioName,
            List<PlayBinding> plays,
            Checkpoint checkpoint,
            List<String> failures) {
        InstanceKey direct = new InstanceKey(actorType, scenarioName);
        if (checkpoint.markings().containsKey(direct)) return direct;

        List<PlayBinding> byInstance = plays.stream()
                .filter(play -> play.actorType().equals(actorType))
                .filter(play -> play.instanceId().equals(scenarioName))
                .toList();
        if (byInstance.size() == 1) {
            return new InstanceKey(actorType, byInstance.get(0).instanceId());
        }

        List<PlayBinding> byAgent = plays.stream()
                .filter(play -> play.actorType().equals(actorType))
                .filter(play -> play.agentId().equals(scenarioName))
                .toList();
        if (byAgent.size() == 1) {
            return new InstanceKey(actorType, byAgent.get(0).instanceId());
        }
        if (byAgent.size() > 1) {
            failures.add("ISCN instance '" + scenarioName + " : " + actorType
                    + "' is ambiguous: AOL agent '" + scenarioName + "' plays " + byAgent.size()
                    + " matching role instances; use an AOL role instance id as the ISCN name");
        } else {
            failures.add("ISCN instance '" + scenarioName + " : " + actorType
                    + "' has no matching AOL role instance or unique AOL agent binding");
        }
        return null;
    }

    private static void compareObserved(
            String instance,
            Set<String> observedElements,
            IStarMarking expected,
            IStarMarking actual,
            List<String> failures) {
        for (String element : observedElements) {
            if (expected.goalTaskStatuses().containsKey(element)) {
                var expectedStatus = expected.goalTaskStatus(element);
                var actualStatus = actual.goalTaskStatus(element);
                if (actualStatus != expectedStatus) {
                    failures.add(instance + "." + element + ": ISCN=" + expectedStatus
                            + ", BPMN/OCL=" + actualStatus);
                }
                continue;
            }
            if (!expected.qualityStatuses().containsKey(element)) continue;
            var expectedStatus = expected.qualityStatus(element);
            var actualStatus = actual.qualityStatus(element);
            if (actualStatus != expectedStatus) {
                failures.add(instance + "." + element + ": ISCN=" + expectedStatus
                        + ", BPMN/OCL=" + actualStatus);
            }
        }
    }

    private static Map<String, Set<String>> observations(
            IStarScenarioCompiler.Result oracle,
            Map<String, ScenarioInstance> declarations) {
        Map<String, Set<String>> result = new LinkedHashMap<>();
        oracle.markings().keySet().forEach(id -> result.put(id, new LinkedHashSet<>()));

        for (ScenarioStmt statement : oracle.scenario().statements()) {
            switch (statement) {
                case ScenarioStmt.Fire fire ->
                        observe(oracle, declarations, result, fire.instanceId(), fire.elementId(), null);
                case ScenarioStmt.Assign assign ->
                        observe(oracle, declarations, result, assign.instanceId(), assign.elementId(), null);
                case ScenarioStmt.Aggregate aggregate ->
                        observe(oracle, declarations, result, null, aggregate.elementId(), aggregate.actorType());
            }
        }
        return result;
    }

    private static void observe(
            IStarScenarioCompiler.Result oracle,
            Map<String, ScenarioInstance> declarations,
            Map<String, Set<String>> observations,
            String instanceId,
            String elementId,
            String restrictedActorType) {
        if (instanceId != null) {
            observations.computeIfAbsent(instanceId, ignored -> new LinkedHashSet<>()).add(elementId);
            return;
        }
        if (declarations.isEmpty()) {
            observations.computeIfAbsent(IStarScenarioCompiler.DEFAULT_INSTANCE,
                    ignored -> new LinkedHashSet<>()).add(elementId);
            return;
        }

        String actorType = restrictedActorType != null
                ? restrictedActorType
                : oracle.model().ownerOf(elementId).orElse(null);
        for (ScenarioInstance declaration : declarations.values()) {
            if (actorType == null || actorType.equals(declaration.actorType())) {
                observations.computeIfAbsent(declaration.name(),
                        ignored -> new LinkedHashSet<>()).add(elementId);
            }
        }
    }

    private static void collectPlays(AolGroupInstance group, List<PlayBinding> out) {
        for (AolPlay play : group.plays()) {
            out.add(new PlayBinding(play.roleType(), play.instanceId(), play.agentId()));
        }
        group.subgroups().forEach(child -> collectPlays(child, out));
    }
}
