package com.pulsarntk.winhelper.itf;



public interface ASyncRenderable extends Renderable {
    public ASyncRenderer getASyncRenderer();

    public class ASyncRenderer implements Runnable {
        protected Renderable renderable;
        protected Thread renderThread = new Thread();
        protected long lastRender = 0;
        protected long sleepBetweenFrames = 0;
        protected boolean status = false;

        public ASyncRenderer(Renderable renderable) {
            this.renderable = renderable;
        }

        public ASyncRenderer(Renderable renderable, int frameRate) {
            this.renderable = renderable;
            setFrameRate(frameRate);
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

        public boolean getStatus() {
            return status;
        }

        public void setFrameRate(long frameRate) {
            this.sleepBetweenFrames = 1000 / frameRate;
        }

        @Override
        public void run() {
            while (this.status) {
                renderable.render();
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
}
