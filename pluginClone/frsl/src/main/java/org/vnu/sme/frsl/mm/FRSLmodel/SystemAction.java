package org.vnu.sme.frsl.mm.FRSLmodel;

import java.util.ArrayList;
import java.util.Set;

import org.tzi.use.uml.ocl.type.Type;

public class SystemAction extends Action {
    /*
     * ----------------------------------------
     * 
     * SystemAction Properties.
     * 
     * ----------------------------------------
     */
    Actor actor;
    ArrayList<ObjVar> objVars;
    ArrayList<String> values;

    /*
     * ----------------------------------------
     * 
     * SystemAction Getters.
     * 
     * ----------------------------------------
     */
    public Actor getActor() {
        return actor;
    }

    public ArrayList<ObjVar> getObjVars() {
        return objVars;
    }

    public ArrayList<String> getValues() {
        return values;
    }

    /*
     * ----------------------------------------
     * 
     * SystemAction Setters.
     * 
     * ----------------------------------------
     */
    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public void setObjVars(ArrayList<ObjVar> objVars) {
        this.objVars = objVars;
    }

    public void setValues(ArrayList<String> values) {
        this.values = values;
    }

    /*
     * ----------------------------------------
     * 
     * SystemAction Constructors.
     * 
     * ----------------------------------------
     */
    public SystemAction(Actor actor, ArrayList<ObjVar> objVars, ArrayList<String> values) {
        this.actor = actor;
        this.objVars = objVars;
        this.values = values;
    }

    /*
     * ----------------------------------------
     * 
     * TypeImpl Inherited Methods.
     * 
     * ----------------------------------------
     */
    @Override
    public StringBuilder toString(StringBuilder sb) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'toString'");
    }
    
    @Override
    public Set<? extends Type> allSupertypes() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'allSupertypes'");
    }

    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'equals'");
    }

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'hashCode'");
    }
    
}
