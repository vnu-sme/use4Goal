package org.vnu.sme.frsl.mm.FRSLmodel;

import java.util.ArrayList;
import java.util.Set;

import org.tzi.use.uml.ocl.type.Type;

public class UCStep extends Step {
    /*
     * ----------------------------------------
     * 
     * UCStep Properties.
     * 
     * ----------------------------------------
     */
    private Usecase includedUC;
    
    /*
     * ----------------------------------------
     * 
     * UCStep Getters.
     * 
     * ----------------------------------------
     */
    

    public Usecase getIncludedUC() {
        return includedUC;
    }

    /*
     * ----------------------------------------
     * 
     * UCStep Setters.
     * 
     * ----------------------------------------
     */
    

    public void setIncludedUC(Usecase includedUC) {
        this.includedUC = includedUC;
    }

    /*
     * ----------------------------------------
     * 
     * UCStep Constructors.
     * 
     * ----------------------------------------
     */
    public UCStep(String name, String description, Usecase includedUC, ArrayList<AltFlow> flow) {
        super(flow, description);
        this.name = name;
        this.includedUC = includedUC;
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
        return this.getName() + " include " + this.getIncludedUC().getName();
    }
}
