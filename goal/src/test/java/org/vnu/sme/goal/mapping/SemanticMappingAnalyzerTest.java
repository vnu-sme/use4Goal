package org.vnu.sme.goal.mapping;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.vnu.sme.goal.analysis.mapping.SemanticMappingAnalyzer;
import org.vnu.sme.goal.dsl.acl.parser.AclCompiler;
import org.vnu.sme.goal.dsl.acl.mm.AclAttribute;
import org.vnu.sme.goal.dsl.acl.mm.AclModel;
import org.vnu.sme.goal.dsl.acl.mm.AclPrimitiveType;
import org.vnu.sme.goal.dsl.acl.mm.AclRole;
import org.vnu.sme.goal.dsl.bpmn.mm.ActivityConstraint;
import org.vnu.sme.goal.dsl.bpmn.mm.BpmnModel;
import org.vnu.sme.goal.dsl.bpmn.mm.Lane;
import org.vnu.sme.goal.dsl.bpmn.parser.BpmnCompiler;
import org.vnu.sme.goal.dsl.istar.mm.AndRefinement;
import org.vnu.sme.goal.dsl.istar.mm.Goal;
import org.vnu.sme.goal.dsl.istar.mm.GoalModel;
import org.vnu.sme.goal.dsl.istar.mm.GoalType;
import org.vnu.sme.goal.dsl.istar.mm.IStarOclConstraint;
import org.vnu.sme.goal.dsl.istar.mm.Role;
import org.vnu.sme.goal.dsl.istar.parser.IStarCompiler;
import org.vnu.sme.goal.translate.aclistarbpmn2eventb.EventBExportRequest;
import org.vnu.sme.goal.translate.aclistarbpmn2eventb.EventBExportService;

class SemanticMappingAnalyzerTest {
    private static final Path EXAMPLES=Path.of("src/main/resources/examples/mtg");
    @TempDir Path output;

    @Test void generatesMappingsCoverageAndOrphanDiagnosticsWithoutATrace() throws Exception {
        var acl=AclCompiler.compile(EXAMPLES.resolve("mtg_old.acl"));
        var istar=IStarCompiler.compile(EXAMPLES.resolve("mtg_old.istar"));
        var bpmn=BpmnCompiler.compile(EXAMPLES.resolve("mtg_old.bpmn2"));
        assertTrue(acl.ok(),()->String.join("\n",acl.errors()));
        assertTrue(istar.ok(),()->String.join("\n",istar.errors()));
        assertTrue(bpmn.ok(),()->String.join("\n",bpmn.errors()));

        var result=SemanticMappingAnalyzer.analyze(acl.model(),istar.model(),bpmn.model());
        assertFalse(result.mappings().isEmpty());
        assertTrue(result.mappings().stream().anyMatch(x->x.activityIds().contains("decideMeetingDetails")
                && x.taskId().equals("DecideDetails")),result.mappings().toString());
        assertTrue(result.mappings().stream().anyMatch(x->x.activityIds().contains("chooseTimeAndDate")
                && x.taskId().equals("ChooseMeetingTime")),result.mappings().toString());
        assertTrue(result.taskCoverage().stream().anyMatch(x->x.taskId().equals("DecideDetails")
                && x.status().name().equals("COVERED")));
        assertEquals(bpmn.model().processes().stream().mapToInt(p->(int)p.flowElements().stream()
                .filter(org.vnu.sme.goal.dsl.bpmn.mm.Activity.class::isInstance).count()).sum(),
                result.activityCoverage().size());
    }

    @Test void eventBExportIncludesMappingReportsAndProofObligations() throws Exception {
        var export=new EventBExportService().export(new EventBExportRequest(
                EXAMPLES.resolve("mtg_old.acl"),EXAMPLES.resolve("mtg_old.istar"),EXAMPLES.resolve("mtg_old.bpmn2"),
                output,"MappingEventB"));
        assertTrue(export.success(),()->String.join("\n",export.diagnostics()));
        Path report=export.projectDirectory().resolve("MappingEventB_mapping.md");
        Path csv=export.projectDirectory().resolve("MappingEventB_mapping.csv");
        assertTrue(Files.isRegularFile(report)); assertTrue(Files.isRegularFile(csv));
        String reportText=Files.readString(report),machine=Files.readString(
                export.projectDirectory().resolve("MappingEventB_machine.bum"));
        assertTrue(reportText.contains("decideMeetingDetails"));
        assertTrue(reportText.contains("DecideDetails"));
        assertTrue(reportText.contains("PENDING:"),reportText);
        assertTrue(machine.contains("org.eventb.core.theorem=\"true\""),machine);
        assertTrue(Files.readString(export.projectDirectory().resolve("MappingEventB_properties.ltl"))
                .contains("MAPPING_SOUNDNESS"));
    }

