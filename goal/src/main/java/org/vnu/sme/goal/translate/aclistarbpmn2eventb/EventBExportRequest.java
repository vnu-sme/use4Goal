package org.vnu.sme.goal.translate.aclistarbpmn2eventb;

import java.nio.file.Path;
import java.util.Objects;

/** Files and target project selected by the user. */
public record EventBExportRequest(Path acl, Path istar, Path bpmn, Path outputDirectory,
                                  String projectName, Path previousMapping) {
    public EventBExportRequest(Path acl, Path istar, Path bpmn, Path outputDirectory, String projectName) {
        this(acl, istar, bpmn, outputDirectory, projectName, null);
    }
    public EventBExportRequest {
        Objects.requireNonNull(acl, "acl");
        Objects.requireNonNull(istar, "istar");
        Objects.requireNonNull(bpmn, "bpmn");
        Objects.requireNonNull(outputDirectory, "outputDirectory");
        Objects.requireNonNull(projectName, "projectName");
        if (projectName.isBlank()) throw new IllegalArgumentException("Project name must not be blank");
    }
}
