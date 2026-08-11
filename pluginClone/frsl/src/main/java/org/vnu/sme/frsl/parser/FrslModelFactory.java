package org.vnu.sme.frsl.parser;

import java.util.ArrayList;
import java.util.Map;

import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.ModelFactory;
import org.tzi.use.uml.ocl.type.Type;
import org.vnu.sme.frsl.mm.FRSLmodel.Action;
import org.vnu.sme.frsl.mm.FRSLmodel.Actor;
import org.vnu.sme.frsl.mm.FRSLmodel.ActorAction;
import org.vnu.sme.frsl.mm.FRSLmodel.ActorStep;
import org.vnu.sme.frsl.mm.FRSLmodel.AltFlow;
import org.vnu.sme.frsl.mm.FRSLmodel.Extend;
import org.vnu.sme.frsl.mm.FRSLmodel.ExtensionPoint;
import org.vnu.sme.frsl.mm.FRSLmodel.ObjVar;
import org.vnu.sme.frsl.mm.FRSLmodel.RejoinStep;
import org.vnu.sme.frsl.mm.FRSLmodel.SnapshotPattern;
import org.vnu.sme.frsl.mm.FRSLmodel.Step;
import org.vnu.sme.frsl.mm.FRSLmodel.SystemAction;
import org.vnu.sme.frsl.mm.FRSLmodel.SystemStep;
import org.vnu.sme.frsl.mm.FRSLmodel.UCStep;
import org.vnu.sme.frsl.mm.FRSLmodel.Usecase;
import org.vnu.sme.frsl.mm.FRSLmodel.UsecasePostcondition;
import org.vnu.sme.frsl.mm.FRSLmodel.UsecasePrecondition;
import org.vnu.sme.frsl.mm.FRSLmodel.VarLink;

public class FrslModelFactory extends ModelFactory {
	public Actor createActor(String name, String description) {
		return new Actor(name, description);
	}

	// TODO: Finish this when the Usecase constructor is finished.
	public Usecase createUsecase(String name) {
		return new Usecase(name);
	}
	public Usecase createUsecase(String name, String description, Actor primaryActor, ArrayList<Actor> secondaryActors,
			UsecasePrecondition precondition, UsecasePostcondition postcondition) {
		return new Usecase(name, description, primaryActor, secondaryActors, precondition, postcondition);
	}

	public UsecasePrecondition createUsecasePrecondition(String description, SnapshotPattern snapshot) {
		return new UsecasePrecondition(description, snapshot);
	}

	public UsecasePostcondition createUsecasePostcondition(String description, SnapshotPattern snapshot) {
		return new UsecasePostcondition(description, snapshot);
	}

	public SnapshotPattern createSnapshotPattern(String name, String description, ArrayList<ObjVar> objects,
			ArrayList<VarLink> links) {
		return new SnapshotPattern(name, description, objects, links);
	}

	public ObjVar createObjVar(String name, boolean isMatched, Type type) {
		return new ObjVar(name, isMatched, type);
	}

	public VarLink createVarLink(boolean isNeg, ObjVar lhsObjVars, ObjVar rhsObjVars, MAssociation assoc) {
		return new VarLink(isNeg, lhsObjVars, rhsObjVars, assoc);
	}

	public ActorStep createActorStep( String name, String description, SnapshotPattern preSnapshot,
			SnapshotPattern postSnapshot, ArrayList<Action> actions, ArrayList<AltFlow> flow) {
		return new ActorStep(name, description, preSnapshot, postSnapshot, actions, flow);
	}

	public SystemStep createSystemStep( String name, String description, SnapshotPattern preSnapshot,
			SnapshotPattern postSnapshot, ArrayList<Action> actions,  ArrayList<AltFlow> flow) {
		return new SystemStep(name, description, preSnapshot, postSnapshot, actions, flow);
	}

	public ActorAction createActorAction(Actor actor, ArrayList<ObjVar> objVars) {
		return new ActorAction(actor, objVars);	
	}

	public SystemAction createSystemAction(Actor actor, ArrayList<ObjVar> objVars, ArrayList<String> values) {
		return new SystemAction(actor, objVars, values);
	}

	public RejoinStep createRejoinStep(String name, Step rejoinTo, String description, SnapshotPattern condition,  ArrayList<AltFlow> flow ) {
		return new RejoinStep(name, rejoinTo, description, condition, flow);
	}

	public AltFlow createAltFlow(String name, String description, Step baseStep, SnapshotPattern condition
			) {
		return new AltFlow(name, description, baseStep, condition);
	}

	public UCStep createUCStep(String name, String description, Usecase includedUC,  ArrayList<AltFlow> flow) {
		return new UCStep(name, description, includedUC, flow);
	}

	public Extend createExtend(Usecase extend, Usecase extendUc, Map<String, ExtensionPoint> point) {
		return new Extend(extend, extendUc, point);
	}

	public ExtensionPoint createExtensionPoint(String name, String des, Map<String, Step> step, SnapshotPattern condition) {
		return new ExtensionPoint(name, des, step, condition);
	}
}
