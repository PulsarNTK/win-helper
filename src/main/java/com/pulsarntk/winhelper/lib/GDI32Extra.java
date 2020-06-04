package com.pulsarntk.winhelper.lib;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.win32.W32APIOptions;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinGDI.BITMAPINFO;

public interface GDI32Extra extends Kernel32 {
    GDI32Extra INSTANCE = (GDI32Extra) Native.load("gdi32", GDI32Extra.class, W32APIOptions.DEFAULT_OPTIONS);

    int GetDIBits(HDC hdc, HBITMAP hbmp, int uStartScan, int cScanLines, int lpvBits, BITMAPINFO lpbi, int uUsage);

}
