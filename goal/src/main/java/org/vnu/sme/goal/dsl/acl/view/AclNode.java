package org.vnu.sme.goal.dsl.acl.view;

import java.util.List;

public final class AclNode {
    public final String id;
    public final String label;
    public final AclNodeKind kind;
    public final String subtitle;
    public final List<String> details;
    public int x, y, w, h;

    public AclNode(String id, String label, AclNodeKind kind, String subtitle, List<String> details) {
        this.id = id;
        this.label = label;
        this.kind = kind;
        this.subtitle = subtitle;
        this.details = List.copyOf(details);
    }
}
