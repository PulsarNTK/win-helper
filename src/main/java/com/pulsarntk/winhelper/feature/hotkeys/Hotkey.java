package com.pulsarntk.winhelper.feature.hotkeys;

import com.pulsarntk.winhelper.feature.hotkeys.action.itf.Action;
import com.pulsarntk.winhelper.utils.RegisterHotkey;

public class Hotkey extends RegisterHotkey {
    public Action action;
    public KEY key;
    public boolean ctrl;
    public boolean shift;
    public boolean alt;
    public boolean windows;
    public boolean active;

    public Hotkey(Action action, KEY key, boolean ctrl, boolean shift, boolean alt, boolean windows, boolean active) {
        super(key.code, modifiersToInt(ctrl, shift, alt, windows));
        this.action = action;
        this.key = key;
        this.ctrl = ctrl;
        this.shift = shift;
        this.alt = alt;
        this.windows = windows;
        this.active = active;
        setHotkeyListener(action.getListener());
        action.setKey(key);
    }

    public boolean updateHotkey() {
        if (active) {
            unRegister();
            vKCode = key.code;
            fsModifiers = modifiersToInt(ctrl, shift, alt, windows);
            setHotkeyListener(action.getListener());
            action.setKey(key);
            return register();
        }
        return unRegister();
    }


    public String getAction() {
        return action.getName();
    }

    public String getKey() {
        return key.name();
    }

    public boolean getCtrl() {
        return ctrl;
    }

    public boolean getShift() {
        return shift;
    }

    public boolean getAlt() {
        return alt;
    }

    public boolean getWindows() {
        return windows;
    }

    public boolean getActive() {
        return active;
    }

    public static int modifiersToInt(boolean ctrl, boolean shift, boolean alt, boolean windows) {
        int modifier = 0;
        if (ctrl)
            modifier |= 0x02;
        if (shift)
            modifier |= 0x04;
        if (alt)
            modifier |= 0x01;
        if (windows)
            modifier |= 0x08;
        return modifier;
    }
}
