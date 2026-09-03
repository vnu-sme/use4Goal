package org.vnu.sme.goal.feature.openaol;

import java.nio.file.Path;

import org.vnu.sme.goal.dsl.aol.parser.AolCompiler;
import org.vnu.sme.goal.dsl.aol.view.AolSpecText;

public final class AolDemoMain {

    private AolDemoMain() {}

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("usage: AolDemoMain input.aol");
            System.exit(2);
        }
        AolCompiler.Result result = AolCompiler.compile(Path.of(args[0]));
        if (!result.ok()) {
            result.errors().forEach(System.err::println);
            System.exit(1);
        }
        System.out.print(AolSpecText.render(result.model()));
    }
}
