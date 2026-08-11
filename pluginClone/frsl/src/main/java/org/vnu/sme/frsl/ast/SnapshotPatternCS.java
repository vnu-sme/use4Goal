package org.vnu.sme.frsl.ast;

import java.util.ArrayList;

import org.antlr.runtime.Token;
import org.vnu.sme.frsl.mm.FRSLmodel.ObjVar;
import org.vnu.sme.frsl.mm.FRSLmodel.SnapshotPattern;
import org.vnu.sme.frsl.mm.FRSLmodel.VarLink;
import org.vnu.sme.frsl.parser.Context;

public class SnapshotPatternCS {
	/*
	 * ----------------------------------------
	 * 
	 * SnapshotPatternCS Properties.
	 * 
	 * ----------------------------------------
	 */
	private Token fName;
	private String description;
	private ArrayList<ObjVarCS> objectsCS;
	private ArrayList<Token> negObjectsCS;
	private ArrayList<VarLinkCS> linksCS;

	//TODO: GEN FUNCTION FOR THIS CONSTRAINTCS!!!! - Currently parsed as a string.
	private ArrayList<ConstraintCS> constraintsCS;

	/*
	 * ----------------------------------------
	 * 
	 * SnapshotPatternCS Getters.
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
		if (this.fName == null) {
//			Note: Disable warning.
//			System.out.println("Warning: This SnapshotPatternCS object's fName is not defined.");
		}
		return fName;
	}

	/**
	 * Returns a string (that is embedded in fName).
	 * 
	 * @see #getfName() Don't be mistaken with this (get-a-token).
	 * @return getfName().getText()
	 */
	public String getName() {
		if (this.getfName() == null) {
//			Note: Disable warning.
//			System.out.println(
//					"Warning: This SnapshotPatternCS object's name couldn't be derrived from undefined fName. "
//					+ "An empty string is returned."
//			);
			return "";
		}
		return fName.getText();
	}

	public String getDescription() {
		return description;
	}

	public ArrayList<ObjVarCS> getObjectsCS() {
		return objectsCS;
	}

	public ArrayList<Token> getNegObjectsCS() {
		return negObjectsCS;
	}

	public ArrayList<VarLinkCS> getLinksCS() {
		return linksCS;
	}

	public ArrayList<ConstraintCS> getConstraintsCS() {
		return constraintsCS;
	}

	/*
	 * ----------------------------------------
	 * 
	 * SnapshotPatternCS Setters.
	 * 
	 * ----------------------------------------
	 */
	public void setfName(Token fName) {
		this.fName = fName;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setObjectsCS(ArrayList<ObjVarCS> objectsCS) {
		this.objectsCS = objectsCS;
	}

	public void setNegObjectsCS(ArrayList<Token> negObjectsCS) {
		this.negObjectsCS = negObjectsCS;
	}

	public void setLinksCS(ArrayList<VarLinkCS> linksCS) {
		this.linksCS = linksCS;
	}

	public void setConstraintsCS(ArrayList<ConstraintCS> constraintsCS) {
		this.constraintsCS = constraintsCS;
	}

	/*
	 * ----------------------------------------
	 * 
	 * SnapshotPatternCS Adders/Removers.
	 * 
	 * ----------------------------------------
	 */
	public void addObjectCS(ObjVarCS objectCS) {
		if (this.objectsCS == null) {
			this.objectsCS = new ArrayList<>();
		}
		this.objectsCS.add(objectCS);
	}

	public void removeObjectCS(Token negObjectCS) {
		// TODO: Handle if negObjectCS is not already exist (and null check).
		// Note: This rule is currently broken (null pointer exception). FIXIT!!!

		// this.objectsCS.removeIf(e -> e.getName() == negObjectCS.getText());
	}

	public void addLinkCS(VarLinkCS linkCS) {
		if (this.linksCS == null) {
			this.linksCS = new ArrayList<>();
		}
		this.linksCS.add(linkCS);
	}

	public void addConstraintCS(ConstraintCS constraintCS) {
		if (this.constraintsCS == null) {
			this.constraintsCS = new ArrayList<>();
		}
		this.constraintsCS.add(constraintCS);
	}

	/*
	 * ----------------------------------------
	 * 
	 * SnapshotPatternCS Constructors.
	 * 
	 * ----------------------------------------
	 */
	public SnapshotPatternCS() {
		this.objectsCS = new ArrayList<>();
		this.negObjectsCS = new ArrayList<>();
		this.linksCS = new ArrayList<>();
		this.constraintsCS = new ArrayList<>();
	}

	/*
	 * ----------------------------------------
	 * 
	 * SnapshotPattern Generator.
	 * 
	 * ----------------------------------------
	 */
	public SnapshotPattern visitPreOrder(Context ctx) {
		ArrayList<ObjVar> objects = new ArrayList<>();
		ArrayList<VarLink> links = new ArrayList<>();
		
		
		for (ObjVarCS objectCS : objectsCS) {
			objects.add(objectCS.visitPreOrder(ctx));
		}
		
		for (VarLinkCS linkCS : linksCS) {
			links.add(linkCS.visitPreOrder(ctx));
		}

		for (ConstraintCS constrain : constraintsCS) {
			constrain.show();
		}

		// TODO: Check if ConstraintCS could be reduced into a String.
		
		SnapshotPattern snapshotPattern = ctx.modelFactory().createSnapshotPattern(this.getName(),
				this.getDescription(), objects, links);

//		ctx.typeTableAdd(this.getfName(), snapshotPattern);

		return snapshotPattern;
	}
}
