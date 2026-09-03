package org.vnu.sme.goal.translate.aclistarbpmn2eventb.translate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vnu.sme.goal.dsl.acl.mm.AclModel;
import org.vnu.sme.goal.dsl.acl.mm.AclRelation;
import org.vnu.sme.goal.dsl.acl.mm.AclRole;

/**
 * OCL-fragment-to-Event-B translator. Parses the small subset described by {@link OclAst} via
 * {@link OclParser} and walks the resulting tree with an {@link Emitter} that resolves both
 * shadow-model navigation ({@code target_Participant_in_MeetingUnit},
 * {@code source_Agent_plays_Secretary}) and canonical ACL navigation ({@code self.group},
 * {@code self.Participant}, {@code self.knownContact}) through the same rules, mirroring
 * {@link org.vnu.sme.goal.dsl.acl.ocl.AclOclPropertyResolver}'s resolution for the abstract-vs-concrete
 * declaring Role distinction (ACL_OCL_SEMANTICS.md §6.1). Anything the emitter cannot resolve
 * returns {@code null} rather than an approximated formula -- deliberately fail-closed.
 */
final class OclToEventB {
    private static final Pattern SHADOW_OWNER = Pattern.compile("(target|source)_([A-Za-z_]\\w*)_in_([A-Za-z_]\\w*)");
    private static final Pattern SHADOW_PLAYS = Pattern.compile("(target|source)_Agent_plays_([A-Za-z_]\\w*)");

    private OclToEventB() {}

    static String translateBpmnSelfGuard(String source, Map<String, String> attributes, String ownerType, AclModel acl) {
        OclAst ast = OclParser.parse(source);
        if (ast == null) return null;
        Emitter emitter = new Emitter(acl, attributes, Map.of("self", ownerType), "self");
        return emitter.emitPred(ast);
    }

    static List<String> translateBpmnSelfGuardConjuncts(String source, Map<String, String> attributes,
                                                         String ownerType, AclModel acl) {
        OclAst ast=OclParser.parse(source);
        if(ast==null) return List.of();
        Emitter emitter=new Emitter(acl,attributes,Map.of("self",ownerType),"self");
        List<String> result=new ArrayList<>();
        for(OclAst conjunct:splitTopLevelAnd(ast)) {
            String predicate=emitter.emitPred(conjunct);
            if(predicate==null) return List.of();
            result.add(predicate);
        }
        return result;
    }

    static String translate(String source, Map<String, String> attributes, AclModel acl) {
        OclAst ast = OclParser.parse(source);
        if (ast == null) return null;
        Emitter emitter = new Emitter(acl, attributes, Map.of(), "self");
        return emitter.emitPred(ast);
    }

    static String translateSelf(String source, String actorType, boolean universal,
                                Map<String, String> attributes, AclModel acl) {
        OclAst ast = OclParser.parse(source);
        if (ast == null) return null;
        Emitter emitter = new Emitter(acl, attributes, Map.of("self", actorType), "instance");
        String body = emitter.emitPred(ast);
        if (body == null) return null;
        String q = universal ? "∀" : "∃";
        String connector = universal ? " ⇒ " : " ∧ ";
        return "(" + q + "instance·instance∈" + emitter.classId(actorType) + connector + "(" + body + "))";
    }

    static String translateContext(String source, List<String> contextTypes, List<String> contextVariables,
                                   Map<String, String> attributes, AclModel acl) {
        if (contextTypes.isEmpty() || contextTypes.size() != contextVariables.size()) return null;
        OclAst ast = OclParser.parse(source);
        if (ast == null) return null;
        return new Emitter(acl, attributes, contextTypes, contextVariables).emitPred(ast);
    }

    static List<String> translateBpmnSelfPostEffect(String source, Map<String, String> attributes,
                                                     String ownerType, AclModel acl) {
        return translatePostEffectConjuncts(source, new Emitter(acl, attributes, Map.of("self", ownerType), "self"),
                "self", ownerType);
    }

