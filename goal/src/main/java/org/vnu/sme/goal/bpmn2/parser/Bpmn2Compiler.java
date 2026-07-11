package org.vnu.sme.goal.bpmn2.parser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import org.antlr.v4.runtime.*;
import org.vnu.sme.goal.bpmn2.mm.Bpmn2Model;

public final class Bpmn2Compiler {

    public record Result(Bpmn2Model model, List<String> errors) {
        public boolean ok() { return errors.isEmpty(); }
    }

    public static Result compile(Path file) throws IOException {
        return compileStream(CharStreams.fromPath(file));
    }

    public static Result compile(String source) {
        return compileStream(CharStreams.fromString(source));
    }

    private static Result compileStream(CharStream chars) {
        List<String> errors = new ArrayList<>();

        Bpmn2Lexer  lexer  = new Bpmn2Lexer(chars);
        Bpmn2Parser parser = new Bpmn2Parser(new CommonTokenStream(lexer));

        lexer.removeErrorListeners();
        parser.removeErrorListeners();

        ANTLRErrorListener errListener = new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> rec, Object sym, int line, int col,
                                    String msg, RecognitionException e) {
                errors.add("line " + line + ":" + col + " " + msg);
            }
        };
        lexer.addErrorListener(errListener);
        parser.addErrorListener(errListener);

        Bpmn2Parser.ModelContext tree = parser.model();
        if (!errors.isEmpty()) return new Result(null, errors);

        // AST pipeline: parse tree → CS (AST) → MM
        org.vnu.sme.goal.bpmn2.ast.Bpmn2ModelCS ast = Bpmn2BuildingVisitor.build(tree);
        Bpmn2Model model = Bpmn2ModelFactory.build(ast);
        return new Result(model, Collections.emptyList());
    }
}
