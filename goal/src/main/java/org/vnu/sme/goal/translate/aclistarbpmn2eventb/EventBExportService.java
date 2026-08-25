package org.vnu.sme.goal.translate.aclistarbpmn2eventb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.vnu.sme.goal.dsl.acl.parser.AclCompiler;
import org.vnu.sme.goal.dsl.bpmn.parser.BpmnCompiler;
import org.vnu.sme.goal.analysis.mapping.SemanticMappingAnalyzer;
import org.vnu.sme.goal.analysis.mapping.SemanticMappingReportWriter;
import org.vnu.sme.goal.translate.aclistarbpmn2eventb.ir.EventBProject;
import org.vnu.sme.goal.translate.aclistarbpmn2eventb.serialize.RodinProjectWriter;
import org.vnu.sme.goal.translate.aclistarbpmn2eventb.translate.AclIStarBpmn2EventBTranslator;
import org.vnu.sme.goal.translate.aclistarbpmn2eventb.translate.MappingObligationCompiler;
import org.vnu.sme.goal.dsl.istar.parser.IStarCompiler;

/** Public application service used by the GUI, CLI, and tests. */
public final class EventBExportService {
    public EventBExportResult export(EventBExportRequest request) {
        List<String> errors = validateRequest(request);
        if (!errors.isEmpty()) return failed(request, errors);
        try {
            AclCompiler.Result acl = AclCompiler.compile(request.acl());
            IStarCompiler.Result istar = IStarCompiler.compile(request.istar());
            BpmnCompiler.Result bpmn = BpmnCompiler.compile(request.bpmn());
            errors.addAll(prefix("ACL", acl.errors()));
            errors.addAll(prefix("iStar", istar.errors()));
            errors.addAll(prefix("BPMN", bpmn.errors()));
            if (!errors.isEmpty()) return failed(request, errors);
            var mapping = SemanticMappingAnalyzer.analyze(acl.model(), istar.model(), bpmn.model());
            EventBProject project = AclIStarBpmn2EventBTranslator.translate(request.projectName(), acl.model(), istar.model(), bpmn.model(), errors);
            var verifiedMapping = MappingObligationCompiler.compile(project, mapping, acl.model(), istar.model(), bpmn.model());
            project = verifiedMapping.project();
            Path target = request.outputDirectory().resolve(AclIStarBpmn2EventBTranslator.id(request.projectName()));
            List<Path> files = new ArrayList<>(RodinProjectWriter.write(project, target, errors));
            files.addAll(SemanticMappingReportWriter.write(mapping, verifiedMapping.plan(), target,
                    AclIStarBpmn2EventBTranslator.id(request.projectName()), request.previousMapping()));
            return new EventBExportResult(true, target, files, errors);
        } catch (IOException | RuntimeException exception) {
            errors.add(exception.getMessage() == null ? exception.getClass().getSimpleName() : exception.getMessage());
            return failed(request, errors);
        }
    }

    private static List<String> validateRequest(EventBExportRequest r) {
        List<String> e = new ArrayList<>();
        check(e, r.acl(), ".acl"); check(e, r.istar(), ".istar"); check(e, r.bpmn(), ".bpmn2");
        return e;
    }
    private static void check(List<String> errors, Path path, String extension) {
        if (!Files.isRegularFile(path)) errors.add("File does not exist: " + path);
        else if (!path.getFileName().toString().toLowerCase().endsWith(extension)) errors.add("Expected " + extension + " file: " + path);
    }
    private static List<String> prefix(String prefix, List<String> messages) { return messages.stream().map(x -> prefix + ": " + x).toList(); }
    private static EventBExportResult failed(EventBExportRequest r, List<String> errors) { return new EventBExportResult(false, r.outputDirectory().resolve(AclIStarBpmn2EventBTranslator.id(r.projectName())), List.of(), errors); }
}
