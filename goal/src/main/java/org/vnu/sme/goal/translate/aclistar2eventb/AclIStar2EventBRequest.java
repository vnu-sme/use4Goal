package org.vnu.sme.goal.translate.aclistar2eventb;

import java.nio.file.Path;
import java.util.Objects;

public record AclIStar2EventBRequest(Path acl,Path istar,Path outputDirectory,String projectName) {
    public AclIStar2EventBRequest {
        Objects.requireNonNull(acl); Objects.requireNonNull(istar); Objects.requireNonNull(outputDirectory);
        if(projectName==null||projectName.isBlank()) throw new IllegalArgumentException("projectName is required");
    }
}
