package org.vnu.sme.goal.istar.mm;

/** Quantified AND refinement: expand child once for every instance of actorType. */
public final class ForRefinement extends ParameterRefinement {

    public ForRefinement(String parent, String child, String actorType) {
        super(parent, child, actorType);
    }
}
