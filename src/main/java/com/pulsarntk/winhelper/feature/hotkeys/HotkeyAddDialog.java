package com.pulsarntk.winhelper.feature.hotkeys;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import com.pulsarntk.winhelper.utils.RegisterHotkey.KEY;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

final class HotkeyAddDialog extends JDialog {
    public JPanel panel = new JPanel();
    public JComboBox<Hotkey.ACTION> actionComboBox = new JComboBox<Hotkey.ACTION>(Hotkey.ACTION.values());
    public JComboBox<KEY> keyComboBox = new JComboBox<KEY>(KEY.getDefinedKeys());
    public JCheckBox ctrlCheckBox = new JCheckBox("Ctrl");
    public JCheckBox shiftCheckBox = new JCheckBox("Shift");
    public JCheckBox altCheckBox = new JCheckBox("Alt");
    public JCheckBox windowsCheckBox = new JCheckBox("Windows");
    public JButton addButton = new JButton("Add");
    public JButton cancelButton = new JButton("Cancel");
    public HotkeyTableModel tableModel;

    public HotkeyAddDialog(HotkeyTableModel tableModel) {
        super();
        this.tableModel = tableModel;
        panel.add(keyComboBox);
        panel.add(actionComboBox);
        panel.add(ctrlCheckBox);
        panel.add(shiftCheckBox);
        panel.add(altCheckBox);
        panel.add(windowsCheckBox);
        panel.add(addButton);
        panel.add(cancelButton);
        getContentPane().add(panel);
        setResizable(false);
        pack();

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                tableModel
                        .add(new Hotkey((Hotkey.ACTION) actionComboBox.getSelectedItem(), (KEY) keyComboBox.getSelectedItem(), ctrlCheckBox.isSelected(), shiftCheckBox.isSelected(), altCheckBox.isSelected(), windowsCheckBox.isSelected(), true));
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
    }

    private void setToDefaultParam() {
        actionComboBox.setSelectedIndex(0);
        keyComboBox.setSelectedIndex(0);
        ctrlCheckBox.setSelected(false);
        shiftCheckBox.setSelected(false);
        altCheckBox.setSelected(false);
        windowsCheckBox.setSelected(false);
    }
}
