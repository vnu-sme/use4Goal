package org.vnu.sme.goal.trace.bpmn;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.tzi.use.parser.use.USECompiler;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.ModelFactory;
import org.vnu.sme.goal.dsl.bpmn.mm.BpmnModel;
import org.vnu.sme.goal.dsl.bpmn.parser.BpmnCompiler;
import org.vnu.sme.goal.trace.bpmn.BpmnOclConstraintCompiler.ConstraintInfo;

/**
 * End-to-end compiler for BPMN2 Option 2 OCL inputs:
 * BPMN2 model + USE domain model + BPMN-OCL context map.
 */
public final class BpmnOclValidationCompiler {

    public record Result(
            BpmnModel bpmnModel,
            MModel useModel,
            Map<String, String> contextTypes,
            Map<String, ConstraintInfo> constraints,
            List<String> errors) {
        public boolean ok() { return errors.isEmpty(); }
    }

    private BpmnOclValidationCompiler() {}

    public static Result compile(Path bpmnFile, Path useFile, Path contextMapFile) throws IOException {
        List<String> errors = new ArrayList<>();

        BpmnCompiler.Result bpmnResult = BpmnCompiler.compile(bpmnFile);
        if (!bpmnResult.ok()) {
            errors.addAll(bpmnResult.errors());
            return new Result(null, null, Map.of(), Map.of(), errors);
        }
        BpmnModel bpmnModel = bpmnResult.model();

        StringWriter useErrBuf = new StringWriter();
        MModel useModel = USECompiler.compileSpecification(
                Files.readString(useFile), useFile.toString(), new PrintWriter(useErrBuf), new ModelFactory());
        if (useModel == null) {
            errors.add("errors in '" + useFile + "': " + useErrBuf);
            return new Result(bpmnModel, null, Map.of(), Map.of(), errors);
        }

        Map<String, String> contextTypes;
        try {
            contextTypes = BpmnOclContextMapParser.parse(contextMapFile);
        } catch (IllegalArgumentException ex) {
            errors.add(ex.getMessage());
            return new Result(bpmnModel, useModel, Map.of(), Map.of(), errors);
        }

        BpmnOclConstraintCompiler.Result oclResult =
                BpmnOclConstraintCompiler.compile(bpmnModel, useModel, contextTypes);
        if (!oclResult.ok()) {
            errors.addAll(oclResult.errors());
        }

        return new Result(bpmnModel, useModel, contextTypes, oclResult.constraints(), errors);
    }
}
