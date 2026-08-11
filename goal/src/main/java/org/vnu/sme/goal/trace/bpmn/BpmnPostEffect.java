package org.vnu.sme.goal.trace.bpmn;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Synthesizes an executable SOIL action from a BPMN activity's {@code post}
 * clause, for the subset of OCL that is an assignment-style conjunction:
 * bare or negated {@code owner.attr} (boolean), {@code owner.attr = Expr},
 * or {@code Coll->[select(v | Cond)->]forAll(v | <same conjunction over v>)}.
 *
 * <p>{@code Expr} may reference {@code owner.attr@pre}; the marker is
 * stripped before emitting SOIL because {@code :=}'s right-hand side is
 * already evaluated against the pre-mutation state, exactly what
 * {@code @pre} denotes — no dual-state evaluation is needed.
 *
 * <p>A {@code post} outside this shape (inequalities, disjunctions,
 * existentials) returns null: it is still legal OCL, just
 * verification-only — nothing synthesizes an action from it.
 */
public final class BpmnPostEffect {
    private static final Pattern PRE_MARKER = Pattern.compile("\\.(\\w+)@pre\\b");
    private static final Pattern ASSIGN_EQ = Pattern.compile("(?s)^([A-Za-z_]\\w*)\\.(\\w+)\\s*=\\s*(.+)$");
    private static final Pattern BARE_BOOL = Pattern.compile("^(not\\s+)?([A-Za-z_]\\w*)\\.(\\w+)$");
    private static final Pattern SELECT_FORALL = Pattern.compile(
            "(?s)^(.+?)->select\\((\\w+)\\s*\\|\\s*(.+?)\\)\\s*->forAll\\(\\2\\s*\\|\\s*(.+)\\)$");
    private static final Pattern FORALL = Pattern.compile(
            "(?s)^(.+?)->forAll\\((\\w+)\\s*\\|\\s*(.+)\\)$");

    private BpmnPostEffect() {}

    /** Returns SOIL statements (no {@code begin}/{@code end} wrapper), or null if unsupported. */
    public static String toSoil(String postOcl) {
        if (postOcl == null || postOcl.isBlank()) return null;
        String normalized = postOcl.replaceAll("\\s+", " ").trim();
        List<String> statements = new ArrayList<>();
        for (String conjunct : splitTopLevelAnd(normalized)) {
            String statement = statementFor(conjunct.trim());
            if (statement == null) return null;
            statements.add(statement);
        }
        return statements.isEmpty() ? null : String.join(" ", statements);
    }

    private static String statementFor(String conjunct) {
        Matcher selectForAll = SELECT_FORALL.matcher(conjunct);
        if (selectForAll.matches()) {
            return loopStatement(selectForAll.group(1).trim(), selectForAll.group(2),
                    selectForAll.group(3).trim(), selectForAll.group(4).trim());
        }
        Matcher forAll = FORALL.matcher(conjunct);
        if (forAll.matches()) {
            return loopStatement(forAll.group(1).trim(), forAll.group(2), null, forAll.group(3).trim());
        }
        return assignmentFor(conjunct);
    }

    private static String loopStatement(String collection, String var, String selectCond, String body) {
        List<String> inner = new ArrayList<>();
        for (String conjunct : splitTopLevelAnd(body)) {
            String statement = assignmentFor(conjunct.trim());
            if (statement == null) return null;
            inner.add(statement);
        }
        if (inner.isEmpty()) return null;
        String source = selectCond == null ? collection
                : collection + "->select(" + var + " | " + selectCond + ")";
        return "for " + var + " in " + source + " do " + String.join(" ", inner) + " end;";
    }

    private static String assignmentFor(String conjunct) {
        Matcher bareBool = BARE_BOOL.matcher(conjunct);
        if (bareBool.matches()) {
            boolean negated = bareBool.group(1) != null;
            return bareBool.group(2) + "." + bareBool.group(3) + " := " + !negated + ";";
        }
        Matcher assignEq = ASSIGN_EQ.matcher(conjunct);
        if (assignEq.matches()) {
            String rhs = PRE_MARKER.matcher(assignEq.group(3).trim()).replaceAll(".$1");
            return assignEq.group(1) + "." + assignEq.group(2) + " := " + rhs + ";";
        }
        return null;
    }

    /** Splits on top-level {@code and} (not inside parentheses). */
    private static List<String> splitTopLevelAnd(String text) {
        List<String> parts = new ArrayList<>();
        int depth = 0, start = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '(') depth++;
            else if (c == ')') depth--;
            else if (depth == 0 && text.regionMatches(i, " and ", 0, 5)) {
                parts.add(text.substring(start, i));
                start = i + 5;
                i += 4;
            }
        }
        parts.add(text.substring(start));
        return parts;
    }
}
