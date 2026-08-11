package org.vnu.sme.goal.dsl.acl.parser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.vnu.sme.goal.dsl.acl.ast.AclModelCS;
import org.vnu.sme.goal.dsl.acl.mm.AclModel;

public final class AclCompiler {
    public record Result(AclModelCS ast, AclModel model, List<String> errors) {
        public Result {
            errors = List.copyOf(errors);
        }

        public boolean ok() {
            return errors.isEmpty() && ast != null && model != null;
        }
    }

    private AclCompiler() {}

    public static Result compile(Path file) throws IOException {
        Objects.requireNonNull(file, "file");
        return compileStream(CharStreams.fromPath(file), file.toString());
    }

    public static Result compile(String source) {
        return compile(source, "<memory>");
    }

    public static Result compile(String source, String sourceName) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(sourceName, "sourceName");
        return compileStream(CharStreams.fromString(source), sourceName);
    }

    private static Result compileStream(CharStream input, String sourceName) {
        List<String> errors = new ArrayList<>();
        ACLLexer lexer = new ACLLexer(input);
        ACLParser parser = new ACLParser(new CommonTokenStream(lexer));

        lexer.removeErrorListeners();
        parser.removeErrorListeners();
        ANTLRErrorListener listener = new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int column,
                                    String message, RecognitionException exception) {
                errors.add(format(sourceName, line, column, "syntax", message));
            }
        };
        lexer.addErrorListener(listener);
        parser.addErrorListener(listener);

        ACLParser.ModelContext parseTree = parser.model();
        if (!errors.isEmpty()) return new Result(null, null, errors);

        AclModelCS ast = new AclBuildingVisitor().visitModel(parseTree);
        AclModelFactory.Result factory = AclModelFactory.create(ast);
        for (AclModelFactory.SemanticError error : factory.errors()) {
            errors.add(format(sourceName, error.location().line(), error.location().column(),
                    "semantic", error.message()));
        }
        return new Result(ast, factory.model(), errors);
    }

    private static String format(String sourceName, int line, int column, String phase, String message) {
        return sourceName + ":" + line + ":" + column + ": " + phase + ": " + message;
    }
}
