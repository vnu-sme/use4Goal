package org.vnu.sme.goal.verify.aclstate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A state-only execution scenario. Control-flow elements are deliberately not
 * named: the BPMN conformance evaluator infers them from the ordered AOL
 * snapshots and the loaded BPMN contracts.
 *
 * <pre>
 * acl-state-scenario v1.0 ClassroomRun {
 *   acl "classroom.acl";
 *   bpmn "classroom.bpmn2";
 *   state "state_00.aol";
 *   state "state_01.aol";
 * }
 * </pre>
 */
public record AclStateScenario(String name, Path sourceFile, Path aclFile,
                               Path bpmnFile, List<Path> stateFiles) {
    private static final Pattern HEADER = Pattern.compile(
            "^acl-state-scenario\\s+v1\\.0\\s+([A-Za-z_][A-Za-z0-9_]*)\\s*\\{$");
    private static final Pattern STATEMENT = Pattern.compile(
            "^(acl|bpmn|state)\\s+\"((?:\\\\.|[^\"\\\\])*)\"\\s*;$");

    public AclStateScenario {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(sourceFile, "sourceFile");
        Objects.requireNonNull(aclFile, "aclFile");
        Objects.requireNonNull(bpmnFile, "bpmnFile");
        stateFiles = List.copyOf(stateFiles);
        if (stateFiles.size() < 2) {
            throw new IllegalArgumentException("an ACL state scenario requires at least two state snapshots");
        }
    }

    public static AclStateScenario load(Path file) throws IOException {
        Objects.requireNonNull(file, "file");
        Path source = file.toAbsolutePath().normalize();
        List<String> lines = Files.readAllLines(source);
        String name = null;
        String acl = null;
        String bpmn = null;
        List<String> states = new ArrayList<>();
        boolean opened = false;
        boolean closed = false;

        for (int index = 0; index < lines.size(); index++) {
            String line = stripLineComment(lines.get(index)).trim();
            if (line.isEmpty()) continue;
            int lineNumber = index + 1;
            if (!opened) {
                Matcher header = HEADER.matcher(line);
                if (!header.matches()) {
                    throw syntax(source, lineNumber,
                            "expected 'acl-state-scenario v1.0 <Name> {'");
                }
                name = header.group(1);
                opened = true;
                continue;
            }
            if (line.equals("}")) {
                closed = true;
                for (int rest = index + 1; rest < lines.size(); rest++) {
                    if (!stripLineComment(lines.get(rest)).isBlank()) {
                        throw syntax(source, rest + 1, "content after closing '}'");
                    }
                }
                break;
            }
            Matcher statement = STATEMENT.matcher(line);
            if (!statement.matches()) {
                throw syntax(source, lineNumber,
                        "expected acl, bpmn, or state followed by a quoted path and ';'");
            }
            String value = unescape(statement.group(2));
            switch (statement.group(1)) {
                case "acl" -> {
                    if (acl != null) throw syntax(source, lineNumber, "duplicate acl declaration");
                    acl = value;
                }
                case "bpmn" -> {
                    if (bpmn != null) throw syntax(source, lineNumber, "duplicate bpmn declaration");
                    bpmn = value;
                }
                case "state" -> states.add(value);
                default -> throw new IllegalStateException("unreachable scenario statement");
            }
        }
        if (!opened || !closed) throw syntax(source, lines.size(), "missing closing '}'");
        if (acl == null) throw syntax(source, 0, "missing acl declaration");
        if (bpmn == null) throw syntax(source, 0, "missing bpmn declaration");
        if (states.size() < 2) throw syntax(source, 0, "at least two state declarations are required");

        Path base = source.getParent();
        List<Path> resolvedStates = states.stream().map(value -> resolve(base, value)).toList();
        return new AclStateScenario(name, source, resolve(base, acl), resolve(base, bpmn), resolvedStates);
    }

    private static String stripLineComment(String line) {
        boolean quoted = false;
        boolean escaped = false;
        for (int i = 0; i + 1 < line.length(); i++) {
            char c = line.charAt(i);
            if (escaped) {
                escaped = false;
                continue;
            }
            if (c == '\\' && quoted) {
                escaped = true;
                continue;
            }
            if (c == '"') quoted = !quoted;
            if (!quoted && c == '/' && line.charAt(i + 1) == '/') return line.substring(0, i);
        }
        return line;
    }

    private static String unescape(String value) {
        return value.replace("\\\"", "\"").replace("\\\\", "\\");
    }

    private static Path resolve(Path base, String value) {
        Path path = Path.of(value);
        return (path.isAbsolute() ? path : base.resolve(path)).toAbsolutePath().normalize();
    }

    private static IllegalArgumentException syntax(Path source, int line, String detail) {
        return new IllegalArgumentException(source.getFileName() + (line > 0 ? ":" + line : "")
                + ": " + detail);
    }
}
