package org.vnu.sme.frsl.ast;

import java.util.ArrayList;

import org.vnu.sme.frsl.mm.FRSLmodel.ActStep;
import org.vnu.sme.frsl.mm.FRSLmodel.Action;
import org.vnu.sme.frsl.mm.FRSLmodel.ActorStep;
import org.vnu.sme.frsl.mm.FRSLmodel.AltFlow;
import org.vnu.sme.frsl.mm.FRSLmodel.SnapshotPattern;
import org.vnu.sme.frsl.mm.FRSLmodel.Step;
import org.vnu.sme.frsl.parser.Context;

public class ActorStepCS extends ActStepCS {
    public ActorStepCS() {
		super();
	}



	@Override
	ActStep visitActStep(Context ctx, String useName, ArrayList<Action> actions, ArrayList<AltFlow> flow,
			SnapshotPattern preSnapshot, SnapshotPattern postSnapshot) {
		ActorStep step = ctx.modelFactory().createActorStep( this.getName(), this.getDescription(), preSnapshot, postSnapshot, actions, flow);

		// Add to type table.
		// TODO: Make up a better solution for this.
		//       The current solution is to add the step to the type table, which breaks when duplicated names are found.
		//       For example, step01 of UC1 and step01 of UC2 are considered the "redefinition of step01".
		ctx.typeTableAdd(this.getfName(), step, useName);

		return step;
	}
}
