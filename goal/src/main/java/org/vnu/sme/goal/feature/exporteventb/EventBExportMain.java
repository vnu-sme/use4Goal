package org.vnu.sme.goal.feature.exporteventb;

import java.nio.file.Path;

import org.vnu.sme.goal.translate.aclistarbpmn2eventb.EventBExportRequest;
import org.vnu.sme.goal.translate.aclistarbpmn2eventb.EventBExportService;

/** Headless entry point: acl istar bpmn output-directory project-name [previous-mapping.csv]. */
public final class EventBExportMain {
    private EventBExportMain() {}
    public static void main(String[] args) {
        if (args.length != 5 && args.length != 6) {
            System.err.println("Usage: EventBExportMain <model.acl> <model.istar> <model.bpmn2> <output-dir> <project-name> [previous-mapping.csv]");
            System.exit(2);
        }
        var request = new EventBExportRequest(Path.of(args[0]), Path.of(args[1]), Path.of(args[2]), Path.of(args[3]),
                args[4], args.length == 6 ? Path.of(args[5]) : null);
        var result = new EventBExportService().export(request);
        result.diagnostics().forEach(System.err::println);
        if (!result.success()) System.exit(1);
        result.generatedFiles().forEach(System.out::println);
    }
}
