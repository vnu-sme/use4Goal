package org.vnu.sme.frsl.ast;

import java.util.ArrayList;

import org.vnu.sme.frsl.mm.FRSLmodel.Actor;
import org.vnu.sme.frsl.mm.FRSLmodel.ObjVar;
import org.vnu.sme.frsl.mm.FRSLmodel.SystemAction;
import org.vnu.sme.frsl.parser.Context;

public class SystemActionCS extends ActionCS {
    /*
     * ----------------------------------------
     * 
     * SystemActionCS Properties.
     * 
     * ----------------------------------------
     */
    private ArrayList<String> fValuesCS;

    /*
     * ----------------------------------------
     * 
     * SystemActionCS Getters.
     * 
     * ----------------------------------------
     */

    public ArrayList<String> getfValuesCS() {
        return fValuesCS;
    }

    /*
     * ----------------------------------------
     * 
     * SystemActionCS Getters.
     * 
     * ----------------------------------------
     */
    public void setfValuesCS(ArrayList<String> fValuesCS) {
        this.fValuesCS = fValuesCS;
    }

    /*
     * ----------------------------------------
     * 
     * SystemActionCS Adders/Removers.
     * 
     * ----------------------------------------
     */
    public void addfValueCS(String fValueCS) {
        if (this.fValuesCS == null) {
            this.fValuesCS = new ArrayList<>();
        }
        this.fValuesCS.add(fValueCS);
    }

    /*
     * ----------------------------------------
     * 
     * SystemActionCS Constructors.
     * 
     * ----------------------------------------
     */
    public SystemActionCS() {
        this.fValuesCS = new ArrayList<>();
    }

    /*
     * ----------------------------------------
     * 
     * SystemAction Generator.
     * 
     * ----------------------------------------
     */
    @Override
    public SystemAction visitPreOrder(Context ctx) {
        return _visitPreOrder(ctx);
    }

    public SystemAction _visitPreOrder(Context ctx) {
        
        
        Actor actor = ctx.typeTableLookup(this.getfActorCS().getText(), Actor.class);
                       
        ArrayList<ObjVar> objVars = new ArrayList<>();
        for (ObjVarCS objVarCS : objVarsCS) {
            objVars.add(objVarCS.visitPreOrder(ctx));
        }
        
        return ctx.modelFactory().createSystemAction(actor, objVars, fValuesCS);
    }
}
