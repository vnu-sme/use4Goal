package org.vnu.sme.frsl.mm.FRSLmodel;

import java.util.Set;
import java.util.ArrayList;

import org.tzi.use.uml.ocl.type.Type;

public class ActorAction extends Action {
    /*
     * ----------------------------------------
     * 
     * ActorAction Constructors.
     * 
     * ----------------------------------------
     */
    public ActorAction(Actor actor, ArrayList<ObjVar> objVars) {
        this.actor = actor;
        this.objVars = objVars;
    }

    /*
     * ----------------------------------------
     * 
     * TypeImple Inherited Methods.
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
