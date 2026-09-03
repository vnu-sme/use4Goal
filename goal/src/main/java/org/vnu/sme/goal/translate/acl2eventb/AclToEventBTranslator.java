package org.vnu.sme.goal.translate.acl2eventb;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.vnu.sme.goal.dsl.acl.mm.*;
import org.vnu.sme.goal.translate.aclistarbpmn2eventb.ir.EventBProject;
import org.vnu.sme.goal.translate.aclistarbpmn2eventb.ir.EventBProject.*;

/**
 * UML-B-style structural translation of one ACL model.
 *
 * <p>A carrier set in the Context is the identity pool of a root class. The
 * corresponding Machine variable is its current extent. Attributes,
 * associations, ownership and role-playing are Machine state, never frozen
 * population constants.</p>
 */
public final class AclToEventBTranslator {
    private AclToEventBTranslator() {}

    public static EventBProject translate(String projectName, AclModel acl, List<String> diagnostics) {
        String base=id(projectName);
        List<String> sets=new ArrayList<>(List.of("AGENT_ID","STRING"));
        List<Constant> constants=new ArrayList<>();
        List<Predicate> axioms=new ArrayList<>();
        List<String> variables=new ArrayList<>();
        List<Predicate> invariants=new ArrayList<>();
        List<Trace> traces=new ArrayList<>();
        Map<String,String> extents=new LinkedHashMap<>();
        Map<String,String> plays=new LinkedHashMap<>();

        constants.add(new Constant("EMPTY_STRING"));
        axiom(axioms,"EMPTY_STRING ∈ STRING");

        variables.add("AGENTS");
        invariant(invariants,"AGENTS ⊆ AGENT_ID","inv_type_AGENTS");
        traces.add(new Trace("ACL","synthetic Agent class","AGENT_ID / AGENTS"));

        for(AclEntity entity:acl.entities()) {
            String extent="E_"+id(entity.name()); extents.put(entity.name(),extent); variables.add(extent);
            if(entity.specializes().isPresent()) {
                invariant(invariants,extent+" ⊆ E_"+id(entity.specializes().orElseThrow()),"inv_type_"+extent);
            } else {
                String carrier=extent+"_ID"; sets.add(carrier);
                invariant(invariants,extent+" ⊆ "+carrier,"inv_type_"+extent);
            }
            traces.add(new Trace("ACL","entity "+entity.name(),extent));
        }
        for(AclGroup group:acl.groups()) {
            String extent="G_"+id(group.name()); extents.put(group.name(),extent); variables.add(extent);
            if(group.specializes().isPresent()) {
                invariant(invariants,extent+" ⊆ G_"+id(group.specializes().orElseThrow()),"inv_type_"+extent);
            } else {
                String carrier=extent+"_ID"; sets.add(carrier);
                invariant(invariants,extent+" ⊆ "+carrier,"inv_type_"+extent);
            }
            traces.add(new Trace("ACL","group "+group.name(),extent));
        }
        // ACL Role inheritance is not UML class inclusion: every Role keeps a
        // distinct occurrence pool and a play edge links parent and child.
        for(AclRole role:acl.roles()) {
            String extent="R_"+id(role.name()), carrier=extent+"_ID";
            extents.put(role.name(),extent); sets.add(carrier); variables.add(extent);
            invariant(invariants,extent+" ⊆ "+carrier,"inv_type_"+extent);
            traces.add(new Trace("ACL","role "+role.name(),extent));
        }

        for(AclEnum enumeration:acl.enums()) {
            String enumSet=id(enumeration.name()).toUpperCase(Locale.ROOT); sets.add(enumSet);
            List<String> literals=enumeration.literals().stream().map(AclToEventBTranslator::id).toList();
            literals.forEach(x->constants.add(new Constant(x)));
            if(!literals.isEmpty()) axiom(axioms,"partition("+enumSet+", "
                    +String.join(", ",literals.stream().map(x->"{"+x+"}").toList())+")");
            traces.add(new Trace("ACL","enum "+enumeration.name(),enumSet));
        }

        acl.entities().forEach(e->addAttributes(e.name(),e.attributes(),extents,variables,invariants,traces));
        acl.groups().forEach(g->addAttributes(g.name(),g.attributes(),extents,variables,invariants,traces));
        acl.roles().forEach(r->addAttributes(r.name(),r.attributes(),extents,variables,invariants,traces));

        for(AclRelation relation:acl.relations()) {
            String name=id(relation.name()), left=extents.get(relation.source().type()),
                    right=extents.get(relation.target().type());
            variables.add(name);
            invariant(invariants,name+" ∈ "+left+" ↔ "+right,"inv_type_"+name);
            addEndpointCardinality(invariants,name,left,relation.target().multiplicity(),false,"inv_card_"+name+"_target");
            addEndpointCardinality(invariants,name,right,relation.source().multiplicity(),true,"inv_card_"+name+"_source");
            if(relation.kind()==RelationKind.COMPOSITION)
                invariant(invariants,atMost("part",right,name+"∼[{part}]",1),"inv_composite_"+name);
            traces.add(new Trace("ACL",relation.kind().name().toLowerCase(Locale.ROOT)+" "+relation.name(),name));
        }

        Map<String,AclOwner> ownerByTarget=new LinkedHashMap<>();
        for(AclOwner owner:acl.owners()) {
            ownerByTarget.put(owner.target(),owner);
            String name="owns_"+id(owner.target()), left=extents.get(owner.sourceGroup()), right=extents.get(owner.target());
            variables.add(name);
            invariant(invariants,name+" ∈ "+left+" ↔ "+right,"inv_type_"+name);
            addCardinality(invariants,name,left,owner.multiplicity(),"inv_card_"+name);
            invariant(invariants,atLeast("member",right,name+"∼[{member}]",1),"inv_owner_total_"+name);
            invariant(invariants,atMost("member",right,name+"∼[{member}]",1),"inv_owner_unique_"+name);
            traces.add(new Trace("ACL","owner "+owner.sourceGroup()+" -> "+owner.target(),name));
        }

        for(AclRole role:acl.roles()) {
            String name="plays_"+id(role.name()); plays.put(role.name(),name); variables.add(name);
            String parent=role.parentRoles().isEmpty()?"AGENTS":extents.get(role.parentRoles().get(0));
            String child=extents.get(role.name());
            invariant(invariants,name+" ∈ "+parent+" ↔ "+child,"inv_type_"+name);
            invariant(invariants,atLeast("child",child,name+"∼[{child}]",1),"inv_play_total_"+name);
            invariant(invariants,atMost("child",child,name+"∼[{child}]",1),"inv_play_unique_"+name);
            traces.add(new Trace("ACL","plays "+role.name(),name));
        }

        addOwnerScopeAndCompatibility(acl,extents,plays,ownerByTarget,invariants);

        List<Assignment> initialisation=new ArrayList<>();
        for(String variable:variables)
            initialisation.add(new Assignment("act_init_"+id(variable),variable+" ≔ ∅"));
        Event init=new Event("INITIALISATION",List.of(),List.of(),initialisation);
        Context context=new Context(base+"_ctx",List.copyOf(new LinkedHashSet<>(sets)),constants,axioms);
        Machine machine=new Machine(base+"_machine",context.name(),variables,invariants,List.of(init));
        return new EventBProject(base,context,machine,traces,List.of());
    }

