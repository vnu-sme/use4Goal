package org.vnu.sme.frsl.mm.FRSLmodel;

import java.util.Map;
import java.util.Set;

import org.tzi.use.uml.ocl.type.Type;
import org.tzi.use.uml.ocl.type.TypeImpl;

public class Extend extends TypeImpl {

    private Usecase fExtendstion; 
    private Usecase fExtendedUC;

    private Map<String, ExtensionPoint> fExtendPoint;

    public Extend (Usecase extend, Usecase extendUC, Map<String, ExtensionPoint> extendPoint ) {
        this.fExtendstion = extend;
        this.fExtendedUC = extendUC;
        this.fExtendPoint = extendPoint;
    }

    /*
     * 
     * GETTER
     * 
     */

    public Usecase getfExtendstion() {
        return fExtendstion;
    }

    public Usecase getfExtendedUC() {
        return fExtendedUC;
    }

    public Map<String, ExtensionPoint> getfExtendPoint() {
        return fExtendPoint;
    }

    /*
     * 
     * 
     * SETTER
     * 
     */

    public void setfExtendstion(Usecase fExtendstion) {
        this.fExtendstion = fExtendstion;
    }

    public void setfExtendedUC(Usecase fExtendedUC) {
        this.fExtendedUC = fExtendedUC;
    }

    public void setfExtendPoint(Map<String, ExtensionPoint> fExtendPoint) {
        this.fExtendPoint = fExtendPoint;
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
