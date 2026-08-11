package org.vnu.sme.goal.dsl.istar.mm;

/** goalTaskElement --resolves--> obstacle */
public final class Resolution {

    private String element;
    private String obstacle;

    public Resolution(String element, String obstacle) {
        this.element  = element;
        this.obstacle = obstacle;
    }

    public String element()  { return element; }
    public String obstacle() { return obstacle; }

    public void setElement(String element)   { this.element = element; }
    public void setObstacle(String obstacle) { this.obstacle = obstacle; }
}
