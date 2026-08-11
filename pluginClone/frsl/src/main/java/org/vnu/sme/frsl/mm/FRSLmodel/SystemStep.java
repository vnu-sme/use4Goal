package org.vnu.sme.frsl.mm.FRSLmodel;

import java.util.ArrayList;

public class SystemStep extends ActStep{
    
    public SystemStep (String name, String description, SnapshotPattern preSnapshot,
            SnapshotPattern postSnapshot, ArrayList<Action> actions, ArrayList<AltFlow> flow) {
        super(name,description, preSnapshot, postSnapshot, actions, flow );
    }
}
