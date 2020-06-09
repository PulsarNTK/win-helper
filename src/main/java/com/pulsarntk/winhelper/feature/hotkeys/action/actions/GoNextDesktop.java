package com.pulsarntk.winhelper.feature.hotkeys.action.actions;

import javax.swing.JDialog;
import com.pulsarntk.winhelper.feature.hotkeys.action.itf.Action;
import com.pulsarntk.winhelper.feature.hotkeys.action.itf.CustomAction;
import com.pulsarntk.winhelper.lib.VirtualDesktopAccessor;
import com.pulsarntk.winhelper.settings.Setting;
import com.pulsarntk.winhelper.utils.RegisterHotkey.HotkeyListener;
import com.sun.jna.platform.win32.WinUser.MSG;

public class GoNextDesktop implements Action {
    static HotkeyListener listener = new HotkeyListener() {
        @Override
        public void onKeyPress(MSG msg) {
            VirtualDesktopAccessor.INSTANCE.GoToDesktopNumber(VirtualDesktopAccessor.INSTANCE.GetCurrentDesktopNumber() + 1);
        }
    };

    public GoNextDesktop() {
    }

    @Override
    public HotkeyListener getListener() {
        return listener;
    }

    @Override
    public String getName() {
        return "Go Next Desktop";
    }

    @Override
    public String toString() {
        return getName();
    }

}
