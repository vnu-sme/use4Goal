package org.vnu.sme.frsl.ast;

import java.util.ArrayList;

import org.antlr.runtime.Token;
import org.vnu.sme.frsl.mm.FRSLmodel.AltFlow;
import org.vnu.sme.frsl.mm.FRSLmodel.RejoinStep;
import org.vnu.sme.frsl.mm.FRSLmodel.SnapshotPattern;
import org.vnu.sme.frsl.mm.FRSLmodel.Step;
import org.vnu.sme.frsl.parser.Context;

public class RejoinStepCS extends StepCS {
    /*
     * ----------------------------------------
     * 
     * RejoinStepCS Properties.
     * 
     * ----------------------------------------
     */
    private Token fRejoinToCS;
    private String description;
    private SnapshotPatternCS conditionCS;

    /*
     * ----------------------------------------
     * 
     * RejoinStepCS Getters.
     * 
     * ----------------------------------------
     */
    public Token getfRejoinToCS() {
        return fRejoinToCS;
    }

    public String getDescription() {
        return description;
    }

    public SnapshotPatternCS getConditionCS() {
        return conditionCS;
    }

    /*
     * ----------------------------------------
     * 
     * RejoinStepCS Setters.
     * 
     * ----------------------------------------
     */
    public void setfRejoinToCS(Token fRejoinToCS) {
        this.fRejoinToCS = fRejoinToCS;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setConditionCS(SnapshotPatternCS fConditionCS) {
        this.conditionCS = fConditionCS;
    }

    /*
     * ----------------------------------------
     * 
     * RejoinStepCS Constructors.
     * 
     * ----------------------------------------
     */
    public RejoinStepCS(Token fRejoinToCS) {
        super();
        this.fRejoinToCS = fRejoinToCS;
    }

    /*
     * ----------------------------------------
     *
     * RejoinStep Generator.
     *
     * ----------------------------------------
     */
    @Override
    public Step visitPreOrder(Context ctx, String useName, ArrayList<AltFlow> flow) {
        return _visitPreOrder(ctx, useName, flow);
    }

    public RejoinStep _visitPreOrder(Context ctx, String useName, ArrayList<AltFlow> flow) {
        Step rejoinTo = ctx.typeTableLookup(this.getfRejoinToCS().getText()+useName, Step.class);
        SnapshotPattern condition = conditionCS.visitPreOrder(ctx);

        RejoinStep step =  ctx.modelFactory().createRejoinStep(this.getName(), rejoinTo, description, condition, flow);
        // ctx.typeTableAdd(this.get, step);
        
        return step;
    }
}
