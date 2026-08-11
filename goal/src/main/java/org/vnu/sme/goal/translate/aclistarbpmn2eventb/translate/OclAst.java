package org.vnu.sme.goal.translate.aclistarbpmn2eventb.translate;

/**
 * Abstract syntax for the small OCL fragment the Event-B exporter understands: booleans,
 * not/and/or/implies, equality, enum/string literals, attribute and shadow-model navigation,
 * and the exists/forAll/select/includes/isEmpty/notEmpty/allInstances/oclIsUndefined
 * collection operations. Anything outside this fragment fails to parse (the parser returns
 * {@code null}) rather than being approximated.
 */
sealed interface OclAst {
    record BoolLit(boolean value) implements OclAst {}
    /** {@code #name} (enum literal) or a quoted string literal (name may be empty for {@code ''}). */
    record Lit(String name, boolean quoted) implements OclAst {}
    record Ident(String name) implements OclAst {}
    record Not(OclAst x) implements OclAst {}
    record Bin(String op, OclAst a, OclAst b) implements OclAst {}
    /** {@code base.name} or {@code base.name()}. */
    record Dot(OclAst base, String name, boolean parens) implements OclAst {}
    /**
     * {@code base->op(...)}. For {@code exists/forAll/select}, {@code var} is the bound name and
     * {@code arg} is the body/condition. For {@code includes}, {@code var} is null and {@code arg}
     * is the single argument. For {@code isEmpty/notEmpty}, both are null.
     */
    record Arrow(OclAst base, String op, String var, OclAst arg) implements OclAst {}
}
