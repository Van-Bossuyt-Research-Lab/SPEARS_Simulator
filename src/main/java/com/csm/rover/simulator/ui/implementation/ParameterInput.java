package com.csm.rover.simulator.ui.implementation;

import com.csm.rover.simulator.objects.util.ParamMap;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Color;
import java.awt.Component;
import java.util.*;

class ParameterInput extends JScrollPane {

    private Set<InputChangeListener> listeners;

    private JPanel panel;
    private List<JTextField> fields;

    ParameterInput(){
        listeners = new HashSet<>();
        textHistory = new HashMap<>();
        fields = Collections.emptyList();
        setOpaque(false);
        setBackground(Color.LIGHT_GRAY);

        panel = new JPanel();
        panel.setLayout(new MigLayout("", "[fill,grow][fill,grow][fill,grow]", "fill"));
        panel.setOpaque(false);
        this.setViewportView(panel);
    }

    void addInputChangeListener(InputChangeListener listen){
        listeners.add(listen);
    }

    private void fireInputChangeEvent(InputChangeEvent e){
        for (InputChangeListener listener : listeners){
            listener.parametersChanged(e);
        }
    }

    void setOptions(List<String> options){
        panel.removeAll();
        fields = new ArrayList<>();
        for (int i = 0; i < options.size(); i++){
            JLabel label = new JLabel(options.get(i)+":");
            label.setHorizontalAlignment(SwingConstants.RIGHT);
            panel.add(label, "cell 0 "+i);

            JTextField field = new JTextField();
            field.setName(options.get(i));
            field.setHorizontalAlignment(SwingConstants.CENTER);
            field.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void changedUpdate(DocumentEvent e) {
                    if (!correcting){
                        SwingUtilities.invokeLater(() -> checkTextChange(field));
                    }
                }

                @Override
                public void insertUpdate(DocumentEvent e) {
                    changedUpdate(e);
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    changedUpdate(e);
                }
            });
            fields.add(field);
            panel.add(field, "cell 1 "+i+" 2 1");
        }
        forceRepaint();
    }

    Map<String, Double> getParameters() throws NumberFormatException {
        ParamMap.Builder map = ParamMap.newParamMap();
        for (JTextField field : fields){
            map.addParameter(field.getName(), Double.parseDouble(field.getText()));
        }
        return map.build();
    }

    void fillInFields(Map<String, Double> params){
        for (JTextField field : fields){
            if (params.containsKey(field.getName())){
                field.setText(params.get(field.getName())+"");
            }
        }
    }

    boolean isComplete(){
        if (this.fields.isEmpty()){
            return true;
        }
        try {
            getParameters();
            return true;
        }
        catch (NumberFormatException e){
            return false;
        }
    }

    private Map<JTextField, String> textHistory;
    private boolean correcting = false;
    private void checkTextChange(JTextField field){
        String currentText = field.getText();
        String oldText = textHistory.containsKey(field) ? textHistory.get(field) : "";
        if (!currentText.equals("")) {
            double d;
            try {
                d = Double.parseDouble(currentText);
                if (d < 0) {
                    throw new NumberFormatException("Cannot be negative");
                }
                if (currentText.contains("f") || currentText.contains("d")){
                    throw new NumberFormatException("Ignore float notation");
                }
                if (currentText.contains(" ")){
                    throw new NumberFormatException("No Spaces Allowed");
                }
            }
            catch (NumberFormatException e) {
                correcting = true;
                field.setText(oldText);
                correcting = false;
                return;
            }
            fireInputChangeEvent(new InputChangeEvent(this, field.getName(), d));
        }
        else {
            fireInputChangeEvent(new InputChangeEvent(this, field.getName(), -1));
        }
        textHistory.put(field, field.getText());
    }

    private void forceRepaint(){
        Component parent = this.getParent();
        while (parent.getParent() != null){
            parent = parent.getParent();
        }
        parent.setSize(parent.getWidth()+1, parent.getHeight()+1);
        parent.setSize(parent.getWidth()-1, parent.getHeight()-1);
        repaint();
    }

}

interface InputChangeListener {
    void parametersChanged(InputChangeEvent e);
}

class InputChangeEvent {
    private final ParameterInput origin;
    private final String fieldChanged;
    private final double newValue;

    InputChangeEvent(ParameterInput origin, String fieldChanged, double newValue){
        this.origin = origin;
        this.fieldChanged = fieldChanged;
        this.newValue = newValue;
    }

    ParameterInput getOrigin(){
        return origin;
    }

    String getFieldChanged(){
        return fieldChanged;
    }

    double getNewValue(){
        return newValue;
    }
}
