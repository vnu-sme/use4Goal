package org.vnu.sme.goal.translate.aclistarbpmn2eventb.translate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.vnu.sme.goal.dsl.acl.mm.*;
import org.vnu.sme.goal.translate.aclistarbpmn2eventb.ir.EventBProject;
import org.vnu.sme.goal.translate.aclistarbpmn2eventb.ir.EventBProject.*;

/** Shared name/type bridge for extensions layered on the canonical ACL UML-B project. */
final class AclEventBCompositionSupport {
    private AclEventBCompositionSupport() {}

    static Map<String,String> symbols(AclModel acl) {
        Map<String,String> result=new LinkedHashMap<>();
        acl.entities().forEach(x->result.put("$type."+x.name(),"E_"+id(x.name())));
        acl.groups().forEach(x->result.put("$type."+x.name(),"G_"+id(x.name())));
        acl.roles().forEach(x->result.put("$type."+x.name(),"R_"+id(x.name())));
        acl.entities().forEach(x->attributes(x.name(),x.attributes(),result));
        acl.groups().forEach(x->attributes(x.name(),x.attributes(),result));
        acl.roles().forEach(x->attributes(x.name(),x.attributes(),result));
        acl.relations().forEach(x->result.put("$relation."+x.name(),id(x.name())));
        acl.owners().forEach(x->{
            result.put("$owner."+x.target(),"owns_"+id(x.target()));
            result.put("$ownerGroup."+x.target(),x.sourceGroup());
        });
        acl.roles().forEach(x->{
            result.put("$plays."+x.name(),"plays_"+id(x.name()));
            result.put("$playsParentType."+x.name(),x.parentRoles().isEmpty()?"$Agent":x.parentRoles().get(0));
        });
        return result;
    }

    private static void attributes(String owner,List<AclAttribute> attributes,Map<String,String> result) {
        String domain=result.get("$type."+owner);
        for(AclAttribute attribute:attributes) {
            String variable=id(owner)+"_"+id(attribute.name());
            String type=domain+(attribute.optional()?" ⇸ ":" → ")+range(attribute.type());
            result.put(owner+"."+attribute.name(),variable);
            result.put("$functionType."+variable,type);
        }
    }

    static String range(AclDataType type) {
        return switch(type.sourceName()) {
            case "Boolean" -> "BOOL";
            case "Integer" -> "ℤ";
            case "Real" -> "ℝ";
            case "String" -> "STRING";
            default -> id(type.sourceName()).toUpperCase(Locale.ROOT);
        };
    }

    static EventBProject replaceMachine(EventBProject base,List<String> variables,
            List<Predicate> invariants,List<Event> events,List<Trace> traces,List<Property> properties) {
        Machine machine=new Machine(base.machine().name(),base.context().name(),variables,invariants,events);
        return new EventBProject(base.name(),base.context(),machine,traces,properties);
    }

    static String id(String value) { return AclIStarBpmn2EventBTranslator.id(value); }
}
