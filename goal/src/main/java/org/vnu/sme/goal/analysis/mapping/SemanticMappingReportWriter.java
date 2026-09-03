package org.vnu.sme.goal.analysis.mapping;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.vnu.sme.goal.analysis.mapping.MappingVerificationPlan.GenerationStatus;

/** Writes the human- and machine-readable mapping, coverage, diagnostics, and evolution reports. */
public final class SemanticMappingReportWriter {
    private SemanticMappingReportWriter() {}

    public static List<Path> write(SemanticMappingAnalysis analysis, MappingVerificationPlan verification,
                                   Path projectDirectory, String projectName, Path previousMapping) throws IOException {
        Path markdown=projectDirectory.resolve(projectName+"_mapping.md");
        Path csv=projectDirectory.resolve(projectName+"_mapping.csv");
        Files.writeString(markdown,markdown(analysis,verification),StandardCharsets.UTF_8);
        Files.writeString(csv,csv(analysis,verification),StandardCharsets.UTF_8);
        List<Path> result=new ArrayList<>(List.of(markdown,csv));
        if(previousMapping!=null) {
            Path diff=projectDirectory.resolve(projectName+"_mapping_diff.md");
            Files.writeString(diff,diff(previousMapping,analysis),StandardCharsets.UTF_8);
            result.add(diff);
        }
        return List.copyOf(result);
    }

    private static String markdown(SemanticMappingAnalysis a,MappingVerificationPlan verification) {
        StringBuilder x=new StringBuilder("# Semantic mapping analysis\n\n")
                .append("> This table is generated even when conformance fails. `STATICALLY_SUPPORTED` is a candidate; only a discharged Rodin PO certifies it.\n\n")
                .append("## Task–Activity mappings\n\n")
                .append("| Mapping | BPMN Activity/fragment | iStar Task | Actor/lane | Goals supported | Kind | Score | Candidate | Verification |\n")
                .append("|---|---|---|---|---|---|---:|---|---|\n");
        for(var m:a.mappings()) {
            var v=verification.forMapping(m.id());
            String verified=v==null?"not generated":v.status()==GenerationStatus.GENERATED
                    ? "PENDING: `"+v.rodinProofObligation()+"`; ProB `"+v.probPropertyId()+"`"
                    : "UNSUPPORTED: "+v.explanation();
            row(x,m.id(),String.join(" → ",m.activityIds()),m.taskId(),m.taskActor()+" / "+blank(m.laneRole()),
                    String.join(", ",m.supportedGoals()),m.kind().name(),Integer.toString(m.score()),
                    m.candidateStatus().name(),verified);
            x.append("\nEvidence: `").append(escapeCode(m.evidence())).append("`\n\n");
        }
        if(a.mappings().isEmpty()) x.append("|—|—|—|—|—|—|—|—|No candidate mapping found|\n");

        x.append("\n## Task coverage\n\n| Task | Actor | Status | Mapping ids | Missing post-state properties |\n")
                .append("|---|---|---|---|---|\n");
        a.taskCoverage().forEach(t->row(x,t.taskId(),t.actor(),t.status().name(),String.join(", ",t.mappingIds()),
                String.join(", ",t.missingPostProperties())));

        x.append("\n## Goal coverage\n\n| Goal | Actor | Root | Status | Via mapped Tasks | Direct Activity effects |\n")
                .append("|---|---|---|---|---|---|\n");
        a.goalCoverage().forEach(g->row(x,g.goalId(),g.actor(),Boolean.toString(g.root()),g.status().name(),
                String.join(", ",g.mappingIds()),String.join(", ",g.directActivityIds())));

        x.append("\n## Activity classification\n\n| Activity | Process | Lane | Status | Mappings | Directly affected Goals |\n")
                .append("|---|---|---|---|---|---|\n");
        a.activityCoverage().forEach(activity->row(x,activity.activityId(),activity.processId(),blank(activity.laneRole()),
                activity.status().name(),String.join(", ",activity.mappingIds()),
                String.join(", ",activity.directlyAffectedGoals())));

        x.append("\n## Diagnostics\n\n");
        if(a.diagnostics().isEmpty()) x.append("No mapping diagnostics.\n");
        else a.diagnostics().forEach(d->x.append("- **").append(d.severity()).append(" ").append(d.code())
                .append("** — ").append(d.elementKind()).append(" `").append(d.elementId()).append("`: ")
                .append(d.message()).append('\n'));
        x.append("\n## Verification semantics\n\n")
                .append("- Rodin obligations are theorem guards containing the weakest precondition of the iStar Task postcondition under the BPMN Activity actions.\n")
                .append("- ProB properties check the same implication over reachable states and can produce a counterexample.\n")
                .append("- An open Rodin PO is `INCONCLUSIVE`, not automatically false; a ProB counterexample is `REFUTED`.\n")
                .append("- Composite mappings are reported but require a later Event-B atomicity/group-refinement development.\n");
        return x.toString();
    }

