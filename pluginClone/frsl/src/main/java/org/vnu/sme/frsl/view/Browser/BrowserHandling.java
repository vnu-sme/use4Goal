package org.vnu.sme.frsl.view.Browser;

import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;

import org.vnu.sme.frsl.mm.FRSLmodel.UseType;

public class BrowserHandling implements MouseListener {
    
    private Browser browser;

    private Rectangle fRectangle;

    private UseType useType;
    private Map<UseType, Boolean> fHighlightElements;


    public BrowserHandling(Browser browser) {
        this.browser = browser;
        fHighlightElements = new HashMap<UseType, Boolean>();

    }

    public void setSelectedNodeRectangle( Rectangle rec ) {
        fRectangle = rec;
    }

    public void setActor(UseType useType) {
        this.useType = useType;
    }

    private void tryToFireStateChangeEvent( MouseEvent e ) {
        if ( e.getModifiersEx() == InputEvent.BUTTON2_DOWN_MASK ) {
            if ( fRectangle != null && fRectangle.contains( e.getPoint() ) ) {
                boolean highlight = false;
                System.out.println("sout put mouse");
                if ( fHighlightElements.containsKey( useType ) ) {
                    highlight = 
                        fHighlightElements.get( useType ).booleanValue();
                    if ( highlight ) {
                        highlight = false;
                    } else {
                        highlight = true;
                    }
                    fHighlightElements.put( useType, Boolean.valueOf( highlight ) );
                } else {
                    highlight = true;
                    fHighlightElements.put( useType, Boolean.valueOf( highlight ) );
                }
                browser.fireStateChanged( useType, highlight );
            }
        }
    }

    private boolean maybeShowPopup(MouseEvent e) {
        return false;
    }

    public void mousePressed(MouseEvent e) {
        maybeShowPopup(e);
    }

    public void mouseClicked(MouseEvent e) {
        tryToFireStateChangeEvent( e );
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
        maybeShowPopup(e);
    }
}
