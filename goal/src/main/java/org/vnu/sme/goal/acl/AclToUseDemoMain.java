package org.vnu.sme.goal.acl;

import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

import org.vnu.sme.goal.acl.parser.AclCompiler;
import org.vnu.sme.goal.acl.use.AclUseTranslator;

public final class AclToUseDemoMain {

    private AclToUseDemoMain() {}

    public static void main(String[] args) throws Exception {
        if (args.length < 1 || args.length > 2) {
            System.err.println("usage: AclToUseDemoMain input.acl [output.use]");
            System.exit(2);
        }
        AclCompiler.Result result = AclCompiler.compile(Path.of(args[0]));
        if (!result.ok()) {
            result.errors().forEach(System.err::println);
            System.exit(1);
        }
        String translated = AclUseTranslator.translate(result.model());
        if (args.length == 2) Files.writeString(Path.of(args[1]), translated, StandardCharsets.UTF_8);
        else System.out.print(translated);
    }
}
