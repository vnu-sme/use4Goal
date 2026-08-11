package org.vnu.sme.frsl.mm.FRSLmodel;

/*
 * TODO:
 * - Implement hashCode() and equals().
 * - Check other TypeImpl Inherited Methods.
 */

import java.util.Set;

import org.tzi.use.uml.ocl.type.Type;

/*
 * TODO:
 * - Implement more properties and getters/setters.
 * - Implement hashCode() and equals().
 * - Check other TypeImpl Inherited Methods.
 */

import org.tzi.use.uml.ocl.type.TypeImpl;
import org.vnu.sme.frsl.view.Browser.PrintVisitor;

public class UsecasePostcondition extends TypeImpl implements UseType{
	/*
	 * ----------------------------------------
	 * 
	 * UsecasePostcondition Properties.
	 * 
	 * ----------------------------------------
	 */
	private String name;
	private SnapshotPattern snapshot;

	/*
	 * ----------------------------------------
	 * 
	 * UsecasePostcondition Setters.
	 * 
	 * ----------------------------------------
	 */
	public String getName() {
		return name;
	}

	public SnapshotPattern getSnapshot() {
		return snapshot;
	}

	/*
	 * ----------------------------------------
	 * 
	 * UsecasePostcondition Getters.
	 * 
	 * ----------------------------------------
	 */
	public void setName(String name) {
		this.name = name;
	}

	public void setSnapshot(SnapshotPattern snapshot) {
		this.snapshot = snapshot;
	}

	/*
	 * ----------------------------------------
	 * 
	 * UsecasePostcondition Constructors.
	 * 
	 * ----------------------------------------
	 */
	public UsecasePostcondition(String name, SnapshotPattern snapshot) {
		this.name = name;
		this.snapshot = snapshot;
	}

	@Override
	public void visitPrint(PrintVisitor pv) {
		pv.visitPrintUsecasePostcondition(this);
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
		if (this.getName() == null) {
			return sb.append("Post condition");
		}
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
