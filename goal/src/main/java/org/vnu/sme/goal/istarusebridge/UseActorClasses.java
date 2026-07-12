package org.vnu.sme.goal.istarusebridge;

import java.util.List;

import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MClassifier;
import org.tzi.use.uml.mm.MModel;
import org.vnu.sme.goal.istar.mm.Actor;
import org.vnu.sme.goal.istar.mm.Agent;
import org.vnu.sme.goal.istar.mm.GoalModel;

/**
 * Naming convention that replaces a separate istar&lt;-&gt;.use mapping file: every .istar
 * actor type T must have a same-named .use class, and that class must descend from the
 * .use marker associationclass matching T's declared actorKind (role -&gt; Role,
 * agent -&gt; Agent). "Is this class an Actor or a plain Entity" is answered by UML
 * generalization, checkable here, not by a side file.
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
        String expectedRoot = (actor instanceof Agent) ? "Agent" : "Role";
        boolean descends = cls.allParents().stream().map(MClassifier::name).anyMatch(expectedRoot::equals);
        if (!descends) {
            errors.add(".use class '" + actorTypeName + "' does not descend from '" + expectedRoot
                    + "' (required for istar actor kind '" + (actor instanceof Agent ? "agent" : "role") + "')");
            return null;
        }
        return cls;
    }
}
