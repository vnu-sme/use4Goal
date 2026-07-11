package org.vnu.sme.goal.istar.mm;

/**
 * Refinement that adds one implicit binding to the current instantiation context.
 * The element declaring the relation is the child; {@code parent} is the target
 * element receiving that child after expansion.
 */
public abstract sealed class ParameterRefinement implements Refinement
        permits ForRefinement, PickRefinement {

    private String parent;
    private String child;
    private String actorType;

    protected ParameterRefinement(String parent, String child, String actorType) {
        this.parent = parent;
        this.child = child;
        this.actorType = actorType;
    }

    @Override public String parent() { return parent; }
    public String child()            { return child; }
    public String actorType()        { return actorType; }

    public void setParent(String parent)     { this.parent = parent; }
    public void setChild(String child)       { this.child = child; }
    public void setActorType(String actorType) { this.actorType = actorType; }
}
