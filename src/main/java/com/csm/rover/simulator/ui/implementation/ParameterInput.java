package com.csm.rover.simulator.ui.implementation;

import com.csm.rover.simulator.objects.util.ParamMap;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ParameterInput extends JScrollPane {

    private JPanel panel;
    private List<JTextField> fields;

    ParameterInput(){
        textHistory = new HashMap<>();
        setOpaque(false);
        setBackground(Color.LIGHT_GRAY);

        panel = new JPanel();
        panel.setLayout(new MigLayout("", "[fill,grow][fill,grow][fill,grow]", "fill"));
        panel.setOpaque(false);
        this.setViewportView(panel);
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
                    SwingUtilities.invokeLater(() -> checkTextChange(field));
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

    Map<String, Double> getParameters(){
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

    private Map<JTextField, String> textHistory;
    private void checkTextChange(JTextField field){
        String currentText = field.getText();
        String oldText = textHistory.containsKey(field) ? textHistory.get(field) : "";
        if (!currentText.equals("")) {
            try {
                double d = Double.parseDouble(currentText);
                if (d < 0) {
                    throw new NumberFormatException("Cannot be negative");
                }
                if (currentText.contains("f")){
                    throw new NumberFormatException("Ignore float notation");
                }
            }
            catch (NumberFormatException e) {
                field.setText(oldText);
            }
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
