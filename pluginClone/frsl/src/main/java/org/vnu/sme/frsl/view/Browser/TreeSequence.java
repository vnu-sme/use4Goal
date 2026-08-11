package org.vnu.sme.frsl.view.Browser;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.vnu.sme.frsl.mm.FRSLmodel.Actor;
import org.vnu.sme.frsl.mm.FRSLmodel.AltFlow;
import org.vnu.sme.frsl.mm.FRSLmodel.RejoinStep;
import org.vnu.sme.frsl.mm.FRSLmodel.Step;
import org.vnu.sme.frsl.mm.FRSLmodel.Usecase;
import org.vnu.sme.frsl.mm.FRSLmodel.UsecasePostcondition;
import org.vnu.sme.frsl.mm.FRSLmodel.UsecasePrecondition;

public class TreeSequence extends TreeNode{
    
    public TreeSequence(Usecase usecase) {
        super();

        top = usecase.getName();

        
        listActor(usecase);
        preCOndition(usecase);
        postCOndition(usecase);
        step(usecase);
    }

    private void listActor(Usecase usecase) {
        Map<String, Actor> map = new TreeMap<>();
        map.put(usecase.getPrimaryActor().getName(), usecase.getPrimaryActor());

        for (Actor second : usecase.getSecondaryActors()) {
            map.put(second.getName(), second);
        }
        Collection<Actor> actor = map.values();
        list.add(actor);
        listName.add("actor");
        lenght += 1;
    }

    private void preCOndition(Usecase usecase) {
        Map<String, UsecasePrecondition> map = new TreeMap<>();
        map.put("preCondition", usecase.getPrecondition());
        Collection<?> pre = map.values();
        
        
        list.add(pre);
        listName.add("condition");
        lenght += 1;
    }

    private void postCOndition(Usecase usecase) {
        Map<String, UsecasePostcondition> map = new TreeMap<>();
        map.put("postCondition", usecase.getPostcondition());
        Collection<UsecasePostcondition> post = map.values();

        list.add(post);
        listName.add("condition");
        lenght+= 1;
        
    }

    private void step(Usecase usecase) {
        Map <String , Step> mapStep = new TreeMap<>();
        Map <String, Step> mapLoop = new TreeMap<>();
        Map <String , AltFlow> mapFlow = new TreeMap<>();

        Step step = usecase.getFirstStep();
        if (step == null) return;
        mapStep.put(step.getName(), step);
        while (step.getNextstep() != null) {
            step = step.getNextstep();
            if (step instanceof RejoinStep) {
                mapLoop.put(step.getName(), step);
                continue;
            }
            mapStep.put(step.getName(), step);
            if (step.getAltFlow() != null && step.getAltFlow().size()>0) {
                for (AltFlow flow : step.getAltFlow()) {
                    mapFlow.put(flow.getDescription(), flow);
                }
            }
        }

        Collection<Step> listep = mapStep.values();
        Collection<AltFlow> lsFlow = mapFlow.values();
        Collection<Step> loop = mapLoop.values();

        list.add(listep);
        listName.add("Basic flow");
        lenght += 1;

        list.add(lsFlow);
        listName.add("Alt flow");
        lenght += 1;

        list.add(loop);
        listName.add("Loop");
        lenght += 1;

    }

    // actor
    // des 
    // post, pre
    // basiflow
    // alt flow
    // extend
    // include

}

