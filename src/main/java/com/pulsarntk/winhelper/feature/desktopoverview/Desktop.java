package com.pulsarntk.winhelper.feature.desktopoverview;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import com.pulsarntk.winhelper.feature.desktopoverview.Window;
import com.pulsarntk.winhelper.itf.ASyncImageRenderer;
import com.pulsarntk.winhelper.itf.Renderable;
import java.awt.image.BufferedImage;



public class Desktop extends ASyncImageRenderer {
    public int desktopNumber;
    private List<Window> windows = new ArrayList<Window>();
    public Dimension size;

    public Graphics graphics;
    public boolean status = false;


    public Desktop(int desktopNumber, Dimension size) {
        super(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        this.desktopNumber = desktopNumber;
        this.size = size;
    }

    public void clearList() {
        this.windows.clear();
    }

    public void setWindowsRender(boolean b) {
        for (Window window : windows) {
            window.setASyncRender(b);
        }
    }

    public void setWindowsFrameRate(int frameRate) {
        for (Window window : windows) {
            window.setFrameRate(frameRate);
        }
    }

    public void addWindow(Window window) {
        windows.add(0, window);
    }

    @Override
    public void render() {
        graphics = bufferedImage.getGraphics();
        graphics.setColor(new Color(0, 0, 0, 255));
        graphics.drawRect(-1, -1, size.width + 1, size.height + 1);
        for (Window window : windows) {
            graphics.drawImage(window.getImage(), window.rect.left, window.rect.top, null);
        }
        graphics.dispose();
    }

    @Override
    public void setFrameRate(long frameRate) {
        for (Window window : windows) {
            window.setFrameRate(frameRate);
        }
        super.setFrameRate(frameRate);
    }

    @Override
    public void setASyncRender(boolean b) {
        for (Window window : windows) {
            window.setASyncRender(b);
        }
        super.setASyncRender(b);
    }


    @Override
    public void startASyncRender() {
        for (Window window : windows) {
            window.startASyncRender();
        }
        super.startASyncRender();
    }

    @Override
    public void stopASyncRender() {
        for (Window window : windows) {
            window.stopASyncRender();
        }
        super.stopASyncRender();
    }

}
