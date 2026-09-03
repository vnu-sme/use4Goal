package org.vnu.sme.goal.translate.aclistarbpmn2eventb;

import java.nio.file.Path;
import java.util.List;

public record EventBExportResult(boolean success, Path projectDirectory,
                                 List<Path> generatedFiles, List<String> diagnostics) {
    public EventBExportResult {
        generatedFiles = List.copyOf(generatedFiles);
        diagnostics = List.copyOf(diagnostics);
    }
}
