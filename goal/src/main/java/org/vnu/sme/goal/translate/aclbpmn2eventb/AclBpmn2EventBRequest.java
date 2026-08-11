package org.vnu.sme.goal.translate.aclbpmn2eventb;

import java.nio.file.Path;
import java.util.Objects;

public record AclBpmn2EventBRequest(Path acl,Path bpmn,Path outputDirectory,String projectName) {
    public AclBpmn2EventBRequest {
        Objects.requireNonNull(acl); Objects.requireNonNull(bpmn); Objects.requireNonNull(outputDirectory);
        if(projectName==null||projectName.isBlank()) throw new IllegalArgumentException("projectName is required");
    }
}
