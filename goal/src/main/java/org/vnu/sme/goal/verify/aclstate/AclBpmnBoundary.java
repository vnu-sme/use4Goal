package org.vnu.sme.goal.verify.aclstate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vnu.sme.goal.dsl.acl.mm.AclModel;

/** Finite scope used by Kodkod to generate ACL object diagrams symbolically. */
public record AclBpmnBoundary(Path file, String name, int snapshots, int loopBound,
                              int integerMin, int integerMax,
                              Map<String, Scope> objectScopes,
                              Map<String, Scope> linkScopes,
                              List<String> stringAtoms,
                              List<String> realAtoms) {
    private static final Pattern HEADER = Pattern.compile(
            "(?s)\\s*acl-bpmn-boundary\\s+v1\\.0\\s+([A-Za-z_][A-Za-z0-9_]*)\\s*\\{(.*)}\\s*");
    private static final Pattern CARDINALITY = Pattern.compile("(\\d+)(?:\\.\\.(\\d+))?");

    public record Scope(int lower, int upper) {
        public Scope {
            if (lower < 0 || upper < lower) {
                throw new IllegalArgumentException("invalid scope " + lower + ".." + upper);
            }
        }

        public boolean exact() { return lower == upper; }
        @Override public String toString() { return exact() ? String.valueOf(lower) : lower + ".." + upper; }
    }

    public AclBpmnBoundary {
        Objects.requireNonNull(file, "file");
        Objects.requireNonNull(name, "name");
        objectScopes = Map.copyOf(objectScopes);
        linkScopes = Map.copyOf(linkScopes);
        stringAtoms = List.copyOf(stringAtoms);
        realAtoms = List.copyOf(realAtoms);
    }

    public static AclBpmnBoundary load(Path source, AclModel model) throws IOException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(model, "model");
        Path file = source.toAbsolutePath().normalize();
        String text = Files.readString(file).replaceAll("(?m)//.*$", "");
        Matcher header = HEADER.matcher(text);
        if (!header.matches()) {
            throw new IllegalArgumentException("Expected: acl-bpmn-boundary v1.0 Name { ... }");
        }

        Integer snapshots = null;
        int loopBound = 3;
        int integerMin = -8;
        int integerMax = 8;
        Map<String, Scope> objects = new LinkedHashMap<>();
        Map<String, Scope> links = new LinkedHashMap<>();
        Set<String> strings = new LinkedHashSet<>();
        Set<String> reals = new LinkedHashSet<>();

        for (String raw : header.group(2).split(";")) {
            String statement = raw.trim();
            if (statement.isEmpty()) continue;
            String[] parts = statement.split("\\s+", 3);
            switch (parts[0]) {
                case "snapshots" -> {
                    require(parts.length == 2, statement, "snapshots <positive integer>");
                    snapshots = positive(parts[1], "snapshots");
                }
                case "loop-bound" -> {
                    require(parts.length == 2, statement, "loop-bound <non-negative integer>");
                    loopBound = nonNegative(parts[1], "loop-bound");
                }
                case "integer" -> {
                    require(parts.length == 2, statement, "integer <min>..<max>");
                    Matcher range = Pattern.compile("(-?\\d+)\\.\\.(-?\\d+)").matcher(parts[1]);
                    require(range.matches(), statement, "integer <min>..<max>");
                    integerMin = Integer.parseInt(range.group(1));
                    integerMax = Integer.parseInt(range.group(2));
                    require(integerMin <= integerMax, statement, "integer range with min <= max");
                    require((long) integerMax - integerMin <= 255, statement,
                            "integer range containing at most 256 values");
                }
                case "objects" -> {
                    require(parts.length == 3, statement, "objects <Classifier> <n|lower..upper>");
                    putUnique(objects, parts[1], scope(parts[2], statement), "object classifier");
                }
                case "links" -> {
                    require(parts.length == 3, statement, "links <Association> <n|lower..upper>");
                    putUnique(links, parts[1], scope(parts[2], statement), "association");
                }
                case "string" -> strings.add(quoted(statement.substring("string".length()).trim(), statement));
                case "real" -> {
                    require(parts.length == 2, statement, "real <finite literal>");
                    try { Double.parseDouble(parts[1]); }
                    catch (NumberFormatException ex) { throw syntax(statement, "real <finite literal>"); }
                    reals.add(parts[1]);
                }
                default -> throw syntax(statement, "a boundary declaration");
            }
        }

        if (snapshots == null) throw new IllegalArgumentException("Boundary must declare snapshots <n>;");
        if (snapshots > 100) throw new IllegalArgumentException("snapshots must not exceed 100");
        if (loopBound > 20) throw new IllegalArgumentException("loop-bound must not exceed 20");

        Set<String> classifiers = new LinkedHashSet<>();
        model.entities().forEach(value -> classifiers.add(value.name()));
        model.roles().forEach(value -> classifiers.add(value.name()));
        model.groups().forEach(value -> classifiers.add(value.name()));
        List<String> diagnostics = new ArrayList<>();
        for (String classifier : classifiers) {
            if (!objects.containsKey(classifier)) diagnostics.add("missing objects scope for " + classifier);
        }
        objects.keySet().stream().filter(value -> !classifiers.contains(value))
                .forEach(value -> diagnostics.add("unknown classifier in boundary: " + value));
        Set<String> relations = new LinkedHashSet<>();
        model.relations().forEach(value -> relations.add(value.name()));
        links.keySet().stream().filter(value -> !relations.contains(value))
                .forEach(value -> diagnostics.add("unknown association in boundary: " + value));
        for (var entry : links.entrySet()) {
            long possible = model.relations().stream().filter(value -> value.name().equals(entry.getKey()))
                    .mapToLong(value -> {
                        Scope sourceScope = objects.get(value.source().type());
                        Scope targetScope = objects.get(value.target().type());
                        return sourceScope == null || targetScope == null ? 0
                                : (long) sourceScope.upper() * targetScope.upper();
                    }).findFirst().orElse(0);
            if (entry.getValue().upper() > possible) {
                diagnostics.add("link upper scope for " + entry.getKey() + " exceeds " + possible);
            }
        }
        if (!diagnostics.isEmpty()) {
            throw new IllegalArgumentException("Invalid ACL/BPMN boundary:\n" + String.join("\n", diagnostics));
        }

        return new AclBpmnBoundary(file, header.group(1), snapshots, loopBound,
                integerMin, integerMax, objects, links, new ArrayList<>(strings), new ArrayList<>(reals));
    }

    private static Scope scope(String text, String statement) {
        Matcher matcher = CARDINALITY.matcher(text);
        if (!matcher.matches()) throw syntax(statement, "<n> or <lower>..<upper>");
        int lower = Integer.parseInt(matcher.group(1));
        int upper = matcher.group(2) == null ? lower : Integer.parseInt(matcher.group(2));
        if (upper > 30) throw new IllegalArgumentException("object/link scope must not exceed 30: " + statement);
        return new Scope(lower, upper);
    }

    private static int positive(String value, String name) {
        int result = nonNegative(value, name);
        if (result == 0) throw new IllegalArgumentException(name + " must be positive");
        return result;
    }

    private static int nonNegative(String value, String name) {
        try {
            int result = Integer.parseInt(value);
            if (result < 0) throw new NumberFormatException();
            return result;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(name + " must be a non-negative integer");
        }
    }

    private static String quoted(String value, String statement) {
        if (value.length() < 2 || value.charAt(0) != '"' || value.charAt(value.length() - 1) != '"') {
            throw syntax(statement, "string \"literal\"");
        }
        return value.substring(1, value.length() - 1).replace("\\\"", "\"").replace("\\\\", "\\");
    }

    private static <T> void putUnique(Map<String, T> map, String name, T value, String kind) {
        if (map.putIfAbsent(name, value) != null) {
            throw new IllegalArgumentException("duplicate " + kind + " boundary: " + name);
        }
    }

    private static void require(boolean condition, String statement, String expected) {
        if (!condition) throw syntax(statement, expected);
    }

    private static IllegalArgumentException syntax(String statement, String expected) {
        return new IllegalArgumentException("Invalid boundary statement '" + statement
                + "'; expected " + expected);
    }
}
