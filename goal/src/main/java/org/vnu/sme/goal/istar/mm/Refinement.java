package org.vnu.sme.goal.istar.mm;

/**
 * iStar 2.0 refinement links (both go from children → parent):
 *  - {@link AndRefinement}: all children must be satisfied (T-shaped arrowhead at parent)
 *  - {@link OrRefinement}:  at least one child satisfies parent (solid arrow at parent)
 *  - {@link ParameterRefinement}: quantified template link expanded by scenarios
 */
public sealed interface Refinement
        permits AndRefinement, OrRefinement, ParameterRefinement {

    String parent();
}
