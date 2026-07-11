package org.vnu.sme.goal.istar.view;

/**
 * Adapter: wraps one iStar 2.0 MM element (actor or intentional element) as a
 * placeable node. Position/size are mutable — the View (drag) and the
 * LayoutBuilder are the only writers; painting only reads them.
 */
public final class IStarNode {

    public final String      id;
    public final String      label;
    public final IStarNodeKind kind;
    public final String      actorId;      // owning actor id; null for ACTOR nodes themselves
    public final boolean     actorIsRole;  // meaningful only when kind == ACTOR (Role vs Agent glyph)
    public final boolean     actorBoundary;
    public final boolean     dependencyMarker;

    public int x, y, w, h;

    public IStarNode(String id, String label, IStarNodeKind kind, String actorId, boolean actorIsRole) {
        this(id, label, kind, actorId, actorIsRole, false, false);
    }

    public IStarNode(String id, String label, IStarNodeKind kind, String actorId,
                     boolean actorIsRole, boolean actorBoundary, boolean dependencyMarker) {
        this.id = id;
        this.label = label;
        this.kind = kind;
        this.actorId = actorId;
        this.actorIsRole = actorIsRole;
        this.actorBoundary = actorBoundary;
        this.dependencyMarker = dependencyMarker;
    }
}
