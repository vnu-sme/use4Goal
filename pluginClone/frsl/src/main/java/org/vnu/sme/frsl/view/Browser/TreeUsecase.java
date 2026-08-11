package org.vnu.sme.frsl.view.Browser;

import java.util.Collection;

import org.vnu.sme.frsl.mm.FRSLmodel.Actor;
import org.vnu.sme.frsl.mm.FRSLmodel.FrslModel;
import org.vnu.sme.frsl.mm.FRSLmodel.Usecase;

public class TreeUsecase extends TreeNode {

    
    public TreeUsecase(FrslModel model) {
        super();
        top = model.getfName();

        Collection<Usecase> usecase = model.usecases();
        list.add(usecase);
        listName.add("usecase");
        lenght += 1;

        Collection<Actor> actor = model.actors();
        list.add(actor);
        listName.add("actor");
        lenght += 1;
    }

    
}
