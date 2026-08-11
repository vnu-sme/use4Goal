package org.vnu.sme.frsl.mm.SSDmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SsdModel {
    
    private Map<String,LifeLineUC> allLifeLine;

    private Map<Integer,MessageUC> allMessageUCs;

    private int numMess;

    private ArrayList<FrameUC> allFrame;

    public SsdModel(){
        allMessageUCs = new HashMap<Integer, MessageUC>();
        allLifeLine = new HashMap<String, LifeLineUC>();
        allFrame = new ArrayList<FrameUC>();
        numMess = 0;
    }

    public void addLifeLine(LifeLineUC lifeLine) {
        allLifeLine.put(lifeLine.getLifeName(),lifeLine);
    }

    public void addMessage(MessageUC mess) {
        allMessageUCs.put(numMess, mess);
        numMess++;
    }

    public void addFrame(FrameUC frame) {
        allFrame.add(frame);
    }

    public ArrayList<FrameUC> getAllFrame() {
        return allFrame;
    }


    public Map<String, LifeLineUC> getAllLifeLine() {
        return allLifeLine;
    }

    public Map<Integer, MessageUC> getAllMessage() {
        return allMessageUCs;
    }

    public LifeLineUC getLifeLineUC(String name) {
        return allLifeLine.get(name);
    }

    public MessageUC getMessageUC(Integer num) {
        return allMessageUCs.get(num);
    }

    public MessageUC getMessageUCLast() {
        return allMessageUCs.get(numMess - 1);
    }

    public int getNumMess() {
        return numMess;
    }
}