    static List<String> translatePostEffect(String source, Map<String, String> attributes, AclModel acl) {
        OclAst ast = OclParser.parse(source);
        if (ast == null) return List.of();
        Emitter emitter = new Emitter(acl, attributes, Map.of(), "self");
        List<String> result = new ArrayList<>();
        for (OclAst conjunct : splitTopLevelAnd(ast)) {
            List<String> assignments = emitter.emitBatchAssignment(conjunct);
            if (assignments == null) return List.of();
            result.addAll(assignments);
        }
        return result;
    }

    private static List<String> translatePostEffectConjuncts(String source, Emitter emitter,
                                                              String selfName, String ownerType) {
        OclAst ast = OclParser.parse(source);
        if (ast == null) return List.of();
        List<String> result = new ArrayList<>();
        for (OclAst conjunct : splitTopLevelAnd(ast)) {
            List<String> direct = emitter.emitSelfAssignment(conjunct, selfName, ownerType);
            if (direct != null) { result.addAll(direct); continue; }
            List<String> batch = emitter.emitBatchAssignment(conjunct);
            if (batch == null) return List.of();
            result.addAll(batch);
        }
        return result;
    }

    private static List<OclAst> splitTopLevelAnd(OclAst ast) {
        List<OclAst> parts = new ArrayList<>();
        collectAnd(ast, parts);
        return parts;
    }
    private static void collectAnd(OclAst ast, List<OclAst> out) {
        if (ast instanceof OclAst.Bin b && b.op().equals("and")) {
            collectAnd(b.a(), out);
            collectAnd(b.b(), out);
        } else out.add(ast);
    }

    /** Resolution + emission over a parsed tree. One instance per translation call. */
    private static final class Emitter {
        private final AclModel acl;
        private final Map<String, String> attrs;
        private final Map<String, String> scope; // bound variable name -> ACL classifier/role type
        private final String selfOutputName;
        private final List<String> contextTypes;
        private final List<String> contextVariables;
        private int freshCounter = 0;

        Emitter(AclModel acl, Map<String, String> attrs, Map<String, String> scope, String selfOutputName) {
            this.acl = acl; this.attrs = attrs; this.scope = new LinkedHashMap<>(scope);
            this.selfOutputName = selfOutputName;
            this.contextTypes = scope.containsKey("self") ? List.of(scope.get("self")) : List.of();
            this.contextVariables = scope.containsKey("self") ? List.of(selfOutputName) : List.of();
        }

        Emitter(AclModel acl, Map<String, String> attrs, List<String> contextTypes, List<String> contextVariables) {
            this.acl = acl; this.attrs = attrs;
            this.scope = new LinkedHashMap<>(Map.of("self", contextTypes.get(0)));
            this.selfOutputName = contextVariables.get(0);
            this.contextTypes = List.copyOf(contextTypes);
            this.contextVariables = List.copyOf(contextVariables);
        }

        String emitPred(OclAst ast) {
            Res r = eval(ast);
            return toPred(r);
        }

        /** {@code self.attr = Expr} / {@code not self.attr} / bare {@code self.attr} -- one
         *  Event-B relation-override assignment per conjunct, {@code self} scoped to one object. */
        List<String> emitSelfAssignment(OclAst conjunct, String selfName, String ownerType) {
            String attrName; String rawValue;
            if (conjunct instanceof OclAst.Not n && n.x() instanceof OclAst.Dot d
                    && d.base() instanceof OclAst.Ident id && id.name().equals("self") && !d.parens()) {
                attrName = d.name(); rawValue = "FALSE";
            } else if (conjunct instanceof OclAst.Dot d
                    && d.base() instanceof OclAst.Ident id && id.name().equals("self") && !d.parens()) {
                attrName = d.name(); rawValue = "TRUE";
            } else if (conjunct instanceof OclAst.Bin b && b.op().equals("=")
                    && b.a() instanceof OclAst.Dot d
                    && d.base() instanceof OclAst.Ident id && id.name().equals("self") && !d.parens()) {
                attrName = d.name();
                Res value = eval(b.b());
                if (!(value instanceof Res.Value v) || !v.bindings().isEmpty()) return null;
                rawValue = scalar(v.term());
            } else return null;
            String attr = attrs.get(ownerType + "." + attrName);
            if (attr == null) return null;
            return List.of(attr + " ≔ " + attr + "  {" + selfName + " ↦ " + rawValue + "}");
        }

