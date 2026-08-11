package org.vnu.sme.frsl.ast;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import org.antlr.runtime.Token;
import org.vnu.sme.frsl.mm.FRSLmodel.Extend;
import org.vnu.sme.frsl.mm.FRSLmodel.ExtensionPoint;
import org.vnu.sme.frsl.mm.FRSLmodel.Usecase;
import org.vnu.sme.frsl.parser.Context;

public class ExtendCS { 
    /*
     * ----------------------------------------
     * 
     * ExtendCS Properties.
     * 
     * ----------------------------------------
     */
    private Token fExtensionCS;
    private Token fExtendedUCCS;
    private ArrayList<Token> fExtPointsCS;

    /*
     * ----------------------------------------
     * 
     * ExtendCS Getters.
     * 
     * ----------------------------------------
     */
    public Token getfExtensionCS() {
        return fExtensionCS;
    }

    public Token getfExtendedUCCS() {
        return fExtendedUCCS;
    }

    public ArrayList<Token> getfExtPointsCS() {
        return fExtPointsCS;
    }

    /*
     * ----------------------------------------
     * 
     * ExtendCS Setters.
     * 
     * ----------------------------------------
     */
    public void setfExtensionCS(Token fExtensionCS) {
        this.fExtensionCS = fExtensionCS;
    }

    public void setfExtendedUCCS(Token fExtendedUCCS) {
        this.fExtendedUCCS = fExtendedUCCS;
    }

    public void setfExtPointsCS(ArrayList<Token> fExtPointsCS) {
        this.fExtPointsCS = fExtPointsCS;
    }

    /*
     * ----------------------------------------
     * 
     * ExtendCS Adders/Removers.
     * 
     * ----------------------------------------
     */    
    public void addfExtPointCS(Token fExtPointCS) {
        if (this.fExtPointsCS == null) {
            this.fExtPointsCS = new ArrayList<Token>();
        }
        
        this.fExtPointsCS.add(fExtPointCS);
    }

    /*
     * ----------------------------------------
     * 
     * ExtendCS Constructors.
     * 
     * ----------------------------------------
     */
    public ExtendCS() {
        this.fExtPointsCS = new ArrayList<Token>();
    }

    public ExtendCS(Token fExtensionCS) {
        this.fExtensionCS = fExtensionCS;
        this.fExtPointsCS = new ArrayList<Token>();
    }

    /*
     * ----------------------------------------
     * 
     * Extend Generator.
     * 
     * ----------------------------------------
     */
    public Extend visitPreOrder(Context ctx) {
        Usecase extendcs = ctx.typeTableLookup(fExtensionCS.getText(), Usecase.class);
        Usecase extendcsUC = ctx.typeTableLookup(fExtendedUCCS.getText(), Usecase.class);

        Map<String, ExtensionPoint> extendPoint = new TreeMap<String, ExtensionPoint>();
        for (Token extendCS : fExtPointsCS) {
            ExtensionPoint extPoint = ctx.typeTableLookup(extendCS.getText(), ExtensionPoint.class);
            extendPoint.put(extPoint.getName(), extPoint);
        }

        Extend extend = ctx.modelFactory().createExtend(extendcs, extendcsUC, extendPoint);

        // ctx.typeTableAdd(fExtendedUCCS, extend);
        return extend;
    }
}
