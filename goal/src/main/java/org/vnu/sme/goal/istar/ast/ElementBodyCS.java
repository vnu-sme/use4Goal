package org.vnu.sme.goal.istar.ast;

import java.util.List;

/**
 * Sealed hierarchy for the body items of an actor in iStar 2.0.
 * One variant per grammar alternative (intentional elements + links).
 * Fields store only what appears in the token stream — no semantics here.
 */
public sealed interface ElementBodyCS {

    /** goal <id> */
    record GoalCS(String id) implements ElementBodyCS {}

    /** task <id> */
    record TaskCS(String id) implements ElementBodyCS {}

    /** resource <id> */
    record ResourceCS(String id) implements ElementBodyCS {}

    /** quality <id> */
    record QualityCS(String id) implements ElementBodyCS {}

    /** and-refine <parent> : <child1>, <child2>, ... */
    record AndRefineCS(String parent, List<String> children) implements ElementBodyCS {}

    /** or-refine <parent> : <child> */
    record OrRefineCS(String parent, String child) implements ElementBodyCS {}

    /** needed-by <resource> for <task> */
    record NeededByCS(String resource, String task) implements ElementBodyCS {}

    /** contributes <element> make|help|hurt|break|unknown|some+|some- to <quality> */
    record ContributionCS(String element, String type, String quality) implements ElementBodyCS {}

    /** qualifies <quality> <element> */
    record QualificationCS(String quality, String element) implements ElementBodyCS {}

    /** <actor> is-a <target> */
    record IsACS(String actor, String target) implements ElementBodyCS {}

    /** <actor> participates-in <target> */
    record ParticipatesCS(String actor, String target) implements ElementBodyCS {}
}