    private static void addAttributes(String owner,List<AclAttribute> attributes,Map<String,String> extents,
                                      List<String> variables,List<Predicate> invariants,List<Trace> traces) {
        for(AclAttribute attribute:attributes) {
            String name=id(owner)+"_"+id(attribute.name()); variables.add(name);
            // UML-B scalar Property: [1] is the default (total function),
            // while explicit ACL `optional` means [0..1] (partial function).
            String arrow=attribute.optional()?" ⇸ ":" → ";
            invariant(invariants,name+" ∈ "+extents.get(owner)+arrow+range(attribute.type()),"inv_type_"+name);
            traces.add(new Trace("ACL",owner+"."+attribute.name(),name));
        }
    }

    private static String range(AclDataType type) {
        return switch(type.sourceName()) {
            case "Boolean" -> "BOOL";
            case "Integer" -> "ℤ";
            case "Real" -> "ℝ";
            case "String" -> "STRING";
            default -> id(type.sourceName()).toUpperCase(Locale.ROOT);
        };
    }

    private static void addOwnerScopeAndCompatibility(AclModel acl,Map<String,String> extents,
            Map<String,String> plays,Map<String,AclOwner> ownerByTarget,List<Predicate> invariants) {
        for(AclRole child:acl.roles()) {
            AclOwner childOwner=ownerByTarget.get(child.name()); if(childOwner==null) continue;
            String ancestor=child.name(), ancestorOccurrences="{c}"; Set<String> seen=new LinkedHashSet<>();
            while(seen.add(ancestor)) {
                AclRole definition=acl.findRole(ancestor).orElse(null);
                if(definition==null||definition.parentRoles().isEmpty()) break;
                ancestorOccurrences=plays.get(ancestor)+"∼["+ancestorOccurrences+"]";
                ancestor=definition.parentRoles().get(0);
                AclOwner ancestorOwner=ownerByTarget.get(ancestor); if(ancestorOwner==null) continue;
                String childGroups="owns_"+id(child.name())+"∼[{c}]";
                String expected=ascendOwnedGroup(childGroups,childOwner.sourceGroup(),ancestorOwner.sourceGroup(),ownerByTarget);
                invariant(invariants,"∀c·c∈"+extents.get(child.name())+" ⇒ owns_"+id(ancestor)
                        +"∼["+ancestorOccurrences+"] = "+expected,"inv_scope_"+id(child.name())+"_"+id(ancestor));
            }
        }
        for(AclGroup group:acl.groups()) {
            List<AclOwner> roleOwners=acl.owners().stream().filter(o->o.sourceGroup().equals(group.name()))
                    .filter(o->acl.findRole(o.target()).isPresent()).toList();
            Set<String> compatible=new LinkedHashSet<>();
            group.compatibilities().stream().filter(c->c.type()==AclCompatibilityType.COMPATIBLE)
                    .forEach(c->compatible.add(pairKey(c.fromRole(),c.toRole())));
            for(AclOwner owner:roleOwners) {
                String role=owner.target(), relation="owns_"+id(role);
                invariant(invariants,"∀g,r1,r2·g↦r1∈"+relation+" ∧ g↦r2∈"+relation+" ∧ "
                        +agentSet(acl,plays,role,"r1")+" = "+agentSet(acl,plays,role,"r2")+" ⇒ r1=r2",
                        "inv_no_duplicate_agent_"+id(group.name())+"_"+id(role));
            }
            for(int i=0;i<roleOwners.size();i++) for(int j=i+1;j<roleOwners.size();j++) {
                String left=roleOwners.get(i).target(),right=roleOwners.get(j).target();
                if(compatible.contains(pairKey(left,right))||roleAncestorOf(acl,left,right)||roleAncestorOf(acl,right,left)) continue;
                invariant(invariants,"∀g,r1,r2·g↦r1∈owns_"+id(left)+" ∧ g↦r2∈owns_"+id(right)+" ⇒ "
                        +agentSet(acl,plays,left,"r1")+" ∩ "+agentSet(acl,plays,right,"r2")+" = ∅",
                        "inv_incompatible_"+id(group.name())+"_"+id(left)+"_"+id(right));
            }
        }
    }

