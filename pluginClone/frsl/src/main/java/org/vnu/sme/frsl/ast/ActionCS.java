package org.vnu.sme.frsl.ast;

import java.util.ArrayList;

import org.antlr.runtime.Token;
import org.vnu.sme.frsl.mm.FRSLmodel.Action;
import org.vnu.sme.frsl.parser.Context;

public abstract class ActionCS {
    /*
     * ----------------------------------------
     * 
     * ActionCS Properties.
     * 
     * ----------------------------------------
     */
    protected Token fActorCS;
    protected ArrayList<ObjVarCS> objVarsCS;


    /*
     * ----------------------------------------
     * 
     * ActionCS Getters.
     * 
     * ----------------------------------------
     */
    public Token getfActorCS() {
        return fActorCS;
    }

    public ArrayList<ObjVarCS> getObjVarsCS() {
        return objVarsCS;
    }

    /*
     * 
     * ----------------------------------------
     * 
     * ActionCS Setters.
     * 
     * ----------------------------------------
     */
    public void setfActorCS(Token fActorCS) {
        this.fActorCS = fActorCS;
    }

    public void setObjVarsCS(ArrayList<ObjVarCS> objVarsCS) {
        this.objVarsCS = objVarsCS;
    }

    /*
     * 
     * ----------------------------------------
     * 
     * ActionCS Adders/Removers.
     * 
     * ----------------------------------------
     */
    public void addObjVarCS(ObjVarCS objVarCS) {
        if (this.objVarsCS == null) {
            this.objVarsCS = new ArrayList<>();
        }
        this.objVarsCS.add(objVarCS);
    }

    /*
     * 
     * 
     * ----------------------------------------
     * 
     * ActionCS Constructors.
     * 
     * ----------------------------------------
     */
    public ActionCS() {
        this.objVarsCS = new ArrayList<>();
    }

    /*
     * ----------------------------------------
     * 
     * Action Generator.
     * 
     * ----------------------------------------
     */
    public abstract Action visitPreOrder(Context ctx);
}
