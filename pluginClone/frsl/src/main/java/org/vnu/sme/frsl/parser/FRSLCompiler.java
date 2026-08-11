package org.vnu.sme.frsl.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collection;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.tzi.use.api.UseApiException;
import org.tzi.use.api.UseModelApi;
import org.tzi.use.parser.ParseErrorHandler;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MInvalidModelException;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.sys.MSystemException;
import org.vnu.sme.frsl.ast.FrslModelCS;
import org.vnu.sme.frsl.mm.FRSLmodel.FrslModel;

public class FRSLCompiler {
	private FRSLCompiler() {
	};

	public static FrslModel compileSpecification(String inName, PrintWriter err, MModel model)
			throws MSystemException, FileNotFoundException {
		InputStream inStream = new FileInputStream(inName);
		ParseErrorHandler errHandler = new ParseErrorHandler(inName, err);
		ANTLRInputStream input;
		try {
			input = new ANTLRInputStream(inStream);
			input.name = inName;
		} catch (IOException exception) {
			err.println(exception.getMessage());
			return null;
		}

		FRSLLexer lexer = new FRSLLexer(input);
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);
		FRSLParser parser = new FRSLParser(tokenStream);
		lexer.init(errHandler);
		parser.init(errHandler);
		try {
			FrslModelCS frslModelCS = parser.frslModelCS();
			if (errHandler.errorCount() == 0) {
				Context ctx = new Context(inName, err, null, new FrslModelFactory());
				ctx.setModel(model);

				

				// ctx.setFrslModel(model);
				return frslModelCS.visitPreOrder(ctx);
			}
		} catch (RecognitionException e) {
			err.println(
					String.format("%s:%d:%d:%s", parser.getSourceName(), e.line, e.charPositionInLine, e.getMessage()));
		} catch (MInvalidModelException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			err.println("Error during FRSL semantic analysis: " + e.getMessage());
			e.printStackTrace(err);
		}
		err.flush();
		return null;
	}


}
