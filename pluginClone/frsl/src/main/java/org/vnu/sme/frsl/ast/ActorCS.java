package org.vnu.sme.frsl.ast;

import org.antlr.runtime.Token;
import org.tzi.use.api.UseApiException;
import org.tzi.use.api.UseModelApi;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MDataType;
import org.vnu.sme.frsl.mm.FRSLmodel.Actor;
import org.vnu.sme.frsl.parser.Context;

public class ActorCS {
	/*
	 * ----------------------------------------
	 * 
	 * ActorCS Properties.
	 * 
	 * ----------------------------------------
	 */
	private Token fName;
	private String description;

	/*
	 * ----------------------------------------
	 * 
	 * ActorCS Getters.
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

	/**
	 * Returns the description.
	 * 
	 * @return description
	 */
	public String getDescription() {
		return description;
	}

	/*
	 * ----------------------------------------
	 * 
	 * ActorCS Setters.
	 * 
	 * ----------------------------------------
	 */
	/**
	 * Set the fName property.
	 * 
	 * @param fName
	 */
	public void setfName(Token fName) {
		this.fName = fName;
	}

	/**
	 * Set the description property.
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/*
	 * ----------------------------------------
	 * 
	 * ActorCS Constructors.
	 * 
	 * ----------------------------------------
	 */
	public ActorCS() {
	}

	public ActorCS(Token fName) {
		this.fName = fName;
	}

	/*
	 * ----------------------------------------
	 * 
	 * Actor Generator.
	 * 
	 * ----------------------------------------
	 */
	public Actor visitPreOrder(Context ctx) {
		Actor actor = ctx.modelFactory().createActor(this.getName(), this.getDescription());

		ctx.typeTableAdd(this.getfName(), actor);

		
		return actor;
	}
}
