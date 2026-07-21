package org.vnu.sme.goal.istarusebridge;

import java.util.List;

import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MModel;
import org.vnu.sme.goal.istar.mm.Actor;
import org.vnu.sme.goal.istar.mm.Agent;
import org.vnu.sme.goal.istar.mm.GoalModel;

/**
 * Naming convention that replaces a separate istar&lt;-&gt;.use mapping file: every .istar
 * actor type T must have a same-named .use class. The USE file generated from ACL preserves
 * ACL Role classes directly, so no synthetic Role superclass is required. The ACL/iStar
 * conformance phase is responsible for proving that the same-named classifier is an ACL Role.
 */
public final class UseActorClasses {

    private UseActorClasses() {}

    public static MClass resolve(GoalModel gm, MModel useModel, String actorTypeName, List<String> errors) {
        Actor actor = gm.findActor(actorTypeName).orElse(null);
        MClass cls = useModel.getClass(actorTypeName);
        if (cls == null) {
            errors.add("no .use class named '" + actorTypeName + "' for the actor type declared in .istar");
            return null;
        }
        // A declared iStar Agent still maps naturally to Agent (or one of its subclasses).
        // A declared iStar Role maps directly to the same-named ACL Role class.
        if (actor instanceof Agent && !cls.name().equals("Agent")
                && cls.allParents().stream().noneMatch(parent -> parent.name().equals("Agent"))) {
            errors.add(".use class '" + actorTypeName + "' is not Agent or a subclass of Agent");
            return null;
        }
        return cls;
    }
}
