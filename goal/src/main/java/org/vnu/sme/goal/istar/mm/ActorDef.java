package org.vnu.sme.goal.istar.mm;

import java.util.List;

public final class ActorDef {

    private final String              name;
    private final ActorKind           kind;
    private final List<IntentionalElement> elements;
    private final List<Refinement>    refinements;
    private final List<Contribution>  contributions;
    private final List<Qualification> qualifications;
    private final List<NeededBy>      neededBys;
    private final List<Association>   associations;

    public ActorDef(String name, ActorKind kind,
                    List<IntentionalElement> elements,
                    List<Refinement>    refinements,
                    List<Contribution>  contributions,
                    List<Qualification> qualifications,
                    List<NeededBy>      neededBys,
                    List<Association>   associations) {
        this.name           = name;
        this.kind           = kind;
        this.elements       = List.copyOf(elements);
        this.refinements    = List.copyOf(refinements);
        this.contributions  = List.copyOf(contributions);
        this.qualifications = List.copyOf(qualifications);
        this.neededBys      = List.copyOf(neededBys);
        this.associations   = List.copyOf(associations);
    }

    public String              name()           { return name; }
    public ActorKind           kind()           { return kind; }
    public List<IntentionalElement> elements()  { return elements; }
    public List<Refinement>    refinements()    { return refinements; }
    public List<Contribution>  contributions()  { return contributions; }
    public List<Qualification> qualifications() { return qualifications; }
    public List<NeededBy>      neededBys()      { return neededBys; }
    public List<Association>   associations()   { return associations; }
}
