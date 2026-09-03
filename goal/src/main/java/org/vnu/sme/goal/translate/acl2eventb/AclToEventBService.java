package org.vnu.sme.goal.translate.acl2eventb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.vnu.sme.goal.dsl.acl.parser.AclCompiler;
import org.vnu.sme.goal.translate.aclistarbpmn2eventb.EventBExportResult;
import org.vnu.sme.goal.translate.aclistarbpmn2eventb.serialize.RodinProjectWriter;

/** Compiles one ACL file and writes one ACL-only Rodin project. */
public final class AclToEventBService {
    public EventBExportResult export(AclToEventBRequest request) {
        List<String> diagnostics=new ArrayList<>();
        Path target=request.outputDirectory().resolve(AclToEventBTranslator.id(request.projectName()));
        if(!Files.isRegularFile(request.acl())) diagnostics.add("File does not exist: "+request.acl());
        else if(!request.acl().getFileName().toString().toLowerCase().endsWith(".acl"))
            diagnostics.add("Expected .acl file: "+request.acl());
        if(!diagnostics.isEmpty()) return new EventBExportResult(false,target,List.of(),diagnostics);
        try {
            AclCompiler.Result compiled=AclCompiler.compile(request.acl());
            diagnostics.addAll(compiled.errors().stream().map(x->"ACL: "+x).toList());
            if(!diagnostics.isEmpty()) return new EventBExportResult(false,target,List.of(),diagnostics);
            var project=AclToEventBTranslator.translate(request.projectName(),compiled.model(),diagnostics);
            List<Path> files=RodinProjectWriter.write(project,target,diagnostics,false);
            return new EventBExportResult(true,target,files,diagnostics);
        } catch(IOException|RuntimeException exception) {
            diagnostics.add(exception.getMessage()==null?exception.getClass().getSimpleName():exception.getMessage());
            return new EventBExportResult(false,target,List.of(),diagnostics);
        }
    }
}
