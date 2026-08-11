package org.vnu.sme.frsl.ast;


import org.antlr.runtime.Token;
import org.tzi.use.parser.SemanticException;
import org.tzi.use.parser.ocl.ASTType;
import org.tzi.use.uml.ocl.type.Type;
import org.vnu.sme.frsl.mm.FRSLmodel.ObjVar;
import org.vnu.sme.frsl.parser.Context;

public class ObjVarCS {
	/*
	 * ----------------------------------------
	 * 
	 * ObjVarCS Properties.
	 * 
	 * ----------------------------------------
	 */
	private Token fName;
	private boolean isMatched = false;
	private ASTType fType;

	/*
	 * ----------------------------------------
	 * 
	 * ObjVarCS Getters.
	 * 
	 * ----------------------------------------
	 */
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

	public boolean isMatched() {
		return isMatched;
	}

	public ASTType getfType() {
		return fType;
	}

	/*
	 * ----------------------------------------
	 * 
	 * ObjVarCS Setters.
	 * 
	 * ----------------------------------------
	 */
	public void setfName(Token fName) {
		this.fName = fName;
	}

	public void setMatched(boolean isMatched) {
		this.isMatched = isMatched;
	}

	public void setfType(ASTType fType) {
		this.fType = fType;
	}

	/*
	 * ----------------------------------------
	 * 
	 * ObjVarCS Constructors.
	 * 
	 * ----------------------------------------
	 */
	public ObjVarCS() {
	}

	/*
	 * ----------------------------------------
	 * 
	 * ObjVar Generator.
	 * 
	 * ----------------------------------------
	 */
	public ObjVar visitPreOrder(Context ctx) {
		// Lookup for type-reference.
		// oldTODO: Check if this is generalized enough.
		// Type type = ctx.typeTableLookup(this.fType.getStartToken().getText());
		// -- old way.

		Type type = null;
		try {
			type = fType.gen(ctx);
		} catch (SemanticException e) {
			e.printStackTrace();
		}

		ObjVar objVar = ctx.modelFactory().createObjVar(this.getName(), this.isMatched(), type);

		// Define the variable in the type table if it is not already defined.

		// TODO: Figure out if this solution can be used with NegObjVar.
		if (ctx.typeTableLookup(this.getName(), true) == null) {
			ctx.typeTableAdd(this.getfName(), objVar);
		} 

		return objVar;
	}
}
