package org.vnu.sme.goal.feature.istaruseocl;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.vnu.sme.goal.dsl.acl.mm.AclModel;
import org.vnu.sme.goal.dsl.acl.parser.AclCompiler;
import org.vnu.sme.goal.dsl.istar.mm.GoalModel;
import org.vnu.sme.goal.dsl.istar.parser.IStarCompiler;
import org.vnu.sme.goal.translate.aclistar2use.AclIStar2UseTranslator;
import org.vnu.sme.goal.translate.aclistar2use.IStarUseOclService;

/**
 * Quick smoke-test: compile mtg.acl + mtg.istar and print the unified .use output to stdout.
 * Run with: mvn exec:java -pl goal -Dexec.mainClass=org.vnu.sme.goal.feature.istaruseocl.IStarUseOclDemo
 */
public final class IStarUseOclDemo {

    public static void main(String[] args) throws Exception {
        Path aclPath   = Paths.get("src/main/resources/examples/mtg/mtg.acl");
        Path istarPath = Paths.get("src/main/resources/examples/mtg/mtg.istar");
        Path outputFile = Paths.get("target/mtg_verification.use");

        System.out.println("=== Parsing ACL: " + aclPath + " ===");
        AclCompiler.Result aclResult = AclCompiler.compile(aclPath);
        if (!aclResult.ok()) {
            System.err.println("ACL errors: " + aclResult.errors());
            return;
        }
        AclModel acl = aclResult.model();
        System.out.println("ACL ok. roles=" + acl.roles().stream().map(r -> r.name()).toList());

        System.out.println("\n=== Parsing iStar: " + istarPath + " ===");
        IStarCompiler.Result istarResult = IStarCompiler.compile(istarPath);
        if (!istarResult.ok()) {
            System.err.println("iStar errors: " + istarResult.errors());
            return;
        }
        GoalModel gm = istarResult.model();
        System.out.println("iStar ok. actors=" + gm.getActors().stream().map(a -> a.name()).toList());

        System.out.println("\n=== Translating → .use (class diagram + OCL) + .tocl (temporal props) ===");
        AclIStar2UseTranslator.Result useResult = AclIStar2UseTranslator.translate(acl, gm);
        if (!useResult.diagnostics().isEmpty()) {
            System.out.println("Diagnostics:");
            useResult.diagnostics().forEach(d -> System.out.println("  · " + d));
        }
        System.out.println(useResult.useText());
        System.out.println("--- .tocl ---");
        System.out.println(useResult.toclText());

        System.out.println("\n=== Writing to: " + outputFile + " ===");
        IStarUseOclService.Result svcResult = IStarUseOclService.translate(aclPath, istarPath, outputFile);
        if (svcResult.ok()) {
            System.out.println("Written: " + svcResult.outputFile());
            System.out.println("Written: " + svcResult.toclFile());
        } else {
            System.err.println("Errors: " + svcResult.errors());
        }
    }
}
