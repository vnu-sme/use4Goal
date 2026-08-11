package org.vnu.sme.frsl.mm.FRSLmodel;

import java.util.Map;
import java.util.Set;

import org.tzi.use.uml.ocl.type.Type;
import org.tzi.use.uml.ocl.type.TypeImpl;

public class ExtensionPoint extends TypeImpl {

    private String name;
    private Map<String, Step> stepLocation;
    private String description;
    private SnapshotPattern condition;
   
    public ExtensionPoint (String name, String description, Map<String, Step> step, SnapshotPattern condition) {
        this.name = name;
        this.description = description;
        this.stepLocation = step;
        this.condition = condition;
    }

    /*
     * 
     * 
     * GETTER
     * 
     */

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Map<String, Step> getStepLocation() {
        return stepLocation;
    }

    public SnapshotPattern getCondition() {
        return condition;
    }

    /*
     * 
     * 
     * SETTER
     * 
     */

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStepLocation(Map<String, Step> stepLocation) {
        this.stepLocation = stepLocation;
    }

    public void setCondition(SnapshotPattern condition) {
        this.condition = condition;
    }

    @Override
    public Set<? extends Type> allSupertypes() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'allSupertypes'");
    }

    @Override
    public boolean equals(Object arg0) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'equals'");
    }

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'hashCode'");
    }

    @Override
    public StringBuilder toString(StringBuilder arg0) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'toString'");
    }
    
}