        /** {@code Coll->[select(...)->]forAll(v | <conjunction of v.attr = Expr / bare-bool>)}:
         *  a relation-override assignment per body conjunct, applied to the whole navigated set. */
        List<String> emitBatchAssignment(OclAst conjunct) {
            if (!(conjunct instanceof OclAst.Arrow forAll) || !forAll.op().equals("forAll") || forAll.var() == null)
                return null;
            Res base = eval(forAll.base());
            if (!(base instanceof Res.Obj set)) return null;
            String var = forAll.var();
            scope.put(var, set.type());
            try {
                List<String> result = new ArrayList<>();
                for (OclAst atom : splitTopLevelAnd(forAll.arg())) {
                    String attrName; String rawValue;
                    if (atom instanceof OclAst.Not n && n.x() instanceof OclAst.Dot d
                            && d.base() instanceof OclAst.Ident id && id.name().equals(var) && !d.parens()) {
                        attrName = d.name(); rawValue = "FALSE";
                    } else if (atom instanceof OclAst.Dot d
                            && d.base() instanceof OclAst.Ident id && id.name().equals(var) && !d.parens()) {
                        attrName = d.name(); rawValue = "TRUE";
                    } else if (atom instanceof OclAst.Bin b && b.op().equals("=")
                            && b.a() instanceof OclAst.Dot d
                            && d.base() instanceof OclAst.Ident id && id.name().equals(var) && !d.parens()) {
                        attrName = d.name();
                        Res value = eval(b.b());
                        if (!(value instanceof Res.Value v) || !v.bindings().isEmpty()) return null;
                        rawValue = scalar(v.term());
                    } else return null;
                    String attr = attrs.get(set.type() + "." + attrName);
                    if (attr == null) return null;
                    // Override only the selected domain while preserving values for every
                    // object outside it. Domain-restricting the whole result to set.term()
                    // would turn a total attribute function into a partial one and fail INV.
                    result.add(attr + " ≔ " + attr + "  (" + set.term() + " × {" + rawValue + "})");
                }
                return result;
            } finally { scope.remove(var); }
        }

        // ── Core evaluation ─────────────────────────────────────────────────

        private sealed interface Res {
            record Obj(String term, String type) implements Res {}
            record Value(String term, List<String[]> bindings) implements Res {}
            record Pred(String formula) implements Res {}
            record TypeRef(String typeName) implements Res {}
        }

        private String toPred(Res r) {
            return switch (r) {
                case null -> null;
                case Res.Pred p -> p.formula();
                case Res.Value v -> wrapBindings(v.bindings(), v.term() + " = {TRUE}");
                case Res.Obj o -> null;
                case Res.TypeRef t -> null;
            };
        }

        private Res eval(OclAst ast) {
            return switch (ast) {
                case OclAst.BoolLit b -> new Res.Value(b.value() ? "{TRUE}" : "{FALSE}", List.of());
                case OclAst.Lit l -> evalLit(l);
                case OclAst.Ident id -> evalIdent(id.name());
                case OclAst.Not n -> {
                    String inner = toPred(eval(n.x()));
                    yield inner == null ? null : new Res.Pred("¬(" + inner + ")");
                }
                case OclAst.Bin b -> evalBin(b);
                case OclAst.Dot d -> evalDot(d);
                case OclAst.Arrow a -> evalArrow(a);
            };
        }

