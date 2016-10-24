package com.csm.rover.simulator.ui.implementation;

import java.awt.Font;

class FontFunctions {

    static Font bold(Font font){
        return new Font(font.getName(), Font.BOLD, font.getSize());
    }

    static Font italic(Font font){
        return new Font(font.getName(), Font.ITALIC, font.getSize());
    }

    static Font bigger(Font font, int n){
        return new Font(font.getName(), font.getStyle(), font.getSize()+n);
    }

    static Font bigger(Font font){
        return bigger(font, 2);
    }

    static Font smaller(Font font, int n){
        return new Font(font.getName(), font.getStyle(), font.getSize()-n);
    }

    static Font smaller(Font font){
        return smaller(font, 2);
    }

}
