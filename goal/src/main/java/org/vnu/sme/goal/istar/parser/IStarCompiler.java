package org.vnu.sme.goal.istar.parser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import org.antlr.v4.runtime.*;
import org.vnu.sme.goal.istar.mm.IStarModel;

public final class IStarCompiler {

    public record Result(IStarModel model, List<String> errors) {
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

        IStarLexer  lexer  = new IStarLexer(chars);
        IStarParser parser = new IStarParser(new CommonTokenStream(lexer));

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

        IStarParser.ModelContext tree = parser.model();
        if (!errors.isEmpty()) return new Result(null, errors);

        // AST pipeline: parse tree → CS (AST) → MM
        org.vnu.sme.goal.istar.ast.IStarModelCS ast = IStarBuildingVisitor.build(tree);
        IStarModel model = IStarModelFactory.build(ast);

        // Semantic validation (MM-layer rules applied after type resolution)
        List<String> semErrors = new ArrayList<>();
        for (org.vnu.sme.goal.istar.mm.ActorDef actor : model.getActors()) {
            for (org.vnu.sme.goal.istar.mm.Refinement ref : actor.refinements()) {
                if (ref instanceof org.vnu.sme.goal.istar.mm.Refinement.And and
                        && and.children().size() < 2) {
                    semErrors.add("semantic: and-refine '" + and.parent()
                            + "' in actor '" + actor.name()
                            + "' must have at least 2 children (found "
                            + and.children().size() + ")");
                }
            }
        }
        if (!semErrors.isEmpty()) return new Result(null, semErrors);

        return new Result(model, Collections.emptyList());
    }
}
