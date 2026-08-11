package org.vnu.sme.frsl.mm.FRSLmodel;

import java.util.ArrayList;
import java.util.Set;

import org.tzi.use.uml.ocl.type.Type;

public abstract class ActStep extends Step {
    /*
     * ----------------------------------------
     * 
     * ActStep Properties.
     * 
     * ----------------------------------------
     */
    protected SnapshotPattern preSnapshot;
    protected SnapshotPattern postSnapshot;
    protected ArrayList<Action> actions;

    /*
     * ----------------------------------------
     * 
     * ActStep Getters.
     * 
     * ----------------------------------------
     */

    public SnapshotPattern getPreSnapshot() {
        return preSnapshot;
    }

    public SnapshotPattern getPostSnapshot() {
        return postSnapshot;
    }

    public ArrayList<Action> getActions() {
        return actions;
    }

    /*
     * ----------------------------------------
     * 
     * ActStep Setters.
     * 
     * ----------------------------------------
     */

    public void setPreSnapshot(SnapshotPattern preSnapshot) {
        this.preSnapshot = preSnapshot;
    }

    public void setPostSnapshot(SnapshotPattern postSnapshot) {
        this.postSnapshot = postSnapshot;
    }

    public void setActions(ArrayList<Action> actions) {
        this.actions = actions;
    }

    /*
     * ----------------------------------------
     * 
     * ActStep Constructors.
     * 
     * ----------------------------------------
     */
    public ActStep( String name, String description, SnapshotPattern preSnapshot,
            SnapshotPattern postSnapshot, ArrayList<Action> actions, ArrayList<AltFlow> flow) {
        super(flow, description);
        this.name = name;
        this.preSnapshot = preSnapshot;
        this.postSnapshot = postSnapshot;
        this.actions = actions;
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
        return   this.getName();
    }
}
