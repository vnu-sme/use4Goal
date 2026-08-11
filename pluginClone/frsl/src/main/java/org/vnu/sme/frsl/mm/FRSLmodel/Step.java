package org.vnu.sme.frsl.mm.FRSLmodel;

import java.util.ArrayList;

import org.tzi.use.uml.ocl.type.TypeImpl;
import org.vnu.sme.frsl.view.Browser.PrintVisitor;

public abstract class Step extends TypeImpl implements UseType{
    /*
     * ----------------------------------------
     * 
     * Step Properties.
     * 
     * ----------------------------------------
     */
    protected ArrayList<AltFlow> altFlow;
    protected Step nextstep;
    protected String name;
    private String description;
    // nextStep

    /*
     * ----------------------------------------
     * 
     * Step Getters.
     * 
     * ----------------------------------------
     */

    public ArrayList<AltFlow> getAltFlow() {
        return altFlow;
    }

    public Step getNextstep() {
        return nextstep;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
    /*
     * ----------------------------------------
     * 
     * Step Setters.
     * 
     * ----------------------------------------
     */

    public void setAltFlow(ArrayList<AltFlow> altFlow) {
         this.altFlow = altFlow;
    }

    public void setNextstep(Step nextstep) {
         this.nextstep = nextstep;
     }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    /*
     * ----------------------------------------
     * 
     * Step Constructors.
     * 
     * ----------------------------------------
     */
    public Step( ArrayList<AltFlow> flow, String description) {
        this.altFlow = flow;
        this.description = description;
    }

    @Override
    public StringBuilder toString(StringBuilder sb) {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'toString'");
        return sb.append(this.getName());
    }

    /*
     * ----------------------------------------
     * 
     * Step add
     * 
     * ----------------------------------------
     */
    public void addAltFlow(AltFlow alt) {
        if(altFlow == null) {
            altFlow = new ArrayList<AltFlow>();
        }
        altFlow.add(alt);
    }

    @Override
	public void visitPrint(PrintVisitor pv) {
		pv.visitPrintStep(this);
	}

    public abstract String getOperationName();
}
