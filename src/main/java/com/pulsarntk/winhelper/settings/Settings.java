package com.pulsarntk.winhelper.settings;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;
import com.pulsarntk.winhelper.itf.Feature;

public class Settings extends Setting {
    public final JFrame frame = new JFrame("Settings");
    public final JPanel topPanel = new JPanel(new GridLayout(0, 1));
    public final SystemTray tray = SystemTray.getSystemTray();
    public final PopupMenu trayPopup = new PopupMenu();
    public ArrayList<Feature> fList;

    public Settings(ArrayList<Feature> fList) {
        super("Settings");
        this.fList = fList;

        frame.setContentPane(topPanel);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        for (Feature feature : fList) {
            add(feature, newSettings(feature.getName()));
        }
        frame.pack();
        frame.setResizable(false);
        if (!addSystemTray())
            System.out.println("TrayIcon could not be added.");


    }

    private boolean addSystemTray() {
        if (SystemTray.isSupported()) {
            TrayIcon trayIcon;
            try {
                trayIcon = new TrayIcon(ImageIO.read(new File("interface.png")));
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            trayIcon.setImageAutoSize(true);
            trayIcon.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    frame.setVisible(!frame.isVisible());
                }

            });

            MenuItem settingsItem = new MenuItem("Settings");
            MenuItem exitItem = new MenuItem("Exit");
            settingsItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    frame.setVisible(true);
                }
            });
            exitItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });

            trayPopup.addSeparator();
            trayPopup.add(settingsItem);
            trayPopup.add(exitItem);
            trayIcon.setPopupMenu(trayPopup);

            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    private void add(Feature feature, Setting setting) {
        final GridBagLayout layout = new GridBagLayout();
        final GridBagConstraints c = new GridBagConstraints();
        final JPanel panel = new JPanel(layout);
        final Icon icon = new ImageIcon("interface.png");

        final JCheckBox checkBox = new JCheckBox();
        final JLabel label = new JLabel(feature.getName());
        final JButton button = new JButton(icon);
        final boolean enabled = setting.optBoolean("enabled");
        CheckboxMenuItem trayItem = new CheckboxMenuItem(feature.getName());

        ItemListener itemListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                boolean isEnabled = e.getStateChange() == ItemEvent.SELECTED;
                if (isEnabled) {
                    feature.enable();
                } else {
                    feature.disable();
                }
                trayItem.setState(isEnabled);
                checkBox.setSelected(isEnabled);
                button.setEnabled(isEnabled);
                setting.put("enabled", isEnabled);
            }
        };


        trayItem.setState(enabled);
        trayItem.addItemListener(itemListener);
        trayPopup.add(trayItem);

        if (enabled) {
            feature.enable();
        } else {
            feature.disable();
        }


        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        button.setEnabled(enabled);
        checkBox.setSelected(enabled);
        label.setFont(new Font("Consolas", Font.PLAIN, 18));
        label.setToolTipText(feature.getDescription());

        checkBox.addItemListener(itemListener);
        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                feature.getOptionsDialog().setVisible(true);
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
