package org.vnu.sme.goal.maxgoal.mm;

import java.util.List;

public record Actor(String name, ActorKind kind, List<Intentional> intentionals) {}
