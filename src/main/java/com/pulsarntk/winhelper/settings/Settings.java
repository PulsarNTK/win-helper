package com.pulsarntk.winhelper.settings;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import com.pulsarntk.winhelper.itf.Feature;

public class Settings extends Setting {
    public JFrame frame = new JFrame("Settings");
    public JPanel topPanel = new JPanel(new GridLayout(0, 1));

    public Settings(ArrayList<Feature> fList) {
        super("Settings");
        frame.setContentPane(topPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        for (Feature feature : fList) {
            add(feature, newSettings(feature.getName()));
        }
        frame.pack();
        frame.setResizable(false);
    }

    private void add(Feature feature, Setting setting) {
        final GridBagLayout layout = new GridBagLayout();
        final GridBagConstraints c = new GridBagConstraints();
        final JPanel panel = new JPanel(layout);
        final Icon icon = new ImageIcon("C:\\Users\\Pulsar\\Desktop\\interface.png");

        final JCheckBox checkBox = new JCheckBox();
        final JLabel label = new JLabel(feature.getName());
        final JButton button = new JButton(icon);
        final boolean enabled = setting.optBoolean("enabled");


        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        button.setEnabled(enabled);
        checkBox.setSelected(enabled);

        label.setFont(new Font("Consolas", Font.PLAIN, 18));
        label.setToolTipText(feature.getDescription());

        checkBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                button.setEnabled(checkBox.isSelected());
                setting.put("enabled", checkBox.isSelected());
                if (checkBox.isSelected())
                    feature.enable();
                else if (!checkBox.isSelected())
                    feature.disable();

            }
        });

        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                feature.getSettingsFrame().setVisible(true);
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
