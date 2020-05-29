package com.pulsarntk.winhelper.feature.hotkeys.action.actions;

import javax.swing.JDialog;
import com.pulsarntk.winhelper.feature.hotkeys.action.itf.Action;
import com.pulsarntk.winhelper.feature.hotkeys.action.itf.CustomAction;
import com.pulsarntk.winhelper.lib.VirtualDesktopAccessor;
import com.pulsarntk.winhelper.settings.Setting;
import com.pulsarntk.winhelper.utils.RegisterHotkey.HotkeyListener;
import com.sun.jna.platform.win32.WinUser.MSG;

public class DefaultAction implements Action {
    static HotkeyListener listener = new HotkeyListener() {
        @Override
        public void onKeyPress(MSG msg) {
            System.out.println("Default Action");
        }
    };

    public DefaultAction() {
    }

    @Override
    public HotkeyListener getListener() {
        return listener;
    }

    @Override
    public String getName() {
        return "Default Action";
    }

    @Override
    public String toString() {
        return "Default Action";
    }

}