    @Test void reportsUnmappedTasksUncoveredRootsAndOrphanActivities() {
        var attributes=List.of(
                new AclAttribute("done",AclPrimitiveType.BOOLEAN,false,true,Optional.empty()),
                new AclAttribute("missing",AclPrimitiveType.BOOLEAN,false,true,Optional.empty()),
                new AclAttribute("auditNoise",AclPrimitiveType.BOOLEAN,false,true,Optional.empty()));
        var acl=new AclModel("3.0","DiagnosticAcl",List.of(),List.of(),
                List.of(new AclRole("Worker",List.of(),attributes)),List.of(),List.of(),List.of(),List.of(),List.of());

        Goal root=new Goal("Root",GoalType.ACHIEVE), missingRoot=new Goal("MissingRoot",GoalType.ACHIEVE);
        var required=new org.vnu.sme.goal.dsl.istar.mm.Task("Required",null,
                new IStarOclConstraint("self.done"));
        var missing=new org.vnu.sme.goal.dsl.istar.mm.Task("Missing",null,
                new IStarOclConstraint("self.missing"));
        var actor=new Role("Worker",List.of(root,required,missingRoot,missing),
                List.of(new AndRefinement("Root",List.of("Required")),
                        new AndRefinement("MissingRoot",List.of("Missing"))),
                List.of(),List.of(),List.of(),List.of());
        var goals=new GoalModel("DiagnosticGoals");goals.addActor(actor);

        var work=new org.vnu.sme.goal.dsl.bpmn.mm.Task("doWork","Do work",
                List.of(new ActivityConstraint(ActivityConstraint.Kind.POST,"self.done")));
        var orphan=new org.vnu.sme.goal.dsl.bpmn.mm.Task("writeAuditNoise","Write audit noise",
                List.of(new ActivityConstraint(ActivityConstraint.Kind.POST,"self.auditNoise")));
        var lane=new Lane("Worker","Worker",List.of(work,orphan));
        var process=new org.vnu.sme.goal.dsl.bpmn.mm.Process("Work",null,null,List.of(lane),
                List.of(work,orphan),List.of());
        var bpmn=new BpmnModel("DiagnosticBpmn");bpmn.addProcess(process);

        var result=SemanticMappingAnalyzer.analyze(acl,goals,bpmn);
        assertTrue(result.diagnostics().stream().anyMatch(x->x.code().equals("UNMAPPED_TASK")
                &&x.elementId().equals("Missing")),result.diagnostics().toString());
        assertTrue(result.diagnostics().stream().anyMatch(x->x.code().equals("UNCOVERED_ROOT_GOAL")
                &&x.elementId().equals("MissingRoot")),result.diagnostics().toString());
        assertTrue(result.diagnostics().stream().anyMatch(x->x.code().equals("ORPHAN_ACTIVITY")
                &&x.elementId().equals("writeAuditNoise")),result.diagnostics().toString());
    }

    @Test void comparesARecomputedMappingWithAPreviousCsv() throws Exception {
        var first=new EventBExportService().export(new EventBExportRequest(
                EXAMPLES.resolve("mtg_old.acl"),EXAMPLES.resolve("mtg_old.istar"),EXAMPLES.resolve("mtg_old.bpmn2"),
                output,"FirstMapping"));
        assertTrue(first.success(),()->String.join("\n",first.diagnostics()));
        Path previous=first.projectDirectory().resolve("FirstMapping_mapping.csv");
        var second=new EventBExportService().export(new EventBExportRequest(
                EXAMPLES.resolve("mtg_old.acl"),EXAMPLES.resolve("mtg_old.istar"),EXAMPLES.resolve("mtg_old.bpmn2"),
                output,"SecondMapping",previous));
        assertTrue(second.success(),()->String.join("\n",second.diagnostics()));
        String diff=Files.readString(second.projectDirectory().resolve("SecondMapping_mapping_diff.md"));
        assertTrue(diff.contains("RETAINED"),diff);
        assertFalse(diff.contains("|NEW|"),diff);
        assertFalse(diff.contains("|REMOVED|"),diff);
    }
}
