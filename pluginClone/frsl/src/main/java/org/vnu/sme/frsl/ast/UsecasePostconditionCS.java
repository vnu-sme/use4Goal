package org.vnu.sme.frsl.ast;

import org.vnu.sme.frsl.mm.FRSLmodel.SnapshotPattern;
import org.vnu.sme.frsl.mm.FRSLmodel.UsecasePostcondition;
import org.vnu.sme.frsl.parser.Context;

public class UsecasePostconditionCS {
    /*
     * ----------------------------------------
     * 
     * UsecasePostconditionCS Properties.
     * 
     * ----------------------------------------
     */
    private String description;
    private SnapshotPatternCS snapshotCS;

    /*
     * ----------------------------------------
     * 
     * UsecaseCS Getters.
     * 
     * ----------------------------------------
     */
    public String getDescription() {
        return description;
    }

    public SnapshotPatternCS getSnapshotCS() {
        return snapshotCS;
    }

    /*
     * ----------------------------------------
     * 
     * UsecaseCS Setters.
     * 
     * ----------------------------------------
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public void setSnapshotCS(SnapshotPatternCS snapshotCS) {
        this.snapshotCS = snapshotCS;
    }

    /*
     * ----------------------------------------
     * 
     * UsecasePostconditionCS Constructors.
     * 
     * ----------------------------------------
     */
    public UsecasePostconditionCS() {
    }

    public UsecasePostconditionCS(String description, SnapshotPatternCS snapshotCS) {
        this.description = description;
        this.snapshotCS = snapshotCS;
    }

    /*
     * ----------------------------------------
     * 
     * UsecasePostcondition Generator.
     * 
     * ----------------------------------------
     */
    public UsecasePostcondition visitPreOrder(Context ctx) {
        SnapshotPattern snapshot = this.getSnapshotCS().visitPreOrder(ctx);

        UsecasePostcondition usecasePostcondition = ctx.modelFactory().createUsecasePostcondition(this.getDescription(),
                snapshot);

        return usecasePostcondition;
    }
}
