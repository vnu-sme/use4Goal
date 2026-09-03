package org.vnu.sme.goal.translate.aclistarbpmn2use;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.vnu.sme.goal.dsl.acl.parser.AclCompiler;
import org.vnu.sme.goal.dsl.bpmn.parser.BpmnCompiler;
import org.vnu.sme.goal.dsl.istar.parser.IStarCompiler;
import org.vnu.sme.goal.usegen.UseOutputWriter;

/** File-based ACL + BPMN + iStar to USE/TOCL pipeline. */
public final class AclBpmnIStarUseOclService {
    private AclBpmnIStarUseOclService() {}

    public record Result(Path outputFolder, UseOutputWriter.Written written,
                         AclBpmnIStar2UseTranslator.Result useResult,
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

    public static Result translate(Path aclPath, Path bpmnPath, Path istarPath, Path outputFolder) {
        List<String> errors = new ArrayList<>();
        var acl = compileAcl(aclPath, errors);
        if (acl == null) return new Result(outputFolder, null, null, errors);
        var bpmn = compileBpmn(bpmnPath, errors);
        if (bpmn == null) return new Result(outputFolder, null, null, errors);
        var istar = compileIStar(istarPath, errors);
        if (istar == null) return new Result(outputFolder, null, null, errors);

        var translated = AclBpmnIStar2UseTranslator.translate(acl, bpmn, istar);
        if (!translated.ok()) {
            errors.add("Translation stopped because an input model uses unsupported semantics.");
            return new Result(outputFolder, null, translated, errors);
        }

        UseOutputWriter.Written written = null;
        try {
            written = UseOutputWriter.writeToFolder(outputFolder,
                    AclBpmnIStar2UseTranslator.outputModelName(istar, bpmn),
                    translated.useText(), translated.toclText());
        } catch (IOException exception) {
            errors.add("Cannot write output folder '" + outputFolder + "': " + exception.getMessage());
        }
        return new Result(outputFolder, written, translated, List.copyOf(errors));
    }

    private static org.vnu.sme.goal.dsl.acl.mm.AclModel compileAcl(Path path, List<String> errors) {
        try {
            var result = AclCompiler.compile(path);
            if (result.ok()) return result.model();
            errors.addAll(result.errors());
        } catch (IOException exception) {
            errors.add("Cannot read ACL file '" + path + "': " + exception.getMessage());
        }
        return null;
    }

    private static org.vnu.sme.goal.dsl.bpmn.mm.BpmnModel compileBpmn(Path path, List<String> errors) {
        try {
            var result = BpmnCompiler.compile(path);
            if (result.ok()) return result.model();
            errors.addAll(result.errors());
        } catch (IOException exception) {
            errors.add("Cannot read BPMN file '" + path + "': " + exception.getMessage());
        }
        return null;
    }

    private static org.vnu.sme.goal.dsl.istar.mm.GoalModel compileIStar(Path path, List<String> errors) {
        try {
            var result = IStarCompiler.compile(path);
            if (result.ok()) return result.model();
            errors.addAll(result.errors());
        } catch (IOException exception) {
            errors.add("Cannot read iStar file '" + path + "': " + exception.getMessage());
        }
        return null;
    }

    public static void main(String[] args) {
        if (args.length != 4) {
            System.err.println("Usage: AclBpmnIStarUseOclService <model.acl> <model.bpmn2> <model.istar> <output-folder>");
            return;
        }
        Result result = translate(Path.of(args[0]), Path.of(args[1]), Path.of(args[2]), Path.of(args[3]));
        result.allDiagnostics().forEach(System.err::println);
        if (!result.ok()) throw new IllegalStateException("ACL + BPMN + iStar to USE/TOCL translation failed");
        System.out.println(result.written().useFile());
        if (result.written().toclFile() != null) System.out.println(result.written().toclFile());
    }
}
