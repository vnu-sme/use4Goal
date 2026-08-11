package org.vnu.sme.frsl.mm.FRSLmodel;

/*
 * TODO:
 * - Implement hashCode() and equals().
 * - Check other TypeImpl Inherited Methods.
 */

import java.util.Set;

import org.tzi.use.uml.ocl.type.Type;
import org.tzi.use.uml.ocl.type.TypeImpl;
import org.vnu.sme.frsl.view.Browser.PrintVisitor;

public class Actor extends TypeImpl implements UseType {
	/*
	 * ----------------------------------------
	 * 
	 * Actor Properties.
	 * 
	 * ----------------------------------------
	 */
	private String name;
	private String description;

	/*
	 * ----------------------------------------
	 * 
	 * Actor Getters.
	 * 
	 * ----------------------------------------
	 */
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	/*
	 * ----------------------------------------
	 * 
	 * Actor Setters.
	 * 
	 * ----------------------------------------
	 */
	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/*
	 * ----------------------------------------
	 * 
	 * Actor Constructors.
	 * 
	 * ----------------------------------------
	 */
	public Actor(String name, String description) {
		this.name = name;
		this.description = description;
	}

	/*
	 * TypeImpl Inherited Methods.
	 */
	@Override
	public StringBuilder toString(StringBuilder sb) {
		return sb.append(this.getName());
	}

	@Override
	public Set<? extends Type> allSupertypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void visitPrint(PrintVisitor print) {
		print.visitPrintActor(this);
	}
}
