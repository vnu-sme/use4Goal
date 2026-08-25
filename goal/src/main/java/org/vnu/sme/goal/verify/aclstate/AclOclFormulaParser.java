package org.vnu.sme.goal.verify.aclstate;

import java.util.ArrayList;
import java.util.List;

/** Parser for the executable ACL/OCL Boolean fragment used by Kodkod. */
final class AclOclFormulaParser {
    sealed interface Node permits Literal, Name, Unary, Binary, Property, Call, AtPre {}
    record Literal(Object value) implements Node {}
    record Name(String value) implements Node {}
    record Unary(String operator, Node operand) implements Node {}
    record Binary(String operator, Node left, Node right) implements Node {}
    record Property(Node source, String name) implements Node {}
    record Call(Node source, String operation, String variable, List<Node> arguments) implements Node {
        Call { arguments = List.copyOf(arguments); }
    }
    record AtPre(Node expression) implements Node {}

    private enum Kind { ID, STRING, NUMBER, TRUE, FALSE, AND, OR, NOT, IMPLIES,
                        EQ, NE, LT, LE, GT, GE, DOT, ARROW, LP, RP, BAR, COMMA,
                        HASH, COLON2, AT_PRE, EOF }
    private record Token(Kind kind, String text, int column) {}

    private final List<Token> tokens;
    private int at;

    private AclOclFormulaParser(String source) {
        tokens = lex(source);
    }

    static Node parse(String source) {
        if (source == null || source.isBlank()) return new Literal(Boolean.TRUE);
        AclOclFormulaParser parser = new AclOclFormulaParser(source);
        Node result = parser.implies();
        parser.expect(Kind.EOF);
        return result;
    }

    private Node implies() {
        Node left = or();
        return accept(Kind.IMPLIES) ? new Binary("implies", left, implies()) : left;
    }

    private Node or() {
        Node result = and();
        while (accept(Kind.OR)) result = new Binary("or", result, and());
        return result;
    }

    private Node and() {
        Node result = equality();
        while (accept(Kind.AND)) result = new Binary("and", result, equality());
        return result;
    }

    private Node equality() {
        Node result = comparison();
        while (peek(Kind.EQ) || peek(Kind.NE)) {
            String operator = take().text();
            result = new Binary(operator, result, comparison());
        }
        return result;
    }

    private Node comparison() {
        Node result = unary();
        while (peek(Kind.LT) || peek(Kind.LE) || peek(Kind.GT) || peek(Kind.GE)) {
            String operator = take().text();
            result = new Binary(operator, result, unary());
        }
        return result;
    }

    private Node unary() {
        if (accept(Kind.NOT)) return new Unary("not", unary());
        return navigation(primary());
    }

    private Node primary() {
        if (accept(Kind.TRUE)) return new Literal(Boolean.TRUE);
        if (accept(Kind.FALSE)) return new Literal(Boolean.FALSE);
        if (peek(Kind.STRING)) return new Literal(take().text());
        if (peek(Kind.NUMBER)) {
            String value = take().text();
            return new Literal(value.contains(".") ? Double.valueOf(value) : Long.valueOf(value));
        }
        if (accept(Kind.HASH)) return new Literal(expect(Kind.ID).text());
        if (accept(Kind.LP)) {
            Node result = implies();
            expect(Kind.RP);
            return result;
        }
        String name = expect(Kind.ID).text();
        if (accept(Kind.COLON2)) return new Literal(name + "::" + expect(Kind.ID).text());
        return new Name(name);
    }

    private Node navigation(Node initial) {
        Node result = initial;
        while (true) {
            if (accept(Kind.AT_PRE)) {
                result = new AtPre(result);
                continue;
            }
            boolean arrow;
            if (accept(Kind.DOT)) arrow = false;
            else if (accept(Kind.ARROW)) arrow = true;
            else return result;
            String operation = expect(Kind.ID).text();
            if (!accept(Kind.LP)) {
                if (arrow) throw error("'->" + operation + "' requires parentheses");
                result = new Property(result, operation);
                continue;
            }
            String variable = null;
            List<Node> arguments = new ArrayList<>();
            if (!peek(Kind.RP)) {
                if ((operation.equals("forAll") || operation.equals("exists"))
                        && peek(Kind.ID) && peek(1, Kind.BAR)) {
                    variable = take().text();
                    take();
                    arguments.add(implies());
                } else {
                    arguments.add(implies());
                    while (accept(Kind.COMMA)) arguments.add(implies());
                }
            }
            expect(Kind.RP);
            result = new Call(result, operation, variable, arguments);
        }
    }

