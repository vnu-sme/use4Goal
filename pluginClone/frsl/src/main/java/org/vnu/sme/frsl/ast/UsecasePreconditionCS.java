package org.vnu.sme.frsl.ast;

import org.vnu.sme.frsl.mm.FRSLmodel.SnapshotPattern;
import org.vnu.sme.frsl.mm.FRSLmodel.UsecasePrecondition;
import org.vnu.sme.frsl.parser.Context;

public class UsecasePreconditionCS {
	/*
	 * ----------------------------------------
	 * 
	 * UsecasePreconditionCS Properties.
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
	 * UsecasePreconditionCS Constructors.
	 * 
	 * ----------------------------------------
	 */
	public UsecasePreconditionCS() {
	}

	public UsecasePreconditionCS(String description, SnapshotPatternCS snapshotCS) {
		this.description = description;
		this.snapshotCS = snapshotCS;
	}

	/*
	 * ----------------------------------------
	 * 
	 * UsecasePrecondition Generator.
	 * 
	 * ----------------------------------------
	 */
	public UsecasePrecondition visitPreOrder(Context ctx) {
		SnapshotPattern snapshot = this.getSnapshotCS().visitPreOrder(ctx);

		UsecasePrecondition usecasePrecondition = ctx.modelFactory().createUsecasePrecondition(this.getDescription(),
				snapshot);

		return usecasePrecondition;
	}
}
