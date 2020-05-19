package com.pulsarntk.winhelper.feature.desktopoverview.utils;

import com.pulsarntk.winhelper.itf.ASyncRenderable;
import com.sun.jna.platform.win32.WinDef.RECT;
import java.awt.image.BufferedImage;


public class WindowInfo implements ASyncRenderable {
    public final int hwnd;
    public final RECT rect;
    public final String title;
    public final int desktopNumber;
    public final boolean isMaximized;
    public BufferedImage image;
    private ASyncRenderer aSyncRenderer = new ASyncRenderer(this);

    public WindowInfo(int hwnd, RECT rect, String title, int desktopNumber, boolean isMaximized) {
        this.hwnd = hwnd;
        this.rect = rect;
        this.title = title;
        this.desktopNumber = desktopNumber;
        this.isMaximized = isMaximized;
    }

    public String toString() {
        return String.format("(%d,%d)-(%d,%d) - (D->%d) : \"%s\"", rect.left, rect.top, rect.right, rect.bottom, desktopNumber, title);
    }

    @Override
    public void render() {
        WindowCapture.capture(this);
    }

    @Override
    public ASyncRenderer getASyncRenderer() {
        return aSyncRenderer;
    }
}