    private boolean peek(Kind kind) { return tokens.get(at).kind() == kind; }
    private boolean peek(int distance, Kind kind) {
        return at + distance < tokens.size() && tokens.get(at + distance).kind() == kind;
    }
    private boolean accept(Kind kind) { if (!peek(kind)) return false; at++; return true; }
    private Token take() { return tokens.get(at++); }
    private Token expect(Kind kind) {
        Token token = take();
        if (token.kind() != kind) throw error("expected " + kind + " but found '" + token.text() + "'");
        return token;
    }
    private IllegalArgumentException error(String message) {
        int column = tokens.get(Math.min(at, tokens.size() - 1)).column();
        return new IllegalArgumentException("symbolic ACL/OCL at column " + column + ": " + message);
    }

    private static List<Token> lex(String source) {
        List<Token> result = new ArrayList<>();
        int at = 0;
        while (at < source.length()) {
            while (at < source.length() && Character.isWhitespace(source.charAt(at))) at++;
            if (at >= source.length()) break;
            int column = at;
            char c = source.charAt(at);
            if (c == '\'') {
                StringBuilder value = new StringBuilder();
                at++;
                while (at < source.length() && source.charAt(at) != '\'') {
                    if (source.charAt(at) == '\\' && at + 1 < source.length()) at++;
                    value.append(source.charAt(at++));
                }
                if (at >= source.length()) throw new IllegalArgumentException("unterminated OCL string");
                at++;
                result.add(new Token(Kind.STRING, value.toString(), column));
                continue;
            }
            if (Character.isDigit(c) || c == '-' && at + 1 < source.length()
                    && Character.isDigit(source.charAt(at + 1))) {
                int start = at++;
                while (at < source.length()
                        && (Character.isDigit(source.charAt(at)) || source.charAt(at) == '.')) at++;
                result.add(new Token(Kind.NUMBER, source.substring(start, at), column));
                continue;
            }
            if (Character.isLetter(c) || c == '_') {
                int start = at++;
                while (at < source.length() && (Character.isLetterOrDigit(source.charAt(at))
                        || source.charAt(at) == '_')) at++;
                String word = source.substring(start, at);
                Kind kind = switch (word.toLowerCase()) {
                    case "true" -> Kind.TRUE;
                    case "false" -> Kind.FALSE;
                    case "and" -> Kind.AND;
                    case "or" -> Kind.OR;
                    case "not" -> Kind.NOT;
                    case "implies" -> Kind.IMPLIES;
                    default -> Kind.ID;
                };
                result.add(new Token(kind, word, column));
                continue;
            }
            at++;
            switch (c) {
                case '.' -> result.add(new Token(Kind.DOT, ".", column));
                case '(' -> result.add(new Token(Kind.LP, "(", column));
                case ')' -> result.add(new Token(Kind.RP, ")", column));
                case '|' -> result.add(new Token(Kind.BAR, "|", column));
                case ',' -> result.add(new Token(Kind.COMMA, ",", column));
                case '#' -> result.add(new Token(Kind.HASH, "#", column));
                case '=' -> result.add(new Token(Kind.EQ, "=", column));
                case '@' -> {
                    if (source.startsWith("pre", at)) {
                        at += 3;
                        result.add(new Token(Kind.AT_PRE, "@pre", column));
                    } else throw new IllegalArgumentException("symbolic ACL/OCL at column " + column
                            + ": expected @pre");
                }
                case ':' -> {
                    if (at < source.length() && source.charAt(at) == ':') {
                        at++;
                        result.add(new Token(Kind.COLON2, "::", column));
                    } else throw new IllegalArgumentException("symbolic ACL/OCL at column " + column
                            + ": unexpected ':'");
                }
                case '-' -> {
                    if (at < source.length() && source.charAt(at) == '>') {
                        at++;
                        result.add(new Token(Kind.ARROW, "->", column));
                    } else throw new IllegalArgumentException("symbolic ACL/OCL at column " + column
                            + ": unexpected '-'");
                }
                case '<' -> {
                    if (at < source.length() && source.charAt(at) == '>') {
                        at++;
                        result.add(new Token(Kind.NE, "<>", column));
                    } else if (at < source.length() && source.charAt(at) == '=') {
                        at++;
                        result.add(new Token(Kind.LE, "<=", column));
                    } else result.add(new Token(Kind.LT, "<", column));
                }
                case '>' -> {
                    if (at < source.length() && source.charAt(at) == '=') {
                        at++;
                        result.add(new Token(Kind.GE, ">=", column));
                    } else result.add(new Token(Kind.GT, ">", column));
                }
                default -> throw new IllegalArgumentException("symbolic ACL/OCL at column " + column
                        + ": unexpected character '" + c + "'");
            }
        }
        result.add(new Token(Kind.EOF, "", source.length()));
        return List.copyOf(result);
    }
}
