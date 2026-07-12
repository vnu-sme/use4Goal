package org.vnu.sme.goal.istarusebridge;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.tzi.use.parser.Symtable;
import org.tzi.use.parser.ocl.OCLCompiler;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.ocl.expr.Expression;
import org.vnu.sme.goal.istar.mm.Actor;
import org.vnu.sme.goal.istar.mm.GoalModel;
import org.vnu.sme.goal.istar.mm.GoalTaskElement;
import org.vnu.sme.goal.istar.mm.IntentionalElement;

/**
 * Compiles each Goal/Task's raw OCL guard (IStar.g4's {@code ocl {[ ... ]}} clause,
 * captured verbatim by IStarBuildingVisitor into {@link GoalTaskElement#oclSource()})
 * against the real .use MModel, using the actual USE OCL engine ({@link OCLCompiler}) --
 * not a reimplementation of it. self is bound via a {@link Symtable} exactly as
 * {@code ASTInvariantClause} does for a plain {@code context C inv:} clause in a
 * regular .use file.
 */
public final class IStarOclConstraintCompiler {

    public record ConstraintInfo(String elementId, String actorType, MClass contextClass, Expression expr) {}

    public record Result(Map<String, ConstraintInfo> constraints, List<String> errors) {
        public boolean ok() { return errors.isEmpty(); }
    }

    private IStarOclConstraintCompiler() {}

    public static Result compile(GoalModel gm, MModel useModel, ContextResolution resolution) {
        List<String> errors = new ArrayList<>();
        Map<String, ConstraintInfo> constraints = new LinkedHashMap<>();

        for (Actor actor : gm.getActors()) {
            for (IntentionalElement e : actor.elements()) {
                if (!(e instanceof GoalTaskElement gte)) continue;
                if (gte.oclSource() == null || gte.oclSource().isBlank()) continue;

                String actorType = resolution.actorTypeOf(gm, gte.id());
                MClass contextClass = UseActorClasses.resolve(gm, useModel, actorType, errors);
                if (contextClass == null) continue;

                StringWriter sw = new StringWriter();
                PrintWriter err = new PrintWriter(sw);
                Symtable vars = new Symtable();
                Expression expr;
                try {
                    vars.add("self", contextClass, null);
                    expr = OCLCompiler.compileExpression(useModel, gte.oclSource(), gte.id(), err, vars);
                } catch (org.tzi.use.parser.SemanticException ex) {
                    errors.add("ocl '" + gte.id() + "' (self : " + actorType + "): " + ex.getMessage());
                    continue;
                }
                err.flush();

                if (expr == null) {
                    errors.add("ocl '" + gte.id() + "' (self : " + actorType + "): " + sw);
                } else {
                    constraints.put(gte.id(), new ConstraintInfo(gte.id(), actorType, contextClass, expr));
                }
            }
        }
        return new Result(constraints, errors);
    }
}
