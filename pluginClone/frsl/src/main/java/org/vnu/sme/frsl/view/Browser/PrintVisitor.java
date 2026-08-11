package org.vnu.sme.frsl.view.Browser;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import org.vnu.sme.frsl.mm.FRSLmodel.ActStep;
import org.vnu.sme.frsl.mm.FRSLmodel.Actor;
import org.vnu.sme.frsl.mm.FRSLmodel.ActorAction;
import org.vnu.sme.frsl.mm.FRSLmodel.ActorStep;
import org.vnu.sme.frsl.mm.FRSLmodel.AltFlow;
import org.vnu.sme.frsl.mm.FRSLmodel.ObjVar;
import org.vnu.sme.frsl.mm.FRSLmodel.RejoinStep;
import org.vnu.sme.frsl.mm.FRSLmodel.SnapshotPattern;
import org.vnu.sme.frsl.mm.FRSLmodel.Step;
import org.vnu.sme.frsl.mm.FRSLmodel.SystemAction;
import org.vnu.sme.frsl.mm.FRSLmodel.SystemStep;
import org.vnu.sme.frsl.mm.FRSLmodel.UCStep;
import org.vnu.sme.frsl.mm.FRSLmodel.Usecase;
import org.vnu.sme.frsl.mm.FRSLmodel.UsecasePostcondition;
import org.vnu.sme.frsl.mm.FRSLmodel.UsecasePrecondition;
import org.vnu.sme.frsl.mm.FRSLmodel.VarLink;

public class PrintVisitor {

    // protected PrintWriter
    protected PrintWriter fPrint;
    protected int fIndent = 0;
    protected final int fIndentStep = 4;

    public PrintVisitor(StringWriter writer) {
        fPrint = new PrintWriter(writer);
    }

    protected void println(String s) {
        fPrint.print(s);
        println();

    }

    protected void print(String s) {
        fPrint.print(s);
    }

    protected void println() {
        print("<br>");
        fPrint.println();
    }

    protected String doubleDot() {
        return ":";
    }

    protected String dotComma() {
        return ";";
    }

    protected String comma() {
        return ",";
    }

    protected String ws() {
        return "&nbsp;";
    }

    protected String end() {
        return keyword("end");
    }

    protected String keyword(String s) {
        return "<strong>" + s + "</strong>";
    }

    public void visitPrintActor(Actor actor) {
        println(keyword("actor") + ws() + actor.getName());
        if (actor.getDescription() != null) {
            inIndent();
            visitPrintDescription(actor.getDescription());
        }
        println(end());
    }

    public void visitPrintUsecase(Usecase usecase) {
        println(keyword("usecase") + ws() + usecase.getName());
        inIndent();
        visitPrintUsecaseAtributte(usecase);
        if (usecase.getPrecondition() != null) {
            visitPrintUsecasePrecondition(usecase.getPrecondition());
        }
        if (usecase.getPostcondition() != null) {
            visitPrintUsecasePostcondition(usecase.getPostcondition());
        }

        Step step = usecase.getFirstStep();
        if (step != null) {
            visitPrintStep(step);
            while (step.getNextstep() != null) {
                step = step.getNextstep();
                visitPrintStep(step);
            }
        }

        step = usecase.getFirstStep();
        if (step != null) {
            if (step.getAltFlow().size() > 0 && step.getAltFlow() != null) {
                for (AltFlow flow : step.getAltFlow()) {
                    visitPrintAltFlow(flow);
                }
            }
            while (step.getNextstep() != null) {
                if (step.getAltFlow().size() > 0 && step.getAltFlow() != null) {
                    for (AltFlow flow : step.getAltFlow()) {
                        visitPrintAltFlow(flow);
                    }
                }
                step = step.getNextstep();
            }
        }
        printEnd();

    }

    public void visitPrintAltFlow(AltFlow flow) {
        indent();
        println("altStep" + ws() + "at" + ws() + flow.getBaseStep().getName());
        inIndent();
        visitPrintDescription(flow.getDescription());
        indent();
        println("when");
        inIndent();
        visitePrintSnapShot(flow.getCondition());
        deIndent();
        indent();
        println(end());
        println();

        Step step = flow.getFirstStep();
        visitPrintStep(step);
        while (step.getNextstep() != null) {
            step = step.getNextstep();
            visitPrintStep(step);
        }

        printEnd();
    }

    public void visitPrintUsecasePrecondition(UsecasePrecondition pre) {
        indent();
        println(keyword("ucPrecondition"));

        visitPrintDescription(pre.getName());
        inIndent();
        if (pre.getSnapshot() != null) {
            visitePrintSnapShot(pre.getSnapshot());
        }
        printEnd();
    }

    public void visitPrintStep(Step step) {
        visitPrintTypeStep(step);
        visitPrintDescription(step.getDescription());

        if (step instanceof ActStep) {
            visitPrintFormStep((ActStep) step);
        } else if (step instanceof UCStep) {
            visitPrintFormStep((UCStep) step);
        } else if (step instanceof RejoinStep) {
            visitPrintFormStep((RejoinStep) step);
        }
        println();
    }

    private void visitPrintFormStep(RejoinStep step) {
        indent();
        println(keyword("when"));
        inIndent();
        visitePrintSnapShot(step.getCondition());

        deIndent();
        indent();
        println(end());
    }

