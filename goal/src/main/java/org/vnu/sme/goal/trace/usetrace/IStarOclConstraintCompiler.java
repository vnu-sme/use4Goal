package org.vnu.sme.goal.trace.usetrace;

import org.vnu.sme.goal.dsl.istar.mm.ContextResolution;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tzi.use.parser.Symtable;
import org.tzi.use.parser.ocl.OCLCompiler;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.ocl.expr.Expression;
import org.vnu.sme.goal.dsl.istar.mm.Actor;
import org.vnu.sme.goal.dsl.istar.mm.GoalModel;
import org.vnu.sme.goal.dsl.istar.mm.GoalTaskElement;
import org.vnu.sme.goal.dsl.istar.mm.IntentionalElement;
import org.vnu.sme.goal.dsl.acl.mm.AclModel;
import org.vnu.sme.goal.dsl.acl.ocl.AclOclPropertyResolver;

/**
 * Compiles each Goal/Task's raw OCL guard (IStar.g4's {@code ocl {[ ... ]}} clause,
 * captured verbatim by IStarBuildingVisitor into {@link GoalTaskElement#oclSource()})
 * against the real .use MModel, using the actual USE OCL engine ({@link OCLCompiler}) --
 * not a reimplementation of it. self is bound via a {@link Symtable} exactly as
 * {@code ASTInvariantClause} does for a plain {@code context C inv:} clause in a
 * regular .use file.
 */
public final class IStarOclConstraintCompiler {

    public record ConstraintInfo(String elementId, String actorType, List<String> contextTypes, MClass contextClass,
                                 Expression expr, List<Expression> preconditions) {}

    private static final Pattern CONTEXT_PARENT = Pattern.compile(
            "\\bself((?:\\s*\\.\\s*outer)+)\\b");

    public record Result(Map<String, ConstraintInfo> constraints, List<String> errors) {
        public boolean ok() { return errors.isEmpty(); }
    }

    private IStarOclConstraintCompiler() {}

    public static Result compile(GoalModel gm, MModel useModel, ContextResolution resolution) {
        return compile(gm, useModel, resolution, null);
    }

    public static Result compile(GoalModel gm, MModel useModel, ContextResolution resolution, AclModel acl) {
        List<String> errors = new ArrayList<>();
        Map<String, ConstraintInfo> constraints = new LinkedHashMap<>();
        Map<String, MClass> actorClasses = new LinkedHashMap<>();
        for (Actor actor : gm.getActors()) {
            MClass cls = UseActorClasses.resolve(gm, useModel, actor.name(), errors);
            if (cls != null) actorClasses.put(actor.name(), cls);
        }

        for (Actor actor : gm.getActors()) {
            for (IntentionalElement e : actor.elements()) {
                if (!(e instanceof GoalTaskElement gte)) continue;
                boolean hasSatisfaction = gte.oclSource() != null && !gte.oclSource().isBlank();
                if (!hasSatisfaction) continue;

                String actorType = resolution.actorTypeOf(gm, gte.id());
                List<String> contextTypes = resolution.contextTypesOf(gm, gte.id());
                MClass contextClass = UseActorClasses.resolve(gm, useModel, actorType, errors);
                if (contextClass == null) continue;

                int preIndex = 1;
                List<Expression> compiledPreconditions = new ArrayList<>();
                for (var pre : gte.preconditions()) {
                    String label = gte.id() + "::pre#" + preIndex++;
                    Expression compiled = compileExpression(useModel,
                            resolvedSource(acl, actorType, contextTypes, pre.oclBody(), label, errors), label,
                            actorType, contextClass, actorClasses, errors);
                    if (compiled != null) compiledPreconditions.add(compiled);
                }
                StringWriter sw = new StringWriter();
                PrintWriter err = new PrintWriter(sw);
                Symtable vars = new Symtable();
                Expression expr;
                try {
                    vars.add("self", contextClass, null);
                    for (var actorClass : actorClasses.entrySet()) {
                        vars.add(lowerFirst(actorClass.getKey()), actorClass.getValue(), null);
                    }
                    String resolvedSource = resolvedSource(
                            acl, actorType, contextTypes, gte.oclSource(), gte.id(), errors);
                    expr = OCLCompiler.compileExpression(useModel, resolvedSource, gte.id(), err, vars);
                } catch (org.tzi.use.parser.SemanticException ex) {
                    errors.add("ocl '" + gte.id() + "' (self : " + actorType + "): " + ex.getMessage());
                    continue;
                }
                err.flush();

                if (expr == null) {
                    errors.add("ocl '" + gte.id() + "' (self : " + actorType + "): " + sw);
                } else {
                    constraints.put(gte.id(), new ConstraintInfo(gte.id(), actorType, contextTypes, contextClass,
                            expr, List.copyOf(compiledPreconditions)));
                }
            }
        }
        return new Result(constraints, errors);
    }

    private static String resolvedSource(AclModel acl, String actorType, List<String> contextTypes,
                                         String source, String label, List<String> errors) {
        Matcher matcher = CONTEXT_PARENT.matcher(source);
        StringBuffer rewritten = new StringBuffer();
        while (matcher.find()) {
            int depth = (int) Pattern.compile("outer").matcher(matcher.group(1)).results().count();
            if (depth >= contextTypes.size()) {
                errors.add("ocl '" + label + "': self.outer depth " + depth
                        + " exceeds context stack " + contextTypes);
                matcher.appendReplacement(rewritten, "self");
            } else {
                matcher.appendReplacement(rewritten,
                        Matcher.quoteReplacement(lowerFirst(contextTypes.get(depth))));
            }
        }
        matcher.appendTail(rewritten);
        Map<String, String> contextualVariables = new LinkedHashMap<>();
        for (String type : contextTypes) contextualVariables.put(lowerFirst(type), type);
        return AclOclPropertyResolver.rewrite(
                acl, actorType, contextualVariables, rewritten.toString());
    }

    private static Expression compileExpression(MModel useModel, String source, String label, String actorType,
            MClass contextClass, Map<String, MClass> actorClasses, List<String> errors) {
        StringWriter sw = new StringWriter();
        PrintWriter err = new PrintWriter(sw);
        Symtable vars = new Symtable();
        try {
            vars.add("self", contextClass, null);
            for (var actorClass : actorClasses.entrySet()) {
                vars.add(lowerFirst(actorClass.getKey()), actorClass.getValue(), null);
            }
            Expression expr = OCLCompiler.compileExpression(useModel, source, label, err, vars);
            err.flush();
            if (expr == null) errors.add("ocl '" + label + "' (self : " + actorType + "): " + sw);
            return expr;
        } catch (org.tzi.use.parser.SemanticException ex) {
            errors.add("ocl '" + label + "' (self : " + actorType + "): " + ex.getMessage());
            return null;
        }
    }

    private static String lowerFirst(String value) {
        if (value == null || value.isEmpty()) return value;
        return Character.toLowerCase(value.charAt(0)) + value.substring(1);
    }
}
