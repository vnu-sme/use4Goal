package org.vnu.sme.goal.dsl.dcr.parser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.vnu.sme.goal.dsl.dcr.ast.DcrModelCS;
import org.vnu.sme.goal.dsl.dcr.mm.DcrModel;

public final class DCRCompiler {
    private DCRCompiler() {}

    public record Result(DcrModel model, List<String> errors) {
        public boolean ok() {
            return errors.isEmpty();
        }
    }

    public static Result compile(Path file) throws IOException {
        return compileStream(CharStreams.fromPath(file));
    }

    public static Result compile(String source) {
        return compileStream(CharStreams.fromString(source));
    }

    private static Result compileStream(CharStream chars) {
        List<String> errors = new ArrayList<>();
        DCRLexer lexer = new DCRLexer(chars);
        DCRParser parser = new DCRParser(new CommonTokenStream(lexer));

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

        DCRParser.ModelContext tree = parser.model();
        if (!errors.isEmpty()) {
            return new Result(null, errors);
        }

        DcrModelCS ast = DcrBuildingVisitor.build(tree);
        DcrModel model = DcrModelFactory.build(ast);
        List<String> semanticErrors = model.validate();
        if (!semanticErrors.isEmpty()) {
            return new Result(null, semanticErrors);
        }
        return new Result(model, Collections.emptyList());
    }
}
