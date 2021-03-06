package com.pulsarntk.winhelper.feature.hotkeys.action.actions;

import com.pulsarntk.winhelper.feature.hotkeys.action.itf.Action;
import com.pulsarntk.winhelper.lib.User32Extra;
import com.pulsarntk.winhelper.lib.VirtualDesktopAccessor;
import com.pulsarntk.winhelper.utils.RegisterHotkey.HotkeyListener;
import com.sun.jna.platform.win32.WinUser.MSG;

public class MoveWindowPreviousDesktopNGo implements Action {

    static HotkeyListener listener = new HotkeyListener() {
        @Override
        public void onKeyPress(MSG msg) {
            int toDesktop = VirtualDesktopAccessor.INSTANCE.GetCurrentDesktopNumber() - 1;
            VirtualDesktopAccessor.INSTANCE.MoveWindowToDesktopNumber(User32Extra.INSTANCE.GetForegroundWindow(), toDesktop);
            VirtualDesktopAccessor.INSTANCE.GoToDesktopNumber(toDesktop);
        }
    };

    @Override
    public String getName() {
        return "Move Window Previous Desktop And Go";
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
