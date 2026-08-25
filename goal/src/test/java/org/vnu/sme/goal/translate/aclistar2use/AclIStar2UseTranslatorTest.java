package org.vnu.sme.goal.translate.aclistar2use;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import org.vnu.sme.goal.translate.aclbpmn2use.AclBpmn2UseTranslator;

class AclIStar2UseTranslatorTest {

    private static final Path CLASSROOM = Path.of("src/main/resources/examples/classroom");

    @Test
    void translatesClassAttachedGoalAndTaskSemanticsIntoUseAndTocl() throws Exception {
        var acl = AclCompiler.compile(CLASSROOM.resolve("classroom.acl"));
        var istar = IStarCompiler.compile(CLASSROOM.resolve("classroom.istar"));
        assertTrue(acl.ok(), () -> String.join("\n", acl.errors()));
        assertTrue(istar.ok(), () -> String.join("\n", istar.errors()));

        var translated = AclIStar2UseTranslator.translate(acl.model(), istar.model());
        String use = translated.useText();
        String tocl = translated.toclText();

        assertTrue(translated.diagnostics().isEmpty(),
                () -> String.join("\n", translated.diagnostics()));
        assertTrue(use.contains("class Agent\nattributes\n  id : Integer"));
        assertTrue(use.contains("class Teacher\nattributes\n  id : Integer"));
        assertTrue(use.contains("ClassCompleted_condition() : Boolean =\n"
                + "    self.LessonDelivered_condition() and self.AttendanceSummaryRecorded_condition()"));
        assertTrue(use.contains("AttendanceSummaryRecorded_condition() : Boolean =\n"
                + "    self.RecordAttendanceManually_postHolds() or "
                + "self.RecordAttendanceElectronically_postHolds()"));
        assertTrue(use.contains("RecordAttendanceElectronically_preHolds() : Boolean"));
        assertTrue(use.contains("RecordAttendanceElectronically_postHolds() : Boolean"));
        assertTrue(use.contains("ParticipatesInClass_condition() : Boolean =\n"
                + "    self.AttendanceMarked_condition() and self.PresentForLesson_condition()"));
        assertFalse(use.contains("_activation()"));
        assertFalse(use.contains("_localCondition()"));
        assertFalse(use.contains("_holds()"));
        assertFalse(use.contains("Refinement structural invariants"));
        assertTrue(tocl.contains("sometime self.ClassCompleted_condition()"));
        assertTrue(tocl.contains("sometime self.ParticipatesInClass_condition()"));
        assertFalse(tocl.contains("alwaysPast"));
        assertUseCompiles(use);
    }

    @Test
    void fileServiceReadsAclAndIStarAndWritesGeneratedArtifacts(@TempDir Path output) {
        var result = IStarUseOclFolderService.translate(
                CLASSROOM.resolve("classroom.acl"),
                CLASSROOM.resolve("classroom.istar"),
                output);

        assertTrue(result.ok(), () -> String.join("\n", result.allDiagnostics()));
        assertNotNull(result.written());
        assertTrue(Files.isRegularFile(result.written().useFile()));
        assertTrue(Files.isRegularFile(result.written().toclFile()));
    }

    @Test
    void fileServiceRejectsLegacyForallAndPickInsteadOfApproximatingThem(@TempDir Path output) {
        Path mtg = Path.of("src/main/resources/examples/mtg");
        var result = IStarUseOclFolderService.translate(
                mtg.resolve("mtg_old.acl"),
                mtg.resolve("mtg_old.istar"),
                output);

        assertFalse(result.ok());
        assertTrue(result.allDiagnostics().stream().anyMatch(message ->
                message.contains("uses forall/pick")));
        assertNull(result.written());
    }

    @Test
    void classroomBpmnUsesTheSameAclAndCompilesToUse() throws Exception {
        var acl = AclCompiler.compile(CLASSROOM.resolve("classroom.acl"));
        var bpmn = BpmnCompiler.compile(CLASSROOM.resolve("classroom.bpmn2"));
        assertTrue(acl.ok(), () -> String.join("\n", acl.errors()));
        assertTrue(bpmn.ok(), () -> String.join("\n", bpmn.errors()));

        var translated = AclBpmn2UseTranslator.translate(acl.model(), bpmn.model());
        assertUseCompiles(translated.useText());
    }

    private static void assertUseCompiles(String use) {
        StringWriter errors = new StringWriter();
        var model = USECompiler.compileSpecification(use, "generated-classroom.use",
                new PrintWriter(errors), new ModelFactory());
        assertNotNull(model, () -> errors + "\n" + use);
    }
}
