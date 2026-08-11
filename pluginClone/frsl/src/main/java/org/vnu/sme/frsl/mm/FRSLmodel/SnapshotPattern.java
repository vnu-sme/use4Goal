package org.vnu.sme.frsl.mm.FRSLmodel;

import java.util.ArrayList;
import java.util.Set;

import org.tzi.use.uml.ocl.type.Type;

/*
 * TODO:
 * - Implement hashCode() and equals().
 * - Check other TypeImpl Inherited Methods.
 */

import org.tzi.use.uml.ocl.type.TypeImpl;

public class SnapshotPattern extends TypeImpl {
	/*
	 * ----------------------------------------
	 * 
	 * SnapshotPattern Properties.
	 * 
	 * ----------------------------------------
	 */
	private String name;
	private String description;
	private ArrayList<ObjVar> objects = new ArrayList<>();
	private ArrayList<VarLink> links = new ArrayList<>();

	/*
	 * ----------------------------------------
	 * 
	 * SnapshotPattern Getters.
	 * 
	 * ----------------------------------------
	 */
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public ArrayList<ObjVar> getObjects() {
		return objects;
	}

	public ArrayList<VarLink> getLinks() {
		return links;
	}

	/*
	 * ----------------------------------------
	 * 
	 * SnapshotPattern Setters.
	 * 
	 * ----------------------------------------
	 */
	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setObjects(ArrayList<ObjVar> objects) {
		this.objects = objects;
	}

	public void setLinks(ArrayList<VarLink> links) {
		this.links = links;
	}

	/*
	 * ----------------------------------------
	 * 
	 * SnapshotPattern Constructors.
	 * 
	 * ----------------------------------------
	 */
	public SnapshotPattern(String name, String description, ArrayList<ObjVar> objects, ArrayList<VarLink> links) {
		this.name = name;
		this.description = description;
		this.objects = objects;
		this.links = links;
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