        private Res evalLit(OclAst.Lit l) {
            if (!l.quoted()) return new Res.Value("{" + AclIStarBpmn2EventBTranslator.id(l.name()) + "}", List.of());
            if (l.name().isEmpty()) return new Res.Value("{EMPTY_STRING}", List.of());
            return null; // non-empty string literals are not modelled as Event-B constants (yet)
        }

        private Res evalIdent(String name) {
            if (scope.containsKey(name)) {
                String outputName = name.equals("self") ? selfOutputName : name;
                return new Res.Obj("{" + outputName + "}", scope.get(name));
            }
            if (name.equals("self")) return null; // unscoped context: self has no meaning
            return new Res.TypeRef(name);
        }

        private Res evalBin(OclAst.Bin b) {
            if (b.op().equals("=") || b.op().equals("<>")) {
                Res a = eval(b.a()), c = eval(b.b());
                if (!(a instanceof Res.Value va) || !(c instanceof Res.Value vc)) return null;
                List<String[]> bindings = new ArrayList<>(va.bindings());
                bindings.addAll(vc.bindings());
                String core = va.term() + (b.op().equals("<>") ? " ≠ " : " = ") + vc.term();
                return new Res.Pred(wrapBindings(bindings, core));
            }
            String left = toPred(eval(b.a()));
            String right = toPred(eval(b.b()));
            if (left == null || right == null) return null;
            String connector = switch (b.op()) { case "and" -> " ∧ "; case "or" -> " ∨ "; default -> " ⇒ "; };
            return new Res.Pred("(" + left + ")" + connector + "(" + right + ")");
        }

        private Res evalDot(OclAst.Dot d) {
            int contextDepth = contextDepth(d);
            if (contextDepth >= 0) {
                if (contextDepth >= contextTypes.size()) return null;
                return new Res.Obj("{" + contextVariables.get(contextDepth) + "}", contextTypes.get(contextDepth));
            }
            if (d.name().equals("allInstances") && d.parens() && d.base() instanceof OclAst.Ident id
                    && !scope.containsKey(id.name()) && !id.name().equals("self")) {
                return new Res.Obj(classId(id.name()), id.name());
            }
            Res base = eval(d.base());
            if (!(base instanceof Res.Obj obj)) return null;
            if (d.name().equals("oclIsUndefined") && d.parens()) {
                return new Res.Pred(obj.term() + " = ∅");
            }
            return resolveNav(obj.term(), obj.type(), d.name());
        }

        private int contextDepth(OclAst ast) {
            if (ast instanceof OclAst.Ident id && id.name().equals("self")) return 0;
            if (ast instanceof OclAst.Dot dot && dot.name().equals("outer") && !dot.parens()) {
                int parent = contextDepth(dot.base());
                return parent < 0 ? -1 : parent + 1;
            }
            return -1;
        }

        private Res evalArrow(OclAst.Arrow a) {
            Res base = eval(a.base());
            if (!(base instanceof Res.Obj set)) return null;
            return switch (a.op()) {
                case "exists", "forAll" -> {
                    if (a.var() == null) yield null;
                    String quantifiedSet = alphaRenameComprehension(set.term(), a.var());
                    scope.put(a.var(), set.type());
                    String body;
                    try { body = toPred(eval(a.arg())); } finally { scope.remove(a.var()); }
                    if (body == null) yield null;
                    yield a.op().equals("exists")
                            ? new Res.Pred("(∃" + a.var() + "·" + a.var() + "∈" + quantifiedSet + " ∧ (" + body + "))")
                            : new Res.Pred("(∀" + a.var() + "·" + a.var() + "∈" + quantifiedSet + " ⇒ (" + body + "))");
                }
                case "select" -> {
                    if (a.var() == null) yield null;
                    scope.put(a.var(), set.type());
                    String cond;
                    try { cond = toPred(eval(a.arg())); } finally { scope.remove(a.var()); }
                    if (cond == null) yield null;
                    yield new Res.Obj("{" + a.var() + "·" + a.var() + "∈" + set.term() + " ∧ (" + cond + ")∣" + a.var() + "}", set.type());
                }
                case "includes" -> {
                    if (a.var() != null || a.arg() == null) yield null;
                    Res item = eval(a.arg());
                    if (!(item instanceof Res.Obj itemSet)) yield null;
                    String itemTerm=coerceRole(itemSet.term(),itemSet.type(),set.type());
                    yield itemTerm==null?null:new Res.Pred(itemTerm + " ⊆ " + set.term());
                }
                case "isEmpty" -> new Res.Pred(set.term() + " = ∅");
                case "notEmpty" -> new Res.Pred(set.term() + " ≠ ∅");
                default -> null;
            };
        }

