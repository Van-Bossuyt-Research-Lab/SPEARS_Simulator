package com.csm.rover.simulator.ui.implementation;

import com.csm.rover.simulator.ui.visual.Application;
import com.csm.rover.simulator.ui.visual.MainMenu;
import com.csm.rover.simulator.ui.visual.PopUp;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.UIManager;

public class UiFactory {
    private static final Logger LOG = LogManager.getLogger(UiFactory.class);

    static {
        setupEnv();
        application = new Form2();
        menu = new EmbeddedMenuBar();
    }

    private static Application application;
    private static MainMenu menu;

    private UiFactory() {}

    private static void setupEnv(){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            LOG.log(Level.WARN, "UI Look and Feel failed to be set to: " + UIManager.getSystemLookAndFeelClassName(), e);
        }
    }

    public static Application getApplication(){
        return application;
    }

    public static MainMenu getMainMenu(){
        return menu;
    }

    public static PopUp newPopUp(){
        return new PopUpImpl();
    }

}
