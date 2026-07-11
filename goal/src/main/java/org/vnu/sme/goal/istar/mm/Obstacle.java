package org.vnu.sme.goal.istar.mm;

/** iStar 2.0 Obstacle — obstructs a {@link GoalTaskElement}, resolved by one. */
public final class Obstacle implements ConcreteIntentionalElement {

    private String       id;
    private ObstacleType type;

    public Obstacle(String id, ObstacleType type) {
        this.id   = id;
        this.type = type;
    }

    @Override public String id() { return id; }
    public ObstacleType type()   { return type; }

    public void setId(String id)               { this.id = id; }
    public void setType(ObstacleType type)     { this.type = type; }
}
