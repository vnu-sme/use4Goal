package org.vnu.sme.frsl.ast;

import org.antlr.runtime.Token;
import org.tzi.use.uml.mm.MAssociation;
import org.vnu.sme.frsl.mm.FRSLmodel.ObjVar;
import org.vnu.sme.frsl.mm.FRSLmodel.VarLink;
import org.vnu.sme.frsl.parser.Context;

public class VarLinkCS {
	/*
	 * ----------------------------------------
	 * 
	 * VarLinkCS Properties.
	 * 
	 * ----------------------------------------
	 */
	private boolean isNeg = false;
	private Token lhsfObjVarsCS;
	private Token rhsfObjVarsCS;
	private Token fAssocCS;

	/*
	 * ----------------------------------------
	 * 
	 * VarLinkCS Getters.
	 * 
	 * ----------------------------------------
	 */
	public boolean isNeg() {
		return isNeg;
	}

	public Token getLhsfObjVarsCS() {
		return lhsfObjVarsCS;
	}

	public Token getRhsfObjVarsCS() {
		return rhsfObjVarsCS;
	}

	public Token getfAssocCS() {
		return fAssocCS;
	}

	public void getAll () {
		System.out.println(this.isNeg);
		System.out.println(this.lhsfObjVarsCS);
		System.out.println(this.rhsfObjVarsCS);
		System.out.println(this.fAssocCS);
	}

	/*
	 * ----------------------------------------
	 * 
	 * VarLinkCS Setters.
	 * 
	 * ----------------------------------------
	 */
	public void setNeg(boolean isNeg) {
		this.isNeg = isNeg;
	}

	public void setLhsfObjVarsCS(Token lhs) {
		this.lhsfObjVarsCS = lhs;
	}

	public void setRhsfObjVarsCS(Token rhs) {
		this.rhsfObjVarsCS = rhs;
	}

	public void setfAssocCS(Token fAssocCS) {
		this.fAssocCS = fAssocCS;
	}

	/*
	 * ----------------------------------------
	 * 
	 * VarLinkCS Constructors.
	 * 
	 * ----------------------------------------
	 */
	public VarLinkCS() {
	}

	/*
	 * ----------------------------------------
	 * 
	 * VarLinkCS Generator.
	 * 
	 * ----------------------------------------
	 */
	public VarLink visitPreOrder(Context ctx) {
		ObjVar lhsObjVars = ctx.typeTableLookup(lhsfObjVarsCS.getText(), ObjVar.class); 
		ObjVar rhsObjVars = ctx.typeTableLookup(rhsfObjVarsCS.getText(), ObjVar.class);

		MAssociation assoc = ctx.model().getAssociation(getfAssocCS().getText());

		if (assoc == null) {
			throw new IllegalArgumentException("No assoc with the name " + getfAssocCS().getText());
		}

		VarLink varLink = ctx.modelFactory().createVarLink(isNeg(), lhsObjVars, rhsObjVars, assoc);
		return varLink;
	}
}