    private static String csv(SemanticMappingAnalysis a,MappingVerificationPlan verification) {
        StringBuilder x=new StringBuilder("stable_key,mapping_id,activity_ids,task_id,task_actor,kind,score,candidate_status,verification_generation,rodin_po,prob_property,evidence\n");
        for(var m:a.mappings()) {
            var v=verification.forMapping(m.id());
            csvRow(x,m.stableKey(),m.id(),String.join("+",m.activityIds()),m.taskId(),m.taskActor(),m.kind().name(),
                    Integer.toString(m.score()),m.candidateStatus().name(),v==null?"NOT_GENERATED":v.status().name(),
                    v==null?"":v.rodinProofObligation(),v==null?"":v.probPropertyId(),m.evidence());
        }
        return x.toString();
    }

    private static String diff(Path previous,SemanticMappingAnalysis current) throws IOException {
        Map<String,String> old=readPrevious(previous),now=new LinkedHashMap<>(),currentIds=new LinkedHashMap<>();
        current.mappings().forEach(x->{now.put(x.stableKey(),fingerprint(x));currentIds.put(x.stableKey(),x.id());});
        StringBuilder x=new StringBuilder("# Semantic mapping evolution\n\nPrevious mapping: `")
                .append(previous.toAbsolutePath()).append("`\n\n| Stable mapping key | Change | Current mapping |\n")
                .append("|---|---|---|\n");
        for(String key:old.keySet()) row(x,key,!now.containsKey(key)?"REMOVED":old.get(key).equals(now.get(key))
                ?"RETAINED":"CHANGED",currentIds.getOrDefault(key,"—"));
        for(String key:now.keySet()) if(!old.containsKey(key)) row(x,key,"NEW",currentIds.get(key));
        if(old.isEmpty()&&now.isEmpty()) row(x,"—","UNCHANGED_EMPTY","—");
        return x.toString();
    }

    private static Map<String,String> readPrevious(Path path) throws IOException {
        if(!Files.isRegularFile(path)) throw new IOException("Previous mapping CSV does not exist: "+path);
        List<String> lines=Files.readAllLines(path,StandardCharsets.UTF_8); Map<String,String> result=new LinkedHashMap<>();
        for(int i=1;i<lines.size();i++) {
            List<String> cells=parseCsv(lines.get(i));
            if(cells.size()>=8&&!cells.get(0).isBlank()) result.put(cells.get(0),
                    String.join("|",cells.get(1),cells.get(2),cells.get(3),cells.get(4),cells.get(5),cells.get(6),cells.get(7)));
        }
        return result;
    }

    private static List<String> parseCsv(String line) {
        List<String> result=new ArrayList<>(); StringBuilder cell=new StringBuilder(); boolean quoted=false;
        for(int i=0;i<line.length();i++) {
            char c=line.charAt(i);
            if(c=='"') { if(quoted&&i+1<line.length()&&line.charAt(i+1)=='"'){cell.append('"');i++;}else quoted=!quoted; }
            else if(c==','&&!quoted){result.add(cell.toString());cell.setLength(0);}else cell.append(c);
        }
        result.add(cell.toString()); return result;
    }

    private static String fingerprint(SemanticMappingAnalysis.MappingEntry x) {
        return String.join("|",x.id(),String.join("+",x.activityIds()),x.taskId(),x.taskActor(),x.kind().name(),
                Integer.toString(x.score()),x.candidateStatus().name());
    }

    private static void row(StringBuilder x,String...cells) {
        x.append('|'); for(String cell:cells)x.append(escapeTable(cell)).append('|'); x.append('\n');
    }
    private static void csvRow(StringBuilder x,String...cells) {
        for(int i=0;i<cells.length;i++){if(i>0)x.append(',');x.append('"').append(cells[i].replace("\"","\"\"")).append('"');}x.append('\n');
    }
    private static String escapeTable(String value){return blank(value).replace("|","\\|").replace("\n"," ");}
    private static String escapeCode(String value){return blank(value).replace("`","'").replace("\n"," ");}
    private static String blank(String value){return value==null||value.isBlank()?"—":value;}
}
