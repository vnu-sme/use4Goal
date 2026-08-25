package org.vnu.sme.goal.translate.aclbpmn2use;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.vnu.sme.goal.dsl.acl.mm.AclModel;
import org.vnu.sme.goal.dsl.acl.parser.AclCompiler;
import org.vnu.sme.goal.dsl.bpmn.mm.BpmnModel;
import org.vnu.sme.goal.dsl.bpmn.parser.BpmnCompiler;
import org.vnu.sme.goal.usegen.UseOutputWriter;

/**
 * Service layer that orchestrates the ACL + BPMN → USE/OCL pipeline, writing
 * into a caller-chosen output FOLDER (auto-named {@code <model>.use} /
 * {@code <model>.tocl}) -- same shape as {@link
 * org.vnu.sme.goal.translate.aclistar2use.IStarUseOclFolderService}, but for BPMN via
 * {@link AclBpmn2UseTranslator}.
 */
public final class BpmnUseOclService {

    private BpmnUseOclService() {}

    /** Command-line entry point mirroring the GUI action. */
    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Usage: BpmnUseOclService <model.acl> <model.bpmn2> <output-folder>");
            return;
        }
        Result result = translate(Path.of(args[0]), Path.of(args[1]), Path.of(args[2]));
        result.allDiagnostics().forEach(System.err::println);
        if (!result.ok()) {
            throw new IllegalStateException("ACL + BPMN to USE translation failed");
        }
        if (result.written() != null) {
            System.out.println(result.written().useFile());
            if (result.written().toclFile() != null) {
                System.out.println(result.written().toclFile());
            }
        }
    }

    public record Result(
            Path outputFolder,
            UseOutputWriter.Written written,
            AclBpmn2UseTranslator.Result useResult,
            List<String> errors) {

        public boolean ok() {
            return errors.isEmpty() && useResult != null && useResult.ok();
        }

        public List<String> allDiagnostics() {
            List<String> all = new ArrayList<>(errors);
            if (useResult != null) all.addAll(useResult.diagnostics());
            return all;
        }
    }

    public static Result translate(Path aclPath, Path bpmnPath, Path outputFolder) {
        List<String> errors = new ArrayList<>();

        AclCompiler.Result aclResult;
        try {
            aclResult = AclCompiler.compile(aclPath);
        } catch (IOException ex) {
            errors.add("Cannot read ACL file '" + aclPath + "': " + ex.getMessage());
            return new Result(outputFolder, null, null, errors);
        }
        if (!aclResult.ok()) {
            errors.addAll(aclResult.errors());
            return new Result(outputFolder, null, null, errors);
        }
        AclModel acl = aclResult.model();

        BpmnCompiler.Result bpmnResult;
        try {
            bpmnResult = BpmnCompiler.compile(bpmnPath);
        } catch (IOException ex) {
            errors.add("Cannot read BPMN file '" + bpmnPath + "': " + ex.getMessage());
            return new Result(outputFolder, null, null, errors);
        }
        if (!bpmnResult.ok()) {
            errors.addAll(bpmnResult.errors());
            return new Result(outputFolder, null, null, errors);
        }
        BpmnModel bpmn = bpmnResult.model();

        AclBpmn2UseTranslator.Result useResult = AclBpmn2UseTranslator.translate(acl, bpmn);

        UseOutputWriter.Written written = null;
        try {
            String modelName = AclBpmn2UseTranslator.sanitize(bpmn.name()) + "_Verification";
            written = UseOutputWriter.writeToFolder(outputFolder, modelName,
                    useResult.useText(), useResult.toclText());
        } catch (IOException ex) {
            errors.add("Cannot write output folder '" + outputFolder + "': " + ex.getMessage());
        }

        return new Result(outputFolder, written, useResult, errors);
    }
}