    private static String ascendOwnedGroup(String term,String childGroup,String ancestorGroup,
                                            Map<String,AclOwner> ownerByTarget) {
        String current=childGroup,result=term; Set<String> seen=new LinkedHashSet<>();
        while(!current.equals(ancestorGroup)&&seen.add(current)) {
            AclOwner owner=ownerByTarget.get(current); if(owner==null) return "∅";
            result="owns_"+id(current)+"∼["+result+"]"; current=owner.sourceGroup();
        }
        return result;
    }
    private static String agentSet(AclModel acl,Map<String,String> plays,String role,String variable) {
        String current=role,result="{"+variable+"}"; Set<String> seen=new LinkedHashSet<>();
        while(seen.add(current)) {
            result=plays.get(current)+"∼["+result+"]";
            AclRole definition=acl.findRole(current).orElseThrow();
            if(definition.parentRoles().isEmpty()) return result;
            current=definition.parentRoles().get(0);
        }
        return "∅";
    }
    private static boolean roleAncestorOf(AclModel acl,String ancestor,String child) {
        String current=child; Set<String> seen=new LinkedHashSet<>();
        while(seen.add(current)) {
            AclRole role=acl.findRole(current).orElse(null);
            if(role==null||role.parentRoles().isEmpty()) return false;
            current=role.parentRoles().get(0); if(current.equals(ancestor)) return true;
        }
        return false;
    }

