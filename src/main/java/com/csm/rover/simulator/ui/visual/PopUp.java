package com.csm.rover.simulator.ui.visual;

public interface PopUp {

    enum Options { OK_OPTION, CANCEL_OPTION, YES_OPTION, NO_OPTION };
    enum Buttons { DEFAULT_OPTIONS, OK_CANCEL_OPTIONS, YES_NO_OPTIONS, YES_NO_CANCEL_OPTIONS };

    PopUp setSubject(String title);

    PopUp setMessage(String text);

    Options showConfirmDialog(Buttons btns);

    int showOptionDialog(String[] options);

    String showInputDialog();

}
