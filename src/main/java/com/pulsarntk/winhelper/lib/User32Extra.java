package com.pulsarntk.winhelper.lib;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.win32.W32APIOptions;
import com.sun.jna.win32.StdCallLibrary;

public interface User32Extra extends User32 {

    User32Extra INSTANCE = (User32Extra) Native.load("user32", User32Extra.class, W32APIOptions.DEFAULT_OPTIONS);

    final int GW_HWNDNEXT = 2;

    HDC GetWindowDC(HWND hwnd);

    boolean GetClientRect(HWND hwnd, RECT rect);

    HDC GetDC(int hwnd);

    boolean EnumWindows(WndEnumProc wndenumproc, int lParam);

    boolean IsWindowVisible(int hwnd);

    int GetWindowRect(int hwnd, RECT r);

    void GetWindowTextA(int hwnd, byte[] buffer, int buflen);

    int GetTopWindow(int hwnd);

    int GetWindow(int hwnd, int flag);

    boolean PrintWindow(int hwnd, HDC hdcBlt, int nFlags);

    boolean IsZoomed(int hwnd);

    HWND WindowFromPoint(long point);

    boolean GetCursorPos(long[] lpPoint);

    public static interface WndEnumProc extends StdCallLibrary.StdCallCallback {
        boolean callback(int hwnd, int lParam);
    }
}
