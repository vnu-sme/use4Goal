package org.vnu.sme.goal.istar.parser;

import java.util.*;

import org.vnu.sme.goal.istar.ast.*;
import org.vnu.sme.goal.istar.mm.*;

/**
 * Converts the iStar 2.0 AST (CS layer) into the runtime MetaModel (MM layer).
 * Dependency direction: factory → ast, factory → mm.  Neither ast nor mm knows the other.
 */
public final class IStarModelFactory {

    private IStarModelFactory() {}

    public static IStarModel build(IStarModelCS cs) {
        IStarModel model = new IStarModel(cs.name());

        for (ActorDefCS aCS : cs.actors()) {
            model.addActor(buildActor(aCS));
        }
        for (DependencyCS dCS : cs.dependencies()) {
            model.addDependency(new Dependency(dCS.depender(), dCS.dependum(), dCS.dependee()));
        }
        return model;
    }

    private static ActorDef buildActor(ActorDefCS cs) {
        ActorKind kind = ActorKind.from(cs.kind());

        List<IntentionalElement> elements    = new ArrayList<>();
        List<Refinement>         refinements = new ArrayList<>();
        List<Contribution>       contribs    = new ArrayList<>();
        List<Qualification>      qualifs     = new ArrayList<>();
        List<NeededBy>           neededBys   = new ArrayList<>();
        List<Association>        assocs      = new ArrayList<>();

        for (ElementBodyCS item : cs.body()) {
            switch (item) {
                case ElementBodyCS.GoalCS     e -> elements.add(new IntentionalElement.Goal(e.id()));
                case ElementBodyCS.TaskCS     e -> elements.add(new IntentionalElement.Task(e.id()));
                case ElementBodyCS.ResourceCS e -> elements.add(new IntentionalElement.Resource(e.id()));
                case ElementBodyCS.QualityCS  e -> elements.add(new IntentionalElement.Quality(e.id()));
                case ElementBodyCS.AndRefineCS e ->
                        refinements.add(new Refinement.And(e.parent(), e.children()));
                case ElementBodyCS.OrRefineCS  e ->
                        refinements.add(new Refinement.Or(e.parent(), e.child()));
                case ElementBodyCS.NeededByCS  e ->
                        neededBys.add(new NeededBy(e.resource(), e.task()));
                case ElementBodyCS.ContributionCS e ->
                        contribs.add(new Contribution(
                                e.element(), ContribType.from(e.type()), e.quality()));
                case ElementBodyCS.QualificationCS e ->
                        qualifs.add(new Qualification(e.quality(), e.element()));
                case ElementBodyCS.IsACS e ->
                        assocs.add(new Association(e.actor(), AssocKind.IS_A, e.target()));
                case ElementBodyCS.ParticipatesCS e ->
                        assocs.add(new Association(e.actor(), AssocKind.PARTICIPATES_IN, e.target()));
            }
        }
        return new ActorDef(cs.id(), kind, elements, refinements, contribs, qualifs, neededBys, assocs);
    }
}