        // ── Navigation resolution ────────────────────────────────────────────

        private Res resolveNav(String term, String elementType, String name) {
            Matcher owner = SHADOW_OWNER.matcher(name);
            if (owner.matches()) return resolveOwnerShadow(term, owner.group(1), owner.group(2));
            Matcher plays = SHADOW_PLAYS.matcher(name);
            if (plays.matches()) return resolvePlaysShadow(term, plays.group(1), plays.group(2));
            if (name.equals("group")) return resolveOwnerBackward(term, elementType);
            Res member = resolveGroupMember(term, elementType, name);
            if (member != null) return member;
            Res relation = resolveRelationEndpoint(term, elementType, name);
            if (relation != null) return relation;
            return resolveAttribute(term, elementType, name);
        }

        private Res resolveOwnerShadow(String term, String direction, String roleOrGroup) {
            String ownerRel = attrs.get("$owner." + roleOrGroup);
            if (ownerRel == null) return null;
            if (direction.equals("target")) return new Res.Obj(ownerRel + "[" + term + "]", roleOrGroup);
            String groupType = attrs.get("$ownerGroup." + roleOrGroup);
            if (groupType == null) return null;
            return new Res.Obj(ownerRel + "∼[" + term + "]", groupType);
        }

        private Res resolvePlaysShadow(String term, String direction, String role) {
            String relation = attrs.get("$plays." + role);
            String parentType = attrs.get("$playsParentType." + role);
            if (relation == null || parentType == null) return null;
            if (direction.equals("source")) {
                return new Res.Obj(relation + "∼[" + term + "]", parentType);
            }
            return new Res.Obj(relation + "[" + term + "]", role);
        }

        private Res resolveOwnerBackward(String term, String elementType) {
            String ownerRel = attrs.get("$owner." + elementType);
            String groupType = attrs.get("$ownerGroup." + elementType);
            if (ownerRel == null || groupType == null) return null;
            return new Res.Obj(ownerRel + "∼[" + term + "]", groupType);
        }

        private Res resolveGroupMember(String term, String elementType, String name) {
            if (acl.groups().stream().noneMatch(g -> g.name().equals(elementType))) return null;
            String ownerRel = attrs.get("$owner." + name);
            String ownerGroupOfName = attrs.get("$ownerGroup." + name);
            if (ownerRel == null || !elementType.equals(ownerGroupOfName)) return null;
            return new Res.Obj(ownerRel + "[" + term + "]", name);
        }

        private Res resolveRelationEndpoint(String term, String elementType, String name) {
            for (AclRelation relation : acl.relations()) {
                String rel = attrs.get("$relation." + relation.name());
                if (rel == null) continue;
                if (relation.target().roleName().equals(Optional.of(name))) {
                    String source=coerceRole(term,elementType,relation.source().type());
                    if(source!=null) return new Res.Obj(rel + "[" + source + "]", relation.target().type());
                }
                if (relation.source().roleName().equals(Optional.of(name))) {
                    String target=coerceRole(term,elementType,relation.target().type());
                    if(target!=null) return new Res.Obj(rel + "∼[" + target + "]", relation.source().type());
                }
            }
            return null;
        }

