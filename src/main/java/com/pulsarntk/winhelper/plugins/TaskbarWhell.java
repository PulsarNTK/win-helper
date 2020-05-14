package com.pulsarntk.winhelper.plugins;

import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;
import lc.kra.system.mouse.GlobalMouseHook;
import lc.kra.system.mouse.event.GlobalMouseAdapter;

import com.sun.jna.*;
import com.sun.jna.win32.*;

import lc.kra.system.mouse.event.GlobalMouseEvent;

import com.pulsarntk.winhelper.lib.VirtualDesktopAccessor;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.POINT;
import com.sun.jna.platform.win32.User32;

public class TaskbarWhell {
    GlobalMouseHook mouseHook = new GlobalMouseHook();
    String TASKBAR_CLASS = "Shell_TrayWnd";
    long[] cursorPoint = new long[1];
    HWND hwndFromPoint;
    HWND hwndParent;

    public void install() {
        mouseHook.addMouseListener(new GlobalMouseAdapter() {
            @Override
            public void mouseWheel(GlobalMouseEvent event) {
                char[] className = new char[512];

                _user32.INSTANCE.GetCursorPos(cursorPoint);
                HWND hwndFromPoint = _user32.INSTANCE.WindowFromPoint(cursorPoint[0]);
                HWND hwndParent = User32.INSTANCE.GetAncestor(hwndFromPoint, 2);
                User32.INSTANCE.GetClassName(hwndParent, className, 512);

                if (TASKBAR_CLASS.equals(new String(className).replaceAll(String.valueOf((char) 0), ""))) {
                    int whell = event.getDelta();
                    if (whell != 0) {
                        int currentDesktop = VirtualDesktopAccessor.INSTANCE.GetCurrentDesktopNumber();
                        if (whell > 0) {
                            VirtualDesktopAccessor.INSTANCE.GoToDesktopNumber(currentDesktop - 1);
                        } else {
                            VirtualDesktopAccessor.INSTANCE.GoToDesktopNumber(currentDesktop + 1);
                        }
                    }
                }
            }
        });
    }

    public interface _user32 extends StdCallLibrary {
        _user32 INSTANCE = (_user32) Native.load("user32", _user32.class, W32APIOptions.DEFAULT_OPTIONS);

        HWND WindowFromPoint(long point);

        boolean GetCursorPos(long[] lpPoint);
    }
}
