package org.vnu.sme.goal.usegen;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Shared "write generated USE/TOCL text into a folder" step, used by every
 * folder-output translation action (iStar+ACL, BPMN+ACL, ...). Always writes
 * {@code <modelName>.use}; writes {@code <modelName>.tocl} only when the
 * translator actually produced temporal content, since {@code always}/{@code
 * sometime}/{@code alwaysPast} are not core OCL and only some source models
 * (e.g. iStar goals with a GoalType) have anything temporal to say.
 */
public final class UseOutputWriter {

    private UseOutputWriter() {}

    /** {@code toclFile} is {@code null} when no {@code .tocl} file was written. */
    public record Written(Path useFile, Path toclFile) {}

    public static Written writeToFolder(Path outputFolder, String modelName,
                                        String useText, String toclText) throws IOException {
        Files.createDirectories(outputFolder);
        Path useFile = outputFolder.resolve(modelName + ".use");
        Files.writeString(useFile, useText, StandardCharsets.UTF_8);

        Path toclFile = null;
        if (toclText != null && !toclText.isBlank()) {
            toclFile = outputFolder.resolve(modelName + ".tocl");
            Files.writeString(toclFile, toclText, StandardCharsets.UTF_8);
        }
        return new Written(useFile, toclFile);
    }
}
