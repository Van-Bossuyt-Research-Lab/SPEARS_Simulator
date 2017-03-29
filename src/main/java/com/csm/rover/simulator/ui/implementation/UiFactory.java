/*
 * SPEARS: Simulated Physics and Environment for Autonomous Risk Studies
 * Copyright (C) 2017  Colorado School of Mines
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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

    public static AcceleratedView newAcceleratedView(){
        return new AcceleratedViewImpl();
    }

}
