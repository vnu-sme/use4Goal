package org.vnu.sme.goal.translate.aclistar2use;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.vnu.sme.goal.dsl.acl.mm.AclModel;
import org.vnu.sme.goal.dsl.acl.parser.AclCompiler;
import org.vnu.sme.goal.dsl.istar.mm.GoalModel;
import org.vnu.sme.goal.dsl.istar.parser.IStarCompiler;

/**
 * Service layer that orchestrates the full ACL + iStar → USE + TOCL pipeline.
 *
 * <p>Responsibilities:
 * <ol>
 *   <li>Parse the {@code .acl} source → {@link AclModel}.</li>
 *   <li>Parse the {@code .istar} source → {@link GoalModel}.</li>
 *   <li>Delegate to {@link AclIStar2UseTranslator}, which produces plain-OCL
 *       {@code .use} text (class diagram + derived Goal/Task query operations)
 *       and separate TOCL text (temporal goal properties).</li>
 *   <li>Write the {@code .use} text to the requested output file, and the TOCL
 *       text to a companion {@code .tocl} file next to it (same basename) --
 *       kept apart because {@code always}/{@code sometime}
 *       are not core OCL keywords, so a plain USE compile of the {@code .use}
 *       file must never contain them.</li>
 * </ol>
 *
 * <p>This class is intentionally stateless so it can be called from Swing
 * event-dispatch threads via a {@code SwingWorker} without holding mutable state.
 */
public final class IStarUseOclService {

    private IStarUseOclService() {}

    /** Immutable result of one translation run. */
    public record Result(
            Path outputFile,
            Path toclFile,
            AclIStar2UseTranslator.Result useResult,
            List<String> errors) {

        public boolean ok() {
            return errors.isEmpty() && useResult != null && useResult.ok();
        }

        /** Combined diagnostics: I/O errors + translator warnings. */
        public List<String> allDiagnostics() {
            List<String> all = new ArrayList<>(errors);
            if (useResult != null) all.addAll(useResult.diagnostics());
            return all;
        }
    }

    /**
     * Run the full pipeline and write the {@code .use} file plus its companion
     * {@code .tocl} file.
     *
     * @param aclPath    path to the {@code .acl} source file
     * @param istarPath  path to the {@code .istar} source file
     * @param outputFile path of the {@code .use} file to write (including filename);
     *                    the {@code .tocl} file is written next to it under the same basename
     * @return {@link Result} — always non-null; check {@link Result#ok()} for errors
     */
    public static Result translate(Path aclPath, Path istarPath, Path outputFile) {
        List<String> errors = new ArrayList<>();

        // ── 1. Parse ACL ───────────────────────────────────────────────────────
        Path toclFile = toclSibling(outputFile);

        AclCompiler.Result aclResult;
        try {
            aclResult = AclCompiler.compile(aclPath);
        } catch (IOException ex) {
            errors.add("Cannot read ACL file '" + aclPath + "': " + ex.getMessage());
            return new Result(outputFile, toclFile, null, errors);
        }
        if (!aclResult.ok()) {
            errors.addAll(aclResult.errors());
            return new Result(outputFile, toclFile, null, errors);
        }
        AclModel acl = aclResult.model();

        // ── 2. Parse iStar ─────────────────────────────────────────────────────
        IStarCompiler.Result istarResult;
        try {
            istarResult = IStarCompiler.compile(istarPath);
        } catch (IOException ex) {
            errors.add("Cannot read iStar file '" + istarPath + "': " + ex.getMessage());
            return new Result(outputFile, toclFile, null, errors);
        }
        if (!istarResult.ok()) {
            errors.addAll(istarResult.errors());
            return new Result(outputFile, toclFile, null, errors);
        }
        GoalModel gm = istarResult.model();

        // ── 3. Translate → USE text (class diagram + OCL) + TOCL text ──────────
        AclIStar2UseTranslator.Result useResult = AclIStar2UseTranslator.translate(acl, gm);
        if (!useResult.ok()) {
            errors.add("Translation stopped because the iStar model uses unsupported semantics.");
            return new Result(outputFile, toclFile, useResult, errors);
        }

        // ── 4. Write the two output files ────────────────────────────────────
        try {
            if (outputFile.getParent() != null) Files.createDirectories(outputFile.getParent());
            Files.writeString(outputFile, useResult.useText(), StandardCharsets.UTF_8);
            Files.writeString(toclFile, useResult.toclText(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            errors.add("Cannot write output file: " + ex.getMessage());
        }

        return new Result(outputFile, toclFile, useResult, errors);
    }

    /** Companion {@code .tocl} path for a given {@code .use} output path (same
     *  basename, next to it) -- see {@link AclIStar2UseTranslator.Result} for why
     *  TOCL text cannot be embedded in the {@code .use} file itself. */
    private static Path toclSibling(Path useFile) {
        String name = useFile.getFileName().toString();
        String base = name.endsWith(".use") ? name.substring(0, name.length() - 4) : name;
        return useFile.resolveSibling(base + ".tocl");
    }
}
