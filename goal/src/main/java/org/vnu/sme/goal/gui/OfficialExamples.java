package org.vnu.sme.goal.gui;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.JFileChooser;

/** Locates the repository-level {@code examples/} directory used by the plugin. */
public final class OfficialExamples {
    public static final String ROOT_PROPERTY = "conformance.examples";

    private OfficialExamples() {}

    public static Path root() {
        String configured = System.getProperty(ROOT_PROPERTY, "").trim();
        if (!configured.isEmpty()) {
            Path path = Path.of(configured).toAbsolutePath().normalize();
            if (Files.isDirectory(path)) return path;
        }

        Path cursor = Path.of(System.getProperty("user.dir", ".")).toAbsolutePath().normalize();
        for (int depth = 0; cursor != null && depth < 10; depth++, cursor = cursor.getParent()) {
            Path candidate = cursor.resolve("examples");
            if (Files.isDirectory(candidate.resolve("ProposalReview"))) return candidate;
        }
        return Path.of(System.getProperty("user.dir", ".")).toAbsolutePath().normalize();
    }

    public static JFileChooser chooser(String rememberedPath) {
        Path directory = root();
        if (rememberedPath != null && !rememberedPath.isBlank()) {
            Path remembered = Path.of(rememberedPath).toAbsolutePath().normalize();
            Path parent = Files.isDirectory(remembered) ? remembered : remembered.getParent();
            if (parent != null && Files.isDirectory(parent)) directory = parent;
        }
        JFileChooser chooser = new JFileChooser(directory.toFile());
        if (rememberedPath != null && !rememberedPath.isBlank()) {
            File selected = Path.of(rememberedPath).toFile();
            if (selected.exists()) chooser.setSelectedFile(selected);
        }
        return chooser;
    }
}
