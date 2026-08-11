package org.vnu.sme.frsl.mm.UCDmodel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.tzi.use.gui.views.diagrams.DiagramView.DiagramData;
import org.tzi.use.gui.views.diagrams.elements.PlaceableNode;
import org.tzi.use.gui.views.diagrams.elements.edges.EdgeBase;
import org.vnu.sme.frsl.mm.FRSLmodel.Actor;
import org.vnu.sme.frsl.mm.FRSLmodel.Usecase;

public class UcdModel implements DiagramData {

	private Map<String, SystemBounds> fSystemBounds;
	private Map<Usecase, UsecaseNode> fUsecaseToNodeMap;
	private Map<Actor, ActorNode> fActorToNodeMap;
	private Map<String, EdgeBase> fAssociation;
    
    public UcdModel () {
		fSystemBounds = new HashMap<String, SystemBounds>();
		fUsecaseToNodeMap = new HashMap<Usecase, UsecaseNode>();
		fActorToNodeMap = new HashMap<Actor, ActorNode>();
		fAssociation = new HashMap<String, EdgeBase>();
    }

	public Map<String, SystemBounds> getSystemBound() {
		return fSystemBounds;
	}

	public Map<Usecase, UsecaseNode> getUseNodeMap() {
		return fUsecaseToNodeMap;
	}

	public Map<Actor, ActorNode> getActorNodeMap() {
		return fActorToNodeMap;
	}

	public Map<String, EdgeBase> getAssociationMap() {
		return fAssociation;
	}

    public boolean hasNodes() {
    
		return !(fUsecaseToNodeMap.isEmpty() || fActorToNodeMap.isEmpty() );
	}
    @Override
	public Set<PlaceableNode> getNodes() {
        //no override
		Set<PlaceableNode> result = new HashSet<PlaceableNode>();
		return result;
	}

    @Override
	public Set<EdgeBase> getEdges() {
        // no override
		Set<EdgeBase> result = new HashSet<EdgeBase>();
		return result;
	}
}
