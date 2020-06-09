package com.pulsarntk.winhelper.feature.hotkeys.gui;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import com.pulsarntk.winhelper.feature.hotkeys.Hotkey;
import com.pulsarntk.winhelper.feature.hotkeys.action.Actions;
import com.pulsarntk.winhelper.feature.hotkeys.action.handler.CustomActionHandler;
import com.pulsarntk.winhelper.feature.hotkeys.action.itf.Action;
import com.pulsarntk.winhelper.feature.hotkeys.action.itf.CustomAction;
import com.pulsarntk.winhelper.utils.RegisterHotkey.KEY;
import java.awt.Color;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class HotkeyAddDialog extends JDialog {
    public JPanel panel = new JPanel();
    public JComboBox<KEY> keyComboBox = new JComboBox<KEY>(KEY.getDefinedKeys());
    public JComboBox<Action> actionComboBox = new JComboBox<Action>();
    public JButton addActionButton = new JButton("+");
    public JButton removeActionButton = new JButton("-");
    public JButton editActionButton = new JButton("*");
    public JCheckBox ctrlCheckBox = new JCheckBox("Ctrl");
    public JCheckBox shiftCheckBox = new JCheckBox("Shift");
    public JCheckBox altCheckBox = new JCheckBox("Alt");
    public JCheckBox windowsCheckBox = new JCheckBox("Windows");
    public JCheckBox activeCheckBox = new JCheckBox("Active");
    public JButton addButton = new JButton("Add");
    public JButton cancelButton = new JButton("Cancel");
    private ItemListener comboBoxInvalidInputListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            // if (e.getStateChange() == ItemEvent.SELECTED) {
            // if (((Enum) e.getItem()).ordinal() == 0) {
            // ((JComboBox) e.getSource()).setBackground(new Color(128, 8, 8));
            // } else {
            // ((JComboBox) e.getSource()).setBackground(UIManager.getColor("JComboBox.background"));
            // }
            // }
        }
    };
    public HotkeyTableModel tableModel;

    public HotkeyAddDialog(HotkeyTableModel tableModel) {
        super((JFrame) null, "Hotkey Create", true);
        updateActions();
        // ActionHandler.customActionHandler.createAction("com.pulsarntk.winhelper.feature.hotkeys.actions.customaction.ScrollDesktop");
        this.tableModel = tableModel;
        panel.add(actionComboBox);
        panel.add(addActionButton);
        panel.add(removeActionButton);
        panel.add(editActionButton);
        panel.add(keyComboBox);
        panel.add(ctrlCheckBox);
        panel.add(shiftCheckBox);
        panel.add(altCheckBox);
        panel.add(windowsCheckBox);
        panel.add(activeCheckBox);
        panel.add(addButton);
        panel.add(cancelButton);
        activeCheckBox.setSelected(true);
        getContentPane().add(panel);
        setResizable(false);
        pack();

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // if ((Action) actionComboBox.getSelectedItem() == Action.ACTION || (KEY) keyComboBox.getSelectedItem() == KEY.VK_KEY)
                // {
                // if ((ACTION) actionComboBox.getSelectedItem() == ACTION.ACTION)
                // actionComboBox.setBackground(new Color(128, 8, 8));
                // if ((KEY) keyComboBox.getSelectedItem() == KEY.VK_KEY)
                // keyComboBox.setBackground(new Color(128, 8, 8));
                // return;
                // }
                setVisible(false);
                tableModel.add(new Hotkey((Action) actionComboBox.getSelectedItem(), (KEY) keyComboBox.getSelectedItem(), ctrlCheckBox.isSelected(), shiftCheckBox.isSelected(), altCheckBox.isSelected(), windowsCheckBox.isSelected(),
                        activeCheckBox.isSelected()));
                setToDefaultParam();
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                setToDefaultParam();
            }
        });
        addActionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String className = (String) JOptionPane.showInputDialog(null, "Select Custom Action", "Create Custom Action", JOptionPane.PLAIN_MESSAGE, null, CustomActionHandler.customActionsClass.keySet().toArray(), null);
                String name = "";
                while (name.equals("")) {
                    name = (String) JOptionPane.showInputDialog(null, "Enter Custom Action Name", "Create Custom Action", JOptionPane.PLAIN_MESSAGE, null, null, null);
                }
                Actions.customActionHandler.createInstanceFromClassName(className, name, true);
                updateActions();
            }
        });
        removeActionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object s = actionComboBox.getSelectedItem();
                if (((Action) s).isCustom()) {
                    Actions.customActionHandler.removeCustomAction((Action) s);
                    actionComboBox.removeItem(s);
                }
            }
        });
        editActionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object s = actionComboBox.getSelectedItem();
                if (((Action) s).isCustom()) {
                    ((CustomAction) s).getSettingDialog().setVisible(true);
                }
            }
        });
        actionComboBox.addItemListener(comboBoxInvalidInputListener);
        keyComboBox.addItemListener(comboBoxInvalidInputListener);
    }

    private void updateActions() {
        actionComboBox.removeAllItems();
        for (Action action : Actions.getActionsSorted().values()) {
            actionComboBox.addItem(action);
        }
    }

    private void setToDefaultParam() {
        actionComboBox.setSelectedIndex(0);
        keyComboBox.setSelectedIndex(0);
        ctrlCheckBox.setSelected(false);
        shiftCheckBox.setSelected(false);
        altCheckBox.setSelected(false);
        windowsCheckBox.setSelected(false);
        activeCheckBox.setSelected(true);
    }
}
