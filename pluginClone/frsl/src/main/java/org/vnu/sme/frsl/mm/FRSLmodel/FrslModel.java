package org.vnu.sme.frsl.mm.FRSLmodel;

/*
 * TODO:
 * - Implement more properties and getters/setters.
 * - Implement hashCode() and equals().
 * - Check if TypeImpl inheritance is needed.
 */

// import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.tzi.use.uml.mm.MModel;


public class FrslModel extends MModel {
	/*
	 * ----------------------------------------
	 * 
	 * FrslModel Properties.
	 * 
	 * TODO -- Remaining properties.
	 * 
	 * ----------------------------------------
	 */
	// private MModel domainModel;
	private final String fName;
	private Map<String, Actor> actors;
	private Map<String, Usecase> usecases;
	protected Map<String, Extend> ucExtend;
	protected Map<String, ExtensionPoint> ucExtensionPoint;

	/*
	 * ----------------------------------------
	 * 
	 * FrslModel Getters.
	 * 
	 * TODO -- Remaining properties.
	 * 
	 * ----------------------------------------
	 */

	// public MModel getDomainModel() {
	// 	return domainModel;
	// }

	public Collection<Actor> actors() {
		return actors.values();
	}

	public Collection<Usecase> usecases() {
		return usecases.values();
	}

	public Collection<Extend> extend() {
		return ucExtend.values();
	}

	public Map<String, ExtensionPoint> extensionPoint() {
		return ucExtensionPoint;
	}

	public String getfName() {
		return fName;
	}

	/*
	 * ----------------------------------------
	 * 
	 * FrslModel Constructors.
	 * 
	 * ----------------------------------------
	 */
	public FrslModel(String name) {
		// domainModel = model;
		super(name);
		fName = name;
		actors = new TreeMap<>();
		usecases = new TreeMap<>();
		ucExtend = new TreeMap<>();
	}

	/*
	 * ----------------------------------------
	 * 
	 * FrslModel Adders.
	 * 
	 * TODO -- Remaining properties. Also, remove debug-comments when done.
	 * 
	 * ----------------------------------------
	 */
	public void addActor(Actor actor) {
//		System.out.println("FRSLMODEL ADD ACTOR DEBUGGGG");
//		System.out.println("actor " + actor.getName() + "\n\n");
		actors.put(actor.getName(), actor);
	}

	public void addUsecase(Usecase usecase) {
//		System.out.println("FRSLMODEL ADD USECASE DEBUGGGG");
//		System.out.println("usecase " + usecase.getName() + "\n\n");
		usecases.put(usecase.getName(), usecase);
	}

	public void addExtend(Extend extend) {
		// TODO fName
		ucExtend.put(fName, extend);
	}

	public void addExtensionPoint(ExtensionPoint extendPoint) {
		ucExtensionPoint.put(fName, extendPoint);
	}

	// ArrayList<Usecase> getUsecase();
	// ArrayList<Extend> getUcExtend();
}