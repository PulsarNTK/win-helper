package com.pulsarntk.winhelper.utils;

import com.sun.jna.*;
import com.sun.jna.win32.*;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.User32;

public class WindowFromPoint {
    public static HWND windowFromPoint() {
        long[] cursorPoint = new long[1];
        _user32.INSTANCE.GetCursorPos(cursorPoint);
        return _user32.INSTANCE.WindowFromPoint(cursorPoint[0]);
    }

    public static String rootWindowClassFromPoint() {
        char[] className = new char[512];
        User32.INSTANCE.GetClassName(User32.INSTANCE.GetAncestor(windowFromPoint(), 2), className, 512);
        return new String(className).replaceAll(String.valueOf((char) 0), "");
    }

    public interface _user32 extends StdCallLibrary {
        _user32 INSTANCE = (_user32) Native.load("user32", _user32.class, W32APIOptions.DEFAULT_OPTIONS);

        HWND WindowFromPoint(long point);

        boolean GetCursorPos(long[] lpPoint);
    }
}
