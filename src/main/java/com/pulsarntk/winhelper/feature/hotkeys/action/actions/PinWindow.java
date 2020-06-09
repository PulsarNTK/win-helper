package com.pulsarntk.winhelper.feature.hotkeys.action.actions;

import com.pulsarntk.winhelper.feature.hotkeys.action.itf.Action;
import com.pulsarntk.winhelper.lib.User32Extra;
import com.pulsarntk.winhelper.lib.VirtualDesktopAccessor;
import com.pulsarntk.winhelper.utils.RegisterHotkey.HotkeyListener;
import com.sun.jna.platform.win32.WinUser.MSG;

public class PinWindow implements Action {

    static HotkeyListener listener = new HotkeyListener() {
        @Override
        public void onKeyPress(MSG msg) {
            VirtualDesktopAccessor.INSTANCE.PinWindow(User32Extra.INSTANCE.GetForegroundWindow());
        }
    };

    @Override
    public String getName() {
        return "Pin Window";
    }

    @Override
    public HotkeyListener getListener() {
        return listener;
    }

    @Override
    public String toString() {
        return getName();
    }

}
