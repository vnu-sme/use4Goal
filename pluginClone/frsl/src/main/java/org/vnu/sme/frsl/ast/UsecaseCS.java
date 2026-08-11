package org.vnu.sme.frsl.ast;

import org.vnu.sme.frsl.mm.FRSLmodel.Actor;
import org.vnu.sme.frsl.mm.FRSLmodel.AltFlow;
import org.vnu.sme.frsl.mm.FRSLmodel.ExtensionPoint;
import org.vnu.sme.frsl.mm.FRSLmodel.ObjVar;
import org.vnu.sme.frsl.mm.FRSLmodel.Step;
import org.vnu.sme.frsl.mm.FRSLmodel.Usecase;
import org.vnu.sme.frsl.mm.FRSLmodel.UsecasePostcondition;
import org.vnu.sme.frsl.mm.FRSLmodel.UsecasePrecondition;

// TODO

import org.vnu.sme.frsl.parser.Context;

import org.antlr.runtime.Token;
import org.tzi.use.api.UseApiException;
import org.tzi.use.api.UseModelApi;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MOperation;
import org.tzi.use.uml.mm.MPrePostCondition;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

public class UsecaseCS {
	/*
	 * ----------------------------------------
	 * 
	 * UsecaseCS Properties.
	 * 
	 * ----------------------------------------
	 */
	// private FrslModelCS frslModelCS; // Removable?
	private Token fName;
	private String description;
	private Token fPrimaryActorCS;
	private ArrayList<Token> fSecondaryActorsCS = new ArrayList<>();;

	private UsecasePreconditionCS preconditionCS;
	private UsecasePostconditionCS postconditionCS;

	private StepCS firstStepCS;
	private ArrayList<ExtensionPointCS> extPointsCS;

	/*
	 * ----------------------------------------
	 * 
	 * UsecaseCS Getters.
	 * 
	 * ----------------------------------------
	 */
	// public FrslModelCS getFrslModelCS() {
	// return frslModelCS;
	// }

	/**
	 * Returns fName (the token).
	 * 
	 * @see #getName() Don't be mistaken with this (get-a-string).
	 * @return fName
	 */
	public Token getfName() {
		return fName;
	}

	/**
	 * Returns a string (that is embedded in fName).
	 * 
	 * @see #getfName() Don't be mistaken with this (get-a-token).
	 * @return getfName().getText()
	 */
	public String getName() {
		return getfName().getText();
	}

	public String getDescription() {
		return description;
	}

	public Token getfPrimaryActorCS() {
		return fPrimaryActorCS;
	}

	public ArrayList<Token> getfSecondaryActorsCS() {
		return fSecondaryActorsCS;
	}

	public UsecasePreconditionCS getPreconditionCS() {
		return preconditionCS;
	}

	public UsecasePostconditionCS getPostconditionCS() {
		return postconditionCS;
	}

	public StepCS getFirstStepCS() {
		return firstStepCS;
	}

	public ArrayList<ExtensionPointCS> getExtPointsCS() {
		return extPointsCS;
	}

	/*
	 * ----------------------------------------
	 * 
	 * UsecaseCS Getters.
	 * 
	 * ----------------------------------------
	 */
	// public void setFrslModelCS(FrslModelCS frslModelCS) {
	// this.frslModelCS = frslModelCS;
	// }

