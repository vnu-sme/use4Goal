package org.vnu.sme.frsl.ast;

import java.util.ArrayList;

import org.antlr.runtime.Token;
import org.vnu.sme.frsl.mm.FRSLmodel.ActStep;
import org.vnu.sme.frsl.mm.FRSLmodel.Action;
import org.vnu.sme.frsl.mm.FRSLmodel.AltFlow;
import org.vnu.sme.frsl.mm.FRSLmodel.SnapshotPattern;
import org.vnu.sme.frsl.mm.FRSLmodel.Step;
import org.vnu.sme.frsl.mm.FRSLmodel.SystemStep;
import org.vnu.sme.frsl.parser.Context;

public abstract class ActStepCS extends StepCS {
	/*
	 * ----------------------------------------
	 * 
	 * ActStepCS Properties.
	 * 
	 * ----------------------------------------
	 */
	protected Token fName;
	protected String description;
	protected SnapshotPatternCS preSnapshotCS;
	protected SnapshotPatternCS postSnapshotCS;
	protected ArrayList<ActionCS> actionsCS;

	/*
	 * ----------------------------------------
	 * 
	 * ActStepCS Getters.
	 * 
	 * ----------------------------------------
	 */



	public String getDescription() {
		return description;
	}

	public SnapshotPatternCS getPreSnapshotCS() {
		return preSnapshotCS;
	}

	public SnapshotPatternCS getPostSnapshotCS() {
		return postSnapshotCS;
	}

	public ArrayList<ActionCS> getActionsCS() {
		return actionsCS;
	}

	/*
	 * ----------------------------------------
	 * 
	 * ActStepCS Setters.
	 * 
	 * ----------------------------------------
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public void setPreSnapshotCS(SnapshotPatternCS preSnapshotCS) {
		this.preSnapshotCS = preSnapshotCS;
	}

	public void setPostSnapshotCS(SnapshotPatternCS postSnapshotCS) {
		this.postSnapshotCS = postSnapshotCS;
	}

	public void setActionsCS(ArrayList<ActionCS> actionsCS) {
		this.actionsCS = actionsCS;
	}

	/*
	 * ----------------------------------------
	 * 
	 * ActStepCS Adders/Removers.
	 * 
	 * ----------------------------------------
	 */
	public void addActionCS(ActionCS actionCS) {
		if (this.actionsCS == null) {
			this.actionsCS = new ArrayList<>();
		}
		this.actionsCS.add(actionCS);
	}

	/*
	 * ----------------------------------------
	 * 
	 * ActStepCS Constructors.
	 * 
	 * ----------------------------------------
	 */
	public ActStepCS() {
		super();
		this.actionsCS = new ArrayList<>();
	}

	@Override
	public Step visitPreOrder(Context ctx, String useName, ArrayList<AltFlow> flow) {
		// TODO Auto-generated method stub
		return _visitPreOrder(ctx, useName, flow);
	}

	private ActStep _visitPreOrder(Context ctx, String useName, ArrayList<AltFlow> flow) {
		SnapshotPattern preSnapshot = preSnapshotCS.visitPreOrder(ctx);
		SnapshotPattern postSnapshot = postSnapshotCS.visitPreOrder(ctx);

		// add action
		ArrayList<Action> actions = new ArrayList<>();
		if (actionsCS != null) {
			for (ActionCS actionCS : actionsCS) {
				actions.add(actionCS.visitPreOrder(ctx));
			}
		}

		return visitActStep(ctx, useName, actions, flow, preSnapshot, postSnapshot);
	}

	abstract ActStep visitActStep(Context ctx, String useName,
			ArrayList<Action> actions, ArrayList<AltFlow> flow, SnapshotPattern preSnapshot, SnapshotPattern postSnapshot ) ;



}
