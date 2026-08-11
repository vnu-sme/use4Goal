package org.vnu.sme.goal.translate.acl2eventb;

import java.nio.file.Path;
import java.util.Objects;

/** Input selected by the ACL-only GUI/CLI exporter. */
public record AclToEventBRequest(Path acl, Path outputDirectory, String projectName) {
    public AclToEventBRequest {
        Objects.requireNonNull(acl,"acl");
        Objects.requireNonNull(outputDirectory,"outputDirectory");
        Objects.requireNonNull(projectName,"projectName");
        if(projectName.isBlank()) throw new IllegalArgumentException("Project name must not be blank");
    }
}
