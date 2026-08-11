package org.vnu.sme.frsl.mm.FRSLmodel;

import java.util.Set;

import org.tzi.use.uml.ocl.type.Type;

/*
 * TODO:
 * - Implement hashCode() and equals().
 * - Check other TypeImpl Inherited Methods.
 */

import org.tzi.use.uml.ocl.type.TypeImpl;

public class ObjVar extends TypeImpl {
	/*
	 * ----------------------------------------
	 * 
	 * ObjVar Properties.
	 * 
	 * ----------------------------------------
	 */
	private String name;
	private boolean isMatched = false;
	private Type type;

	/*
	 * ----------------------------------------
	 * 
	 * ObjVar Getters.
	 * 
	 * ----------------------------------------
	 */
	public String getName() {
		return name;
	}

	public boolean isMatched() {
		return isMatched;
	}

	public Type getType() {
		return type;
	}

	/*
	 * ----------------------------------------
	 * 
	 * ObjVar Setters.
	 * 
	 * ----------------------------------------
	 */
	public void setName(String name) {
		this.name = name;
	}

	public void setMatched(boolean isMatched) {
		this.isMatched = isMatched;
	}

	public void setType(Type type) {
		this.type = type;
	}

	/*
	 * ----------------------------------------
	 * 
	 * ObjVar Constructors.
	 * 
	 * ----------------------------------------
	 */
	public ObjVar(String name, boolean isMatched, Type type) {
		this.name = name;
		this.isMatched = isMatched;
		this.type = type;
	}

	/*
	 * ----------------------------------------
	 * 
	 * TypeImpl Inherited Methods.
	 * 
	 * ----------------------------------------
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

}
