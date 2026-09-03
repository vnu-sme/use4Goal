package org.vnu.sme.goal.dsl.acl.parser;


/**
 * =============================================================================
 * MODULE: ACL CST to semantic-model factory
 * =============================================================================
 * 1. PURPOSE:
 *    Resolves parsed CST declarations into the executable ACL semantic model. A ACLModelCS is the input; linked model objects ready for validation/rendering are the output.
 *
 * 2. CORE MAPPING / LOGIC RULES:
 *    - Register declarations before resolving references to support forward references.
 *    - Convert textual attributes/cardinalities into typed semantic values.
 *    - Reject missing or incompatible references at the semantic boundary.
 *    - Main operations exposed by this file: ok(), create(), build(), group(), relation(), compatibility(), attributes().
 *
 * 3. PIPELINE / WORKFLOW:
 *      1. create / build(ACLModelCS)
 *      2. index declarations
 *      3. resolve references and relationships
 *      4. assemble ACLModel
 * =============================================================================
 */
import java.math.BigInteger;
import java.util.*;
import org.vnu.sme.goal.dsl.acl.ast.*;
import org.vnu.sme.goal.dsl.acl.mm.*;

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
            for(var x:ast.enums()){
                Set<String>s=new LinkedHashSet<>(x.literals());
                if(s.size()!=x.literals().size())error(x.location(),"duplicate literal in enum '"+x.name()+"'");
                AclEnum e=new AclEnum(x.name(),List.copyOf(s));
                if(enums.putIfAbsent(x.name(),e)!=null)error(x.location(),"duplicate enum name '"+x.name()+"'");
                else enumList.add(e);
            }
            List<AclEntity> entities=ast.entities().stream().map(x->new AclEntity(x.name(),x.specializes(),attributes(x.name(),x.attributes()))).toList();
            List<AclRole> roles=ast.roles().stream().map(x->new AclRole(x.name(),x.parentRoles(),attributes(x.name(),x.attributes()))).toList();
            Map<String,AclEntity> entityMap=indexEntity(entities); Map<String,AclRole> roleMap=indexRole(roles);
            List<AclGroup> groups=ast.groups().stream().map(x->group(x,entityMap,roleMap)).toList();
            List<AclCompatibility> compatibility=ast.compatibilities().stream().map(this::compatibility).toList();
            Map<String,AclGroup> declaredGroups=indexGroup(groups);
            groups=groups.stream().map(g->new AclGroup(g.name(),g.specializes(),g.attributes(),
                    g.members(),g.roles(),g.entities(),
                    g.members().stream().filter(m->declaredGroups.containsKey(m.type()))
                            .map(m->new AclSubgroupMembership(declaredGroups.get(m.type()),m.multiplicity())).toList(),
                    compatibility.stream().filter(c->c.groupName().equals(g.name())).toList(),
                    g.roleEntityRelations(),g.cardinalityConstraints(),g.isOrganizationalContext())).toList();
            Map<String,AclGroup> groupMap=indexGroup(groups);
            List<AclRelation> relations=new ArrayList<>();
            for(var source:ast.groups()) for(var member:source.members()) {
                if (roleMap.containsKey(member.type()) || groupMap.containsKey(member.type())
                        || (source.organizationalContext() && entityMap.containsKey(member.type()))) {
                    relations.add(new AclRelation(RelationKind.COMPOSITION,
                            AclContainment.relationName(source.name(), member.type()),
                            new AclEndpoint(source.name(), AclCardinality.bounded(1, 1),
                                    Optional.of(source.organizationalContext()
                                            ? "orgContext" : AclContainment.wholeRoleName())),
                            new AclEndpoint(member.type(), cardinality(member.multiplicity()),
                                    Optional.of(source.organizationalContext()
                                            ? lowerFirst(member.type()) : AclContainment.partRoleName(member.type())))));
                }
            }
            for(var x:ast.relations())relations.add(relation(x));
            for(var source:ast.groups()) for(var member:source.members()) {
                if(!source.organizationalContext() && entityMap.containsKey(member.type())) {
                    error(member.location(), "Entity '"+member.type()+"' cannot be a member of Group '"
                            +source.name()+"'; declare an explicit association, aggregation, or composition instead");
                }
            }
            List<AclGeneralization> gens=new ArrayList<>(); ast.entities().forEach(x->x.specializes().ifPresent(p->gens.add(new AclGeneralization(x.name(),p)))); ast.roles().forEach(x->x.parentRoles().forEach(p->gens.add(new AclGeneralization(x.name(),p)))); ast.groups().forEach(x->x.specializes().ifPresent(p->gens.add(new AclGeneralization(x.name(),p))));
            List<AclInvariant> invariants = ast.invariants().stream()
                    .map(x -> new AclInvariant(x.contextType(), x.name(), x.expression())).toList();
            Set<String> classifiers = new LinkedHashSet<>();
            entities.forEach(x -> classifiers.add(x.name())); roles.forEach(x -> classifiers.add(x.name()));
            groups.forEach(x -> classifiers.add(x.name()));
            Set<String> invariantNames = new LinkedHashSet<>();
            for (var x : ast.invariants()) {
                if (!classifiers.contains(x.contextType()))
                    error(x.location(), "unknown OCL context classifier '" + x.contextType() + "'");
                if (!invariantNames.add(x.contextType() + "::" + x.name()))
                    error(x.location(), "duplicate OCL invariant '" + x.contextType() + "::" + x.name() + "'");
            }
            return new AclModel(ast.version(),ast.name(),enumList,entities,roles,groups,relations,List.of(),compatibility,gens,invariants);
        }
        private AclGroup group(AclGroupCS x,Map<String,AclEntity> entities,Map<String,AclRole> roles){
            List<AclGroupMember> members=x.members().stream().map(m->new AclGroupMember(m.type(),cardinality(m.multiplicity()))).toList();
            List<AclRoleMembership> rp=members.stream().filter(m->roles.containsKey(m.type())).map(m->new AclRoleMembership(m.type(),m.multiplicity())).toList();
            List<AclEntityMembership> ep=members.stream().filter(m->entities.containsKey(m.type())).map(m->new AclEntityMembership(m.type(),m.multiplicity())).toList();
            return new AclGroup(x.name(),x.specializes(),attributes(x.name(),x.attributes()),
                    members,rp,ep,List.of(),List.of(),List.of(),List.of(),x.organizationalContext());
        }
        private AclRelation relation(AclRelationCS x){ List<AclEndpoint> e=x.endpoints().stream().map(v->new AclEndpoint(v.type(),cardinality(v.multiplicity()),v.roleName())).toList(); return new AclRelation(RelationKind.fromSource(x.kind()),x.name(),e.get(0),e.get(1)); }
        private AclCompatibility compatibility(AclCompatibilityCS x){
            return new AclCompatibility(x.fromRole(),x.toRole(),AclCompatibilityType.COMPATIBLE,
                    AclScope.INTER_GROUP,true,true,Objects.toString(x.groupName(),"__model__"));
        }
        private List<AclAttribute> attributes(String owner, List<AclAttributeCS> src) {
            Map<String,AclAttribute> out = new LinkedHashMap<>();
            for (var x : src) {
                if (x.optional() && x.explicitRequired()) {
                    error(x.location(), "attribute '" + x.name()
                            + "' cannot be both optional and required");
                    continue;
                }
                if (out.containsKey(x.name())) {
                    error(x.location(), "duplicate attribute '" + x.name() + "' in '" + owner + "'");
                    continue;
                }
                Optional<AclDataType> t = AclPrimitiveType.fromSource(x.typeName()).map(v -> (AclDataType) v)
                        .or(() -> Optional.ofNullable(enums.get(x.typeName())));
                if (t.isEmpty()) {
                    error(x.location(), "unknown attribute type '" + x.typeName() + "'");
                    continue;
                }
                if (x.defaultValue().isPresent()) {
                    String defVal = x.defaultValue().get();
                    if (t.get() instanceof AclEnum aclEnum) {
                        if (!aclEnum.literals().contains(defVal)) {
                            error(x.location(), "default value '" + defVal + "' is not a literal of enum '"
                                    + aclEnum.name() + "'; valid literals are " + aclEnum.literals());
                        }
                    } else if (!validPrimitiveDefault(t.get().sourceName(), defVal)) {
                        error(x.location(), "default value '" + defVal + "' is not valid for type '"
                                + t.get().sourceName() + "'");
                    }
                }
                out.put(x.name(), new AclAttribute(x.name(), t.get(), x.optional(), x.mutable(), x.defaultValue()));
            }
            return List.copyOf(out.values());
        }
        private AclCardinality cardinality(AclCardinalityCS x){
            try{
                int min=new BigInteger(x.min()).intValueExact();
                AclCardinality result=x.max().isEmpty()?AclCardinality.unlimited(min):AclCardinality.bounded(min,new BigInteger(x.max().get()).intValueExact());
                // Multiplicity [0] or [0..0] means no members are ever allowed — reject as meaningless.
                if(result.min()==0&&result.max().isPresent()&&result.max().getAsInt()==0)
                    error(x.location(),"multiplicity [0..0] is not meaningful; remove the member declaration or use [0..1] for optional membership");
                return result;
            }catch(RuntimeException e){error(x.location(),"invalid cardinality");return AclCardinality.unlimited(0);}
        }
        private static Map<String,AclEntity> indexEntity(List<AclEntity>x){Map<String,AclEntity>m=new HashMap<>();x.forEach(v->m.put(v.name(),v));return m;}
        private static Map<String,AclRole> indexRole(List<AclRole>x){Map<String,AclRole>m=new HashMap<>();x.forEach(v->m.put(v.name(),v));return m;}
        private static Map<String,AclGroup> indexGroup(List<AclGroup>x){Map<String,AclGroup>m=new HashMap<>();x.forEach(v->m.put(v.name(),v));return m;}
        void error(AclSourceLocationCS l,String m){errors.add(new SemanticError(l,m));}

        private static String lowerFirst(String value) {
            if (value == null || value.isEmpty()) return value;
            return Character.toLowerCase(value.charAt(0)) + value.substring(1);
        }

        private static boolean validPrimitiveDefault(String type, String value) {
            return switch (type) {
                case "Boolean" -> value.equals("true") || value.equals("false");
                case "Integer" -> value.matches("-?[0-9]+");
                case "Real" -> value.matches("-?[0-9]+(?:\\.[0-9]+)?");
                case "String" -> value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"");
                default -> true;
            };
        }
    }
}
