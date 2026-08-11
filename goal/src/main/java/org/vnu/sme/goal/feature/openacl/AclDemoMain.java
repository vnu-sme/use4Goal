package org.vnu.sme.goal.feature.openacl;

import java.nio.file.Path;

import org.vnu.sme.goal.dsl.acl.parser.AclCompiler;
import org.vnu.sme.goal.dsl.acl.view.AclSpecText;

public final class AclDemoMain {

    private AclDemoMain() {}

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("usage: AclDemoMain input.acl");
            System.exit(2);
        }
        AclCompiler.Result result = AclCompiler.compile(Path.of(args[0]));
        if (!result.ok()) {
            result.errors().forEach(System.err::println);
            System.exit(1);
        }
        System.out.print(AclSpecText.render(result.model()));
    }
}
