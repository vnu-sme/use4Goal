package org.vnu.sme.goal.acl;

import java.nio.file.Path;

import org.vnu.sme.goal.acl.parser.AclCompiler;
import org.vnu.sme.goal.acl.use.AclUseTranslator;

public final class AclToUseDemoMain {

    private AclToUseDemoMain() {}

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("usage: AclToUseDemoMain input.acl");
            System.exit(2);
        }
        AclCompiler.Result result = AclCompiler.compile(Path.of(args[0]));
        if (!result.ok()) {
            result.errors().forEach(System.err::println);
            System.exit(1);
        }
        System.out.print(AclUseTranslator.translate(result.model()));
    }
}