    private void visitPrintFormStep(UCStep step) {
        inIndent();
        indent();

        println(step.getIncludedUC().getName());
        deIndent();
        indent();
        println(end());
    }

    private void visitPrintFormStep(ActStep step) {
        indent();
        println(keyword("from"));
        inIndent();

        visitePrintSnapShot(step.getPreSnapshot());
        deIndent();

        if (step.getPostSnapshot() != null) {
            indent();
            println(keyword("to"));
            inIndent();
            visitePrintSnapShot(step.getPostSnapshot());
            deIndent();
        }

        if (step.getActions().size() != 0) {
            indent();
            println(keyword("actions"));
            inIndent();
            for (int i = 0; i < step.getActions().size(); i++) {
                if (step instanceof ActorStep) {
                    visitPrintAction((ActorAction) step.getActions().get(i));
                } else {
                    visitPrintAction((SystemAction) step.getActions().get(i));
                }
            }
            deIndent();
        }

        indent();
        println(end());

    }

    private void visitPrintAction(ActorAction action) {
        indent();
        String arror = "->";

        print(action.getActor().getName() + ws() + arror + ws());
        visitPrintObjectVar(action.getObjVars());
        println(dotComma());

    }

    private void visitPrintAction(SystemAction action) {
        indent();
        String arror = "&lt;-";

        print(action.getActor().getName() + ws() + arror + ws());
        visitPrintObjectVar(action.getObjVars());
        visitPrintContrain();
        println(dotComma());

    }

    private void visitPrintContrain() {

    }

    private void visitPrintTypeStep(Step step) {
        indent();
        if (step instanceof ActorStep) {
            print(keyword("actStep"));
        } else if (step instanceof SystemStep) {
            print(keyword("sysStep"));
        } else if (step instanceof UCStep) {
            print(keyword("inclStep"));
        }

        if (step instanceof RejoinStep) {
            print(keyword("rejionStep"));
        }

        print(ws() + step.getName());

        if (step instanceof RejoinStep) {
            RejoinStep rejion = (RejoinStep) step;
            print(ws() + ">" + ws() + rejion.getRejoinTo().getName());
        }
        println();
    }

    public void visitPrintUsecasePostcondition(UsecasePostcondition post) {
        indent();
        println(keyword("ucPostcondition"));

        visitPrintDescription(post.getName());
        inIndent();
        if (post.getSnapshot() != null) {
            visitePrintSnapShot(post.getSnapshot());
        }

        printEnd();
    }

    private void visitePrintSnapShot(SnapshotPattern ssp) {
        if (ssp.getName() != null && ssp.getName().length() > 0) {
            indent();
            println(keyword("snapshotID") + ws() + "="  + ws() + ssp.getName());
        }
        visitPrintDescription(ssp.getDescription());

        for (int i = 0; i < ssp.getObjects().size(); i++) {
            visitPrintObjectVar(ssp.getObjects().get(i));
        }
        for (int i = 0; i < ssp.getLinks().size(); i++) {
            visitPringVarLink(ssp.getLinks().get(i));
        }

    }

    private void visitPrintObjectVar(ObjVar obj) {
        indent();
        println(obj.getName() + ws() + doubleDot() + ws() + obj.getType().toString() + dotComma());
    }

    private void visitPrintObjectVar(ArrayList<ObjVar> listObj) {
        int i = listObj.size();
        for (ObjVar obj : listObj) {
            print(obj.getName() + ws() + doubleDot() + ws() + obj.getType().toString());
            i--;
            if (i == 0)
                return;
            print(comma() + ws());
        }
    }

    private void visitPringVarLink(VarLink link) {
        indent();
        println("(" + link.getLhsObjVar().getName() + ws() + comma() + ws() + link.getRhsObjVar().getName() + ")" + ws()
                + doubleDot() + ws() + link.getAssoc().toString() + dotComma());
    }

    private void visitPrintUsecaseAtributte(Usecase usecase) {
        if (usecase.getDescription() != null) {
            visitPrintDescription(usecase.getDescription());
        }

        indent();
        println("primaryActor" + ws() + "=" + ws() + usecase.getPrimaryActor().getName());

        
        if (usecase.getSecondaryActors().size() != 0) {
            indent();
            print("secondaryActors" + ws() + "=" + ws() + "{");
            for (int i = 0; i < usecase.getSecondaryActors().size(); i++) {
                print(usecase.getSecondaryActors().get(i).getName());
                if (i + 1 == usecase.getSecondaryActors().size()) {
                    break;
                }
                print("," + ws());
            }
            println("}");
        }

        println();
    }

    private void visitPrintDescription(String description) {
        if (description != null) {
            indent();
            println(keyword("description") + ws() + "=" + ws() + "\'" + description + "\'");
        }
    }

    private void printEnd() {
        deIndent();
        indent();
        println(end());
        println();
    }

    private void indent() {
        for (int i = 0; i < fIndent; i++) {
            print(ws());
        }
    }

    private void inIndent() {
        fIndent += fIndentStep;
    }

    private void deIndent() {
        if (fIndent < fIndentStep)
            throw new RuntimeException("error indent");
        fIndent -= fIndentStep;
    }

}
