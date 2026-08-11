package org.vnu.sme.frsl.mm.FRSLmodel;

import java.util.ArrayList;

public class ActorStep extends ActStep {
    
    public ActorStep (String name, String description, SnapshotPattern preSnapshot,
            SnapshotPattern postSnapshot, ArrayList<Action> actions, ArrayList<AltFlow> flow) {
        super(name,description, preSnapshot, postSnapshot, actions, flow );
    }
}
