package com.pulsarntk.winhelper.feature.desktopoverview.utils;

import com.pulsarntk.winhelper.lib.User32Extra;
import java.awt.image.BufferedImage;



import com.sun.jna.Memory;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.WinDef.HBITMAP;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinGDI;
import com.sun.jna.platform.win32.WinGDI.BITMAPINFO;
import com.sun.jna.platform.win32.WinNT.HANDLE;

public class WindowCapture {
    public static void capture(WindowInfo wInfo) {
        int width = wInfo.rect.right - wInfo.rect.left;
        int height = wInfo.rect.bottom - wInfo.rect.top;
        HDC windowDC = User32Extra.INSTANCE.GetDC(wInfo.hwnd);
        HBITMAP outputBitmap = GDI32.INSTANCE.CreateCompatibleBitmap(windowDC, width, height);
        HDC blitDC = GDI32.INSTANCE.CreateCompatibleDC(windowDC);
        HANDLE oldBitmap = GDI32.INSTANCE.SelectObject(blitDC, outputBitmap);
        User32Extra.INSTANCE.PrintWindow(wInfo.hwnd, blitDC, 2);
        GDI32.INSTANCE.SelectObject(blitDC, oldBitmap);
        BITMAPINFO bmi = new BITMAPINFO();
        bmi.bmiHeader.biWidth = width;
        bmi.bmiHeader.biHeight = -height;
        bmi.bmiHeader.biPlanes = 1;
        bmi.bmiHeader.biBitCount = 32;
        bmi.bmiHeader.biCompression = WinGDI.BI_RGB;
        Memory buffer = new Memory(width * height * 4);
        GDI32.INSTANCE.GetDIBits(blitDC, outputBitmap, 0, height, buffer, bmi, WinGDI.DIB_RGB_COLORS);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, width, height, buffer.getIntArray(0, width * height), 0, width);
        wInfo.image = image;
        GDI32.INSTANCE.DeleteObject(blitDC);
        GDI32.INSTANCE.DeleteObject(outputBitmap);
        GDI32.INSTANCE.DeleteObject(windowDC);
    }
}
