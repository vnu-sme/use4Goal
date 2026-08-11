package org.vnu.sme.frsl.view.FRSL2SSD;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import org.vnu.sme.frsl.mm.FRSLmodel.AltFlow;
import org.vnu.sme.frsl.mm.FRSLmodel.Extend;
import org.vnu.sme.frsl.mm.FRSLmodel.ExtensionPoint;
import org.vnu.sme.frsl.mm.FRSLmodel.FrslModel;
import org.vnu.sme.frsl.mm.FRSLmodel.Step;
import org.vnu.sme.frsl.mm.FRSLmodel.UCStep;
import org.vnu.sme.frsl.mm.FRSLmodel.Usecase;
import org.vnu.sme.frsl.mm.SSDmodel.AltFrame;
import org.vnu.sme.frsl.mm.SSDmodel.MessageUC;

public class SSDProcess {

    protected SequenceUC diagram;
    protected FrslModel model;
    protected Usecase usecase;

    protected ArrayList<Step> listStepOrder;

    protected ArrayList<MessageUC> listMesssage;

    protected ArrayList<AltFrame> listFrames;

    protected Map<String, ArrayList<ExtendPoint>> mapExtend;

    private Stack<Step> stackStep;

    private final Step base = null;

    public SSDProcess(FrslModel model, Usecase usecase, SequenceUC diagram) {
        this.model = model;
        this.usecase = usecase;
        this.diagram = diagram;
        listStepOrder = new ArrayList<>();
        listMesssage = new ArrayList<>();
        listFrames = new ArrayList<>();
        stackStep = new Stack<>();
        mapExtend = new TreeMap<>();
        listStepOrder.addFirst(usecase.getFirstStep());

        ExtensionPoint2MapExtend();

    }

    public void ExtensionPoint2MapExtend() {
        for (Extend ex : model.extend()) {
            if (ex.getfExtendedUC() != this.usecase)
                continue;

            Usecase extend = ex.getfExtendstion();
            Collection<ExtensionPoint> pointMap = ex.getfExtendPoint().values();
            for (ExtensionPoint point : pointMap) {
                for (Step base : point.getStepLocation().values()) {
                    ExtendPoint expoint = new ExtendPoint(point, base, extend);

                    if (mapExtend.containsKey(expoint.getName())) {
                        mapExtend.get(expoint.getName()).add(expoint);
                    } else {
                        ArrayList<ExtendPoint> list = new ArrayList<>();
                        list.add(expoint);
                        mapExtend.put(expoint.getName(), list);
                    }
                }
            }
        }
    }

    public void initmodel() {
        diagram.actorAndUC2LifeLine();

        Step step = listStepOrder.getFirst();
        if (step == null)
            return;

        while (listStepOrder.getLast() != null || stackStep.size() != 0) {

            if (listStepOrder.getLast() == null) {
                listStepOrder.removeLast();
                listStepOrder.addLast(stackStep.pop());
                checkFrame();
                continue;
            }

            step = listStepOrder.getLast();

            if (step.getNextstep() != null) { // neu co cai tiep
                listStepOrder.addLast(step.getNextstep()); // cho vao cuoi
            } else {
                listStepOrder.addLast(base); // cho vao dau cham
            }

            diagram.step2MessageUC(step);

            updateFrame(diagram.getMessageUCLast());

            if (step.getAltFlow().size() > 0 || mapExtend.containsKey(step.getName())) {

                AltFrame altFrame = diagram.initAltFlow(step);
                // altFrame.createBaseFlow(diagram.getMessageUCLast());

                listFrames.addFirst(altFrame);

                if (mapExtend.containsKey(step.getName())) {
                    for (ExtendPoint point : mapExtend.get(step.getName())) {
                        UCStep ucStep = new UCStep(point.point.getName(), point.point.getDescription(), point.ucExtend,
                                new ArrayList<>());
                        stackStep.push(ucStep);
                        altFrame.setNumAlt(1);
                    }
                }

                for (AltFlow flow : step.getAltFlow()) {
                    stackStep.push(flow.getFirstStep());
                    altFrame.setNumAlt(1);
                }

            }
        }

        checkLastFrame();
    }

    private void checkLastFrame() {
        if (listFrames.size() == 0)
            return;
        listFrames.getFirst().updatePosMess();
        listFrames.removeFirst();
    }

    private void checkFrame() {
        if (listFrames.getFirst().checkLength()) {
            listFrames.getFirst().updatePosMess();
            listFrames.removeFirst();
        }
        listFrames.getFirst().addAltFlow();
    }

    public void updateFrame(MessageUC mess) {
        for (AltFrame frame : listFrames) {
            frame.addMess(mess);
        }
    }

    public class ExtendPoint {
        public ExtensionPoint point;
        public Step stepBase;
        public Usecase ucExtend;

        public ExtendPoint(ExtensionPoint p, Step s, Usecase u) {
            point = p;
            stepBase = s;
            ucExtend = u;
        }

        public boolean checkIsContain(Step step) {
            return step == stepBase;
        }

        public String getName() {
            return stepBase.getName();
        }
    }
}
