package org.vnu.sme.goal.maxgoal.parser;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.vnu.sme.goal.maxgoal.mm.MAXGoalModel;

public final class MAXGoalCompiler {

    public record Result(MAXGoalModel model, List<String> errors) {
        public boolean ok() { return errors.isEmpty(); }
    }

    public static Result compile(Path file) throws IOException {
        CharStream  chars  = CharStreams.fromPath(file);
        return compileStream(chars);
    }

    public static Result compile(String source) {
        return compileStream(CharStreams.fromString(source));
    }

    private static Result compileStream(CharStream chars) {
        List<String> errors = new ArrayList<>();

        MAXGoalLexer  lexer  = new MAXGoalLexer(chars);
        MAXGoalParser parser = new MAXGoalParser(new CommonTokenStream(lexer));

        lexer.removeErrorListeners();
        parser.removeErrorListeners();

        ANTLRErrorListener errListener = new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?,?> rec, Object sym, int line, int col,
                                    String msg, RecognitionException e) {
                errors.add("line " + line + ":" + col + " " + msg);
            }
        };
        lexer.addErrorListener(errListener);
        parser.addErrorListener(errListener);

        MAXGoalParser.ModelContext tree = parser.model();
        if (!errors.isEmpty()) return new Result(null, errors);

        MAXGoalModel model = MAXGoalBuildingVisitor.build(tree);
        return new Result(model, Collections.emptyList());
    }
}
