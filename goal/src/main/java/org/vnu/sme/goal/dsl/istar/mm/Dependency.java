package org.vnu.sme.goal.dsl.istar.mm;

/**
 * iStar 2.0 Social Dependency:
 *   depender --(D)--> dependum --(D)--> dependee
 *
 * dependum is the id of the mandatory DependumEle intentional element from the
 * metamodel. dependumKind is the concrete-syntax kind used by the compiler to
 * validate that the id resolves to the expected element subclass. dependerElmt/dependeeElmt are the
 * optional "boundary opening": the specific SR element inside depender's/
 * dependee's boundary that the dependency arrow attaches to, instead of the
 * actor circle itself. Null when not specified.
 */
public final class Dependency {

    private String depender;
    private String dependerElmt;
    private String dependumKind;
    private String dependum;
    private String dependee;
    private String dependeeElmt;

    public Dependency(String depender, String dependerElmt, String dependumKind, String dependum,
                       String dependee, String dependeeElmt) {
        this.depender     = depender;
        this.dependerElmt = dependerElmt;
        this.dependumKind = dependumKind;
        this.dependum     = dependum;
        this.dependee     = dependee;
        this.dependeeElmt = dependeeElmt;
    }

    public String depender()     { return depender; }
    public String dependerElmt() { return dependerElmt; }
    public String dependumKind() { return dependumKind; }
    public String dependum()     { return dependum; }
    public String dependee()     { return dependee; }
    public String dependeeElmt() { return dependeeElmt; }

    public void setDepender(String depender)         { this.depender = depender; }
    public void setDependerElmt(String dependerElmt) { this.dependerElmt = dependerElmt; }
    public void setDependumKind(String dependumKind) { this.dependumKind = dependumKind; }
    public void setDependum(String dependum)         { this.dependum = dependum; }
    public void setDependee(String dependee)         { this.dependee = dependee; }
    public void setDependeeElmt(String dependeeElmt) { this.dependeeElmt = dependeeElmt; }
}