    private static void addCardinality(List<Predicate> invariants,String relation,String domain,
                                       AclCardinality cardinality,String label) {
        if(cardinality.min()>0) invariant(invariants,atLeast("x",domain,relation+"[{x}]",cardinality.min()),label+"_min");
        cardinality.max().ifPresent(max->invariant(invariants,atMost("x",domain,relation+"[{x}]",max),label+"_max"));
    }
    private static void addEndpointCardinality(List<Predicate> invariants,String relation,String domain,
            AclCardinality cardinality,boolean inverse,String label) {
        String image=inverse?relation+"∼[{x}]":relation+"[{x}]";
        if(cardinality.min()>0) invariant(invariants,atLeast("x",domain,image,cardinality.min()),label+"_min");
        cardinality.max().ifPresent(max->invariant(invariants,atMost("x",domain,image,max),label+"_max"));
    }
    private static String atLeast(String owner,String domain,String image,int count) {
        if(count==1) return "∀"+owner+"·"+owner+"∈"+domain+" ⇒ "+image+" ≠ ∅";
        List<String> names=new ArrayList<>(),clauses=new ArrayList<>();
        for(int i=1;i<=count;i++) names.add("y"+i);
        names.forEach(name->clauses.add(name+"∈"+image));
        for(int i=0;i<names.size();i++) for(int j=i+1;j<names.size();j++) clauses.add(names.get(i)+"≠"+names.get(j));
        return "∀"+owner+"·"+owner+"∈"+domain+" ⇒ (∃"+String.join(",",names)+"·"+String.join(" ∧ ",clauses)+")";
    }
    private static String atMost(String owner,String domain,String image,int count) {
        if(count==0) return "∀"+owner+"·"+owner+"∈"+domain+" ⇒ "+image+" = ∅";
        List<String> names=new ArrayList<>(),memberships=new ArrayList<>(),equalities=new ArrayList<>();
        for(int i=1;i<=count+1;i++) names.add("y"+i);
        names.forEach(name->memberships.add(name+"∈"+image));
        for(int i=0;i<names.size();i++) for(int j=i+1;j<names.size();j++) equalities.add(names.get(i)+"="+names.get(j));
        return "∀"+owner+"·"+owner+"∈"+domain+" ⇒ (∀"+String.join(",",names)+"·("
                +String.join(" ∧ ",memberships)+") ⇒ ("+String.join(" ∨ ",equalities)+"))";
    }
    private static void axiom(List<Predicate> target,String formula) {
        target.add(new Predicate("axm"+(target.size()+1),formula,false));
    }
    private static void invariant(List<Predicate> target,String formula,String label) {
        target.add(new Predicate(label,formula,false));
    }
    private static String pairKey(String a,String b){return a.compareTo(b)<=0?a+"\0"+b:b+"\0"+a;}
    public static String id(String value) {
        String result=value==null?"unnamed":value.replaceAll("[^A-Za-z0-9_]","_").replaceAll("_+","_");
        if(result.isBlank()) result="unnamed";
        return Character.isDigit(result.charAt(0))?"n_"+result:result;
    }
}
