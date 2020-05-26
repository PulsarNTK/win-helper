package com.pulsarntk.winhelper.feature.hotkeys;

import java.rmi.UnexpectedException;
import com.pulsarntk.winhelper.utils.RegisterHotkey;
import com.pulsarntk.winhelper.utils.RegisterHotkey.HotkeyListener;
import com.pulsarntk.winhelper.utils.RegisterHotkey.KEY;
import com.sun.jna.platform.win32.WinUser.MSG;

public class Hotkey {
    public RegisterHotkey hotkey;
    public ACTION action;
    public KEY key;
    public boolean ctrl;
    public boolean shift;
    public boolean alt;
    public boolean windows;
    public boolean active;

    public Hotkey(ACTION action, KEY key, boolean ctrl, boolean shift, boolean alt, boolean windows, boolean active) {
        try {
            hotkey = new RegisterHotkey(key.code, modifiersToInt(ctrl, shift, alt, windows));
            hotkey.setHotkeyListener(new HotkeyListener() {
                @Override
                public void onKeyPress(MSG msg) {
                    System.out.println("\nTick: " + System.currentTimeMillis() + "\nMSG: " + msg.message);
                }
            });
        } catch (UnexpectedException | InterruptedException e) {
            e.printStackTrace();
        }
        this.action = action;
        this.key = key;
        this.ctrl = ctrl;
        this.shift = shift;
        this.alt = alt;
        this.windows = windows;
        this.active = active;
    }

    public void updateHotkey() {
        unRegisterHotkey();
        if (active) {
            hotkey.vKCode = key.code;
            hotkey.fsModifiers = modifiersToInt(ctrl, shift, alt, windows);
            registerHotkey();
        }
    }

    public void registerHotkey() {
        hotkey.register();
    }

    public void unRegisterHotkey() {
        hotkey.unRegister();
    }

    public String getAction() {
        return action.name();
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

    public int modifiersToInt(boolean ctrl, boolean shift, boolean alt, boolean windows) {
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

    public static enum ACTION {
        NONE, GO_NEXT_DESKTOP, GO_PREVIOUS_DESKTOP
    }
}
