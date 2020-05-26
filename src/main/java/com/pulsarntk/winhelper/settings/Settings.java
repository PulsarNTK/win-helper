package com.pulsarntk.winhelper.settings;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import com.pulsarntk.winhelper.itf.Feature;

public class Settings extends JFrame {
    JPanel topPanel = new JPanel(new GridLayout(0, 1));

    public Settings(ArrayList<Feature> fList) {
        super("Settings");
        setContentPane(topPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        for (Feature feature : fList) {
            x(feature);
        }
        pack();
        setResizable(false);
        // setVisible(true);
    }

    private void x(Feature feature) {
        final Feature f = feature;
        final GridBagLayout layout = new GridBagLayout();
        final GridBagConstraints c = new GridBagConstraints();
        final JPanel panel = new JPanel(layout);
        final Icon icon = new ImageIcon("C:\\Users\\Pulsar\\Desktop\\interface.png");

        final JCheckBox checkBox = new JCheckBox();
        final JLabel label = new JLabel(feature.getName());
        final JButton button = new JButton(icon);


        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        button.setEnabled(false);

        label.setFont(new Font("Consolas", Font.PLAIN, 18));
        label.setToolTipText(feature.getDescription());

        checkBox.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                button.setEnabled(checkBox.isSelected());
            }
        });

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(4, 8, 4, 36);
        panel.add(label, c);

        c.gridx = 1;
        c.weightx = 0;
        c.insets = new Insets(4, 4, 4, 4);
        panel.add(button, c);

        c.gridx = 2;
        panel.add(checkBox, c);

        topPanel.add(panel);
    }
}
