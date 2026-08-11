package org.vnu.sme.goal.dsl.istar.mm;

/** Parameter refinement that chooses one actorType instance to satisfy the child. */
public final class PickRefinement extends ParameterRefinement {

    public PickRefinement(String parent, String child, String actorType) {
        super(parent, child, actorType);
    }
}
