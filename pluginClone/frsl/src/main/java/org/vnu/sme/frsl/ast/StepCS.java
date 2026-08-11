package org.vnu.sme.frsl.ast;

import java.util.ArrayList;

import org.antlr.runtime.Token;
import org.vnu.sme.frsl.mm.FRSLmodel.AltFlow;
import org.vnu.sme.frsl.mm.FRSLmodel.Step;
import org.vnu.sme.frsl.parser.Context;

public abstract class StepCS {
    /*
     * ----------------------------------------
     * 
     * StepCS Properties.
     * 
     * ----------------------------------------
     */
    protected ArrayList<AltFlowCS> altFlowsCS;
    protected StepCS nextStepCS;
    protected Token fName;

    
    protected ArrayList<AltFlowCS> listAltflowCS;

    /*
     * ----------------------------------------
     * 
     * StepCS Getters.
     * 
     * ----------------------------------------
     */
    public ArrayList<AltFlowCS> getAltFlowsCS() {
        return altFlowsCS;
    }

    public ArrayList<AltFlowCS> getListAltFlowCS() {
        return listAltflowCS;
    }

    public StepCS getNextStepCS() {
        return nextStepCS;
    }

	/**
	 * Returns fName (the token).
	 * 
	 * @see #getName() Don't be mistaken with this (get-a-string).
	 * @return fName
	 */
	
    public Token getfName() {
		return fName;
	}
    
	/**
	 * Returns a string (that is embedded in fName).
	 * 
	 * @see #getfName() Don't be mistaken with this (get-a-token).
	 * @return getfName().getText()
	 */
	public String getName() {
		if (fName == null) {
			return "";
		}
		return fName.getText();
	}
    

    /*
     * ----------------------------------------
     * 
     * StepCS Setters.
     * 
     * ----------------------------------------
     */
    public void setAltFlowsCS(ArrayList<AltFlowCS> altFlowsCS) {
        this.altFlowsCS = altFlowsCS;
    }

    public void setListAltFlowsCS(ArrayList<AltFlowCS> altFlowsCS) {
        this.listAltflowCS= altFlowsCS;
    }

    public void setNextStepCS(StepCS nextStepCS) {
        this.nextStepCS = nextStepCS;
    }

	public void setfName(Token fName) {
		this.fName = fName;
	}
    /*
     * ----------------------------------------
     * 
     * StepCS Adders/Removers.
     * 
     * ----------------------------------------
     */
    public void addAltFlowCS(AltFlowCS altFlowCS) {
        if (altFlowsCS == null) {
            altFlowsCS = new ArrayList<AltFlowCS>();
        }
        altFlowsCS.add(altFlowCS);
    }

    public void addListAltFlowCS(AltFlowCS altFlowCS) {
        if (listAltflowCS == null) {
            listAltflowCS = new ArrayList<AltFlowCS>();
        }
        listAltflowCS.add(altFlowCS);
    }

    public void moveAltFlow(StepCS step) {
        for (AltFlowCS alt : step.getListAltFlowCS()) {
            if(alt.getfBaseStepCS().getText().equals(this.getfName().getText())) {
                this.addAltFlowCS(alt);
            } else {
                this.addListAltFlowCS(alt);
            }
        }
    }

    /*
     * ----------------------------------------
     * 
     * StepCS Constructors.
     * 
     * ----------------------------------------
     */
    public StepCS() {
        altFlowsCS = new ArrayList<AltFlowCS>();
        listAltflowCS= new ArrayList<AltFlowCS>();
    }

    /*
     * ----------------------------------------
     * 
     * Step Generator.
     * 
     * ----------------------------------------
     */
    public Step visitPreOrder(Context ctx, String useName) {
        // add alt flow
        ArrayList<AltFlow> flow = new ArrayList<AltFlow>();
        for (AltFlowCS flowCs : altFlowsCS) {
            flow.add(flowCs.visitPreOrder(ctx, useName));
        }
        
        Step step = visitPreOrder(ctx, useName, flow);

        for (AltFlow fl : flow) {
            fl.setBaseStep(step);
        }

        return step;
    }


    public abstract Step visitPreOrder(Context ctx, String useName, ArrayList<AltFlow> flow);
}
