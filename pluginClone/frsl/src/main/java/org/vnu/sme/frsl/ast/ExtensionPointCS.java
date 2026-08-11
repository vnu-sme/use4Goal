package org.vnu.sme.frsl.ast;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import org.antlr.runtime.Token;
import org.vnu.sme.frsl.mm.FRSLmodel.ExtensionPoint;
import org.vnu.sme.frsl.mm.FRSLmodel.SnapshotPattern;
import org.vnu.sme.frsl.mm.FRSLmodel.Step;
import org.vnu.sme.frsl.parser.Context;

public class ExtensionPointCS {
    /*
     * ----------------------------------------
     * 
     * ExtensionPointCS Properties.
     * 
     * ----------------------------------------
     */
    private Token fName;
    private ArrayList<Token> fLocationCS;
    private String description;
    private SnapshotPatternCS conditionCS;

    /*
     * ----------------------------------------
     * 
     * ExtensionPointCS Getters.
     * 
     * ----------------------------------------
     */
    public Token getfName() {
        return fName;
    }

    public ArrayList<Token> getfLocationCS() {
        return fLocationCS;
    }

    public String getDescription() {
        return description;
    }

    public SnapshotPatternCS getConditionCS() {
        return conditionCS;
    }

    /*
     * ----------------------------------------
     * 
     * ExtensionPointCS Setters.
     * 
     * ----------------------------------------
     */
    public void setfName(Token fName) {
        this.fName = fName;
    }

    public void setfLocationCS(ArrayList<Token> fLocationCS) {
        this.fLocationCS = fLocationCS;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setConditionCS(SnapshotPatternCS conditionCS) {
        this.conditionCS = conditionCS;
    }

    /*
     * ----------------------------------------
     * 
     * ExtensionPointCS Adders/Removers.
     * 
     * ----------------------------------------
     */
    public void addfLocationCS(Token fLocationCS) {
        if (this.fLocationCS == null) {
            this.fLocationCS = new ArrayList<>();
        }
        this.fLocationCS.add(fLocationCS);
    }

    /*
     * ----------------------------------------
     * 
     * ExtensionPointCS Constructors.
     * 
     * ----------------------------------------
     */
    public ExtensionPointCS() {
        this.fLocationCS = new ArrayList<Token>();
    }

    /*
     * ----------------------------------------
     * 
     * ExtensionPoint Generator.
     * 
     * ----------------------------------------
     */
    public ExtensionPoint visitPreOrder(Context ctx, String usename) {
        String name = fName.getText();
        SnapshotPattern condition = this.conditionCS.visitPreOrder(ctx);
        Map<String, Step> stepLocation = new TreeMap<String, Step>();
        for (Token stepCs: fLocationCS) {
            Step step = ctx.typeTableLookup(stepCs.getText() + usename, Step.class);
            stepLocation.put(step.getName(), step);
        }
        ExtensionPoint ext = ctx.modelFactory().createExtensionPoint(name, description, stepLocation, condition);

        ctx.typeTableAdd(fName, ext);
        return ext;
    }
}