	public void setfName(Token fName) {
		this.fName = fName;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setfPrimaryActorCS(Token fPrimaryActorCS) {
		this.fPrimaryActorCS = fPrimaryActorCS;
	}

	public void setfSecondaryActorsCS(ArrayList<Token> fSecondaryActorsCS) {
		this.fSecondaryActorsCS = fSecondaryActorsCS;
	}

	public void setPreconditionCS(UsecasePreconditionCS preconditionCS) {
		this.preconditionCS = preconditionCS;
	}

	public void setPostconditionCS(UsecasePostconditionCS postconditionCS) {
		this.postconditionCS = postconditionCS;
	}

	public void setFirstStepCS(StepCS firstStepCS) {
		this.firstStepCS = firstStepCS;
	}

	public void setExtPointsCS(ArrayList<ExtensionPointCS> extPointsCS) {
		this.extPointsCS = extPointsCS;
	}

	/*
	 * ----------------------------------------
	 * 
	 * UsecaseCS Adders/Removers.
	 * 
	 * ----------------------------------------
	 */
	public void addfSecondaryActorCS(Token fSecondaryActorCS) {
		if (fSecondaryActorsCS == null) {
			fSecondaryActorsCS = new ArrayList<>();
		}

		this.fSecondaryActorsCS.add(fSecondaryActorCS);
	}

	public void addExtPointCS(ExtensionPointCS extPointCS) {
		if (extPointsCS == null) {
			extPointsCS = new ArrayList<>();
		}

		this.extPointsCS.add(extPointCS);
	}

	/*
	 * ----------------------------------------
	 * 
	 * UsecaseCS Constructors.
	 * 
	 * ----------------------------------------
	 */
	public UsecaseCS() {
		this.fSecondaryActorsCS = new ArrayList<>();
		this.extPointsCS = new ArrayList<>();
	}

	public UsecaseCS(Token fName) {
		this.fName = fName;
		this.extPointsCS = new ArrayList<ExtensionPointCS>();
	}

	/*
	 * ----------------------------------------
	 * 
	 * Usecase Generator.
	 * 
	 * ----------------------------------------
	 */

	public void visitStep(Context ctx, Step step, StepCS stepCs) {

		Step localStep = step;
		StepCS localstepCs = stepCs;

		while (localstepCs.getNextStepCS() != null) {
			// gen stepCs to step
			StepCS nextStepCs = localstepCs.getNextStepCS();
			Step nextstep = nextStepCs.visitPreOrder(ctx, this.getName());
			localStep.setNextstep(nextstep);

			// sout
			// System.out.println(" size alt flow " + nextStepCs.getfName().getText() + " "
			// + nextStepCs.getAltFlowsCS().size() + " " +
			// nextStepCs.getListAltFlowCS().size());
			// for (AltFlowCS alt : nextStepCs.getListAltFlowCS()) {
			// System.out.println(" alt name " + alt.getName() + " " +
			// alt.getfBaseStepCS().getText());
			// }
			// change local step
			localstepCs = nextStepCs;
			localStep = nextstep;
		}
	}

	public Usecase visitIntanceUC(Context ctx) {
		Usecase usecase = ctx.modelFactory().createUsecase(this.getName());
		ctx.typeTableAdd(this.getfName(), usecase);
		return usecase;
	}

	public Usecase visitPreOrder(Context ctx) {
		// FrslModel frslModel = ctx.getFrslModel();

		// Generate the primary and secondary actors.
		Actor primaryActor = ctx.typeTableLookup(this.fPrimaryActorCS.getText(),
				Actor.class);
		ArrayList<Actor> secondaryActors = new ArrayList<>();

		for (Token secondaryActorCS : fSecondaryActorsCS) {
			secondaryActors.add(ctx.typeTableLookup(secondaryActorCS.getText(),
					Actor.class));
		}

		Usecase usecase = ctx.typeTableLookup(this.getName(), Usecase.class);

		// Helper booleans for precondition and postcondition.
		boolean isPrecondAvailable = (this.getPreconditionCS() != null);
		boolean isPostcondAvailable = (this.getPostconditionCS() != null);

		// Generate the precondition and postcondition.
		UsecasePrecondition precondition = null;
		if (isPrecondAvailable) {
			precondition = this.getPreconditionCS().visitPreOrder(ctx);
		}

		UsecasePostcondition postcondition = null;
		if (isPostcondAvailable) {
			postcondition = this.getPostconditionCS().visitPreOrder(ctx);
		}

		// TODO: Implement the generation of the steps.
		// ADD IT TO THE USECASE.
		StepCS stepCs = this.getFirstStepCS();
		Step step = null;
		if (stepCs != null) {
			step = stepCs.visitPreOrder(ctx, this.getName());
			visitStep(ctx, step, stepCs);
		}

		// add extendPoint
		Map<String, ExtensionPoint> extend = new TreeMap<String, ExtensionPoint>();
		for (ExtensionPointCS extendCs : extPointsCS) {
			ExtensionPoint ext = extendCs.visitPreOrder(ctx, this.getName());
			extend.put(ext.getName(), ext);
		}

		// Generate the Usecase.
		usecase.add(this.getDescription(),
				primaryActor, secondaryActors, precondition,
				postcondition, step, extend);

		// TODO: Create a correspondence USE class for each generated Usecase.
		// mapUsecaseToUSEClass(ctx, precondition, postcondition, listStep);

		return usecase;
	}

	private void mapUsecaseToUSEClass(Context ctx, UsecasePrecondition precondition,
			UsecasePostcondition postcondition, ArrayList<Step> listStep) {
		// Helper booleans for precondition and postcondition.
		boolean isPrecondAvailable = (this.getPreconditionCS() != null);
		boolean isPostcondAvailable = (this.getPostconditionCS() != null);

		// Using the USE Model API.
		UseModelApi useModelApi = new UseModelApi(ctx.model());

		// Create a class for the Usecase.
		MClass UcAsUSEClass = null;
		try {
			UcAsUSEClass = useModelApi.createClass(this.getName(), false);
		} catch (UseApiException e) {
			e.printStackTrace();
		}

		// Map objVars -> attributes.
		HashSet<ObjVar> objVarsPrecond = new HashSet<>();
		HashSet<ObjVar> objVarsPostcond = new HashSet<>();
		HashSet<String> objVarsPrecondStr = new HashSet<>();
		HashSet<String> objVarsPostcondStr = new HashSet<>();

		if (precondition != null) {
			for (ObjVar objVar : precondition.getSnapshot().getObjects()) {
				objVarsPrecond.add(objVar);
				objVarsPrecondStr.add(objVar.toString());
			}
		}

		if (postcondition != null) {
			for (ObjVar objVar : postcondition.getSnapshot().getObjects()) {
				objVarsPostcond.add(objVar);
				objVarsPostcondStr.add(objVar.toString());
			}
		}

		// HashSet<ObjVar> objVars = new HashSet<>();
		// HashSet<String> objVarsStr = new HashSet<>();
		// objVars.addAll(objVarsPrecond);
		// objVars.addAll(objVarsPostcond);
		// objVarsStr.addAll(objVarsPrecondStr);
		// objVarsStr.addAll(objVarsPostcondStr);

		try {
			for (ObjVar objVar : objVarsPrecond) {
				useModelApi.createAttribute(this.getName(), objVar.getName(),
						objVar.getType().toString());
			}

			for (ObjVar objVar : objVarsPostcond) {
				useModelApi.createAttribute(this.getName(), objVar.getName(),
						objVar.getType().toString());
			}
		} catch (UseApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Map steps -> operations (boolean) .
		// TODO: Implement this.
		// TODO: Implement this.
		// TODO: Implement this.
		// TODO: Implement this.

		// Map pre/post conditions (boolean).

		// TODO: Check if MPrePost or MOp should implement the constraints.
		// Currently MPrePost; if MOp, it should be `createQueryOperation`

		// TODO: Fix the problem with "save as USE": the saved .use doesn't have
		// the given constraints.

		// Create enter/exit operations.
		MOperation enter_UC = null;
		MOperation exit_UC = null;
		try {
			enter_UC = useModelApi.createOperation(this.getName(),
					"enter_" + this.getName(), new String[0][0], "Boolean");
			exit_UC = useModelApi.createOperation(this.getName(),
					"exit_" + this.getName(), new String[0][0], "Boolean");
		} catch (UseApiException e) {
			e.printStackTrace();
		}

		try {
			int count = 1;
			DecimalFormat df = new DecimalFormat("00");
			for (Step step : listStep) {
				String stepname = df.format(count);
				useModelApi.createOperation(this.getName(),
						stepname + "_" + step.getOperationName(), new String[0][0], "Boolean");

				count++;
			}

		} catch (UseApiException e) {
			e.printStackTrace();
		}

		// Get the constraints from the pre/post conditions.
		ArrayList<ConstraintCS> precondConstraints = new ArrayList<>();
		if (isPrecondAvailable) {
			precondConstraints = this.getPreconditionCS().getSnapshotCS()
					.getConstraintsCS();
		}

		ArrayList<ConstraintCS> postcondConstraints = new ArrayList<>();
		if (isPostcondAvailable) {
			postcondConstraints = this.getPostconditionCS().getSnapshotCS()
					.getConstraintsCS();
		}

		// Merge the constraints.
		String mergedPrecondConstraints = "";
		String mergedPostcondConstraints = "";

		// Note: Currently ConstraintCS couldn't be resolved at abstract syntax level.
		if (precondConstraints.size() > 0) {
			for (int i = 0; i < precondConstraints.size(); i++) {
				mergedPrecondConstraints += precondConstraints.get(i).getfConstraint();
				if (i < precondConstraints.size() - 1) {
					mergedPrecondConstraints += " and ";
				}
			}
		}

		for (int i = 0; i < postcondConstraints.size(); i++) {
			mergedPostcondConstraints += postcondConstraints.get(i).getfConstraint();

			if (i < postcondConstraints.size() - 1) {
				mergedPostcondConstraints += " and ";
			}
		}

		// System.out.println("Pre -- DEBUG: " + mergedPrecondConstraints);
		// System.out.println("Post -- DEBUG: " + mergedPostcondConstraints);

		// Create pre/post conditions.
		MPrePostCondition precond = null;
		MPrePostCondition postcond = null;
		try {
			if (mergedPrecondConstraints.length() > 0) {
				precond = useModelApi.createPrePostCondition(this.getName(),
						"enter_" + this.getName(), "precond",
						mergedPrecondConstraints, true);
			}

			if (mergedPostcondConstraints.length() > 0) {
				postcond = useModelApi.createPrePostCondition(this.getName(),
						"exit_" + this.getName(), "postcond",
						mergedPrecondConstraints, false);
			}
		} catch (UseApiException e) {
			e.printStackTrace();

			// System.out.println("I know what you are talking about... Here is the list of
			// UC variables...");
			// for (MAttribute a : UcAsUSEClass.attributes()) {
			// System.out.println(a.toString());
			// }
		}
	}
}