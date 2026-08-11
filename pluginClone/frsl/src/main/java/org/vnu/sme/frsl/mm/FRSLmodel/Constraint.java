package org.vnu.sme.frsl.mm.FRSLmodel;

import java.util.Set;

import org.tzi.use.uml.ocl.type.Type;
import org.tzi.use.uml.ocl.type.TypeImpl;

public class Constraint extends TypeImpl {
    
    private String fConstraint;


    public Constraint () {

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
		return sb.append("this.getName()");
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
