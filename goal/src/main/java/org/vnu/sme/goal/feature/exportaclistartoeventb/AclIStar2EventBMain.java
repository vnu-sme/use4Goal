package org.vnu.sme.goal.feature.exportaclistartoeventb;

import java.nio.file.Path;
import org.vnu.sme.goal.translate.aclistar2eventb.*;

public final class AclIStar2EventBMain {
    private AclIStar2EventBMain() {}
    public static void main(String[] args) {
        if(args.length!=4) { System.err.println("Usage: AclIStar2EventBMain <model.acl> <model.istar> <output-dir> <project-name>"); System.exit(2); }
        var result=new AclIStar2EventBService().export(new AclIStar2EventBRequest(
                Path.of(args[0]),Path.of(args[1]),Path.of(args[2]),args[3]));
        result.diagnostics().forEach(System.err::println);
        if(!result.success()) System.exit(1); result.generatedFiles().forEach(System.out::println);
    }
}
