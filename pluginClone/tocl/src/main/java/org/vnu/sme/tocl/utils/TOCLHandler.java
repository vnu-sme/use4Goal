package org.vnu.sme.tocl.utils;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MOperation;
import org.vnu.sme.tocl.parser.TOCLBaseListener;
import org.vnu.sme.tocl.parser.TOCLLexer;
import org.vnu.sme.tocl.parser.TOCLParser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Stack;

public class TOCLHandler {
    public static HashMap<String,String> variableMap = new HashMap<String,String>();
    
    public static String getType(String toclInput, Collection<MClass> classes, String context) {
        CharStream input = null;
        input = CharStreams.fromString(toclInput);
        TOCLLexer lexer = new TOCLLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TOCLParser parser = new TOCLParser(tokens);
        parser.setBuildParseTree(true);
        ParseTree tree = parser.expressionInOcl();
        ParseTreeWalker walker = new ParseTreeWalker();
        TOCLChecker checker = new TOCLChecker(parser, classes, context);
        walker.walk(checker, tree);
        
        return checker.getLastType();
    }
    
    public static Boolean isClassifierType(String toclInput, Collection<MClass> classes, String context) {
        CharStream input = null;
        input = CharStreams.fromString(toclInput);
        TOCLLexer lexer = new TOCLLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TOCLParser parser = new TOCLParser(tokens);
        parser.setBuildParseTree(true);
        ParseTree tree = parser.expressionInOcl();
        ParseTreeWalker walker = new ParseTreeWalker();
        TOCLChecker checker = new TOCLChecker(parser, classes, context);
        walker.walk(checker, tree);
        
        return checker.isLastTypeClassifierType();
    }
    
    public static MOperation getOperation(String classifier, String operation, Collection<MClass> classes) {
        for (MClass c : classes) {
            if (c.name().equals(classifier)) {
                for (MOperation o : c.allOperations()) {
//                    System.out.println("operation: "+o.name());
                    if (o.name().equals(operation)) {
                        return o;
                    }
                }
            }
        }
        return null;
    }
    
    public static class TOCLChecker extends TOCLBaseListener {
        TOCLParser parser;
        Collection<MClass> classes;
        
        enum ClassifierType {
            Object,
            Collection,
            Boolean,
            Integer
        }
        
        private boolean invariantCreated = false;
        private boolean operationDecEntered = false;
        private boolean propertyDecEntered = false;
        private boolean defCreated = false;
        private String currentContext = null;
        private Stack<String> declaredVar = new Stack<String>(); //ensure we need this
        private final String[] queryOps = {"select","reject","collect","exists","forAll","isUnique","sortedBy","includes","excludes","includesAll","excludesAll"};
        private HashMap<String,ClassifierType> queryOpsInput = new HashMap<String,ClassifierType>();
        private HashMap<String,ClassifierType> queryOpsReturn = new HashMap<String,ClassifierType>();
        private String lastType = null;
        
        public TOCLChecker(TOCLParser parser, Collection<MClass> classes, String context) {
            this.parser = parser;
            this.classes = classes;
            currentContext = context;
            
            queryOpsReturn.put("select",ClassifierType.Collection);
            queryOpsReturn.put("reject",ClassifierType.Collection);
            queryOpsReturn.put("collect",ClassifierType.Collection);
            queryOpsReturn.put("exists",ClassifierType.Boolean);
            queryOpsInput.put("forAll",ClassifierType.Boolean);
            queryOpsInput.put("isUnique",ClassifierType.Boolean);
            queryOpsInput.put("sortedBy",ClassifierType.Collection);
            queryOpsInput.put("includes",ClassifierType.Boolean);
            queryOpsInput.put("excludes",ClassifierType.Boolean);
        }
        
        @Override
        public void exitInvOrDef(TOCLParser.InvOrDefContext ctx) {
            if (ctx.getText().startsWith("inv")) {
                invariantCreated = true;
            }
            else {
                defCreated = true;
            }
        }
        
        @Override
        public void exitPropertyContextDecl(TOCLParser.PropertyContextDeclContext ctx) {
            propertyDecEntered = true;
        }
        
