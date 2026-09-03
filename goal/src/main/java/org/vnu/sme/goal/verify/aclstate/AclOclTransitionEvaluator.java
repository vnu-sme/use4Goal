package org.vnu.sme.goal.verify.aclstate;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vnu.sme.goal.dsl.aol.state.AclSystemState;
import org.vnu.sme.goal.dsl.bpmn.mm.ActivityConstraint;
import org.vnu.sme.goal.trace.istartrace.nativeacl.NativeOclEvaluator;

/** Evaluates BPMN OCL contracts directly over one or two native ACL states. */
final class AclOclTransitionEvaluator {
    private static final Pattern AT_PRE_PATH = Pattern.compile(
            "\\b(self(?:\\.[A-Za-z_][A-Za-z0-9_]*)+)@pre\\b");

    private AclOclTransitionEvaluator() {}

    static boolean preconditionsHold(List<ActivityConstraint> constraints,
                                     AclSystemState state, String selfId) {
        for (ActivityConstraint condition : constraints) {
            if (!evaluateSingle(condition.oclBody(), state, selfId)) return false;
        }
        return true;
    }

    static boolean postconditionsHold(List<ActivityConstraint> constraints,
                                      AclSystemState before, AclSystemState after,
                                      String selfId) {
        for (ActivityConstraint condition : constraints) {
            String expression = substituteAtPre(condition.oclBody(), before, selfId);
            if (!evaluateSingle(expression, after, selfId)) return false;
        }
        return true;
    }

    static boolean expressionHolds(String expression, AclSystemState state, String selfId) {
        return expression == null || expression.isBlank() || evaluateSingle(expression, state, selfId);
    }

    static boolean postExpressionHolds(String expression, AclSystemState before,
                                       AclSystemState after, String selfId) {
        return expression == null || expression.isBlank()
                || evaluateSingle(substituteAtPre(expression, before, selfId), after, selfId);
    }

    private static boolean evaluateSingle(String expression, AclSystemState state, String selfId) {
        List<Object> context = new ArrayList<>(1);
        if (selfId != null) {
            AclSystemState.ObjectValue self = state.object(selfId);
            if (self == null) return false;
            context.add(self);
        }
        return NativeOclEvaluator.evaluate(expression, state, context);
    }

    private static String substituteAtPre(String expression, AclSystemState before, String selfId) {
        if (expression == null || expression.isBlank() || !expression.contains("@pre")) return expression;
        Matcher matcher = AT_PRE_PATH.matcher(expression);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            Object value = resolveBeforePath(matcher.group(1), before, selfId);
            matcher.appendReplacement(result, Matcher.quoteReplacement(toOclLiteral(value)));
        }
        matcher.appendTail(result);
        if (result.indexOf("@pre") >= 0) {
            throw new IllegalArgumentException("unsupported @pre expression; expected self.<property-path>@pre");
        }
        return result.toString();
    }

    private static Object resolveBeforePath(String path, AclSystemState before, String selfId) {
        if (selfId == null) throw new IllegalArgumentException("@pre requires a bound process self object");
        Object value = before.object(selfId);
        if (value == null) throw new IllegalArgumentException("missing pre-state self object " + selfId);
        String[] parts = path.split("\\.");
        for (int i = 1; i < parts.length; i++) value = before.property(value, parts[i]);
        return value;
    }

    private static String toOclLiteral(Object value) {
        if (value instanceof Boolean || value instanceof Number) return String.valueOf(value);
        if (value instanceof Enum<?> enumeration) return "#" + enumeration.name();
        if (value instanceof String text) {
            if (text.matches("[A-Za-z_][A-Za-z0-9_]*")) return "#" + text;
            if (text.indexOf('\'') < 0) return "'" + text + "'";
        }
        throw new IllegalArgumentException("unsupported scalar @pre value: " + value);
    }
}
