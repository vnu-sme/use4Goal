package org.vnu.sme.frsl.ast;

import java.util.ArrayList;

import org.antlr.runtime.Token;
import org.vnu.sme.frsl.mm.FRSLmodel.AltFlow;
import org.vnu.sme.frsl.mm.FRSLmodel.Step;
import org.vnu.sme.frsl.mm.FRSLmodel.UCStep;
import org.vnu.sme.frsl.mm.FRSLmodel.Usecase;
import org.vnu.sme.frsl.parser.Context;

public class UCStepCS extends StepCS {
    /*
     * ----------------------------------------
     * 
     * UCStepCS Properties.
     * 
     * ----------------------------------------
     */
    private String description;
    private Token fIncludedUC;

    /*
     * ----------------------------------------
     * 
     * UCStepCS Getters.
     * 
     * ----------------------------------------
     */

    public String getDescription() {
        return description;
    }

    public Token getfIncludedUC() {
        return fIncludedUC;
    }

    /*
     * ----------------------------------------
     * 
     * UCStepCS Setters.
     * 
     * ----------------------------------------
     */

    public void setDescription(String description) {
        this.description = description;
    }

    public void setfIncludedUC(Token fIncludedUC) {
        this.fIncludedUC = fIncludedUC;
    }

    /*
     * ----------------------------------------
     * 
     * UCStepCS Constructor.
     * 
     * ----------------------------------------
     */
    public UCStepCS() {
        super();
    }

    /*
     * ----------------------------------------
     * 
     * UCStep Generator.
     * 
     * ----------------------------------------
     */
    @Override
    public Step visitPreOrder(Context ctx, String useName, ArrayList<AltFlow> flow ) {
        return _visitPreOrder(ctx, useName, flow);
    }

    public UCStep _visitPreOrder(Context ctx, String useName, ArrayList<AltFlow> flow ) {
        Usecase currentUs = ctx.typeTableLookup(useName, Usecase.class);
        Usecase inclu = ctx.typeTableLookup(this.getfIncludedUC().getText(), Usecase.class);
        
        
        UCStep step = ctx.modelFactory().createUCStep(this.getName(), description, inclu, flow);

        currentUs.addIncludeUc(this.getName(), inclu);
        ctx.typeTableAdd(this.getfName(), step, useName);

        return step; // TODO 
    }
 }