        /** A Role child and its parent are distinct occurrences in ACL.  Navigation declared
         * on a parent Role therefore projects child occurrences through the plays chain. */
        private String coerceRole(String term,String actualType,String expectedType) {
            if(actualType.equals(expectedType)) return term;
            if(acl.findRole(actualType).isEmpty()||acl.findRole(expectedType).isEmpty()) return null;
            return ascendRole(term,actualType,expectedType);
        }

        private Res resolveAttribute(String term, String elementType, String name) {
            String declaringRole = findDeclaringRole(elementType, name);
            if (declaringRole == null) {
                String direct = attrs.get(elementType + "." + name);
                if (direct == null) return null;
                return new Res.Value(direct + "[" + term + "]", List.of());
            }
            if (declaringRole.equals(elementType)) {
                String attr = attrs.get(elementType + "." + name);
                if (attr == null) return null;
                return new Res.Value(attr + "[" + term + "]", List.of());
            }
            String attr = attrs.get(declaringRole + "." + name);
            if (attr == null) return null;
            String ancestorTerm = ascendRole(term, elementType, declaringRole);
            if (ancestorTerm == null) return null;
            return new Res.Value(attr + "[" + ancestorTerm + "]", List.of());
        }

        /** Navigate from a child Role occurrence set to the requested ancestor Role set. */
        private String ascendRole(String term, String childRole, String ancestorRole) {
            String current = childRole;
            String result = term;
            java.util.Set<String> seen = new java.util.LinkedHashSet<>();
            while (seen.add(current) && !current.equals(ancestorRole)) {
                String relation = attrs.get("$plays." + current);
                String parent = attrs.get("$playsParentType." + current);
                if (relation == null || parent == null || parent.equals("$Agent")) return null;
                result = relation + "∼[" + result + "]";
                current = parent;
            }
            return current.equals(ancestorRole) ? result : null;
        }

        /** Walks {@code role.parentRoles()} looking for the Role that declares {@code attrName};
         *  returns {@code null} when {@code elementType} is not a Role at all (a Group/Entity, or
         *  an unresolvable ancestor -- e.g. a concrete-ancestor cross-Group case). */
        private String findDeclaringRole(String elementType, String attrName) {
            String current = elementType;
            java.util.Set<String> seen = new java.util.LinkedHashSet<>();
            while (seen.add(current)) {
                AclRole role = acl.findRole(current).orElse(null);
                if (role == null) return null;
                if (role.attributes().stream().anyMatch(a -> a.name().equals(attrName))) return current;
                if (role.parentRoles().isEmpty()) return null;
                current = role.parentRoles().get(0);
            }
            return null;
        }

        /** Attribute navigation remains relational.  ACL owner/play navigation is single-valued
         *  by its context axioms, hence comparing {@code attr[S]} with {@code {value}} has the same
         *  meaning as applying a partial-looking scalar expression, but is defined for every S. */

        private static String scalar(String setTerm) {
            if (setTerm.length() >= 3 && setTerm.charAt(0) == '{'
                    && setTerm.charAt(setTerm.length() - 1) == '}')
                return setTerm.substring(1, setTerm.length() - 1);
            return setTerm;
        }


        private String freshVar() { return "ag" + (++freshCounter); }

        private String alphaRenameComprehension(String term, String outerVariable) {
            String prefix = "{" + outerVariable + "·";
            String suffix = "∣" + outerVariable + "}";
            if (!term.startsWith(prefix) || !term.endsWith(suffix)) return term;
            String replacement = "selected" + (++freshCounter);
            return term.replaceAll("\\b" + Pattern.quote(outerVariable) + "\\b", replacement);
        }

        private String classId(String type) {
            return attrs.getOrDefault("$type." + type, "E_" + AclIStarBpmn2EventBTranslator.id(type));
        }

        private static String wrapBindings(List<String[]> bindings, String core) {
            String pred = core;
            for (int i = bindings.size() - 1; i >= 0; i--) {
                pred = "(∃" + bindings.get(i)[0] + "·" + bindings.get(i)[0] + "∈" + bindings.get(i)[1] + " ∧ (" + pred + "))";
            }
            return pred;
        }
    }
}
