package org.vnu.sme.frsl.mm.FRSLmodel;

import java.util.ArrayList;
import java.util.Set;

import org.tzi.use.uml.ocl.type.Type;

public class RejoinStep extends Step {
    /*
     * ----------------------------------------
     * 
     * RejoinStep Properties.
     * 
     * ----------------------------------------
     */
    private Step rejoinTo;
    private SnapshotPattern condition;

    /*
     * ----------------------------------------
     * 
     * RejoinStep Getters.
     * 
     * ----------------------------------------
     */
    public Step getRejoinTo() {
        return rejoinTo;
    }

    public SnapshotPattern getCondition() {
        return condition;
    }

    /*
     * ----------------------------------------
     * 
     * RejoinStep Setters.
     * 
     * ----------------------------------------
     */
    public void setRejoinTo(Step rejoinTo) {
        this.rejoinTo = rejoinTo;
    }

    public void setCondition(SnapshotPattern condition) {
        this.condition = condition;
    }

    /*
     * ----------------------------------------
     * 
     * RejoinStep Constructors.
     * 
     * ----------------------------------------
     */
    public RejoinStep(String name, Step rejoinTo, String description, SnapshotPattern condition,  ArrayList<AltFlow> flow) {
        super(flow, description);
        this.name = name;
        this.rejoinTo = rejoinTo;
        this.condition = condition;
    }

    /*
     * ----------------------------------------
     *
     * TypeImpl Inherited Methods.
     * 
     * ----------------------------------------
     */

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
    @Override
    public String getOperationName() {
        // TODO Auto-generated method stub
        return this.getName() +" rejion to " + this.getRejoinTo().getOperationName();
    }
    
}
