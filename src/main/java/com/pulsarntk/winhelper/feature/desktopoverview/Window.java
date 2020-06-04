package com.pulsarntk.winhelper.feature.desktopoverview;

import com.pulsarntk.winhelper.itf.Renderable;
import com.pulsarntk.winhelper.itf.ASyncImageRenderer;
import com.pulsarntk.winhelper.lib.Kernel32Extra;
import com.pulsarntk.winhelper.lib.User32Extra;
import com.sun.jna.platform.win32.WinDef.RECT;
import java.awt.image.BufferedImage;
import com.sun.jna.Memory;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.WinDef.HBITMAP;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinGDI;
import com.sun.jna.platform.win32.WinGDI.BITMAPINFO;
import com.sun.jna.platform.win32.WinNT.HANDLE;



public class Window extends ASyncImageRenderer {
    public final int hwnd;
    public final RECT rect;
    public final BITMAPINFO bmi;
    public int WIDTH;
    public int HEIGHT;
    public String title;
    public int desktopNumber;
    public boolean isMaximized;
    public Memory buffer;

    public Window(int hwnd, RECT rect, String title, int desktopNumber, boolean isMaximized) {
        super(rect.right - rect.left, rect.bottom - rect.top, BufferedImage.TYPE_INT_ARGB);
        this.hwnd = hwnd;
        this.rect = rect;
        this.title = title;
        this.desktopNumber = desktopNumber;
        this.isMaximized = isMaximized;
        this.WIDTH = rect.right - rect.left;
        this.HEIGHT = rect.bottom - rect.top;
        this.buffer = new Memory(WIDTH * HEIGHT * 4);
        this.bmi = new BITMAPINFO();
        this.bmi.bmiHeader.biWidth = this.WIDTH;
        this.bmi.bmiHeader.biHeight = -this.HEIGHT;
        this.bmi.bmiHeader.biPlanes = 1;
        this.bmi.bmiHeader.biBitCount = 32;
        this.bmi.bmiHeader.biCompression = WinGDI.BI_RGB;
    }

    public String toString() {
        return String.format("(%d,%d)-(%d,%d) - (D->%d) : \"%s\"", rect.left, rect.top, rect.right, rect.bottom, desktopNumber, title);
    }

    @Override
    public void render() {
        HDC windowDC = User32Extra.INSTANCE.GetDC(this.hwnd);
        HBITMAP outputBitmap = GDI32.INSTANCE.CreateCompatibleBitmap(windowDC, this.WIDTH, this.HEIGHT);
        HDC blitDC = GDI32.INSTANCE.CreateCompatibleDC(windowDC);
        HANDLE oldBitmap = GDI32.INSTANCE.SelectObject(blitDC, outputBitmap);
        User32Extra.INSTANCE.PrintWindow(this.hwnd, blitDC, 2);
        GDI32.INSTANCE.SelectObject(blitDC, oldBitmap);
        GDI32.INSTANCE.GetDIBits(blitDC, outputBitmap, 0, this.HEIGHT, this.buffer, this.bmi, WinGDI.DIB_RGB_COLORS);
        GDI32.INSTANCE.DeleteObject(blitDC);
        GDI32.INSTANCE.DeleteObject(outputBitmap);
        GDI32.INSTANCE.DeleteObject(windowDC);
        this.bufferedImage.setRGB(0, 0, this.WIDTH, this.HEIGHT, this.buffer.getIntArray(0, this.WIDTH * this.HEIGHT), 0, this.WIDTH);
    }

}
