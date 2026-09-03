package org.vnu.sme.goal.translate.aclistarbpmn2eventb.serialize;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.vnu.sme.goal.translate.aclistarbpmn2eventb.ir.EventBProject;
import org.vnu.sme.goal.translate.aclistarbpmn2eventb.ir.EventBProject.*;

/** Serializes the IR to the XML source files consumed by Rodin. */
public final class RodinProjectWriter {
    private RodinProjectWriter() {}

    public static List<Path> write(EventBProject project, Path destination, List<String> diagnostics) throws IOException {
        return write(project, destination, diagnostics, true);
    }

    /** Writes a Rodin project. Pure structural translations can omit the temporal sidecar. */
    public static List<Path> write(EventBProject project, Path destination, List<String> diagnostics,
                                   boolean includeProperties) throws IOException {
        Path parent = destination.toAbsolutePath().getParent();
        if (parent == null) throw new IOException("Output project must have a parent directory");
        Files.createDirectories(parent);
        Path temp = Files.createTempDirectory(parent, ".eventb-export-");
        try {
            write(temp.resolve(".project"), projectFile(project.name()));
            Files.createDirectories(temp.resolve(".settings"));
            write(temp.resolve(".settings/org.eclipse.core.resources.prefs"),
                    "eclipse.preferences.version=1\nencoding/<project>=UTF-8\n");
            write(temp.resolve(project.context().name() + ".buc"), contextFile(project.context()));
            write(temp.resolve(project.machine().name() + ".bum"), machineFile(project.machine()));
            if (includeProperties) write(temp.resolve(project.name() + "_properties.ltl"), properties(project));
            write(temp.resolve(project.name() + "_translation.md"), report(project, diagnostics));
            if (Files.exists(destination)) throw new IOException("Target project already exists: " + destination);
            Files.move(temp, destination, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            deleteTree(temp); throw e;
        }
        List<Path> generated = new ArrayList<>(List.of(destination.resolve(".project"),
                destination.resolve(".settings/org.eclipse.core.resources.prefs"),
                destination.resolve(project.context().name()+".buc"),
                destination.resolve(project.machine().name()+".bum")));
        if (includeProperties) generated.add(destination.resolve(project.name()+"_properties.ltl"));
        generated.add(destination.resolve(project.name()+"_translation.md"));
        return List.copyOf(generated);
    }

    private static String contextFile(Context c) {
        StringBuilder x = header("contextFile", "3", "org.eventb.core.fwd"); int n=0;
        for (String set : c.sets()) element(x, "carrierSet", name(n++), "identifier", set);
        for (Constant constant : c.constants()) element(x, "constant", name(n++), "identifier", constant.identifier());
        for (Predicate a : c.axioms()) predicate(x, "axiom", name(n++), a);
        return close(x, "contextFile");
    }
    private static String machineFile(Machine m) {
        StringBuilder x = header("machineFile", "5", "org.eventb.core.fwd"); int n=0;
        element(x, "seesContext", name(n++), "target", m.contextName());
        for (String variable : m.variables()) element(x, "variable", name(n++), "identifier", variable);
        for (Predicate inv : m.invariants()) predicate(x, "invariant", name(n++), inv);
        for (Event event : m.events()) {
            x.append("  <org.eventb.core.event name=\"").append(esc(name(n++))).append("\" org.eventb.core.convergence=\"0\" org.eventb.core.extended=\"false\" org.eventb.core.label=\"").append(esc(event.label())).append("\">\n");
            for (String p : event.parameters()) element(x, "parameter", name(n++), "identifier", p, 4);
            for (Predicate g : event.guards()) predicate(x, "guard", name(n++), g, 4);
            for (Assignment a : event.actions()) x.append("    <org.eventb.core.action name=\"").append(esc(name(n++))).append("\" org.eventb.core.label=\"").append(esc(a.label())).append("\" org.eventb.core.assignment=\"").append(esc(a.formula())).append("\"/>\n");
            x.append("  </org.eventb.core.event>\n");
        }
        return close(x, "machineFile");
    }
    private static String projectFile(String name) { return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<projectDescription>\n  <name>"+esc(name)+"</name>\n  <comment></comment><projects></projects>\n  <buildSpec><buildCommand><name>org.rodinp.core.rodinbuilder</name><arguments></arguments></buildCommand></buildSpec>\n  <natures><nature>org.rodinp.core.rodinnature</nature></natures>\n</projectDescription>\n"; }
    private static String report(EventBProject p, List<String> diagnostics) {
        StringBuilder x = new StringBuilder("# Event-B translation: ").append(p.name()).append("\n\n## Traceability\n\n| Source | Element | Event-B |\n|---|---|---|\n");
        for (Trace t : p.traces()) x.append('|').append(t.sourceLanguage()).append('|').append(t.sourceElement()).append('|').append(t.targetElement()).append("|\n");
        x.append("\n## Diagnostics\n\n");
        if (diagnostics.isEmpty()) x.append("No diagnostics.\n"); else diagnostics.forEach(d -> x.append("- ").append(d).append('\n'));
        return x.toString();
    }
    private static String properties(EventBProject p) {
        StringBuilder x=new StringBuilder("# ProB LTL properties generated from iStar\n");
        for(var property:p.properties()) x.append("# ").append(property.id()).append(" [").append(property.kind()).append("] ")
                .append(property.source()).append('\n').append(property.formula()).append("\n\n");
        return x.toString();
    }
    private static StringBuilder header(String kind, String version, String config) { return new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<org.eventb.core.").append(kind).append(" org.eventb.core.configuration=\"").append(config).append("\" version=\"").append(version).append("\">\n"); }
    private static String close(StringBuilder x, String kind) { return x.append("</org.eventb.core.").append(kind).append(">\n").toString(); }
    private static void predicate(StringBuilder x, String kind, String name, Predicate p) { predicate(x,kind,name,p,2); }
    private static void predicate(StringBuilder x, String kind, String name, Predicate p, int indent) { x.append(" ".repeat(indent)).append("<org.eventb.core.").append(kind).append(" name=\"").append(esc(name)).append("\" org.eventb.core.label=\"").append(esc(p.label())).append("\" org.eventb.core.predicate=\"").append(esc(p.formula())).append("\""); if(p.theorem()) x.append(" org.eventb.core.theorem=\"true\""); x.append("/>\n"); }
    private static void element(StringBuilder x,String kind,String name,String attr,String value){ element(x,kind,name,attr,value,2); }
    private static void element(StringBuilder x,String kind,String name,String attr,String value,int indent){ x.append(" ".repeat(indent)).append("<org.eventb.core.").append(kind).append(" name=\"").append(esc(name)).append("\" org.eventb.core.").append(attr).append("=\"").append(esc(value)).append("\"/>\n"); }
    private static String name(int n) { return "n" + n; }
    private static String esc(String s) { return s.replace("&","&amp;").replace("\"","&quot;").replace("<","&lt;").replace(">","&gt;").replace("\r","&#13;").replace("\n","&#10;"); }
    private static void write(Path p,String s)throws IOException{ Files.writeString(p,s,StandardCharsets.UTF_8); }
    private static void deleteTree(Path root) throws IOException { if(!Files.exists(root)) return; try(var paths=Files.walk(root)){ for(Path p:paths.sorted(java.util.Comparator.reverseOrder()).toList()) Files.deleteIfExists(p); } }
}
