package org.vnu.sme.goal.translate.aclistarbpmn2use;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.tzi.use.parser.use.USECompiler;
import org.tzi.use.uml.mm.ModelFactory;
import org.vnu.sme.goal.dsl.acl.parser.AclCompiler;
import org.vnu.sme.goal.dsl.bpmn.parser.BpmnCompiler;
import org.vnu.sme.goal.dsl.istar.parser.IStarCompiler;

class AclBpmnIStar2UseTranslatorTest {
    private static final Path CLASSROOM = Path.of("src/main/resources/examples/classroom");

    @Test
    void combinesStructureGoalsAndProcessIntoOneCompilableUseModel() throws Exception {
        var acl = AclCompiler.compile(CLASSROOM.resolve("classroom.acl"));
        var bpmn = BpmnCompiler.compile(CLASSROOM.resolve("classroom.bpmn2"));
        var istar = IStarCompiler.compile(CLASSROOM.resolve("classroom.istar"));
        assertTrue(acl.ok(), () -> String.join("\n", acl.errors()));
        assertTrue(bpmn.ok(), () -> String.join("\n", bpmn.errors()));
        assertTrue(istar.ok(), () -> String.join("\n", istar.errors()));

        var result = AclBpmnIStar2UseTranslator.translate(
                acl.model(), bpmn.model(), istar.model());
        assertTrue(result.ok(), () -> String.join("\n", result.diagnostics()));
        assertTrue(result.useText().contains("class Teacher\nattributes\n  id : Integer"));
        assertTrue(result.useText().contains("ClassCompleted_condition() : Boolean"));
        assertTrue(result.useText().contains("  beginLesson()"));
        assertTrue(result.useText().contains("context Teacher::beginLesson()"));
        assertTrue(result.toclText().contains("sometime self.ClassCompleted_condition()"));
        assertTrue(result.toclText().contains("inv BPMN_TeachingSession_start_classroomReady_occurs:"));
        assertTrue(result.toclText().contains("sometime isCalled(classroomReady())"));
        assertTrue(result.toclText().contains("inv BPMN_TeachingSession_before_beginLesson:"));
        assertUseCompiles(result.useText());
    }

    @Test
    void fileServiceWritesBothArtifacts(@TempDir Path output) {
        var result = AclBpmnIStarUseOclService.translate(
                CLASSROOM.resolve("classroom.acl"),
                CLASSROOM.resolve("classroom.bpmn2"),
                CLASSROOM.resolve("classroom.istar"), output);
        assertTrue(result.ok(), () -> String.join("\n", result.allDiagnostics()));
        assertNotNull(result.written());
        assertTrue(Files.isRegularFile(result.written().useFile()));
        assertTrue(Files.isRegularFile(result.written().toclFile()));
    }

    private static void assertUseCompiles(String use) {
        StringWriter errors = new StringWriter();
        var model = USECompiler.compileSpecification(use, "generated-all.use",
                new PrintWriter(errors), new ModelFactory());
        assertNotNull(model, () -> errors + "\n" + use);
    }
}
