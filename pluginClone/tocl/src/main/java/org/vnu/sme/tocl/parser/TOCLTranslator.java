package org.vnu.sme.tocl.parser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MOperation;
import org.vnu.sme.tocl.utils.TOCLHandler;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TOCLTranslator {
    
    public static String translate(File toclFile, Collection<MClass> classes) {
        CharStream input = null;
        try {
            FileReader reader = new FileReader(toclFile);
            input = CharStreams.fromReader(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        TOCLLexer lexer = new TOCLLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TOCLParser parser = new TOCLParser(tokens);
        parser.setBuildParseTree(true);
        ParseTree tree = parser.expressionInOcl();
        
        ParseTreeWalker walker = new ParseTreeWalker();
        OCLEmitter converter = new OCLEmitter(parser, classes);
        walker.walk(converter, tree);
        return converter.getOCL(tree);
    }
    
    public static class OCLEmitter extends TOCLBaseListener {
        TOCLParser parser;
        Collection<MClass> classes;
        
        public OCLEmitter(TOCLParser parser, Collection<MClass> classes) {
            this.parser = parser;
            this.classes = classes;
        }
        
        ParseTreeProperty<String> ocl = new ParseTreeProperty<>();
        String getOCL(ParseTree ctx) { return ocl.get(ctx); }
        void setOCL(ParseTree ctx, String s) { ocl.put(ctx, s); }
        
        Stack<String> stack = new Stack<>();
        private boolean nestedOCL = false;
        private String TOCLReplacement = null;
        private final String[] toclOperators = {"sometime", "always", "next", "sometimePast", "alwaysPast", "previous", "@next", "@pre"}; //adjust this
        private String currentContext = null;
        private String currentScope = null;
        private String lastPostfixExp = null;
        private String toclOpCallPostfix = null;
        
        Stack<String> eventStack = new Stack<>();
        
        /**
         *
         * @param ctx the parse tree
         */
        @Override
        public void exitExpressionInOcl(TOCLParser.ExpressionInOclContext ctx) {
            // 1. Get the original expression
            TokenStream tokens = parser.getTokenStream();
            String expressionInOcl = tokens.getText(ctx);
            // 2. Replace each part of the expression with the corresponding converted OCL (from stack)
            // 3. Set the converted expression corresponds to the ctx
            CharSequence tocl;
            CharSequence ocl;
            
            while (!stack.isEmpty()) {
                tocl = stack.pop();
                ocl = stack.pop();
                expressionInOcl = expressionInOcl.replace(tocl, ocl);
            }
            
            while (!eventStack.isEmpty()) {
                tocl = eventStack.pop();
                ocl = eventStack.pop();
                expressionInOcl = expressionInOcl.replace(tocl, ocl);
            }
            
            setOCL(ctx, expressionInOcl);
        }
        
        /**
         * Negate the inv body (Wrap it with "not ()"). But why? idk
         * @param ctx the parse tree
         */
        @Override
        public void exitInvOrDef(TOCLParser.InvOrDefContext ctx) {
            // Negate the inv (Why?)
            if (ctx.getChild(0).getText().equals("inv")) {
                TokenStream tokens = parser.getTokenStream();
                String translatedOCL = "";
                String originalTOCL = tokens.getText(ctx);
                
                String invBody = "";
                if (ctx.getChild(1).getText().equals(":")) {
                    // if the inv does not have name
                    invBody = ctx.getChild(2).getText();
                } else {
                    // if the inv has name
                    invBody = ctx.getChild(3).getText();
                }
                // Wrap the invBody with "not ()"
                String negatedInvBody = "not (" + invBody + ")";
                translatedOCL = originalTOCL.replace(invBody, negatedInvBody);
                stack.push(translatedOCL);
                stack.push(originalTOCL);
            }
        }
        
        /**
         * Set the expression to its ctx.
         * @param ctx the parse tree
         */
        @Override
        public void exitOclExpression(TOCLParser.OclExpressionContext ctx) {
            TokenStream tokens = parser.getTokenStream();
            setOCL(ctx, tokens.getText(ctx));
        }
        
        public void enterBinaryOperationExp(TOCLParser.BinaryOperationExpContext ctx) {
            if (ctx.getChildCount() == 3) {
                currentScope = "fromCurrentSnapshot";
//                System.out.println("Current scope: " + currentScope);
            }
        }
        /**
         * Set the binary operation expression to its ctx.
         * @param ctx the parse tree
         */
        @Override
        public void exitBinaryOperationExp(TOCLParser.BinaryOperationExpContext ctx) {
            TokenStream tokens = parser.getTokenStream();
            setOCL(ctx, tokens.getText(ctx));
        }
        
        @Override
        public void enterToclOpCallExp(TOCLParser.ToclOpCallExpContext ctx) {
            toclOpCallPostfix = lastPostfixExp.replace(ctx.getText(),"");
        }
        
        @Override
        public void exitToclOpCallExp(TOCLParser.ToclOpCallExpContext ctx) {
            // This is responsible for converting the @next and @pre operations
            // but it is not working properly
            // and i am planning to leave it for now
            // TODO: fix this.
            TokenStream tokens = parser.getTokenStream();
            String originalTOCL = toclOpCallPostfix +tokens.getText(ctx);
            String translatedOCL = "";
            
            toclOpCallPostfix = toclOpCallPostfix.substring(0, toclOpCallPostfix.length() - 1);
            String expType = TOCLHandler.getType(toclOpCallPostfix, classes, currentContext);
            String operationName = ctx.getChild(0).getText();
            String transitionClass = operationName + "_" + currentContext + "OpC";
            MOperation operation = TOCLHandler.getOperation(operationName, expType, classes);
            int numChildren = ctx.getChildCount();
            
            if (ctx.getChild(1).getText().equals("@next")) {
                translatedOCL = "(let NT = " + toclOpCallPostfix + ".snapshot" + currentContext
                                + ".operationCall in NT.oclIsTypeOf(" + transitionClass
                                + ") and (let NT = oclAsType(" + transitionClass + ") in NT.aSelf = " + toclOpCallPostfix;
                ParseTree argument = ctx.getChild(numChildren -  2);
                if (!(argument.getText().equals("("))) {
                    for (String paramName : operation.paramNames()) {
                        translatedOCL = translatedOCL + " and NT." + paramName + " = " + argument.getChild(0).getText();
                        if (argument.getChildCount() > 1) {
                            argument = argument.getChild(2);
                        } else {
                            break;
                        }
                    }
                }
                translatedOCL = translatedOCL + ")";
            } else {
                // @pre
                translatedOCL = "(let PT = " + toclOpCallPostfix;
            }
            
        }
        
        @Override
        public void enterPostfixExp(TOCLParser.PostfixExpContext ctx) {
            lastPostfixExp = ctx.getText();
        }
        
        @Override
        public void enterClassifierContextDecl(TOCLParser.ClassifierContextDeclContext ctx) {
            int numChildren = ctx.getChildCount();
            currentContext = ctx.getChild(numChildren - 2).getText();
//            currentSnapshotStack.push("self.snapshot" + currentContext);
        }
        
        // Events ========================
        @Override
        public void exitIsCalledEvent(TOCLParser.IsCalledEventContext ctx) {
            TokenStream tokens = parser.getTokenStream();
            String originalTOCL = tokens.getText(ctx);
            String translatedOCL = "";
            
            boolean isBounded = ctx.bounds() != null;
            int times = -1;
            String type = null;
            if (isBounded) {
                times = Integer.parseInt(ctx.bounds().n.getText());
                type = ctx.bounds().quantif == null ? null : ctx.bounds().quantif.getText();
            }
            
            // Handle get parameters
            Map<String, String> params = new HashMap<>();
            if (ctx.eventOp().parameters() != null && ctx.eventOp().parameters().getChildCount() > 0) {
                TOCLParser.ParametersContext paramsCtx = ctx.eventOp().parameters();
                while (paramsCtx != null && paramsCtx.getChildCount() > 0) {
                    String param = paramsCtx.variableDeclaration().simpleName().getText();
                    String paramType = paramsCtx.variableDeclaration().type().getText();
                    //System.out.println("param: " + param);
                    params.put(param, paramType);
                    if (paramsCtx.getChildCount() > 1) {
                        paramsCtx = (TOCLParser.ParametersContext) paramsCtx.getChild(2);
                    } else {
                        break;
                    }
                }
            }
            
            // Get operation name, construct the operation class name.
            // A receiver-aware event, e.g. isCalled(g.collectFromCalendar()),
            // uses the receiver type as the operation owner. The generated
            // filmstrip OpC is then constrained to the same receiver identity.
            String operation = ctx.eventOp().name.getText();
            String receiver = ctx.eventOp().receiver == null ? null : ctx.eventOp().receiver.getText();
            String operationOwner = currentContext;
            String receiverConstraint = "";
            if (receiver != null) {
                operationOwner = TOCLHandler.getType(receiver, classes, currentContext);
                receiverConstraint = " and op.aSelf.id = " + receiver + ".id";
            }
            String operationClass = operation + "_" + operationOwner + "OpC";
            String snapshot = "self.snapshot" + currentContext;

            translatedOCL = operationClass + ".allInstances()->exists(op | op.succ() = " + snapshot + receiverConstraint + " and op.oclIsTypeOf(" + operationClass + "))";

            if (params.isEmpty()) {
                if (isBounded) {
                    if ("at most".equals(type)) {
                        translatedOCL = operationClass + ".allInstances()->select(op | op.succ() = " + snapshot + receiverConstraint + ")->size() <= " + times;
                    } else if ("at least".equals(type)) {
                        translatedOCL = operationClass + ".allInstances()->select(op | op.succ() = " + snapshot + receiverConstraint + ")->size() >= " + times;
                    } else {
                        translatedOCL = operationClass + ".allInstances()->select(op | op.succ() = " + snapshot + receiverConstraint + ")->size() = " + times;
                    }
                } else {
                    translatedOCL = operationClass + ".allInstances()->exists(op | op.succ() = " + snapshot + receiverConstraint + ")";
                }
            } else {
                if (isBounded) {
                    StringBuilder builder = new StringBuilder("(" + operationClass + ".allInstances()->select(op | op.succ() = " + snapshot + receiverConstraint);
                    for (String param : params.keySet()) {
                        // ... and (Set{op.param.succ[paramType]}->closure(p | p.succ[paramType])->includes(param) or Set{op.param.pred[paramType]}->closure(p | p.pred[paramType])->includes(param))
                        builder.append(" and (Set{op.").append(param).append(".succ").append(params.get(param)).append("}->closure(p | p.succ").append(params.get(param)).append(")->includes(").append(param).append(") or Set{op.").append(param).append(".pred").append(params.get(param)).append("}->closure(p | p.pred").append(params.get(param)).append(")->includes(").append(param).append("))");
                        // old implementation
                        //builder.append(" and op.").append(param).append(".id").append(" = ").append(param).append(".id");
                    }
                    if ("at most".equals(type)) {
                        translatedOCL = builder.append(")->size() <= ").append(times).append(")").toString();
                    } else if ("at least".equals(type)) {
                        translatedOCL = builder.append(")->size() >= ").append(times).append(")").toString();
                    } else {
                        translatedOCL = builder.append(")->size() = ").append(times).append(")").toString();
                    }
                } else {
                    StringBuilder builder = new StringBuilder("(" + operationClass + ".allInstances()->exists(op | op.succ() = " + snapshot + receiverConstraint);
                    for (String param : params.keySet()) {
                        // ... and (Set{op.param.succ[paramType]}->closure(p | p.succ[paramType])->includes(param) or Set{op.param.pred[paramType]}->closure(p | p.pred[paramType])->includes(param))
                        builder.append(" and (Set{op.").append(param).append(".succ").append(params.get(param)).append("}->closure(p | p.succ").append(params.get(param)).append(")->includes(").append(param).append(") or Set{op.").append(param).append(".pred").append(params.get(param)).append("}->closure(p | p.pred").append(params.get(param)).append(")->includes(").append(param).append("))");
                        // old implementation
                        //builder.append(" and op.").append(param).append(".id").append(" = ").append(param).append(".id");
                    }
                    translatedOCL = builder.append("))").toString();
                }
            }
            eventStack.push(translatedOCL);
            eventStack.push(originalTOCL);
        }
        
        @Override
        public void exitBecomesTrueEvent(TOCLParser.BecomesTrueEventContext ctx) {
            TokenStream tokens = parser.getTokenStream();
            String originalEvent = tokens.getText(ctx);
            String translatedEvent;
            
            String expressionToSatisfy = getOCL(ctx.getChild(2)); // P
            String roleName = toLowerFirstChar(currentContext); // e.g., "system", "application"
            String currentSnapshot = "self.snapshot" + currentContext; // e.g., "self.snapshotElevator"
            String selectObject = "->any(o | o.id = self.id)";
            
            String objectAtCurrentSnapshot = currentSnapshot + "." + roleName + selectObject;
            String objectAtPreviousSnapshot = currentSnapshot + ".pred()." + roleName + selectObject;
            String P_at_currentSnapshot = expressionToSatisfy.replace("self", "currentObject");
            String P_at_previousSnapshot = expressionToSatisfy.replace("self", "previousObject");
            
            translatedEvent = "(let currentObject = " + objectAtCurrentSnapshot +
                            " in let previousObject = " + objectAtPreviousSnapshot +
                            " in not (" + P_at_previousSnapshot + ") and (" + P_at_currentSnapshot + "))";
            
            eventStack.push(translatedEvent);
            eventStack.push(originalEvent);
        }
        
        @Override
        public void exitEvents(TOCLParser.EventsContext ctx) {
            setOCL(ctx, ctx.getChild(0).getText());
        }
        // ==================================
        
        // TOCL translation
        @Override
        public void exitNextExp(TOCLParser.NextExpContext ctx) {
            TokenStream tokens = parser.getTokenStream();
            String originalTOCL = tokens.getText(ctx);
            String translatedOCL = "";
            
            String expressionToSatisfy = getOCL(ctx.getChild(1));
            while (hasNestedTOCL(expressionToSatisfy)) {
                String originalExpression = stack.pop();
                String translatedExpression = stack.pop();
                expressionToSatisfy = expressionToSatisfy.replace(originalExpression, translatedExpression);
            }
            if (isEvent(expressionToSatisfy)) {
                String originalExpression = eventStack.pop();
                String translatedExpression = eventStack.pop();
                expressionToSatisfy = expressionToSatisfy.replace(originalExpression, translatedExpression);
                // replace "self.snapshot[currentContext]" = "self.snapshot[currentContext].succ()"
                expressionToSatisfy = expressionToSatisfy.replace("self.snapshot" + currentContext, "self.snapshot" + currentContext + ".succ()");
                translatedOCL = "(let CS:Snapshot = self.snapshot" + currentContext + " in " + expressionToSatisfy + ")" ;
            } else {
                // self.attribute -> self.snapshot[Class].succ().[class]->any(o | self.id = o.id)
                String selfReplace = "self.snapshot" + currentContext + ".succ()." + toLowerFirstChar(currentContext) + "->any(o | self.id = o.id)";
                translatedOCL = fillInSelf(expressionToSatisfy).replace("self", selfReplace);
                // replace "allInstances()" to "allInstances()->select(o | o.[currentSnapshot] = self.[currentSnapshot].next)" (all instances within the next snapshot)
//                String prefixAllInstances = getPrefixOfAllInstances(translatedOCL);
//                if (prefixAllInstances != null) {
//                    String allInstancesReplacement = "allInstances()->select(o:" + prefixAllInstances + " | o.snapshot" + prefixAllInstances + " = self.snapshot" + currentContext + ".succ())";
//                    translatedOCL = translatedOCL.replace("allInstances()", allInstancesReplacement);
//                }
            }
            
            System.out.println("Exit Next Expression: " + translatedOCL);
            stack.push(translatedOCL);
            stack.push(originalTOCL);
        }
        
        @Override
        public void exitAlwaysExp(TOCLParser.AlwaysExpContext ctx) {
            TokenStream tokens = parser.getTokenStream();
            String originalTOCL = tokens.getText(ctx);
            String translatedOCL = "";
            
            // always P
            // formal semantics: True if e holds in the current state and all subsequent states
            if (ctx.getChildCount() == 2) {
                String expressionToSatisfy = getOCL(ctx.getChild(1));
                while (hasNestedTOCL(expressionToSatisfy)) {
                    String originalExpression = stack.pop();
                    String translatedExpression = stack.pop();
                    expressionToSatisfy = expressionToSatisfy.replace(originalExpression, translatedExpression);
                }
                if (isEvent(expressionToSatisfy)) {
                    String originalExpression = eventStack.pop();
                    String translatedExpression = eventStack.pop();
                    expressionToSatisfy = expressionToSatisfy.replace(originalExpression, translatedExpression).replace("self.snapshot" + currentContext, "s");
                    
                    if ("fromCurrentSnapshot".equals(currentScope)) {
                        translatedOCL = "(let CS:Snapshot = self.snapshot" + currentContext +
                                        " in Set{CS}->closure(s | s.succ())->excluding(null)->forAll(s | " + expressionToSatisfy + "))";
                    } else {
                        //translatedOCL = "Snapshot.allInstances()->forAll(s | " + expressionToSatisfy + ")";
                        translatedOCL = "(let CS:Snapshot = self.snapshot" + currentContext +
                                " in Set{CS}->closure(s | s.succ())->excluding(null)->forAll(s | " + expressionToSatisfy + "))";
                    }
                } else {
                    // self.attribute -> s.[Class]->any(o | self.id = o.id)
                    String selfReplace = "s." + toLowerFirstChar(currentContext) + "->any(o | self.id = o.id)";
                    String alwaysExpression = fillInSelf(expressionToSatisfy).replace("self", selfReplace);
//                    String prefixAllInstances = getPrefixOfAllInstances(alwaysExpression);
//                    if (prefixAllInstances != null) {
//                        String allInstancesReplacement = "allInstances()->select(o:" + prefixAllInstances + " | o.snapshot" + prefixAllInstances + " = s)";
//                        alwaysExpression = alwaysExpression.replace("allInstances()", allInstancesReplacement);
//                    }
                    // get all snapshots from current snapshot -> check if the always expression is satisfied
                    if ("fromCurrentSnapshot".equals(currentScope)) {
                        translatedOCL = "(let CS:Snapshot = self.snapshot" + currentContext +
                                        " in Set{CS}->closure(s | s.succ())->excluding(null)->forAll(s | " + alwaysExpression + "))";
                    } else {
                        //translatedOCL = "Snapshot.allInstances()->forAll(s | " + alwaysExpression + ")";
                        translatedOCL = "(let CS:Snapshot = self.snapshot" + currentContext +
                                " in Set{CS}->closure(s | s.succ())->excluding(null)->forAll(s | " + alwaysExpression + "))";
                    }
                }
                System.out.println("Exit Always Expression: " + translatedOCL);
                stack.push(translatedOCL);
                stack.push(originalTOCL);
            }
            else {
                // always P until Q
                // formal semantics: True if e1 remains true until e2 becomes true, or
                // indefinitely if e2 never occurs.
                if (ctx.op.getText().equals("until")) {
                    String alwaysExpression = getOCL(ctx.getChild(1));
                    String untilExpression = getOCL(ctx.getChild(3));
                    // until expression should be replaced first
                    while (hasNestedTOCL(untilExpression)) {
                        String originalExpression = stack.pop();
                        String translatedExpression = stack.pop();
                        untilExpression = untilExpression.replace(originalExpression, translatedExpression);
                    }
                    while (hasNestedTOCL(alwaysExpression)) {
                        String originalExpression = stack.pop();
                        String translatedExpression = stack.pop();
                        alwaysExpression = alwaysExpression.replace(originalExpression, translatedExpression);
                    }
                    if (isEvent(alwaysExpression) && isEvent(untilExpression)) {
                        // always [event] until [event]
                        String originalUntilExpression = eventStack.pop();
                        String translatedUntilExpression = eventStack.pop();
                        untilExpression = untilExpression.replace(originalUntilExpression, translatedUntilExpression).replace("self.snapshot" + currentContext, "s");
                        String originalAlwaysExpression = eventStack.pop();
                        String translatedAlwaysExpression = eventStack.pop();
                        alwaysExpression = alwaysExpression.replace(originalAlwaysExpression, translatedAlwaysExpression).replace("self.snapshot" + currentContext, "s");
                        
                        if ("fromCurrentSnapshot".equals(currentScope)) {
                            translatedOCL = "(let CS:Snapshot = self.snapshot" + currentContext +
                                            " in let FS:Set(Snapshot) = Set{CS.succ()}->closure(s | s.succ())->excluding(null)" +
                                            " in let AllFSQ:Set(Snapshot) = FS->select(s | " + untilExpression + ")" +
                                            " in let FSQ:Snapshot = AllFSQ->any(s | Set{s}->closure(s | s.succ())->includesAll(AllFSQ))" +
                                            " in let afterQ:Set(Snapshot) = Set{FSQ}->closure(s | s.succ())" +
                                            " in let FSP:Set(Snapshot) = FS->select(s | " + alwaysExpression + ")" +
                                            " in if FSQ.isDefined() then (if (FSP->size() > 0) then (FS-afterQ = FSP-afterQ) else false endif) else (FS = FSP) endif)";
                        } else {
                            translatedOCL = "(let FSQ:Set(Snapshot) = Snapshot.allInstances()->select(s | " + untilExpression + ") " +
                                            "in if (FSQ->size() > 0) then (FSQ->forAll(s | Set{s.pred()}->closure(s | s.pred())->forAll(s | " + alwaysExpression + "))) else (Snapshot.allInstances()->forAll(s | " + alwaysExpression + ")) endif)";
                        }
                    } else if (isEvent(alwaysExpression)) {
                        // always [event] until [binary expression]
                        // self.attribute -> s.[Class]->any(true)
                        String selfReplace = "s." + toLowerFirstChar(currentContext) + "->any(true)";
                        untilExpression = fillInSelf(untilExpression).replace("self", selfReplace);
                        String originalAlwaysExpression = eventStack.pop();
                        String translatedAlwaysExpression = eventStack.pop();
                        alwaysExpression = alwaysExpression.replace(originalAlwaysExpression, translatedAlwaysExpression).replace("self.snapshot" + currentContext, "s");
                        
                        if ("fromCurrentSnapshot".equals(currentScope)) {
                            translatedOCL = "(let CS:Snapshot = self.snapshot" + currentContext +
                                            " in let FS:Set(Snapshot) = Set{CS.succ()}->closure(s | s.succ())->excluding(null)" +
                                            " in let AllFSQ:Set(Snapshot) = FS->select(s | " + untilExpression + ")" +
                                            " in let FSQ:Snapshot = AllFSQ->any(s | Set{s}->closure(s | s.succ())->includesAll(AllFSQ))" +
                                            " in let afterQ:Set(Snapshot) = Set{FSQ}->closure(s | s.succ())" +
                                            " in let FSP:Set(Snapshot) = FS->select(s | " + alwaysExpression + ")" +
                                            " in if FSQ.isDefined() then (if (FSP->size() > 0) then (FS-afterQ = FSP-afterQ) else false endif) else (FS = FSP) endif)";
                        } else {
                            translatedOCL = "(let FSQ:Set(Snapshot) = Snapshot.allInstances()->select(s | " + untilExpression + ") " +
                                            "in if (FSQ->size() > 0) then (FSQ->forAll(s | Set{s.pred()}->closure(s | s.pred())->forAll(s | " + alwaysExpression + "))) else (Snapshot.allInstances()->forAll(s | " + alwaysExpression + ")) endif)";
                        }
                    } else if (isEvent(untilExpression)) {
                        // always [binary expression] until [event]
                        // self.attribute -> s.[Class]->any(true)
                        String selfReplace = "s." + toLowerFirstChar(currentContext) + "->any(true)";
                        alwaysExpression = fillInSelf(alwaysExpression).replace("self", selfReplace);
                        String originalUntilExpression = eventStack.pop();
                        String translatedUntilExpression = eventStack.pop();
                        untilExpression = untilExpression.replace(originalUntilExpression, translatedUntilExpression).replace("self.snapshot" + currentContext, "s");
                        
                        if ("fromCurrentSnapshot".equals(currentScope)) {
                            translatedOCL = "(let CS:Snapshot = self.snapshot" + currentContext +
                                            " in let FS:Set(Snapshot) = Set{CS.succ()}->closure(s | s.succ())->excluding(null)" +
                                            " in let AllFSQ:Set(Snapshot) = FS->select(s | " + untilExpression + ")" +
                                            " in let FSQ:Snapshot = AllFSQ->any(s | Set{s}->closure(s | s.succ())->includesAll(AllFSQ))" +
                                            " in let afterQ:Set(Snapshot) = Set{FSQ}->closure(s | s.succ())" +
                                            " in let FSP:Set(Snapshot) = FS->select(s | " + alwaysExpression + ")" +
                                            " in if FSQ.isDefined() then (if (FSP->size() > 0) then (FS-afterQ = FSP-afterQ) else false endif) else (FS = FSP) endif)";
                        } else {
                            translatedOCL = "(let FSQ:Set(Snapshot) = Snapshot.allInstances()->select(s | " + untilExpression + ") " +
                                            "in if (FSQ->size() > 0) then (FSQ->forAll(s | Set{s.pred()}->closure(s | s.pred())->forAll(s | " + alwaysExpression + "))) else (Snapshot.allInstances()->forAll(s | " + alwaysExpression + ")) endif)";
                        }
                    } else {
                        // self.attribute -> s.[Class]->any(o | self.id = o.id)
                        String selfReplace = "s." + toLowerFirstChar(currentContext) + "->any(o | self.id = o.id)";
                        alwaysExpression = fillInSelf(alwaysExpression).replace("self", selfReplace);
                        untilExpression = fillInSelf(untilExpression).replace("self", selfReplace);
                        // replace "allInstances()" to "allInstances()->select(o | o.[currentSnapshot] = self.[currentSnapshot])" (all instances within the current snapshot)
//                        String prefixAllInstances = getPrefixOfAllInstances(alwaysExpression);
//                        if (prefixAllInstances != null) {
//                            String allInstancesReplacement = "allInstances()->select(o:" + prefixAllInstances + " | o.snapshot" + prefixAllInstances + " = s)";
//                            alwaysExpression = alwaysExpression.replace("allInstances()", allInstancesReplacement);
//                        }
//                        prefixAllInstances = getPrefixOfAllInstances(untilExpression);
//                        if (prefixAllInstances != null) {
//                            String allInstancesReplacement = "allInstances()->select(o:" + prefixAllInstances + " | o.snapshot" + prefixAllInstances + " = s)";
//                            untilExpression = untilExpression.replace("allInstances()", allInstancesReplacement);
//                        }
                        // get all future snapshots (FS) not including CS
                        // -> get all snapshots from FS that satisfies Q (FSQ)
                        // -> get all snapshots from FS that satisfies P (FSP) including CS
                        // -> check if there is a future snapshot (o)
                        // that satisfies Q and FS - {o->all future snapshots} = FSP - {o->all future snapshots}
                        if ("fromCurrentSnapshot".equals(currentScope)) {
                            translatedOCL = "(let CS:Snapshot = self.snapshot" + currentContext +
                                    " in let FS:Set(Snapshot) = Set{CS.succ()}->closure(s | s.succ())->excluding(null)" +
                                    " in let AllFSQ:Set(Snapshot) = FS->select(s | " + untilExpression + ")" +
                                    " in let FSQ:Snapshot = AllFSQ->any(s | Set{s}->closure(s | s.succ())->includesAll(AllFSQ))" +
                                    " in let afterQ:Set(Snapshot) = Set{FSQ}->closure(s | s.succ())" +
                                    " in let FSP:Set(Snapshot) = FS->select(s | " + alwaysExpression + ")" +
                                    " in if FSQ.isDefined() then (if (FSP->size() > 0) then (FS-afterQ = FSP-afterQ) else false endif) else (FS = FSP) endif)";
                        } else {
                            translatedOCL = "(let FSQ:Set(Snapshot) = Snapshot.allInstances()->select(s | " + untilExpression + ") " +
                                            "in if (FSQ->size() > 0) then (FSQ->forAll(s | Set{s.pred()}->closure(s | s.pred())->forAll(s | " + alwaysExpression + "))) else (Snapshot.allInstances()->forAll(s | " + alwaysExpression + ")) endif)";
                        }
                        
                    }
                    System.out.println("Exit Always Until Expression: \n" + translatedOCL);
                    stack.push(translatedOCL);
                    stack.push(originalTOCL);
                }
                else if (ctx.op.getText().equals("since")) {
                    // always P since Q
                    // formal semantics: True if e1 has been true
                    // since the last time e2 was true.
                    String alwaysExpression = getOCL(ctx.getChild(1));
                    String sinceExpression = getOCL(ctx.getChild(3));
                    // since expression should be replaced first
                    while (hasNestedTOCL(sinceExpression)) {
                        String originalExpression = stack.pop();
                        String translatedExpression = stack.pop();
                        sinceExpression = sinceExpression.replace(originalExpression, translatedExpression);
                    }
                    while (hasNestedTOCL(alwaysExpression)) {
                        String originalExpression = stack.pop();
                        String translatedExpression = stack.pop();
                        alwaysExpression = alwaysExpression.replace(originalExpression, translatedExpression);
                    }
                    if (isEvent(alwaysExpression) && isEvent(sinceExpression)) {
                        // always [event] since [event]
                        String originalSinceExpression = eventStack.pop();
                        String translatedSinceExpression = eventStack.pop();
                        sinceExpression = sinceExpression.replace(originalSinceExpression, translatedSinceExpression).replace("self.snapshot" + currentContext, "s");
                        String originalAlwaysExpression = eventStack.pop();
                        String translatedAlwaysExpression = eventStack.pop();
                        alwaysExpression = alwaysExpression.replace(originalAlwaysExpression, translatedAlwaysExpression).replace("self.snapshot" + currentContext, "s");
                        
                        if ("fromCurrentSnapshot".equals(currentScope)) {
                            translatedOCL = "(let CS:Snapshot = self.snapshot" + currentContext +
                                            " in let PS:Set(Snapshot) = Set{CS.pred()}->closure(s | s.pred())->excluding(null)" +
                                            " in let AllPSQ:Set(Snapshot) = PS->select(s | " + sinceExpression + ")" +
                                            " in let PSQ:Snapshot = AllPSQ->any(s | Set{s}->closure(s | s.pred())->includesAll(AllPSQ))" +
                                            " in let beforeQ:Set(Snapshot) = Set{PSQ}->closure(s | s.pred())" +
                                            " in let PSP:Set(Snapshot) = PS->including(CS)->select(s | " + alwaysExpression + ")" +
                                            " in if PSQ.isDefined() then (if (PSP->size() > 0) then (PS->including(CS)-beforeQ = PSP-beforeQ) else false endif) else (PSP = PS->including(CS)) endif)";
                        } else {
                            translatedOCL = "(let PSQ:Set(Snapshot) = Snapshot.allInstances()->select(s | " + sinceExpression + ") " +
                                            "in if (PSQ->size() > 0) then (PSQ->forAll(s | Set{s.succ()}->closure(s | s.succ())->forAll(s | " + alwaysExpression + "))) else (Snapshot.allInstances()->forAll(s | " + alwaysExpression + ")) endif)";
                        }
                    } else if (isEvent(alwaysExpression)) {
                        // always [event] since [binary expression]
                        // self.attribute -> s.[Class]->any(true)
                        String selfReplace = "s." + toLowerFirstChar(currentContext) + "->any(true)";
                        sinceExpression = fillInSelf(sinceExpression).replace("self", selfReplace);
                        String originalAlwaysExpression = eventStack.pop();
                        String translatedAlwaysExpression = eventStack.pop();
                        alwaysExpression = alwaysExpression.replace(originalAlwaysExpression, translatedAlwaysExpression).replace("self.snapshot" + currentContext, "s");
                        
                        if ("fromCurrentSnapshot".equals(currentScope)) {
                            translatedOCL = "(let CS:Snapshot = self.snapshot" + currentContext +
                                            " in let PS:Set(Snapshot) = Set{CS.pred()}->closure(s | s.pred())->excluding(null)" +
                                            " in let AllPSQ:Set(Snapshot) = PS->select(s | " + sinceExpression + ")" +
                                            " in let PSQ:Snapshot = AllPSQ->any(s | Set{s}->closure(s | s.pred())->includesAll(AllPSQ))" +
                                            " in let beforeQ:Set(Snapshot) = Set{PSQ}->closure(s | s.pred())" +
                                            " in let PSP:Set(Snapshot) = PS->including(CS)->select(s | " + alwaysExpression + ")" +
                                            " in if PSQ.isDefined() then (if (PSP->size() > 0) then (PS->including(CS)-beforeQ = PSP-beforeQ) else false endif) else (PSP = PS->including(CS)) endif)";
                        } else {
                            translatedOCL = "(let PSQ:Set(Snapshot) = Snapshot.allInstances()->select(s | " + sinceExpression + ") " +
                                            "in if (PSQ->size() > 0) then (PSQ->forAll(s | Set{s.succ()}->closure(s | s.succ())->forAll(s | " + alwaysExpression + "))) else (Snapshot.allInstances()->forAll(s | " + alwaysExpression + ")) endif)";
                        }
                    } else if (isEvent(sinceExpression)) {
                        // always [binary expression] since [event]
                        // self.attribute -> s.[Class]->any(true)
                        String selfReplace = "s." + toLowerFirstChar(currentContext) + "->any(true)";
                        alwaysExpression = fillInSelf(alwaysExpression).replace("self", selfReplace);
                        String originalSinceExpression = eventStack.pop();
                        String translatedSinceExpression = eventStack.pop();
                        sinceExpression = sinceExpression.replace(originalSinceExpression, translatedSinceExpression).replace("self.snapshot" + currentContext, "s");
                        
                        if ("fromCurrentSnapshot".equals(currentScope)) {
                            translatedOCL = "(let CS:Snapshot = self.snapshot" + currentContext +
                                            " in let PS:Set(Snapshot) = Set{CS.pred()}->closure(s | s.pred())->excluding(null)" +
                                            " in let AllPSQ:Set(Snapshot) = PS->select(s | " + sinceExpression + ")" +
                                            " in let PSQ:Snapshot = AllPSQ->any(s | Set{s}->closure(s | s.pred())->includesAll(AllPSQ))" +
                                            " in let beforeQ:Set(Snapshot) = Set{PSQ}->closure(s | s.pred())" +
                                            " in let PSP:Set(Snapshot) = PS->including(CS)->select(s | " + alwaysExpression + ")" +
                                            " in if PSQ.isDefined() then (if (PSP->size() > 0) then (PS->including(CS)-beforeQ = PSP-beforeQ) else false endif) else (PSP = PS->including(CS)) endif)";
                        } else {
                            translatedOCL = "(let PSQ:Set(Snapshot) = Snapshot.allInstances()->select(s | " + sinceExpression + ") " +
                                            "in if (PSQ->size() > 0) then (PSQ->forAll(s | Set{s.succ()}->closure(s | s.succ())->forAll(s | " + alwaysExpression + "))) else (Snapshot.allInstances()->forAll(s | " + alwaysExpression + ")) endif)";
                        }
                    } else {
                        // self.attribute -> s.[Class]->any(o | self.id = o.id)
                        String selfReplace = "s." + toLowerFirstChar(currentContext) + "->any(o | self.id = o.id)";
                        alwaysExpression = fillInSelf(alwaysExpression).replace("self", selfReplace);
                        sinceExpression = fillInSelf(sinceExpression).replace("self", selfReplace);
                        // replace "allInstances()" to "allInstances()->select(o | o.[currentSnapshot] = self.[currentSnapshot])" (all instances within the current snapshot)
//                        String prefixAllInstances = getPrefixOfAllInstances(alwaysExpression);
//                        if (prefixAllInstances != null) {
//                            String allInstancesReplacement = "allInstances()->select(o:" + prefixAllInstances + " | o.snapshot" + prefixAllInstances + " = s)";
//                            alwaysExpression = alwaysExpression.replace("allInstances()", allInstancesReplacement);
//                        }
//                        prefixAllInstances = getPrefixOfAllInstances(sinceExpression);
//                        if (prefixAllInstances != null) {
//                            String allInstancesReplacement = "allInstances()->select(o:" + prefixAllInstances + " | o.snapshot" + prefixAllInstances + " = s)";
//                            sinceExpression = sinceExpression.replace("allInstances()", allInstancesReplacement);
//                        }
                        if ("fromCurrentSnapshot".equals(currentScope)) {
                            translatedOCL = "(let CS:Snapshot = self.snapshot" + currentContext +
                                            " in let PS:Set(Snapshot) = Set{CS.pred()}->closure(s | s.pred())->excluding(null)" +
                                            " in let AllPSQ:Set(Snapshot) = PS->select(s | " + sinceExpression + ")" +
                                            " in let PSQ:Snapshot = AllPSQ->any(s | Set{s}->closure(s | s.pred())->includesAll(AllPSQ))" +
                                            " in let beforeQ:Set(Snapshot) = Set{PSQ}->closure(s | s.pred())" +
                                            " in let PSP:Set(Snapshot) = PS->including(CS)->select(s | " + alwaysExpression + ")" +
                                            " in if PSQ.isDefined() then (if (PSP->size() > 0) then (PS->including(CS)-beforeQ = PSP-beforeQ) else false endif) else (PSP = PS->including(CS)) endif)";
                        } else {
                            translatedOCL = "(let PSQ:Set(Snapshot) = Snapshot.allInstances()->select(s | " + sinceExpression + ") " +
                                            "in if (PSQ->size() > 0) then (PSQ->forAll(s | Set{s.succ()}->closure(s | s.succ())->forAll(s | " + alwaysExpression + "))) else (Snapshot.allInstances()->forAll(s | " + alwaysExpression + ")) endif)";
                        }
                    }
                    System.out.println("Exit Always Since Expression: \n" + translatedOCL);
                    stack.push(translatedOCL);
                    stack.push(originalTOCL);
                }
            }
        }
        
        @Override
        public void exitSometimeExp(TOCLParser.SometimeExpContext ctx) {
            TokenStream tokens = parser.getTokenStream();
            String originalTOCL = tokens.getText(ctx);
            String translatedOCL = "";
            
            // sometime P
            // formal semantics: True if e holds in the current state or at least one future state
            if (ctx.getChildCount() == 2) {
                String expressionToSatisfy = getOCL(ctx.getChild(1));
                while (hasNestedTOCL(expressionToSatisfy)) {
                    String originalExpression = stack.pop();
                    String translatedExpression = stack.pop();
                    expressionToSatisfy = expressionToSatisfy.replace(originalExpression, translatedExpression);
                }
                // sometime isCalled(...)
                if (isEvent(expressionToSatisfy)) {
                    String originalExpression = eventStack.pop();
                    String translatedExpression = eventStack.pop();
                    expressionToSatisfy = expressionToSatisfy.replace(originalExpression, translatedExpression);
                    // replace "self.snapshot[currentContext]" = "s"
                    expressionToSatisfy = expressionToSatisfy.replace("self.snapshot" + currentContext, "s");
                    // if currentScope is null then the scope is global, if not then we consider from current snapshot onward
                    if ("fromCurrentSnapshot".equals(currentScope)) {
                        translatedOCL = "(let CS:Snapshot = self.snapshot" + currentContext + " in Set{CS}->closure(s | s.succ())->excluding(null)->exists(s | " + expressionToSatisfy + "))";
                    } else {
                        translatedOCL = "Snapshot.allInstances()->exists(s | " + expressionToSatisfy + ")";
                    }
                } else {
                    // self.attribute -> s.[Class]->any(o | self.id = o.id)
                    String selfReplace = "s." + toLowerFirstChar(currentContext) + "->any(o | self.id = o.id)";
                    expressionToSatisfy = fillInSelf(expressionToSatisfy).replace("self", selfReplace);
                    // replace "allInstances()" to "allInstances()->select(o | o.[currentSnapshot] = self.[currentSnapshot])" (all instances within the current snapshot)
//                    String prefixAllInstances = getPrefixOfAllInstances(expressionToSatisfy);
//                    if (prefixAllInstances != null) {
//                        String allInstancesReplacement = "allInstances()->select(o:" + prefixAllInstances + " | o.snapshot" + prefixAllInstances + " = s)";
//                        expressionToSatisfy = expressionToSatisfy.replace("allInstances()", allInstancesReplacement);
//                    }
                    // check if there is a snapshot (o) in all snapshots that satisfies P
                    if ("fromCurrentSnapshot".equals(currentScope)) {
                        translatedOCL = "(let CS:Snapshot = self.snapshot" + currentContext + " in Set{CS}->closure(s | s.succ())->excluding(null)->exists(s | " + expressionToSatisfy + "))";
                    } else {
                        translatedOCL = "Snapshot.allInstances()->exists(s | " + expressionToSatisfy + ")";
                    }
                }
                System.out.println("Exit Sometime Expression: \n" + translatedOCL);
                stack.push(translatedOCL);
                stack.push(originalTOCL);
            }
            else {
                // sometime P before Q
                // formal semantics: True if e1 becomes true at some point
                // before e2 does.
                if (ctx.op.getText().equals("before")) {
                    String sometimeExpression = getOCL(ctx.getChild(1));
                    String beforeExpression = getOCL(ctx.getChild(3));
                    // before expression should be replaced first
                    while (hasNestedTOCL(beforeExpression)) {
                        String originalExpression = stack.pop();
                        String translatedExpression = stack.pop();
                        beforeExpression = beforeExpression.replace(originalExpression, translatedExpression);
                    }
                    while (hasNestedTOCL(sometimeExpression)) {
                        String originalExpression = stack.pop();
                        String translatedExpression = stack.pop();
                        sometimeExpression = sometimeExpression.replace(originalExpression, translatedExpression);
                    }
                    
                    if (isEvent(sometimeExpression) && isEvent(beforeExpression)) {
                        String originalBeforeExpression = eventStack.pop();
                        String translatedBeforeExpression = eventStack.pop();
                        beforeExpression = beforeExpression.replace(originalBeforeExpression, translatedBeforeExpression).replace("self.snapshot" + currentContext, "s");
                        String originalSometimeExpression = eventStack.pop();
                        String translatedSometimeExpression = eventStack.pop();
                        sometimeExpression = sometimeExpression.replace(originalSometimeExpression, translatedSometimeExpression).replace("self.snapshot" + currentContext, "s");
                        
                        if ("fromCurrentSnapshot".equals(currentScope)) {
                            // every snapshot that satisfies Q must exist a snapshot that satisfies P in all previous snapshots minus this current snapshot
//                            translatedOCL = "(let FS:Set(Snapshot) = Set{self.snapshot" + currentContext + "}->closure(s | s.succ())->excluding(null) " +
//                                    "in let PreS:Set(Snapshot) = Set{self.snapshot" + currentContext + "}->closure(s | s.pred())->excluding(null) " +
//                                    "in let FSQ:Set(Snapshot) = FS->select(s | " + beforeExpression + ") " +
//                                    "in let FSP:Set(Snapshot) = FS->select(s | " + sometimeExpression + ") " +
//                                    "in if (FSQ->size() > 0) then (if (FSP->size() > 0) then (FSQ->forAll(s | (Set{s}->closure(s | s.succ())-PreS)->exists(s_1 | FSP->includes(s_1)))) else false endif) else true endif)";
                            // the closet snapshot that satisfies Q must exist a snapshot that satisfies P in all previous snapshots minus this current snapshot
                            translatedOCL = "(let FS:Set(Snapshot) = Set{self.snapshot" + currentContext + "}->closure(s | s.succ())->excluding(null) " +
                                    "in let PreS:Set(Snapshot) = Set{self.snapshot" + currentContext + ".pred()}->closure(s | s.pred())->excluding(null) " +
                                    "in let AllFSQ:Set(Snapshot) = FS->select(s | " + beforeExpression + ") " +
                                    "in let FSQ:Snapshot = AllFSQ->any(s | Set{s}->closure(s | s.succ())->includesAll(AllFSQ))" +
                                    "in let FSP:Set(Snapshot) = FS->select(s | " + sometimeExpression + ") " +
                                    "in if FSQ.isDefined() then (if (FSP->size() > 0) then ((Set{FSQ.pred()}->closure(s | s.pred())-PreS)->exists(s_1 | FSP->includes(s_1))) else false endif) else false endif)";
                        } else {
                            // every snapshot that satisfies Q must exist a snapshot that satisfies P in all of its previous snapshots
                            translatedOCL = "(let FSQ:Set(Snapshot) = Snapshot.allInstances()->select(s | " + beforeExpression + ") " +
                                    "in if (FSQ->size() > 0) then (FSQ->forAll(s | Set{s.pred()}->closure(s | s.pred())->exists(s | " + sometimeExpression + "))) else true endif)";
                        }
                    } else if (isEvent(sometimeExpression)) {
                        // self.attribute -> s.[Class]->any(o | self.id = o.id)
                        String selfReplace = "s." + toLowerFirstChar(currentContext) + "->any(o | self.id = o.id)";
                        beforeExpression = fillInSelf(beforeExpression).replace("self", selfReplace);
                        String originalSometimeExpression = eventStack.pop();
                        String translatedSometimeExpression = eventStack.pop();
                        sometimeExpression = sometimeExpression.replace(originalSometimeExpression, translatedSometimeExpression).replace("self.snapshot" + currentContext, "s");
                        
                        if ("fromCurrentSnapshot".equals(currentScope)) {
//                            translatedOCL = "(let FS:Set(Snapshot) = Set{self.snapshot" + currentContext + "}->closure(s | s.succ())->excluding(null) " +
//                                    "in let PreS:Set(Snapshot) = Set{self.snapshot" + currentContext + "}->closure(s | s.pred())->excluding(null) " +
//                                    "in let FSQ:Set(Snapshot) = FS->select(s | " + beforeExpression + ") " +
//                                    "in let FSP:Set(Snapshot) = FS->select(s | " + sometimeExpression + ") " +
//                                    "in if (FSQ->size() > 0) then (if (FSP->size() > 0) then (FSQ->forAll(s | (Set{s}->closure(s | s.succ())-PreS)->exists(s_1 | FSP->includes(s_1)))) else false endif) else true endif)";
                            // the closet snapshot that satisfies Q must exist a snapshot that satisfies P in all previous snapshots minus this current snapshot
                            translatedOCL = "(let FS:Set(Snapshot) = Set{self.snapshot" + currentContext + "}->closure(s | s.succ())->excluding(null) " +
                                    "in let PreS:Set(Snapshot) = Set{self.snapshot" + currentContext + ".pred()}->closure(s | s.pred())->excluding(null) " +
                                    "in let AllFSQ:Set(Snapshot) = FS->select(s | " + beforeExpression + ") " +
                                    "in let FSQ:Snapshot = AllFSQ->any(s | Set{s}->closure(s | s.succ())->includesAll(AllFSQ))" +
                                    "in let FSP:Set(Snapshot) = FS->select(s | " + sometimeExpression + ") " +
                                    "in if FSQ.isDefined() then (if (FSP->size() > 0) then ((Set{FSQ.pred()}->closure(s | s.pred())-PreS)->exists(s_1 | FSP->includes(s_1))) else false endif) else (FSP->isEmpty()) endif)";
                        } else {
                            translatedOCL = "(let FSQ:Set(Snapshot) = Snapshot.allInstances()->select(s | " + beforeExpression + ") " +
                                    "in if (FSQ->size() > 0) then (FSQ->forAll(s | Set{s.pred()}->closure(s | s.pred())->exists(s | " + sometimeExpression + "))) else true endif)";
                        }
                    } else if (isEvent(beforeExpression)) {
                        // self.attribute -> s.[Class]->any(o | self.id = o.id)
                        String selfReplace = "s." + toLowerFirstChar(currentContext) + "->any(o | self.id = o.id)";
                        sometimeExpression = fillInSelf(sometimeExpression).replace("self", selfReplace);
                        String originalBeforeExpression = eventStack.pop();
                        String translatedBeforeExpression = eventStack.pop();
                        beforeExpression = beforeExpression.replace(originalBeforeExpression, translatedBeforeExpression).replace("self.snapshot" + currentContext, "s");
                        
                        if ("fromCurrentSnapshot".equals(currentScope)) {
//                            translatedOCL = "(let FS:Set(Snapshot) = Set{self.snapshot" + currentContext + "}->closure(s | s.succ())->excluding(null) " +
//                                    "in let PreS:Set(Snapshot) = Set{self.snapshot" + currentContext + "}->closure(s | s.pred())->excluding(null) " +
//                                    "in let FSQ:Set(Snapshot) = FS->select(s | " + beforeExpression + ") " +
//                                    "in let FSP:Set(Snapshot) = FS->select(s | " + sometimeExpression + ") " +
//                                    "in if (FSQ->size() > 0) then (if (FSP->size() > 0) then (FSQ->forAll(s | (Set{s}->closure(s | s.succ())-PreS)->exists(s_1 | FSP->includes(s_1)))) else false endif) else true endif)";
                            // the closet snapshot that satisfies Q must exist a snapshot that satisfies P in all previous snapshots minus this current snapshot
                            translatedOCL = "(let FS:Set(Snapshot) = Set{self.snapshot" + currentContext + "}->closure(s | s.succ())->excluding(null) " +
                                    "in let PreS:Set(Snapshot) = Set{self.snapshot" + currentContext + ".pred()}->closure(s | s.pred())->excluding(null) " +
                                    "in let AllFSQ:Set(Snapshot) = FS->select(s | " + beforeExpression + ") " +
                                    "in let FSQ:Snapshot = AllFSQ->any(s | Set{s}->closure(s | s.succ())->includesAll(AllFSQ))" +
                                    "in let FSP:Set(Snapshot) = FS->select(s | " + sometimeExpression + ") " +
                                    "in if FSQ.isDefined() then (if (FSP->size() > 0) then ((Set{FSQ.pred()}->closure(s | s.pred())-PreS)->exists(s_1 | FSP->includes(s_1))) else false endif) else (FSP->isEmpty()) endif)";
                        } else {
                            translatedOCL = "(let FSQ:Set(Snapshot) = Snapshot.allInstances()->select(s | " + beforeExpression + ") " +
                                    "in if (FSQ->size() > 0) then (FSQ->forAll(s | Set{s.pred()}->closure(s | s.pred())->exists(s | " + sometimeExpression + "))) else true endif)";
                        }
                    } else {
                        // self.attribute -> s.[Class]->any(o | self.id = o.id)
                        String selfReplace = "s." + toLowerFirstChar(currentContext) + "->any(o | self.id = o.id)";
                        beforeExpression = fillInSelf(beforeExpression).replace("self", selfReplace);
                        // replace "allInstances()" to "allInstances()->select(o | o.[currentSnapshot] = self.[currentSnapshot])" (all instances within the current snapshot)
//                        String prefixAllInstances = getPrefixOfAllInstances(beforeExpression);
//                        if (prefixAllInstances != null) {
//                            String allInstancesReplacement = "allInstances()->select(o:" + prefixAllInstances + " | o.snapshot" + prefixAllInstances + " = s)";
//                            beforeExpression = beforeExpression.replace("allInstances()", allInstancesReplacement);
//                        }
                        sometimeExpression = fillInSelf(sometimeExpression).replace("self", selfReplace);
                        // replace "allInstances()" to "allInstances()->select(o | o.[currentSnapshot] = self.[currentSnapshot])" (all instances within the current snapshot)
//                        prefixAllInstances = getPrefixOfAllInstances(sometimeExpression);
//                        if (prefixAllInstances != null) {
//                            String allInstancesReplacement = "allInstances()->select(o:" + prefixAllInstances + " | o.snapshot" + prefixAllInstances + " = s)";
//                            sometimeExpression = sometimeExpression.replace("allInstances()", allInstancesReplacement);
//                        }
                        // get current snapshot
                        // -> get all future snapshots (FS) not including CS
                        // -> get all future snapshots that satisfies Q (FSQ)
                        // -> get all future snapshots that satisfies P (FSP) including CS
                        // -> check if there is a future snapshot (o)
                        // that satisfies P and the size of its future snapshots is greater than all the size of future snapshots of all snapshots that satisfies Q
                        if ("fromCurrentSnapshot".equals(currentScope)) {
                            translatedOCL = "(let FS:Set(Snapshot) = Set{self.snapshot" + currentContext + "}->closure(s | s.succ())->excluding(null) " +
                                    "in let PreS:Set(Snapshot) = Set{self.snapshot" + currentContext + ".pred()}->closure(s | s.pred())->excluding(null) " +
                                    "in let AllFSQ:Set(Snapshot) = FS->select(s | " + beforeExpression + ") " +
                                    "in let FSQ:Snapshot = AllFSQ->any(s | Set{s}->closure(s | s.succ())->includesAll(AllFSQ))" +
                                    "in let FSP:Set(Snapshot) = FS->select(s | " + sometimeExpression + ") " +
                                    "in if FSQ.isDefined() then (if (FSP->size() > 0) then ((Set{FSQ.pred()}->closure(s | s.pred())-PreS)->exists(s_1 | FSP->includes(s_1))) else false endif) else (FSP->isEmpty()) endif)";
                        } else {
                            translatedOCL = "(let FSQ:Set(Snapshot) = Snapshot.allInstances()->select(s | " + beforeExpression + ") " +
                                    "in if (FSQ->size() > 0) then (FSQ->forAll(s | Set{s.pred()}->closure(s | s.pred())->exists(s | " + sometimeExpression + "))) else true endif)";
                        }
                    }
                    System.out.println("Exit Sometime Before Expression: \n" + translatedOCL);
                    stack.push(translatedOCL);
                    stack.push(originalTOCL);
                }
                else if (ctx.op.getText().equals("since")) {
                    // sometime P since Q
                    // formal semantics: True if e1 has been true at some point since the
                    // last time e2 was true.
                    String sometimeExpression = getOCL(ctx.getChild(1));
                    String sinceExpression = getOCL(ctx.getChild(3));
                    while (hasNestedTOCL(sinceExpression)) {
                        String originalExpression = stack.pop();
                        String translatedExpression = stack.pop();
                        sinceExpression = sinceExpression.replace(originalExpression, translatedExpression);
                    }
                    while (hasNestedTOCL(sometimeExpression)) {
                        String originalExpression = stack.pop();
                        String translatedExpression = stack.pop();
                        sometimeExpression = sometimeExpression.replace(originalExpression, translatedExpression);
                    }
                    
                    if (isEvent(sometimeExpression) && isEvent(sinceExpression)) {
                        String originalSinceExpression = eventStack.pop();
                        String translatedSinceExpression = eventStack.pop();
                        sinceExpression = sinceExpression.replace(originalSinceExpression, translatedSinceExpression).replace("self.snapshot" + currentContext, "s");
                        String originalSometimeExpression = eventStack.pop();
                        String translatedSometimeExpression = eventStack.pop();
                        sometimeExpression = sometimeExpression.replace(originalSometimeExpression, translatedSometimeExpression).replace("self.snapshot" + currentContext, "s");
                        
                        if ("fromCurrentSnapshot".equals(currentScope)) {
//                            translatedOCL = "(let FS:Set(Snapshot) = Set{self.snapshot" + currentContext + "}->closure(s | s.succ())->excluding(null) " +
//                                    "in let FSQ:Set(Snapshot) = FS->select(s | " + sinceExpression + ") " +
//                                    "in let FSP:Set(Snapshot) = FS->select(s | " + sometimeExpression + ") " +
//                                    "in if (FSQ->size() > 0) then (if (FSP->size() > 0) then (FSQ->forAll(s | Set{s}->closure(s | s.succ())->exists(s_1 | FSP->includes(s_1)))) else false endif) else true endif)";
                            // the closet snapshot that satisfies Q must exist a snapshot that satisfies P in all future snapshots minus this current snapshot
//                            translatedOCL = "(let FS:Set(Snapshot) = Set{self.snapshot" + currentContext + "}->closure(s | s.succ())->excluding(null) " +
//                                    "in let AllFSQ:Set(Snapshot) = FS->select(s | " + sinceExpression + ") " +
//                                    "in let FSQ:Snapshot = AllFSQ->any(s | Set{s}->closure(s | s.succ())->includesAll(AllFSQ))" +
//                                    "in let FSP:Set(Snapshot) = FS->select(s | " + sometimeExpression + ") " +
//                                    "in if FSQ.isDefined() then (if (FSP->size() > 0) then (Set{FSQ}->closure(s | s.succ())->exists(s_1 | FSP->includes(s_1))) else false endif) else (FSP->isEmpty()) endif)";
                            translatedOCL = "(let CS:Snapshot = self.snapshot" + currentContext +
                                            " in let PS:Set(Snapshot) = Set{CS.pred()}->closure(s | s.pred())->excluding(null)" +
                                            " in let AllPSQ:Set(Snapshot) = PS->select(s | " + sinceExpression + ")" +
                                            " in let PSQ:Snapshot = AllPSQ->any(s | Set{s}->closure(s | s.pred())->includesAll(AllPSQ))" +
                                            " in let PSP:Set(Snapshot) = PS->select(s | " + sometimeExpression + ")" +
                                            " in if PSQ.isDefined() then (Set{PSQ}->closure(s | s.succ())->excluding(null)->intersection(PS)->exists(s | PSP->includes(s))) else false endif)";
                        } else {
                            translatedOCL = "(let FSQ:Set(Snapshot) = Snapshot.allInstances()->select(s | " + sinceExpression + ") " +
                                    "in FSQ->forAll(s | Set{s}->closure(s | s.succ())->exists(s | " + sometimeExpression + ")))";
                        }
                    } else if (isEvent(sometimeExpression)) {
                        // self.attribute -> s.[Class]->any(o | self.id = o.id)
                        String selfReplace = "s." + toLowerFirstChar(currentContext) + "->any(o | self.id = o.id)";
                        sinceExpression = fillInSelf(sinceExpression).replace("self", selfReplace);
                        String originalSometimeExpression = eventStack.pop();
                        String translatedSometimeExpression = eventStack.pop();
                        sometimeExpression = sometimeExpression.replace(originalSometimeExpression, translatedSometimeExpression).replace("self.snapshot" + currentContext, "s");
                        
                        if ("fromCurrentSnapshot".equals(currentScope)) {
                            translatedOCL = "(let CS:Snapshot = self.snapshot" + currentContext +
                                    " in let PS:Set(Snapshot) = Set{CS.pred()}->closure(s | s.pred())->excluding(null)" +
                                    " in let AllPSQ:Set(Snapshot) = PS->select(s | " + sinceExpression + ")" +
                                    " in let PSQ:Snapshot = AllPSQ->any(s | Set{s}->closure(s | s.pred())->includesAll(AllPSQ))" +
                                    " in let PSP:Set(Snapshot) = PS->select(s | " + sometimeExpression + ")" +
                                    " in if PSQ.isDefined() then (Set{PSQ}->closure(s | s.succ())->excluding(null)->intersection(PS)->exists(s | PSP->includes(s))) else false endif)";
                        } else {
                            translatedOCL = "(let FSQ:Set(Snapshot) = Snapshot.allInstances()->select(s | " + sinceExpression + ") " +
                                    "in FSQ->forAll(s | Set{s}->closure(s | s.succ())->exists(s | " + sometimeExpression + ")))";
                        }
                    } else if (isEvent(sinceExpression)) {
                        // self.attribute -> s.[Class]->any(o | self.id = o.id)
                        String selfReplace = "s." + toLowerFirstChar(currentContext) + "->any(o | self.id = o.id)";
                        sometimeExpression = fillInSelf(sometimeExpression).replace("self", selfReplace);
                        String originalSinceExpression = eventStack.pop();
                        String translatedSinceExpression = eventStack.pop();
                        sinceExpression = sinceExpression.replace(originalSinceExpression, translatedSinceExpression).replace("self.snapshot" + currentContext, "s");
                        
                        if ("fromCurrentSnapshot".equals(currentScope)) {
                            translatedOCL = "(let CS:Snapshot = self.snapshot" + currentContext +
                                    " in let PS:Set(Snapshot) = Set{CS.pred()}->closure(s | s.pred())->excluding(null)" +
                                    " in let AllPSQ:Set(Snapshot) = PS->select(s | " + sinceExpression + ")" +
                                    " in let PSQ:Snapshot = AllPSQ->any(s | Set{s}->closure(s | s.pred())->includesAll(AllPSQ))" +
                                    " in let PSP:Set(Snapshot) = PS->select(s | " + sometimeExpression + ")" +
                                    " in if PSQ.isDefined() then (Set{PSQ}->closure(s | s.succ())->excluding(null)->intersection(PS)->exists(s | PSP->includes(s))) else false endif)";
                        } else {
                            translatedOCL = "(let FSQ:Set(Snapshot) = Snapshot.allInstances()->select(s | " + sinceExpression + ") " +
                                    "in FSQ->forAll(s | Set{s}->closure(s | s.succ())->exists(s | " + sometimeExpression + ")))";
                        }
                    } else {
                        // self.attribute -> s.[Class]->any(o | self.id = o.id)
                        String selfReplace = "s." + toLowerFirstChar(currentContext) + "->any(o | self.id = o.id)";
                        sometimeExpression = fillInSelf(sometimeExpression).replace("self", selfReplace);
                        sinceExpression = fillInSelf(sinceExpression).replace("self", selfReplace);
                        // replace "allInstances()" to "allInstances()->select(o | o.[currentSnapshot] = self.[currentSnapshot])" (all instances within the current snapshot)
//                        String prefixAllInstances = getPrefixOfAllInstances(sometimeExpression);
//                        if (prefixAllInstances != null) {
//                            String allInstancesReplacement = "allInstances()->select(o:" + prefixAllInstances + " | o.snapshot" + prefixAllInstances + " = s)";
//                            sometimeExpression = sometimeExpression.replace("allInstances()", allInstancesReplacement);
//                        }
//                        prefixAllInstances = getPrefixOfAllInstances(sinceExpression);
//                        if (prefixAllInstances != null) {
//                            String allInstancesReplacement = "allInstances()->select(o:" + prefixAllInstances + " | o.snapshot" + prefixAllInstances + " = s)";
//                            sinceExpression = sinceExpression.replace("allInstances()", allInstancesReplacement);
//                        }
                        if ("fromCurrentSnapshot".equals(currentScope)) {
                            translatedOCL = "(let CS:Snapshot = self.snapshot" + currentContext +
                                    " in let PS:Set(Snapshot) = Set{CS.pred()}->closure(s | s.pred())->excluding(null)" +
                                    " in let AllPSQ:Set(Snapshot) = PS->select(s | " + sinceExpression + ")" +
                                    " in let PSQ:Snapshot = AllPSQ->any(s | Set{s}->closure(s | s.pred())->includesAll(AllPSQ))" +
                                    " in let PSP:Set(Snapshot) = PS->select(s | " + sometimeExpression + ")" +
                                    " in if PSQ.isDefined() then (Set{PSQ}->closure(s | s.succ())->excluding(null)->intersection(PS)->exists(s | PSP->includes(s))) else false endif)";
                        } else {
                            translatedOCL = "(let FSQ:Set(Snapshot) = Snapshot.allInstances()->select(s | " + sinceExpression + ") " +
                                    "in FSQ->forAll(s | Set{s}->closure(s | s.succ())->exists(s | " + sometimeExpression + ")))";
                        }
                    }
                    
                    System.out.println("Exit Sometime Since Expression: \n" + translatedOCL);
                    stack.push(translatedOCL);
                    stack.push(originalTOCL);
                }
            }
        }
        
        @Override
        public void exitPreviousExp(TOCLParser.PreviousExpContext ctx) {
            TokenStream tokens = parser.getTokenStream();
            String originalTOCL = tokens.getText(ctx);
            String translatedOCL = "";
            
            String expressionToSatisfy = getOCL(ctx.getChild(1));
            while (hasNestedTOCL(expressionToSatisfy)) {
                String originalExpression = stack.pop();
                String translatedExpression = stack.pop();
                expressionToSatisfy = expressionToSatisfy.replace(originalExpression, translatedExpression);
            }
            if (isEvent(expressionToSatisfy)) {
                String originalExpression = eventStack.pop();
                String translatedExpression = eventStack.pop();
                expressionToSatisfy = expressionToSatisfy.replace(originalExpression, translatedExpression);
                // replace "self.snapshot[currentContext]" = "self.snapshot[currentContext].pred()"
                translatedOCL = expressionToSatisfy.replace("self.snapshot" + currentContext, "self.snapshot" + currentContext + ".pred()");
            } else {
                // self.attribute -> self.pred[Class].attribute
                String selfReplace = "self.snapshot" + currentContext + ".pred()." + toLowerFirstChar(currentContext) + "->any(o | self.id = o.id)";
                translatedOCL = fillInSelf(expressionToSatisfy).replace("self", selfReplace);
                // replace "allInstances()" to "allInstances()->select(o | o.[currentSnapshot] = self.[currentSnapshot].pred)" (all instances within the previous snapshot)
//                String prefixAllInstances = getPrefixOfAllInstances(translatedOCL);
//                if (prefixAllInstances != null) {
//                    String allInstancesReplacement = "allInstances()->select(o:" + prefixAllInstances + " | o.snapshot" + prefixAllInstances + " = self.snapshot" + currentContext + ".pred())";
//                    translatedOCL = translatedOCL.replace("allInstances()", allInstancesReplacement);
//                }
            }
            System.out.println("Exit Previous Expression: " + translatedOCL);
            stack.push(translatedOCL);
            stack.push(originalTOCL);
        }
        
        @Override
        public void exitAlwaysPastExp(TOCLParser.AlwaysPastExpContext ctx) {
            // TODO: This operator is not suitable to specify event notion
            TokenStream tokens = parser.getTokenStream();
            String originalTOCL = tokens.getText(ctx);
            String translatedOCL = "";
            
            String expressionToSatisfy = getOCL(ctx.getChild(1));
            while (hasNestedTOCL(expressionToSatisfy)) {
                String originalExpression = stack.pop();
                String translatedExpression = stack.pop();
                expressionToSatisfy = expressionToSatisfy.replace(originalExpression, translatedExpression);
            }
            if (isEvent(expressionToSatisfy)) {
                String originalExpression = eventStack.pop();
                String translatedExpression = eventStack.pop();
                expressionToSatisfy = expressionToSatisfy.replace(originalExpression, translatedExpression).replace("self.snapshot" + currentContext, "s");
                translatedOCL = "(let CS:Snapshot = self.snapshot" + currentContext +
                                " in Set{CS.pred()}->closure(s | s.pred())->excluding(null)->forAll(s | " + expressionToSatisfy + "))";
            } else {
                // self.attribute -> s.[Class]->any(o | self.id = o.id)
                String selfReplace = "s." + toLowerFirstChar(currentContext) + "->any(o | self.id = o.id)";
                String alwaysExpression = fillInSelf(expressionToSatisfy).replace("self", selfReplace);
//            String prefixAllInstances = getPrefixOfAllInstances(alwaysExpression);
//            if (prefixAllInstances != null) {
//                String allInstancesReplacement = "allInstances()->select(o:" + prefixAllInstances + " | o.snapshot" + prefixAllInstances + " = s)";
//                alwaysExpression = alwaysExpression.replace("allInstances()", allInstancesReplacement);
//            }
                // get current snapshot CS
                // get all past snapshots (PS) not including CS
                // check if all past snapshots satisfies P
                translatedOCL = "(let CS:Snapshot = self.snapshot" + currentContext +
                                " in Set{CS.pred()}->closure(s | s.pred())->excluding(null)->forAll(s | " + alwaysExpression + "))";
            }
            System.out.println("Exit Always Past Expression: " + translatedOCL);
            stack.push(translatedOCL);
            stack.push(originalTOCL);
        }
        
        @Override
        public void exitSometimePastExp(TOCLParser.SometimePastExpContext ctx) {
            TokenStream tokens = parser.getTokenStream();
            String originalTOCL = tokens.getText(ctx);
            String translatedOCL = "";
            
            String expressionToSatisfy = getOCL(ctx.getChild(1));
            while (hasNestedTOCL(expressionToSatisfy)) {
                String originalExpression = stack.pop();
                String translatedExpression = stack.pop();
                expressionToSatisfy = expressionToSatisfy.replace(originalExpression, translatedExpression);
            }
            if (isEvent(expressionToSatisfy)) {
                String originalExpression = eventStack.pop();
                String translatedExpression = eventStack.pop();
                // replace "self.snapshot[currentContext]" = "s"
                expressionToSatisfy = expressionToSatisfy.replace(originalExpression, translatedExpression).replace("self.snapshot" + currentContext, "s");
                translatedOCL = "(let CS:Snapshot = self.snapshot" + currentContext +
                                " in Set{CS.pred()}->closure(s | s.pred())->exists(s | " + expressionToSatisfy + "))";
            } else {
                // self.attribute -> s.[Class]->any(o | self.id = o.id)
                String selfReplace = "s." + toLowerFirstChar(currentContext) + "->any(o | self.id = o.id)";
                String sometimeExpression = fillInSelf(expressionToSatisfy).replace("self", selfReplace);
//                String prefixAllInstances = getPrefixOfAllInstances(sometimeExpression);
//                if (prefixAllInstances != null) {
//                    String allInstancesReplacement = "allInstances()->select(o:" + prefixAllInstances + " | o.snapshot" + prefixAllInstances + " = s)";
//                    sometimeExpression = sometimeExpression.replace("allInstances()", allInstancesReplacement);
//                }
                // get current snapshot CS
                // get all past snapshots (PS) not including CS
                // check if there is a past snapshot that satisfies P
                translatedOCL = "(let CS:Snapshot = self.snapshot" + currentContext +
                                " in Set{CS.pred()}->closure(s | s.pred())->excluding(null)->exists(s | " + sometimeExpression + "))";
            }
            System.out.println("Exit Sometime Past Expression: " + translatedOCL);
            stack.push(translatedOCL);
            stack.push(originalTOCL);
        }
        
        // Utils methods
        /**
         * Check if the TOCL expression has nested TOCL expressions.
         * @param toclExpression the TOCL expression to check.
         * @return true if the TOCL expression has nested TOCL expressions, false otherwise.
         */
        private boolean hasNestedTOCL(String toclExpression) {
            int operatorIndex = -1;
            for (String operator : toclOperators) {
                operatorIndex = toclExpression.indexOf(operator);
                if (operatorIndex > -1) {
                    // Check if the operator is not inside a string
                    if ((countMatches(toclExpression.substring(0, operatorIndex), "'") % 2) == 0) {
                        if (operatorIndex == 0 || Character.isWhitespace(toclExpression.charAt(operatorIndex - 1))) {
                            if (Character.isWhitespace(toclExpression.charAt(operatorIndex + operator.length())) || operator.contains("@")) {
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }
        
        private boolean isEvent(String toclExpression) {
            return toclExpression.contains("isCalled") || toclExpression.contains("becomesTrue");
        }
        
        /**
         * Fill in the self keyword for attributes and operations
         * if they do not have a prefix.
         * @param expression the expression to fill in self keyword.
         * @return the expression with self keyword filled in.
         */
        private String fillInSelf(String expression) {
            String expToFill = expression;
            
            MClass contextClass = null;
            for (MClass mClass : classes) {
                if (mClass.name().equals(currentContext)) {
                    contextClass = mClass;
                    break;
                }
            }
            
            for (MAttribute attribute : contextClass.allAttributes()) {
                String attributeName = attribute.name();
                Pattern pattern = Pattern.compile("(?<![A-Za-z0-9_])" + Pattern.quote(attributeName) + "(?![A-Za-z0-9_])");
                Matcher matcher = pattern.matcher(expToFill);
                StringBuffer replaced = new StringBuffer();
                while (matcher.find()) {
                    int index = matcher.start();
                    boolean hasDotPrefix = index > 0 && expToFill.charAt(index - 1) == '.';
                    boolean hasArrowPrefix = index > 1 && expToFill.charAt(index - 1) == '>' && expToFill.charAt(index - 2) == '-';
                    String replacement = matcher.group();
                    if (!hasDotPrefix && !hasArrowPrefix) {
                        if (attribute.type().isInstantiableCollection()) {
                            replacement = "self->" + attributeName;
                        } else {
                            replacement = "self." + attributeName;
                        }
                    }
                    matcher.appendReplacement(replaced, Matcher.quoteReplacement(replacement));
                }
                matcher.appendTail(replaced);
                expToFill = replaced.toString();
            }
            
//            expression = expToFill;
//            for (MOperation operation : contextClass.allOperations()) {
//                String operationName = operation.name();
//                if (expression.contains(operationName)) {
//                    int operationIndex = expression.indexOf(operationName);
//                    while (operationIndex > -1) {
//                        String replacedExp = expression;
//                        if (operationIndex > 0) {
//                            if (expression.charAt(operationIndex - 1) == '.'
//                                || (expression.charAt(operationIndex - 1) == '-' && expression.charAt(operationIndex - 2) == '>')) {
//                                // Do nothing
//                            } else {
//                                replacedExp = expression.replace(operationName, "self." + operationName);
//                            }
//                        } else {
//                            replacedExp = expression.replace(operationName, "self." + operationName);
//                        }
//                        expToFill = expToFill.replace(expression, replacedExp);
//                        expression = expression.substring(operationIndex + operationName.length());
//                        operationIndex = expression.indexOf(operationName);
//                    }
//                }
//            }
            
            return expToFill;
        }
        
        /**
         * Count the number of matches of a string in another string.
         * @param input the string to search in.
         * @param strToMatch the string to search for.
         * @return the number of matches.
         */
        public Integer countMatches(String input, String strToMatch) {
            int index = input.indexOf(strToMatch);
            int matches = 0;
            while (index != -1) {
                matches++;
                input = input.substring(index + 1);
                index = input.indexOf(strToMatch);
            }
            return matches;
        }
        
        /**
         * Get the prefix of allInstances() in the expression.
         * This only works for the first allInstances() found.
         * @param expression the expression to search for allInstances().
         * @return the prefix of allInstances().
         */
        private String getPrefixOfAllInstances(String expression) {
            int index = expression.indexOf("allInstances()");
            if (index > -1) {
                Pattern pattern = Pattern.compile("\\b(\\w+)\\.allInstances\\(\\)");
                Matcher matcher = pattern.matcher(expression);
                if (matcher.find()) {
                    return matcher.group(1);
                }
            }
            return null;
        }
        
        private String toLowerFirstChar(String str) {
            return str.substring(0, 1).toLowerCase() + str.substring(1);
        }
    }
}
