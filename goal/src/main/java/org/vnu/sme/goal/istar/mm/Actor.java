package org.vnu.sme.goal.istar.mm;

import java.util.List;

/** iStar 2.0 Actor is abstract — only {@link Agent} and {@link Role} are concrete. */
public sealed interface Actor permits Agent, Role {

    String name();
    List<IntentionalElement> elements();
    List<Refinement>         refinements();
    List<Contribution>       contributions();
    List<Qualification>      qualifications();
    List<NeededBy>            neededBys();
    List<Obstruction>         obstructions();
    List<Resolution>          resolutions();
    List<Association>        associations();
}
