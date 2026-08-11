package org.vnu.sme.tocl.mm;

import org.tzi.use.uml.mm.MModel;

public class TOCLModel {
    /**
     * TOCL properties
     */
    private MModel domainModel;
    // add more properties here
    
    /**
     * TOCL model constructor
     */
    public TOCLModel(MModel domainModel) {
        this.domainModel = domainModel;
    }
    
    /**
     * TOCL model getter
     */
    public MModel getDomainModel() {
        return domainModel;
    }
    
    /**
     * TOCL model setter
     */
    public void setDomainModel(MModel domainModel) {
        this.domainModel = domainModel;
    }
    
    // add more methods here
    
}
