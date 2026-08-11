package org.vnu.sme.frsl.mm.FRSLmodel;

import java.util.Set;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.ocl.type.Type;
import org.tzi.use.uml.ocl.type.TypeImpl;

public class VarLink extends TypeImpl {
	/*
	 * ----------------------------------------
	 * 
	 * VarLink Properties.
	 * 
	 * ----------------------------------------
	 */
	private boolean isNeg = false;
	private ObjVar lhsObjVars;
	private ObjVar rhsObjVars;
	private MAssociation assoc;

	/*
	 * ----------------------------------------
	 * 
	 * VarLink Getters.
	 * 
	 * ----------------------------------------
	 */
	public boolean isNeg() {
		return isNeg;
	}

	public ObjVar getLhsObjVar() {
		return lhsObjVars;
	}

	public ObjVar getRhsObjVar() {
		return rhsObjVars;
	}

	public MAssociation getAssoc() {
		return assoc;
	}

	/*
	 * ----------------------------------------
	 * 
	 * VarLink Setters.
	 * 
	 * ----------------------------------------
	 */
	public void setNeg(boolean isNeg) {
		this.isNeg = isNeg;
	}
	public void setLhsObjVar(ObjVar lhsObjVar) {
		this.lhsObjVars = lhsObjVar;
	}

	public void setRhsObjVar(ObjVar rhsObjVar) {
		this.rhsObjVars = rhsObjVar;
	}

	public void setAssoc(MAssociation assoc) {
		this.assoc = assoc;
	}

	/*
	 * ----------------------------------------
	 * 
	 * VarLink Constructors.
	 * 
	 * ----------------------------------------
	 */
	public VarLink(boolean isNeg, ObjVar lhsObjVars, ObjVar rhsObjVars, MAssociation assoc) {
		this.isNeg = isNeg;
		this.lhsObjVars = lhsObjVars;
		this.rhsObjVars = rhsObjVars;
		this.assoc = assoc;
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
		if (isNeg) {
			sb.append("!");
		}
		sb.append("(" + this.getLhsObjVar() + ", " + this.rhsObjVars + ")");
		// sb.append(assoc); -- TODO: MAssociation.toString() is not implemented
		// (therefore a workaround is needed).
		return sb;
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
