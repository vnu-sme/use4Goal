package org.vnu.sme.goal.dsl.istar.mm;

/** obstacle --obstructs--> goalTaskElement */
public final class Obstruction {

    private String obstacle;
    private String element;

    public Obstruction(String obstacle, String element) {
        this.obstacle = obstacle;
        this.element  = element;
    }

    public String obstacle() { return obstacle; }
    public String element()  { return element; }

    public void setObstacle(String obstacle) { this.obstacle = obstacle; }
    public void setElement(String element)   { this.element = element; }
}
