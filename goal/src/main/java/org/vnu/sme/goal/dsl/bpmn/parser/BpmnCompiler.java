package org.vnu.sme.goal.dsl.bpmn.parser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import org.antlr.v4.runtime.*;
import org.vnu.sme.goal.dsl.bpmn.mm.BpmnModel;
// BpmnLexer/BpmnParser are ANTLR-generated from grammars/Bpmn.g4, whose '@header'
// targets this same package -- no cross-package import needed.

public final class BpmnCompiler {

    public record Result(BpmnModel model, List<String> errors) {
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

        BpmnLexer  lexer  = new BpmnLexer(chars);
        BpmnParser parser = new BpmnParser(new CommonTokenStream(lexer));

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

        BpmnParser.ModelContext tree = parser.model();
        if (!errors.isEmpty()) return new Result(null, errors);

        // AST pipeline: parse tree → CS (AST) → MM → semantic validation
        org.vnu.sme.goal.dsl.bpmn.ast.BpmnModelCS ast = BpmnBuildingVisitor.build(tree);
        BpmnModel model = BpmnModelFactory.build(ast);
        List<String> semErrors = BpmnSemanticValidator.validate(model);
        if (!semErrors.isEmpty()) return new Result(null, semErrors);
        return new Result(model, Collections.emptyList());
    }
}
