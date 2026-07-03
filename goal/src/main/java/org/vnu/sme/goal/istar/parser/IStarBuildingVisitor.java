package org.vnu.sme.goal.istar.parser;

import java.util.*;
import java.util.stream.Collectors;

import org.vnu.sme.goal.istar.ast.*;

/**
 * Walks the ANTLR parse tree and builds the iStar 2.0 AST (CS layer).
 * Use {@link IStarModelFactory} to convert the resulting AST to the MM layer.
 */
public final class IStarBuildingVisitor extends IStarBaseVisitor<Object> {

    private IStarModelCS model;

    public static IStarModelCS build(IStarParser.ModelContext ctx) {
        IStarBuildingVisitor v = new IStarBuildingVisitor();
        v.visitModel(ctx);
        return v.model;
    }

    // ── Root ─────────────────────────────────────────────────────────────────

    @Override
    public Object visitModel(IStarParser.ModelContext ctx) {
        List<ActorDefCS> actors = ctx.actorDef().stream()
                .map(a -> (ActorDefCS) visitActorDef(a))
                .collect(Collectors.toList());

        List<DependencyCS> deps = ctx.dependency().stream()
                .map(d -> (DependencyCS) visitDependency(d))
                .collect(Collectors.toList());

        model = new IStarModelCS(ctx.IDENT().getText(), actors, deps);
        return model;
    }

    // ── Actor ─────────────────────────────────────────────────────────────────

    @Override
    public Object visitActorDef(IStarParser.ActorDefContext ctx) {
        String id   = ctx.IDENT().getText();
        String kind = ctx.actorKind().getText();   // "actor" | "role" | "agent"

        List<ElementBodyCS> body = new ArrayList<>();
        for (IStarParser.ActorBodyContext b : ctx.actorBody()) {
            ElementBodyCS item = buildBodyItem(b);
            if (item != null) body.add(item);
        }
        return new ActorDefCS(id, kind, body);
    }

    private ElementBodyCS buildBodyItem(IStarParser.ActorBodyContext body) {
        return switch (body) {
            case IStarParser.BodyGoalContext      b -> new ElementBodyCS.GoalCS(b.IDENT().getText());
            case IStarParser.BodyTaskContext      b -> new ElementBodyCS.TaskCS(b.IDENT().getText());
            case IStarParser.BodyResourceContext  b -> new ElementBodyCS.ResourceCS(b.IDENT().getText());
            case IStarParser.BodyQualityContext   b -> new ElementBodyCS.QualityCS(b.IDENT().getText());
            case IStarParser.BodyAndRefineContext b -> {
                List<String> children = b.IDENT().stream()
                        .skip(1).map(t -> t.getText()).collect(Collectors.toList());
                yield new ElementBodyCS.AndRefineCS(b.IDENT(0).getText(), children);
            }
            case IStarParser.BodyOrRefineContext  b ->
                    new ElementBodyCS.OrRefineCS(b.IDENT(0).getText(), b.IDENT(1).getText());
            case IStarParser.BodyNeededByContext  b ->
                    new ElementBodyCS.NeededByCS(b.IDENT(0).getText(), b.IDENT(1).getText());
            case IStarParser.BodyContribContext   b ->
                    new ElementBodyCS.ContributionCS(
                            b.IDENT(0).getText(),
                            b.contribType().getText(),
                            b.IDENT(1).getText());
            case IStarParser.BodyQualifyContext   b ->
                    new ElementBodyCS.QualificationCS(b.IDENT(0).getText(), b.IDENT(1).getText());
            case IStarParser.BodyIsAContext        b ->
                    new ElementBodyCS.IsACS(b.IDENT(0).getText(), b.IDENT(1).getText());
            case IStarParser.BodyParticipatesContext b ->
                    new ElementBodyCS.ParticipatesCS(b.IDENT(0).getText(), b.IDENT(1).getText());
            default -> null;
        };
    }

    // ── Dependency ────────────────────────────────────────────────────────────

    @Override
    public Object visitDependency(IStarParser.DependencyContext ctx) {
        List<org.antlr.v4.runtime.tree.TerminalNode> ids = ctx.IDENT();
        return new DependencyCS(ids.get(0).getText(), ids.get(1).getText(), ids.get(2).getText());
    }
}
