package org.vnu.sme.goal.acl.parser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.vnu.sme.goal.acl.ast.AclModelCS;
import org.vnu.sme.goal.acl.mm.AclModel;

public final class AclCompiler {

    public record Result(AclModelCS ast, AclModel model, List<String> errors) {
        public boolean ok() { return errors.isEmpty(); }
    }

    private AclCompiler() {}

    public static Result compile(Path file) throws IOException {
        List<String> errors = new ArrayList<>();
        ACLLexer lexer = new ACLLexer(CharStreams.fromPath(file));
        ACLParser parser = new ACLParser(new CommonTokenStream(lexer));

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

        ACLParser.ModelContext tree = parser.model();
        if (!errors.isEmpty()) return new Result(null, null, List.copyOf(errors));

        AclModelCS ast = new AclBuildingVisitor().visitModel(tree);
        AclModelFactory.Result factory = AclModelFactory.create(ast);
        return new Result(ast, factory.model(), factory.errors());
    }
}
