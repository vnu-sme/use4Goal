package org.vnu.sme.frsl.mm.FRSLmodel;

/*
 * TODO:
 * - Implement more properties and getters/setters.
 * - Implement hashCode() and equals().
 * - Check other TypeImpl Inherited Methods.
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.tzi.use.uml.ocl.type.Type;
import org.tzi.use.uml.ocl.type.TypeImpl;
import org.vnu.sme.frsl.view.Browser.PrintVisitor;

public class Usecase extends TypeImpl implements UseType{
	/*
	 * ----------------------------------------
	 * 
	 * Usecase Properties.
	 * 
	 * TODO -- Remaining properties.
	 * 
	 * ----------------------------------------
	 */
	private String name;
	private String description;
	private Actor primaryActor;
	private ArrayList<Actor> secondaryActors;
	private UsecasePrecondition precondition;
	private UsecasePostcondition postcondition;
	protected Step firstStep;
	protected Map<String,ExtensionPoint> extPoint;
	protected Map<String, Usecase> include;
	

	/*
	 * ----------------------------------------
	 * 
	 * Usecase Getters.
	 * 
	 * TODO -- Remaining properties.
	 * 
	 * ----------------------------------------
	 */
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Actor getPrimaryActor() {
		return primaryActor;
	}

	public ArrayList<Actor> getSecondaryActors() {
		return secondaryActors;
	}

	public UsecasePrecondition getPrecondition() {
		return precondition;
	}

	public UsecasePostcondition getPostcondition() {
		return postcondition;
	}

	public Step getFirstStep() {
		return firstStep;
	}

	public Map<String, ExtensionPoint> getExtPoint() {
		return extPoint;
	}

	public Map<String, Usecase> getInclude() {
		return include;
	}

	/*
	 * ----------------------------------------
	 * 
	 * Usecase Setters.
	 * 
	 * TODO -- Remaining properties.
	 * 
	 * ----------------------------------------
	 */
	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setPrimaryActor(Actor primaryActor) {
		this.primaryActor = primaryActor;
	}

	public void setSecondaryActors(ArrayList<Actor> secondaryActors) {
		this.secondaryActors = secondaryActors;
	}

	public void setPrecondition(UsecasePrecondition precondition) {
		this.precondition = precondition;
	}

	public void setPostcondition(UsecasePostcondition postcondition) {
		this.postcondition = postcondition;
	}

	public void setFirstStep(Step firstStep) {
		this.firstStep = firstStep;
	}

	public void setExtPoint(Map<String, ExtensionPoint> extPoint) {
		this.extPoint = extPoint;
	}

	public void setInclude(Map<String, Usecase> include) {
		this.include = include;
	}

	/*
	 * ----------------------------------------
	 * 
	 * Usecase Constructors.
	 * 
	 * ----------------------------------------
	 */

	public Usecase (String name) {
		this.name = name;
		this.include = new TreeMap<String, Usecase>();
	}
	public Usecase(String name, String description, Actor primaryActor, ArrayList<Actor> secondaryActors,
			UsecasePrecondition precondition, UsecasePostcondition postcondition) {
		this.name = name;
		this.description = description;
		this.primaryActor = primaryActor;
		this.secondaryActors = secondaryActors;
		this.precondition = precondition;
		this.postcondition = postcondition;
		this.include = new TreeMap<String, Usecase>();

		// TODO -- Remaining properties.
	}

	public void add (String description, Actor primaryActor, ArrayList<Actor> secondaryActors,
	UsecasePrecondition precondition, UsecasePostcondition postcondition, Step firStep, Map<String, ExtensionPoint> extPoint) {
		this.description = description;
		this.primaryActor = primaryActor;
		this.secondaryActors = secondaryActors;
		this.precondition = precondition;
		this.postcondition = postcondition;
		this.firstStep = firStep;
		this.extPoint = extPoint;
	}

	public void addIncludeUc (String step, Usecase uc) {
		this.include.put(step, uc);
	}

	public Collection<Usecase> include () {
		return this.include.values();
	}

	@Override
	public void visitPrint(PrintVisitor pv) {
		pv.visitPrintUsecase(this);
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

}
