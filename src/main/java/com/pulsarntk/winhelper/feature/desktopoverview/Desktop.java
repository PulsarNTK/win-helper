package com.pulsarntk.winhelper.feature.desktopoverview;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import com.pulsarntk.winhelper.feature.desktopoverview.utils.WindowCapture;
import com.pulsarntk.winhelper.feature.desktopoverview.utils.WindowInfo;
import com.pulsarntk.winhelper.itf.ASyncRenderable;
import com.pulsarntk.winhelper.itf.ASyncRenderable.ASyncRenderer;
import com.pulsarntk.winhelper.itf.Renderable;
import java.awt.image.BufferedImage;



public class Desktop implements ASyncRenderable {
    public int desktopNumber;
    private List<WindowInfo> wList = new ArrayList<WindowInfo>();
    private List<Thread> tList = new ArrayList<Thread>();
    public Dimension size;
    private BufferedImage image;
    private BufferedImage bufferedImage;
    public ASyncRenderer aSyncRenderer = new ASyncRenderer(this);
    public Graphics graphics;


    public Desktop(int desktopNumber, Dimension size) {
        this.desktopNumber = desktopNumber;
        this.size = size;
        this.image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        this.bufferedImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        this.graphics = bufferedImage.getGraphics();
        this.graphics.setColor(new Color(0, 0, 0, 255));
    }

    public BufferedImage getImage() {
        return this.image;
    }

    public BufferedImage renderGetImage() {
        this.render();
        return this.image;
    }

    @Override
    public void render() {
        graphics.drawRect(-1, -1, size.width + 1, size.height + 1);
        if (this.wList.size() == 0)
            return;
        for (WindowInfo wInfo : wList) {
            Thread t = new Thread(new WindowRenderer(wInfo));
            this.tList.add(t);
            t.start();
        }
        for (Thread thread : tList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (WindowInfo wInfo : wList) {
            graphics.drawImage(wInfo.image, wInfo.rect.left, wInfo.rect.top, null);
        }
        tList.clear();
        this.image = bufferedImage;
    }

    public void clearList() {
        this.wList.clear();
    }


    public void addWindowInfo(WindowInfo wInfo) {
        wList.add(0, wInfo);
    }

    private class WindowRenderer implements Runnable {
        WindowInfo wInfo;

        public WindowRenderer(WindowInfo wInfo) {
            this.wInfo = wInfo;
        }

        public void run() {
            WindowCapture.capture(wInfo);
        }
    }

    @Override
    public ASyncRenderer getASyncRenderer() {
        return aSyncRenderer;
    }
}
