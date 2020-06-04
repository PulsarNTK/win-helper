package com.pulsarntk.winhelper.feature.hotkeys.gui;

import javax.swing.table.AbstractTableModel;

import com.pulsarntk.winhelper.utils.RegisterHotkey.KEY;
import com.pulsarntk.winhelper.feature.hotkeys.Hotkey;
import com.pulsarntk.winhelper.feature.hotkeys.action.itf.Action;

import java.util.Vector;

public class HotkeyTableModel extends AbstractTableModel {
    private String[] ColumNames = {"Action", "Hotkey", "Ctrl", "Shift", "Alt", "Windows", "Active"};
    private final Vector<Hotkey> data;

    public HotkeyTableModel(Vector<Hotkey> hotkeys) {
        this.data = hotkeys;
    }

    public void add(Hotkey hotkey) {
        data.add(hotkey);
        if (!hotkey.updateHotkey()) {
            // hotkey.action = Action.ERROR;
        }
        fireTableDataChanged();
    }

    public void remove(int i) {
        data.remove(i);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

    @Override
    public int getColumnCount() {
        return ColumNames.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return ColumNames[columnIndex];
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Hotkey h = data.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return h.action;
            case 1:
                return h.key;
            case 2:
                return h.ctrl;
            case 3:
                return h.shift;
            case 4:
                return h.alt;
            case 5:
                return h.windows;
            case 6:
                return h.active;
            default:
                return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Hotkey h = data.get(rowIndex);
        switch (columnIndex) {
            case 0:
                h.action = (Action) aValue;
                break;
            case 1:
                h.key = (KEY) aValue;
                break;
            case 2:
                h.ctrl = (Boolean) aValue;
                break;
            case 3:
                h.shift = (Boolean) aValue;
                break;
            case 4:
                h.alt = (Boolean) aValue;
                break;
            case 5:
                h.windows = (Boolean) aValue;
                break;
            case 6:
                h.active = (Boolean) aValue;
                break;
        }
        if (!h.updateHotkey()) {
            // h.action = ACTION.ERROR;
        }
        fireTableDataChanged();
    }

}
