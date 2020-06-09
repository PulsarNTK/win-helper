package com.pulsarntk.winhelper.utils;

import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.User32;
import com.pulsarntk.winhelper.lib.User32Extra;

public class WindowFromPoint {
    public static HWND windowFromPoint() {
        long[] cursorPoint = new long[1];
        User32Extra.INSTANCE.GetCursorPos(cursorPoint);
        return User32Extra.INSTANCE.WindowFromPoint(cursorPoint[0]);
    }

    public static String rootWindowClassFromPoint() {
        char[] className = new char[512];
        User32.INSTANCE.GetClassName(User32.INSTANCE.GetAncestor(windowFromPoint(), 2), className, 512);
        return new String(className).replaceAll(String.valueOf((char) 0), "");
    }

    public static HWND rootWindowFromPoint() {
        return User32.INSTANCE.GetAncestor(windowFromPoint(), 2);
    }
}
