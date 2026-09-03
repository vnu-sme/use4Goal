package org.vnu.sme.goal.feature.exportaclbpmntoeventb;

import java.nio.file.Path;
import org.vnu.sme.goal.translate.aclbpmn2eventb.*;

public final class AclBpmn2EventBMain {
    private AclBpmn2EventBMain() {}
    public static void main(String[] args) {
        if(args.length!=4) { System.err.println("Usage: AclBpmn2EventBMain <model.acl> <model.bpmn2> <output-dir> <project-name>"); System.exit(2); }
        var result=new AclBpmn2EventBService().export(new AclBpmn2EventBRequest(
                Path.of(args[0]),Path.of(args[1]),Path.of(args[2]),args[3]));
        result.diagnostics().forEach(System.err::println);
        if(!result.success()) System.exit(1); result.generatedFiles().forEach(System.out::println);
    }
}
