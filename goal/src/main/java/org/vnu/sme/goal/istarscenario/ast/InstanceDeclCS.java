package org.vnu.sme.goal.istarscenario.ast;

import java.util.List;

/** {@code instance c1, c2, c3 : ActorType;} — N objects of one actor type declared in the .istar model. */
public record InstanceDeclCS(List<String> names, String actorType) {
    public InstanceDeclCS {
        names = List.copyOf(names);
    }
}
