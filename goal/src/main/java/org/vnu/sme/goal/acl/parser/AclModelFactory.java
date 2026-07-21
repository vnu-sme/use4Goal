package org.vnu.sme.goal.acl.parser;

import java.math.BigInteger;
import java.util.*;
import org.vnu.sme.goal.acl.ast.*;
import org.vnu.sme.goal.acl.mm.*;

public final class AclModelFactory {
    public record SemanticError(AclSourceLocationCS location,String message){}
    public record Result(AclModel model,List<SemanticError> errors){ public Result{errors=List.copyOf(errors);} public boolean ok(){return model!=null&&errors.isEmpty();}}
    private AclModelFactory(){}
    public static Result create(AclModelCS ast){ Builder b=new Builder(ast); AclModel m=b.build(); if(m!=null) AclSemanticValidator.validate(m).forEach(x->b.error(ast.location(),x)); return new Result(m,b.errors); }

    private static final class Builder {
        private final AclModelCS ast; private final List<SemanticError> errors=new ArrayList<>();
        private final Map<String,AclEnum> enums=new LinkedHashMap<>();
        Builder(AclModelCS ast){this.ast=ast;}
        AclModel build(){
            List<AclEnum> enumList=new ArrayList<>();
            for(var x:ast.enums()){ Set<String>s=new LinkedHashSet<>(x.literals()); if(s.size()!=x.literals().size())error(x.location(),"duplicate literal in enum '"+x.name()+"'"); AclEnum e=new AclEnum(x.name(),List.copyOf(s)); enums.put(x.name(),e); enumList.add(e); }
            List<AclEntity> entities=ast.entities().stream().map(x->new AclEntity(x.name(),x.specializes(),attributes(x.name(),x.attributes()))).toList();
            List<AclRole> roles=ast.roles().stream().map(x->new AclRole(x.name(),x.isAbstract(),x.parentRoles(),attributes(x.name(),x.attributes()))).toList();
            Map<String,AclEntity> entityMap=indexEntity(entities); Map<String,AclRole> roleMap=indexRole(roles);
            List<AclGroup> groups=ast.groups().stream().map(x->group(x,entityMap,roleMap)).toList(); Map<String,AclGroup> groupMap=indexGroup(groups);
            List<AclRelation> relations=new ArrayList<>(); for(var x:ast.relations())relations.add(relation(x));
            List<AclOwner> owners=new ArrayList<>();
            for(var source:ast.groups()) for(var member:source.members()) {
                AclCardinality mult=cardinality(member.multiplicity());
                if(entityMap.containsKey(member.type())) relations.add(new AclRelation(RelationKind.COMPOSITION,"member_"+source.name()+"_"+member.type(),new AclEndpoint(source.name(),AclCardinality.bounded(1,1)),new AclEndpoint(member.type(),mult)));
                else owners.add(new AclOwner(source.name(),member.type(),mult));
            }
            List<AclCompatibility> compatibility=ast.compatibilities().stream().map(this::compatibility).toList();
            List<AclGeneralization> gens=new ArrayList<>(); ast.entities().forEach(x->x.specializes().ifPresent(p->gens.add(new AclGeneralization(x.name(),p)))); ast.roles().forEach(x->x.parentRoles().forEach(p->gens.add(new AclGeneralization(x.name(),p))));
            return new AclModel(ast.version(),ast.name(),enumList,entities,roles,groups,relations,owners,compatibility,gens);
        }
        private AclGroup group(AclGroupCS x,Map<String,AclEntity> entities,Map<String,AclRole> roles){
            List<AclGroupMember> members=x.members().stream().map(m->new AclGroupMember(m.type(),cardinality(m.multiplicity()))).toList();
            List<AclRoleMembership> rp=members.stream().filter(m->roles.containsKey(m.type())).map(m->new AclRoleMembership(m.type(),m.multiplicity())).toList();
            List<AclEntityMembership> ep=members.stream().filter(m->entities.containsKey(m.type())).map(m->new AclEntityMembership(m.type(),m.multiplicity())).toList();
            return new AclGroup(x.name(),Optional.empty(),attributes(x.name(),x.attributes()),members,rp,ep,List.of(),List.of());
        }
        private AclRelation relation(AclRelationCS x){ List<AclEndpoint> e=x.endpoints().stream().map(v->new AclEndpoint(v.type(),cardinality(v.multiplicity()))).toList(); return new AclRelation(RelationKind.fromSource(x.kind()),x.name(),e.get(0),e.get(1)); }
        private AclCompatibility compatibility(AclCompatibilityCS x){
            boolean extendsSubgroups=x.options().stream().filter(AclLinkOptionCS.ExtendsSubgroupsCS.class::isInstance).map(AclLinkOptionCS.ExtendsSubgroupsCS.class::cast).anyMatch(AclLinkOptionCS.ExtendsSubgroupsCS::value);
            boolean bidirectional=x.bidirectionalArrow()||x.options().stream().filter(AclLinkOptionCS.BidirectionalCS.class::isInstance).map(AclLinkOptionCS.BidirectionalCS.class::cast).anyMatch(AclLinkOptionCS.BidirectionalCS::value);
            AclCompatibilityType type=x.options().stream().filter(AclLinkOptionCS.TypeCS.class::isInstance).map(AclLinkOptionCS.TypeCS.class::cast).flatMap(v->AclCompatibilityType.fromSource(v.value()).stream()).findFirst().orElse(AclCompatibilityType.COMPATIBLE);
            AclScope scope=x.options().stream().filter(AclLinkOptionCS.ScopeCS.class::isInstance).map(AclLinkOptionCS.ScopeCS.class::cast).flatMap(v->AclScope.fromSource(v.value()).stream()).findFirst().orElse(AclScope.INTRA_GROUP);
            return new AclCompatibility(x.fromRole(),x.toRole(),type,scope,extendsSubgroups,bidirectional);
        }
        private List<AclAttribute> attributes(String owner,List<AclAttributeCS> src){ Map<String,AclAttribute> out=new LinkedHashMap<>(); for(var x:src){ if(out.containsKey(x.name())){error(x.location(),"duplicate attribute '"+x.name()+"' in '"+owner+"'");continue;} Optional<AclDataType> t=AclPrimitiveType.fromSource(x.typeName()).map(v->(AclDataType)v).or(()->Optional.ofNullable(enums.get(x.typeName()))); if(t.isEmpty()){error(x.location(),"unknown attribute type '"+x.typeName()+"'");continue;} out.put(x.name(),new AclAttribute(x.name(),t.get(),x.required(),x.mutable(),x.defaultValue())); } return List.copyOf(out.values()); }
        private AclCardinality cardinality(AclCardinalityCS x){ try{int min=new BigInteger(x.min()).intValueExact(); return x.max().isEmpty()?AclCardinality.unlimited(min):AclCardinality.bounded(min,new BigInteger(x.max().get()).intValueExact());}catch(RuntimeException e){error(x.location(),"invalid cardinality");return AclCardinality.unlimited(0);} }
        private static Map<String,AclEntity> indexEntity(List<AclEntity>x){Map<String,AclEntity>m=new HashMap<>();x.forEach(v->m.put(v.name(),v));return m;}
        private static Map<String,AclRole> indexRole(List<AclRole>x){Map<String,AclRole>m=new HashMap<>();x.forEach(v->m.put(v.name(),v));return m;}
        private static Map<String,AclGroup> indexGroup(List<AclGroup>x){Map<String,AclGroup>m=new HashMap<>();x.forEach(v->m.put(v.name(),v));return m;}
        void error(AclSourceLocationCS l,String m){errors.add(new SemanticError(l,m));}
    }
}
