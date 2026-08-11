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
 * Same ACL + iStar → USE + TOCL pipeline as {@link IStarUseOclService}, but
 * writing into a caller-chosen output FOLDER (auto-named {@code <model>.use}
 * / {@code <model>.tocl}) instead of an exact file path the caller types out.
 * {@link IStarUseOclService} (and its action/GUI) is left untouched; this is
 * a separate action with the same underlying translator ({@link
 * AclIStar2UseTranslator}, unchanged) and the shared {@link UseOutputWriter}.
 */
public final class IStarUseOclFolderService {

    private IStarUseOclFolderService() {}

    public record Result(
            Path outputFolder,
            UseOutputWriter.Written written,
            AclIStar2UseTranslator.Result useResult,
            List<String> errors) {

        public boolean ok() { return errors.isEmpty(); }

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
