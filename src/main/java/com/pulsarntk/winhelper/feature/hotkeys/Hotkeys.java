package com.pulsarntk.winhelper.feature.hotkeys;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import com.pulsarntk.winhelper.feature.hotkeys.action.Actions;
import com.pulsarntk.winhelper.feature.hotkeys.action.itf.Action;
import com.pulsarntk.winhelper.itf.Feature;
import com.pulsarntk.winhelper.settings.Setting;
import com.pulsarntk.winhelper.utils.RegisterHotkey.KEY;
import com.pulsarntk.winhelper.feature.hotkeys.gui.HotkeyAddDialog;
import com.pulsarntk.winhelper.feature.hotkeys.gui.HotkeyTableModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

public class Hotkeys implements Feature {
    Settings settings = new Settings("Hotkeys");
    Actions actions = new Actions();

    class Settings extends Setting {
        public JDialog frame = new JDialog((JFrame) null);
        public JScrollPane hotkeysPanel;
        public JPanel buttonPanel = new JPanel();
        public final Vector<Hotkey> hotkeys = new Vector<Hotkey>();
        public final HotkeyAddDialog hotkeyAddDialog;
        public final HotkeyTableModel tableModel;
        public final JTable table;

        public Settings(String label) {
            super("Hotkeys");
            tableModel = new HotkeyTableModel(hotkeys);
            hotkeyAddDialog = new HotkeyAddDialog(tableModel);
            readFromJson();
            table = new JTable(tableModel);
            hotkeysPanel = new JScrollPane(table);
            table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.setFillsViewportHeight(true);
            hotkeysPanel.setPreferredSize(new Dimension(0, 360 - 72));
            table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JComboBox<>(Actions.getActionsSorted().values().toArray())));
            table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JComboBox<>(KEY.getDefinedKeys())));
            table.getColumnModel().getColumn(0).setPreferredWidth(192);
            table.getColumnModel().getColumn(1).setPreferredWidth(128);

            JButton addButton = new JButton("Add");
            JButton removeButton = new JButton("Remove");
            buttonPanel.add(addButton);
            buttonPanel.add(removeButton);
            addButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    hotkeyAddDialog.setVisible(true);
                }
            });
            removeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int i = table.getSelectedRow();
                    if (i == -1 || i >= table.getRowCount())
                        return;
                    table.selectAll();
                    tableModel.remove(i);
                    table.repaint();
                    table.changeSelection(i - 1, 0, false, false);
                    writeToJson();
                }
            });

            tableModel.addTableModelListener(new TableModelListener() {
                @Override
                public void tableChanged(TableModelEvent e) {
                    writeToJson();
                }
            });

            frame.getContentPane().add(hotkeysPanel, BorderLayout.NORTH);
            frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
            frame.setPreferredSize(new Dimension(640, 360));
            frame.setResizable(false);
            frame.pack();
        }

        public void writeToJson() {
            JSONArray temp = new JSONArray();
            for (Hotkey hotkey : hotkeys) {
                temp.put(new JSONObject(hotkey));
            }
            put("Hotkey Vector", temp);
        }

        public void readFromJson() {
            JSONArray jsonArray = optJSONArray("Hotkey Vector");
            if (jsonArray == null)
                return;
            for (Object object : jsonArray) {
                try {
                    tableModel.add(new Hotkey(Actions.getActionOrDefault(((JSONObject) object).optString("action")), KEY.valueOf(((JSONObject) object).optString("key")), ((JSONObject) object).optBoolean("ctrl"),
                            ((JSONObject) object).optBoolean("shift"), ((JSONObject) object).optBoolean("alt"), ((JSONObject) object).optBoolean("windows"), ((JSONObject) object).optBoolean("active")));
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

    }

    public List<Hotkey> getActiveHotkeys() {
        return settings.hotkeys.stream().filter(h -> h.active).collect(Collectors.toList());
    }

    @Override
    public JDialog getOptionsDialog() {
        return settings.frame;
    }

    @Override
    public String getName() {
        return "Hotkeys";
    }

    @Override
    public String getDescription() {
        return "Hotkeys";
    }

    @Override
    public void enable() {
        for (Hotkey hotkey : getActiveHotkeys()) {
            hotkey.register();
        }
    }

    @Override
    public void disable() {
        for (Hotkey hotkey : getActiveHotkeys()) {
            hotkey.unRegister();
        }
    }

    @Override
    public void readFromJson() {

    }

    @Override
    public void writeToJson() {

    }

}
