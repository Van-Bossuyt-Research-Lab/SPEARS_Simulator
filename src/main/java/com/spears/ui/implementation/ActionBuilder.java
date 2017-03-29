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

package com.spears.ui.implementation;

import javax.swing.Action;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.TreeMap;

public class ActionBuilder {

    static Action newAction(Runnable action){
        return new Action() {
            private boolean enabled = true;
            private Map<String, Object> values = new TreeMap<>();

            @Override
            public Object getValue(String key) {
                if (values.containsKey(key)){
                    return values.get(key);
                }
                else {
                    return null;
                }
            }

            @Override
            public void putValue(String key, Object value) {
                values.put(key, value);
            }

            @Override
            public void setEnabled(boolean b) {
                enabled = b;
            }

            @Override
            public boolean isEnabled() {
                return enabled;
            }

            @Override
            public void addPropertyChangeListener(PropertyChangeListener listener) {
                throw new IllegalAccessError("Cannot use property listeners with this action");
            }

            @Override
            public void removePropertyChangeListener(PropertyChangeListener listener) {
                throw new IllegalAccessError("Cannot use property listeners with this action");
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                if (enabled){
                    action.run();
                }
            }
        };
    }

}
