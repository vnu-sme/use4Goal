package org.vnu.sme.frsl.ast;

import java.util.Map;
import java.util.TreeMap;

import org.antlr.runtime.Token;
import org.vnu.sme.frsl.mm.FRSLmodel.AltFlow;
import org.vnu.sme.frsl.mm.FRSLmodel.SnapshotPattern;
import org.vnu.sme.frsl.mm.FRSLmodel.Step;
import org.vnu.sme.frsl.parser.Context;

public class AltFlowCS {
    /*
     * ----------------------------------------
     * 
     * AltFlowCS Properties.
     * 
     * ----------------------------------------
     */
    private Token fName;
    private String description;
    private Token fBaseStepCS;
    private SnapshotPatternCS conditionCS;
    private StepCS altStepCS;

    /*
     * ----------------------------------------
     * 
     * AltFlowCS Getters.
     * 
     * ----------------------------------------
     */
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

    public String getDescription() {
        return description;
    }

    public Token getfBaseStepCS() {
        return fBaseStepCS;
    }

    public SnapshotPatternCS getConditionCS() {
        return conditionCS;
    }

    public StepCS getAltStepCS() {
        return altStepCS;
    }

    /*
     * ----------------------------------------
     * 
     * AltFlowCS Setters.
     * 
     * ----------------------------------------
     */
    public void setfName(Token fName) {
        this.fName = fName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setfBaseStepCS(Token fBaseStepCS) {
        this.fBaseStepCS = fBaseStepCS;
    }

    public void setConditionCS(SnapshotPatternCS conditionCS) {
        this.conditionCS = conditionCS;
    }

    public void setAltStepCS(StepCS altStepCS) {
        this.altStepCS = altStepCS;
    }

    /*
     * ----------------------------------------
     * 
     * AltFlowCS Constructors.
     * 
     * ----------------------------------------
     */
    public AltFlowCS() {
    }

    /*
     * ----------------------------------------
     * 
     * AltFlow Generator.
     * 
     * ----------------------------------------
     */
    public AltFlow visitPreOrder(Context ctx, String useName) {
        String name = getName();
        String description = getDescription();

        // TODO: Make up a better solution for this.

        SnapshotPattern condition = conditionCS.visitPreOrder(ctx);
        Step baseStep = altStepCS.visitPreOrder(ctx, useName);

        visitStep(ctx, baseStep, altStepCS);

        return ctx.modelFactory().createAltFlow(name, description, baseStep, condition);
    }

    public void visitStep(Context ctx, Step step, StepCS stepCs) {
        Map<String,Step> listStep = new TreeMap<String, Step>();
		
		listStep.put(step.getName(), step);

		StepCS localstepCs = stepCs;
		Step localStep = step;
		while (localstepCs.getNextStepCS() != null) {
			StepCS nextStepCs = localstepCs.getNextStepCS();
			Step nextstep = nextStepCs.visitPreOrder(ctx, this.getName());
			localStep.setNextstep(nextstep);
			
			
			// change local step
			localstepCs = nextStepCs;
			localStep = nextstep;
			listStep.put(nextstep.getName(), nextstep);
		}
    }



}
