package org.vnu.sme.goal.conformance;

import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.vnu.sme.goal.bpmn2.mm.Bpmn2Model;
import org.vnu.sme.goal.bpmn2.mm.FlowElement;
import org.vnu.sme.goal.bpmn2.parser.Bpmn2Compiler;
import org.vnu.sme.goal.conformance.mapping.ConformanceMapping;
import org.vnu.sme.goal.conformance.mapping.ConformanceMappingParser;
import org.vnu.sme.goal.conformance.semantics.Bpmn2LtsBuilder;
import org.vnu.sme.goal.conformance.semantics.ComplianceChecker;
import org.vnu.sme.goal.conformance.semantics.ComplianceResult;
import org.vnu.sme.goal.conformance.semantics.ProductLts;
import org.vnu.sme.goal.istar.mm.GoalModel;
import org.vnu.sme.goal.istar.mm.Quality;
import org.vnu.sme.goal.istar.mm.IntentionalElement;
import org.vnu.sme.goal.istar.parser.IStarCompiler;

/**
 * Runnable end-to-end demonstration of the conformance mechanism designed in
 * doc/paper/conformance-istar-bpmn2.md, using the paired case study
 * goal/src/main/resources/examples/construction_permit/construction_permit.{istar,bpmn2,map}.
 *
 * <p>Run (after {@code mvn -pl goal -am compile}), from the repository root:
 * <pre>
 *   java -cp goal/target/classes:goal/target/generated-sources/antlr4 \
 *        org.vnu.sme.goal.conformance.ConformanceDemoMain
 * </pre>
 * (add the {@code antlr4-runtime} jar to the classpath as well).
 */
public final class ConformanceDemoMain {

    public static void main(String[] args) throws Exception {
        Path base = Path.of(args.length > 0 ? args[0] : "goal/src/main/resources/examples/construction_permit");

        IStarCompiler.Result istarResult = IStarCompiler.compile(base.resolve("construction_permit.istar"));
        if (!istarResult.ok()) {
            istarResult.errors().forEach(System.err::println);
            System.exit(1);
        }
        GoalModel gm = istarResult.model();

        Bpmn2Compiler.Result bpmnResult = Bpmn2Compiler.compile(base.resolve("construction_permit.bpmn2"));
        if (!bpmnResult.ok()) {
            bpmnResult.errors().forEach(System.err::println);
            System.exit(1);
        }
        Bpmn2Model pm = bpmnResult.model();

        ConformanceMapping map = ConformanceMappingParser.parse(base.resolve("construction_permit.map"));
        List<String> warnings = map.validate(gm, pm);
        warnings.forEach(w -> System.out.println("[warn] " + w));

        Bpmn2LtsBuilder.validateWellFormed(pm);

        ProductLts productLts = new ProductLts(gm, pm, map);

        Set<String> qualityIds = new LinkedHashSet<>();
        for (IntentionalElement e : gm.allElements().values()) {
            if (e instanceof Quality q) qualityIds.add(q.id());
        }

        ComplianceResult result = ComplianceChecker.check(productLts, qualityIds);

        System.out.println();
        System.out.println("Verdict : " + result.verdict());
        System.out.println("Weak    : " + result.weak());
        System.out.println("Stable  : " + result.stable());
        System.out.println("Message : " + result.message());
        if (!result.counterexampleTrace().isEmpty()) {
            System.out.println("Counterexample trace:");
            for (FlowElement n : result.counterexampleTrace()) {
                System.out.println("  -> " + n.id());
            }
        }
    }
}
