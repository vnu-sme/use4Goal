package org.vnu.sme.goal.feature.exportacltoeventb;

import java.nio.file.Path;

import org.vnu.sme.goal.translate.acl2eventb.AclToEventBRequest;
import org.vnu.sme.goal.translate.acl2eventb.AclToEventBService;

/** Headless entry point: model.acl output-directory project-name. */
public final class Acl2EventBMain {
    private Acl2EventBMain() {}
    public static void main(String[] args) {
        if(args.length!=3) {
            System.err.println("Usage: Acl2EventBMain <model.acl> <output-dir> <project-name>");
            System.exit(2);
        }
        var result=new AclToEventBService().export(new AclToEventBRequest(
                Path.of(args[0]),Path.of(args[1]),args[2]));
        result.diagnostics().forEach(System.err::println);
        if(!result.success()) System.exit(1);
        result.generatedFiles().forEach(System.out::println);
    }
}
