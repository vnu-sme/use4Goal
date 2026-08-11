package org.vnu.sme.frsl.mm.FRSLmodel;

import java.util.ArrayList;

import org.tzi.use.uml.ocl.type.TypeImpl;

public abstract class Action extends TypeImpl {
    /*
     * ----------------------------------------
     * 
     * Action Properties.
     * 
     * ----------------------------------------
     */
    protected Actor actor;
    protected ArrayList<ObjVar> objVars;

    /*
     * ----------------------------------------
     * 
     * Action Getters.
     * 
     * ----------------------------------------
     */
    public Actor getActor() {
        return actor;
    }

    public ArrayList<ObjVar> getObjVars() {
        return objVars;
    }

    /*
     * 
     * ----------------------------------------
     * 
     * Action Setters.
     * 
     * ----------------------------------------
     */
    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public void setObjVars(ArrayList<ObjVar> objVars) {
        this.objVars = objVars;
    }

    /*
     * 
     * ----------------------------------------
     * 
     * Action Adders/Removers.
     * 
     * ----------------------------------------
     */
    public void addObjVar(ObjVar objVar) {
        if (objVars == null) {
            objVars = new ArrayList<ObjVar>();
        }
        objVars.add(objVar);
    }
}
