package com.csm.rover.simulator.ui.implementation;

import com.csm.rover.simulator.ui.visual.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.UIManager;

public class UiFactory {
    private static final Logger LOG = LogManager.getLogger(UiFactory.class);

    static {
        setupEnv();
        makeApp();
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

    private static void makeApp(){
        EmbeddedMenuBar menuImpl = new EmbeddedMenuBar();
        application = new Form2(menuImpl);
        menu = menuImpl;
    }

    public static Application getApplication(){
        return application;
    }

    static EmbeddedDesktop getDesktop(){
        return ((Form2)application).desktop;
    }

    public static MainMenu getMainMenu(){
        return menu;
    }

    public static PopUp newPopUp(){
        return new PopUpImpl();
    }

    public static StartupWindow newStartUpWindow(){
        return new StartupWindowImpl();
    }

}
