package com.pulsarntk.winhelper.feature.desktopoverview;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import com.pulsarntk.winhelper.feature.desktopoverview.utils.WindowCapture;
import com.pulsarntk.winhelper.feature.desktopoverview.utils.WindowInfo;
import java.awt.image.BufferedImage;


public class Desktop {
    public int desktopNumber;
    public List<WindowInfo> wList = new ArrayList<WindowInfo>();
    public List<Thread> tList = new ArrayList<Thread>();
    public Dimension size;

    public Desktop(int desktopNumber, Dimension size) {
        this.desktopNumber = desktopNumber;
        this.size = size;
    }

    public BufferedImage render() {
        BufferedImage image = new BufferedImage((int) size.getWidth(), (int) size.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();
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
        return image;
    }

    private class WindowRenderer implements Runnable {
        WindowInfo wInfo;

        public WindowRenderer(WindowInfo wInfo) {
            this.wInfo = wInfo;
        }

        public void run() {
            // long tick = System.currentTimeMillis();
            WindowCapture.capture(wInfo);
            // System.out.println(String.format("###################Print Window###################\n Title: %s\nTime: %d",
            // wInfo.title, System.currentTimeMillis() - tick));
        }
    }


    public void addWindow(WindowInfo wInfo) {
        wList.add(wInfo);
    }
}
