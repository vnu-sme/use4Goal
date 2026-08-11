package org.vnu.sme.frsl.ast;

import java.util.ArrayList;

import org.vnu.sme.frsl.mm.FRSLmodel.Extend;
import org.vnu.sme.frsl.mm.FRSLmodel.FrslModel;
import org.vnu.sme.frsl.mm.FRSLmodel.Usecase;
import org.vnu.sme.frsl.parser.Context;
// import org.tzi.use.parser.use.ASTModel;
import org.tzi.use.uml.mm.MInvalidModelException;
import org.tzi.use.uml.sys.MSystemException;
import org.antlr.runtime.Token;

public class FrslModelCS 
// extends ASTModel 
{
	/*
	 * ----------------------------------------
	 * 
	 * FrslModelCS Properties.
	 * 
	 * ----------------------------------------
	 */
	// protected ASTModel domainModel;
	private final Token fName;
	private ArrayList<ActorCS> actorsCS;
	private ArrayList<UsecaseCS> usecasesCS;
	private ArrayList<ExtendCS> ucExtendsCS;
	private ArrayList<IncludeCS> ucIncludesCs;

	/*
	 * ----------------------------------------
	 * 
	 * FrslModelCS Getters.
	 * 
	 * ----------------------------------------
	 */
	public Token getfName () {
		return this.fName;
	}
	
	public ArrayList<ActorCS> getActorsCS() {
		return actorsCS;
	}

	public ArrayList<UsecaseCS> getUsecasesCS() {
		return usecasesCS;
	}

	public ArrayList<ExtendCS> getUcExtendsCS() {
		return ucExtendsCS;
	}

	public ArrayList<IncludeCS> getUcIncludeCs() {
		return ucIncludesCs;
	}

	/*
	 * ----------------------------------------
	 * 
	 * FrslModelCS Setters.
	 * 
	 * ----------------------------------------
	 */
	public void setActorsCS(ArrayList<ActorCS> actorsCS) {
		this.actorsCS = actorsCS;
	}

	public void setUsecasesCS(ArrayList<UsecaseCS> usecasesCS) {
		this.usecasesCS = usecasesCS;
	}

	public void setUcExtendsCS(ArrayList<ExtendCS> ucExtendsCS) {
		this.ucExtendsCS = ucExtendsCS;
	}

	public void setUcIncludeCs(ArrayList<IncludeCS> ucIncludeCs) {
		this.ucIncludesCs = ucIncludeCs;
	}

	/*
	 * ----------------------------------------
	 * 
	 * FrslModelCS Adders/Removers.
	 * 
	 * ----------------------------------------
	 */
	public void addActorCS(ActorCS actorCS) {
		if (actorCS == null) {
			actorsCS = new ArrayList<ActorCS>();
		}
		actorsCS.add(actorCS);
	}

	public void addUsecaseCS(UsecaseCS usecaseCS) {
		if (usecaseCS == null) {
			usecasesCS = new ArrayList<UsecaseCS>();
		}
		usecasesCS.add(usecaseCS);
	}

	public void addUcExtendCS(ExtendCS ucExtendCS) {
		if (ucExtendCS == null) {
			ucExtendsCS = new ArrayList<ExtendCS>();
		}
		ucExtendsCS.add(ucExtendCS);
	}

	public void addUcIncludeCS(IncludeCS ucIncludeCS) {
		if (ucIncludeCS == null) {
			ucIncludesCs = new ArrayList<IncludeCS>();
		}
		ucIncludesCs.add(ucIncludeCS);
	}

	/*
	 * ----------------------------------------
	 * 
	 * FrslModelCS Constructors.
	 * 
	 * ----------------------------------------
	 */
	public FrslModelCS(Token fName) {
		// super(fName);
		// domainModel = useModel;
		this.fName = fName;
		actorsCS = new ArrayList<ActorCS>();
		usecasesCS = new ArrayList<UsecaseCS>();
		ucExtendsCS = new ArrayList<ExtendCS>();
		ucIncludesCs = new ArrayList<IncludeCS>();
	}

	/*
	 * ----------------------------------------
	 * 
	 * FrslModel Generator.
	 * 
	 * ----------------------------------------
	 */
	public FrslModel visitPreOrder(Context ctx) throws MInvalidModelException, MSystemException {
		FrslModel frslModel = new FrslModel(fName.getText());
		ctx.setFrslModel(frslModel);

		// System.out.println("actorsCS size: " + actorsCS.size());
		// System.out.println("usecasesCS size: " + usecasesCS.size());

		for (ActorCS actorCS : actorsCS) {
			frslModel.addActor(actorCS.visitPreOrder(ctx));
		}

		for (UsecaseCS usecaseCS : usecasesCS) {
			Usecase uc = usecaseCS.visitIntanceUC(ctx);
			// TODO: Handle steps in usecases.
			frslModel.addUsecase(uc);
		}

		for (UsecaseCS usecaseCS : usecasesCS) {
			usecaseCS.visitPreOrder(ctx);
		}

		for (ExtendCS extendCS : ucExtendsCS) {
			Extend ext = extendCS.visitPreOrder(ctx);
			frslModel.addExtend(ext);
		}
	
		// frslModel.setContext(ctx);

		return frslModel;
	}
}
