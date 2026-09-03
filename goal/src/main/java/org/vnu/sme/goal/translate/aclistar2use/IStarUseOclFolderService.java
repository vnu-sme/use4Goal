package org.vnu.sme.goal.translate.aclistar2use;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.vnu.sme.goal.dsl.acl.mm.AclModel;
import org.vnu.sme.goal.dsl.acl.parser.AclCompiler;
import org.vnu.sme.goal.dsl.istar.mm.GoalModel;
import org.vnu.sme.goal.dsl.istar.parser.IStarCompiler;
import org.vnu.sme.goal.usegen.UseOutputWriter;

/**
 * File-based ACL + iStar → USE + TOCL pipeline. The two input models are
 * read from caller-supplied files and the generated artifacts are written into
 * a caller-supplied folder as {@code <iStar-model>_Verification.use} and
 * {@code <iStar-model>_Verification.tocl}.
 */
public final class IStarUseOclFolderService {

    private IStarUseOclFolderService() {}

    /** Command-line entry point mirroring the USE plugin action. */
    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Usage: IStarUseOclFolderService <model.acl> <model.istar> <output-folder>");
            return;
        }
        Result result = translate(Path.of(args[0]), Path.of(args[1]), Path.of(args[2]));
        result.allDiagnostics().forEach(System.err::println);
        if (!result.ok()) {
            throw new IllegalStateException("ACL + iStar to USE/TOCL translation failed");
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
            AclIStar2UseTranslator.Result useResult,
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

    public static Result translate(Path aclPath, Path istarPath, Path outputFolder) {
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

        IStarCompiler.Result istarResult;
        try {
            istarResult = IStarCompiler.compile(istarPath);
        } catch (IOException ex) {
            errors.add("Cannot read iStar file '" + istarPath + "': " + ex.getMessage());
            return new Result(outputFolder, null, null, errors);
        }
        if (!istarResult.ok()) {
            errors.addAll(istarResult.errors());
            return new Result(outputFolder, null, null, errors);
        }
        GoalModel gm = istarResult.model();

        AclIStar2UseTranslator.Result useResult = AclIStar2UseTranslator.translate(acl, gm);
        if (!useResult.ok()) {
            errors.add("Translation stopped because the iStar model uses unsupported semantics.");
            return new Result(outputFolder, null, useResult, errors);
        }

        UseOutputWriter.Written written = null;
        try {
            String modelName = AclIStar2UseTranslator.sanitize(gm.getName()) + "_Verification";
            written = UseOutputWriter.writeToFolder(outputFolder, modelName,
                    useResult.useText(), useResult.toclText());
        } catch (IOException ex) {
            errors.add("Cannot write output folder '" + outputFolder + "': " + ex.getMessage());
        }

        return new Result(outputFolder, written, useResult, errors);
    }
}
