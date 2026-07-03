package org.vnu.sme.goal.maxgoal.mm;

import java.util.*;

public final class MAXGoalModel {

    private final String            name;
    private final List<Actor>       actors       = new ArrayList<>();
    private final List<Dependency>  dependencies = new ArrayList<>();

    private final Map<String, Intentional> intentionalMap = new HashMap<>();
    private final Map<String, String>      ownerMap       = new HashMap<>();

    public MAXGoalModel(String name) { this.name = name; }

    public String getName() { return name; }

    public void addActor(Actor a) {
        actors.add(a);
        for (Intentional i : a.intentionals()) {
            intentionalMap.put(i.name(), i);
            ownerMap.put(i.name(), a.name());
        }
    }

    public void addDependency(Dependency d) { dependencies.add(d); }

    public List<Actor>      getActors()       { return Collections.unmodifiableList(actors); }
    public List<Dependency> getDependencies() { return Collections.unmodifiableList(dependencies); }

    public Optional<Intentional> find(String id) {
        return Optional.ofNullable(intentionalMap.get(id));
    }

    public Optional<Actor> actorOf(String intentionalId) {
        String aname = ownerMap.get(intentionalId);
        if (aname == null) return Optional.empty();
        return actors.stream().filter(a -> a.name().equals(aname)).findFirst();
    }

    public Map<String, Intentional> allIntentionals() {
        return Collections.unmodifiableMap(intentionalMap);
    }
}
