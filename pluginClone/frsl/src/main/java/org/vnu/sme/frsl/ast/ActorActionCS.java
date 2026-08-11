package org.vnu.sme.frsl.ast;

import java.util.ArrayList;

import org.vnu.sme.frsl.mm.FRSLmodel.Action;
import org.vnu.sme.frsl.mm.FRSLmodel.Actor;
import org.vnu.sme.frsl.mm.FRSLmodel.ActorAction;
import org.vnu.sme.frsl.mm.FRSLmodel.ObjVar;
import org.vnu.sme.frsl.parser.Context;

public class ActorActionCS extends ActionCS {
    /*
     * ----------------------------------------
     * 
     * ActorActionCS Properties.
     * 
     * ----------------------------------------
     */
    
    /*
     * ----------------------------------------
     * 
     * ActorActionCS Getters.
     * 
     * ----------------------------------------
     */
  

    /*
     * ----------------------------------------
     * 
     * ActorActionCS Setters.
     * 
     * ----------------------------------------
     */
   

    /*
     * ----------------------------------------
     * 
     * ActorActionCS Adders/Removers.
     * 
     * ----------------------------------------
     */
   

    /*
     * ----------------------------------------
     * 
     * ActorActionCS Constructors.
     * 
     * ----------------------------------------
     */
    public ActorActionCS() {
    }

    /*
     * ----------------------------------------
     * 
     * ActorAction Generator.
     * 
     * ----------------------------------------
     */
    @Override
    public Action visitPreOrder(Context ctx) {
        return _visitPreOrder(ctx);
    }

    private ActorAction _visitPreOrder(Context ctx) {
        
        Actor actor = null;

        // Since fActorCS is optional, we need to check if it is null.
        if (fActorCS != null) {
            actor = ctx.typeTableLookup(this.getfActorCS().getText(),
				Actor.class);
        }
    
        ArrayList<ObjVar> objVars = new ArrayList<>();
        for (ObjVarCS objVarCS : objVarsCS) {
            objVars.add(objVarCS.visitPreOrder(ctx));
        }
        return ctx.modelFactory().createActorAction(actor, objVars);    
    }
}
