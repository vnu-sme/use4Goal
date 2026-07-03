package org.vnu.sme.goal.istar.mm;

import java.awt.Color;

/** iStar 2.0 contribution types for Quality links. */
public enum ContribType {
    MAKE, HELP, HURT, BREAK, UNKNOWN, SOME_PLUS, SOME_MINUS;

    public static ContribType from(String s) {
        return switch (s.toLowerCase()) {
            case "make"   -> MAKE;
            case "help"   -> HELP;
            case "hurt"   -> HURT;
            case "break"  -> BREAK;
            case "some+"  -> SOME_PLUS;
            case "some-"  -> SOME_MINUS;
            default       -> UNKNOWN;
        };
    }

    public String label() {
        return switch (this) {
            case MAKE      -> "Make";
            case HELP      -> "Help";
            case HURT      -> "Hurt";
            case BREAK     -> "Break";
            case UNKNOWN   -> "?";
            case SOME_PLUS -> "Some+";
            case SOME_MINUS -> "Some-";
        };
    }

    public Color color() {
        return switch (this) {
            case MAKE      -> new Color(60,  200, 100);
            case HELP      -> new Color(100, 230, 150);
            case HURT      -> new Color(230, 140,  40);
            case BREAK     -> new Color(220,  60,  60);
            case SOME_PLUS -> new Color(80,  200, 130);
            case SOME_MINUS -> new Color(210, 100,  40);
            case UNKNOWN   -> new Color(150, 150, 150);
        };
    }
}
