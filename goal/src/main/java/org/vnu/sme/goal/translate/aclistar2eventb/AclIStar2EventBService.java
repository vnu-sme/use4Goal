package org.vnu.sme.goal.translate.aclistar2eventb;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.vnu.sme.goal.dsl.acl.parser.AclCompiler;
import org.vnu.sme.goal.dsl.istar.parser.IStarCompiler;
import org.vnu.sme.goal.translate.aclistarbpmn2eventb.EventBExportResult;
import org.vnu.sme.goal.translate.aclistarbpmn2eventb.serialize.RodinProjectWriter;
import org.vnu.sme.goal.translate.aclistarbpmn2eventb.translate.AclIStar2EventBTranslator;
import org.vnu.sme.goal.translate.aclistarbpmn2eventb.translate.AclIStarBpmn2EventBTranslator;

public final class AclIStar2EventBService {
    public EventBExportResult export(AclIStar2EventBRequest request) {
        List<String> diagnostics=new ArrayList<>();
        check(diagnostics,request.acl(),".acl"); check(diagnostics,request.istar(),".istar");
        Path target=request.outputDirectory().resolve(AclIStarBpmn2EventBTranslator.id(request.projectName()));
        if(!diagnostics.isEmpty()) return failed(target,diagnostics);
        try {
            var acl=AclCompiler.compile(request.acl()); var istar=IStarCompiler.compile(request.istar());
            acl.errors().forEach(x->diagnostics.add("ACL: "+x));
            istar.errors().forEach(x->diagnostics.add("iStar: "+x));
            if(!diagnostics.isEmpty()) return failed(target,diagnostics);
            var project=AclIStar2EventBTranslator.translate(request.projectName(),acl.model(),istar.model(),diagnostics);
            var files=RodinProjectWriter.write(project,target,diagnostics,true);
            return new EventBExportResult(true,target,files,diagnostics);
        } catch(Exception exception) {
            diagnostics.add(exception.getMessage()==null?exception.getClass().getSimpleName():exception.getMessage());
            return failed(target,diagnostics);
        }
    }
    private static void check(List<String> errors,Path path,String extension) {
        if(!Files.isRegularFile(path)) errors.add("File does not exist: "+path);
        else if(!path.getFileName().toString().toLowerCase().endsWith(extension)) errors.add("Expected "+extension+" file: "+path);
    }
    private static EventBExportResult failed(Path target,List<String> errors) {
        return new EventBExportResult(false,target,List.of(),errors);
    }
}