        @Override
        public void exitOperationContextDecl(TOCLParser.OperationContextDeclContext ctx) {
            operationDecEntered = true;
        }
        
        @Override
        public void enterClassifierContextDecl(TOCLParser.ClassifierContextDeclContext ctx) {
            int numChildren = ctx.getChildCount();
            currentContext = ctx.getChild(numChildren-2).getText();
            System.out.println(currentContext);
        }
        
        @Override
        public void enterVariableDeclaration(TOCLParser.VariableDeclarationContext ctx) {
            String lastDeclaredVar = ctx.simpleName().SIMPLE_NAME().getText();
            declaredVar.push(lastDeclaredVar);
            System.out.println("variable declared: " + lastDeclaredVar);
            String varType = null;
            
            if (ctx.type() != null) {
                varType = ctx.type().getText();
            }
            System.out.println("is type of: " + varType);
            variableMap.put(lastDeclaredVar,varType);
        }
        
        @Override
        public void exitLetExp(TOCLParser.LetExpContext ctx) {
            variableMap.remove(ctx.getChild(1).getChild(0).getText());
            System.out.println("Removing variable: " + ctx.getChild(1).getChild(0).getText());
        }
        
        @Override
        public void enterPostfixExp(TOCLParser.PostfixExpContext ctx) {
            int numChildren = ctx.getChildCount();
            if (numChildren > 0) {
                String currentClass = ctx.getChild(0).getText();
                boolean isCollection = false;
                if (currentClass.equals("self")) {
                    currentClass = currentContext;
                } else if (variableMap.containsKey(currentClass)) {
                    currentClass = variableMap.get(currentClass);
                }
                for (int i = 1; i < numChildren; i++) {
                    if (i % 2 == 1) {
                        if (ctx.getChild(i).getText().equals(".")) {
                            if (isCollection) {
                                //error
                                TokenStream tokens = parser.getTokenStream();
                                Interval srcInterval = ctx.getSourceInterval();
                            }
                            else {
                                continue;
                            }
                        }
                        else {
                            if (isCollection) {
                                continue;
                            }
                            else {
                                //error
                                TokenStream tokens = parser.getTokenStream();
                                Interval srcInterval = ctx.getSourceInterval();
                            }
                        }
                    }
                    else {
                        String currentAttr = ctx.getChild(i).getText();
                        boolean containsAttribute = false;
                        System.out.println(currentAttr);
                        for (MClass c : classes) {
                            System.out.println("class "+c.name());
                            if (c.name().equals(currentClass)) {
                                for (MAttribute p : c.allAttributes()) {
                                    System.out.println("attribute: "+p.name());
                                    if (p.name().equals(currentAttr)) {
                                        containsAttribute = true;
                                        currentClass = p.type().shortName();
                                        if (p.type().isInstantiableCollection()) {
                                            isCollection = true;
                                        } else {
                                            isCollection = false;
                                        }
                                        System.out.println("current class is "+ currentClass);
                                        break;
                                    }
                                    if (containsAttribute) {
                                        break;
                                    }
                                }
                                if (currentAttr.contains("(")) {
                                    containsAttribute = true;
                                }
                            }
                        }
                        if (!containsAttribute) {
                            TokenStream tokens = parser.getTokenStream();
                            Interval srcInterval = ctx.getSourceInterval();
                        }
                    }
                }
                lastType = currentClass;
            }
        }
        
        public Boolean isLastTypeClassifierType() {
            String lastTypeRecorded = getLastType();
            for (MClass c : classes) {
                if (c.name().equals(lastTypeRecorded)) {
                    return true;
                }
            }
            return false;
        }
        
        public String getLastType() {
            System.out.println("Last type is "+lastType);
            return lastType;
            //return (lastType != null ? lastType : currentContext);
        }
        
        public boolean isValidInvariant() {
            return (invariantCreated && !(defCreated || operationDecEntered || propertyDecEntered));
        }
        
        @Override
        public void exitOclQueryExp(TOCLParser.OclQueryExpContext ctx) {
        
        }
        
        @Override
        public void enterOclQueryExp(TOCLParser.OclQueryExpContext ctx) {
        
        }
    }
}
