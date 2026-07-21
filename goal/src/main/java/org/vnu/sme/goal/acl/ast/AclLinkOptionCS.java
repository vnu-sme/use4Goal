package org.vnu.sme.goal.acl.ast;

import java.util.Objects;

public sealed interface AclLinkOptionCS permits AclLinkOptionCS.ScopeCS,
        AclLinkOptionCS.ExtendsSubgroupsCS, AclLinkOptionCS.BidirectionalCS, AclLinkOptionCS.TypeCS {
    AclSourceLocationCS location();

    record ScopeCS(String value, AclSourceLocationCS location) implements AclLinkOptionCS {
        public ScopeCS {
            Objects.requireNonNull(value, "value");
            Objects.requireNonNull(location, "location");
        }
    }

    record ExtendsSubgroupsCS(boolean value, AclSourceLocationCS location) implements AclLinkOptionCS {
        public ExtendsSubgroupsCS { Objects.requireNonNull(location, "location"); }
    }

    record BidirectionalCS(boolean value, AclSourceLocationCS location) implements AclLinkOptionCS {
        public BidirectionalCS { Objects.requireNonNull(location, "location"); }
    }

    /** compatibility ... type compatible|incompatible; */
    record TypeCS(String value, AclSourceLocationCS location) implements AclLinkOptionCS {
        public TypeCS {
            Objects.requireNonNull(value, "value");
            Objects.requireNonNull(location, "location");
        }
    }
}
