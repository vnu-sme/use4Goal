package org.vnu.sme.goal.istar.mm;

import java.util.List;

/**
 * iStar 2.0 refinement links (both go from children → parent):
 *  - And: all children must be satisfied (T-shaped arrowhead at parent)
 *  - Or:  at least one child satisfies parent (solid arrow at parent)
 */
public sealed interface Refinement
        permits Refinement.And, Refinement.Or {

    String parent();

    record And(String parent, List<String> children) implements Refinement {}
    record Or (String parent, String child)          implements Refinement {}
}
