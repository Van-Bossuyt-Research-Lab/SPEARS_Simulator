package com.csm.rover.simulator.ui.implementation;

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
