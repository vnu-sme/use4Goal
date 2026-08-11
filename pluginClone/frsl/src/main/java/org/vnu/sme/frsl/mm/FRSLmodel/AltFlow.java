package org.vnu.sme.frsl.mm.FRSLmodel;

import java.util.Set;

import org.tzi.use.uml.ocl.type.Type;
import org.tzi.use.uml.ocl.type.TypeImpl;
import org.vnu.sme.frsl.view.Browser.PrintVisitor;

public class AltFlow extends TypeImpl implements UseType{
    /*
     * ----------------------------------------
     * 
     * AltFlow Properties.
     * 
     * ----------------------------------------
     */
    private String name;
    private String description;
    private Step baseStep;
    private SnapshotPattern condition;
    private Step firstStep;

    /*
     * ----------------------------------------
     * 
     * AltFlow Getters.
     * 
     * ----------------------------------------
     */
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Step getBaseStep() {
        return baseStep;
    }

    public SnapshotPattern getCondition() {
        return condition;
    }

    public Step getFirstStep() {
        return firstStep;
    }


    @Override
    public void visitPrint(PrintVisitor pv) {
        pv.visitPrintAltFlow(this);
        
    }


    /*
     * ----------------------------------------
     * 
     * AltFlow Setters.
     * 
     * ----------------------------------------
     */
    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setBaseStep(Step baseStep) {
        this.baseStep = baseStep;
    }

    public void setCondition(SnapshotPattern condition) {
        this.condition = condition;
    }

    public void setFirstStep(Step firstStep) {
        this.firstStep = firstStep;
    }

    /*
     * ----------------------------------------
     * 
     * AltFlow Constructors.
     * 
     * ----------------------------------------
     */
    public AltFlow(String name, String description, Step step, SnapshotPattern condition) {
        this.name = name;
        this.description = description;
        this.firstStep = step;
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
    public StringBuilder toString(StringBuilder sb) {
        // TODO Auto-generated method stub
        // try {
            String to = this.getName() + "at " + baseStep.getName();
            return sb.append(to);
    //     } catch (e) {
    //         throw new UnsupportedOperationException("Unimplemented method 'toString'");
    //     }
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
