package com.pulsarntk.winhelper.itf;

import java.awt.image.BufferedImage;


public abstract class ASyncImageRenderer implements Runnable, Renderable {
    private Thread renderThread = new Thread();
    private long lastRender = 0;
    private long sleepBetweenFrames = 0;
    private boolean status = false;
    private boolean renderStatus = false;
    private BufferedImage image;
    protected BufferedImage bufferedImage;

    public ASyncImageRenderer(int width, int height, int imageType) {
        this.bufferedImage = new BufferedImage(width, height, imageType);
        this.image = new BufferedImage(width, height, imageType);
        this.sleepBetweenFrames = 1000 / 60;
    }

    public ASyncImageRenderer(int width, int height, int imageType, int frameRate) {
        this(width, height, imageType);
        this.sleepBetweenFrames = 1000 / frameRate;
    }

    public void startASyncRender() {
        if (!this.renderThread.isAlive()) {
            this.status = true;
            this.renderThread = new Thread(this);
            this.renderThread.start();
        }
    }

    public void stopASyncRender() {
        this.status = false;
    }

    public void setASyncRender(boolean b) {
        if (b) {
            startASyncRender();
        } else {
            stopASyncRender();
        }
    }

    public void setFrameRate(long frameRate) {
        this.sleepBetweenFrames = 1000 / frameRate;
    }

    public BufferedImage getImage() {
        if (renderStatus)
            return image;
        else
            return bufferedImage;
    }

    private void bufferToImage() {
        renderStatus = false;
        image.getGraphics().drawImage(bufferedImage, 0, 0, null);
        renderStatus = true;
    }

    @Override
    public void run() {
        while (this.status) {
            render();
            bufferToImage();
            if (sleepBetweenFrames != 0) {
                long sleepTime = sleepBetweenFrames - (System.currentTimeMillis() - lastRender);
                if (sleepTime > 0) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                lastRender = System.currentTimeMillis();
            }
        }
    }
}
