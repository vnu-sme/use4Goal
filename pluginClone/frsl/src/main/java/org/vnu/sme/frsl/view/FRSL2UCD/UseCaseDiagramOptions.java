package org.vnu.sme.frsl.view.FRSL2UCD;

import org.tzi.use.gui.views.diagrams.DiagramOptions;

public class UseCaseDiagramOptions extends DiagramOptions {

    protected boolean fShowBrowser = false;

    public UseCaseDiagramOptions() {
        
    }
    public boolean isShowBrowser() {
        return fShowBrowser;
    }

    public void setShowBrowser( boolean show ) {
        fShowBrowser = show;
        onOptionChanged("SHOWMODELBROWSER");
    }
    @Override
    protected void registerAdditionalColors() {
       // No override
    }

    public boolean isShowMutliplicities() {
        return fShowMutliplicities;
    }

    public void setShowMutliplicities( boolean showMutliplicities ) {
        fShowMutliplicities = showMutliplicities;
    }
}
