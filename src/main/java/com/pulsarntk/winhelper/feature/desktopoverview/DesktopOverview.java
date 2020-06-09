package com.pulsarntk.winhelper.feature.desktopoverview;

import java.rmi.UnexpectedException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import com.pulsarntk.winhelper.itf.ASyncRenderable;
import com.pulsarntk.winhelper.itf.ASyncRenderable.ASyncRenderer;
import com.pulsarntk.winhelper.itf.Configurable;
import com.pulsarntk.winhelper.itf.Feature;
import com.pulsarntk.winhelper.itf.Renderable;
import com.pulsarntk.winhelper.lib.User32Extra;
import com.pulsarntk.winhelper.lib.VirtualDesktopAccessor;
import com.pulsarntk.winhelper.lib.VirtualDesktopAccessor.VirtualDesktopListener.Listener;
import com.pulsarntk.winhelper.utils.RegisterHotkey;
import com.pulsarntk.winhelper.utils.RegisterHotkey.HotkeyListener;
import com.pulsarntk.winhelper.utils.RegisterHotkey.KEY;
import com.sun.jna.platform.win32.WinUser.MSG;
import java.awt.*;
import com.pulsarntk.winhelper.lib.VirtualDesktopAccessor;
import com.pulsarntk.winhelper.lib.VirtualDesktopAccessor.VirtualDesktopListener;
import com.sun.jna.platform.win32.WinDef.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;


import com.sun.jna.Memory;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.WinGDI;
import com.sun.jna.platform.win32.WinGDI.BITMAPINFO;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinUser.WinEventProc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import com.pulsarntk.winhelper.feature.desktopbackground.DesktopBackground;
import com.pulsarntk.winhelper.feature.desktopoverview.Window;
import java.awt.image.BufferStrategy;
import com.sun.jna.platform.win32.WinUser.MSG;

public class DesktopOverview implements Feature, ASyncRenderable {
    private JFrame overviewFrame = new JFrame("Desktop Overview") {
        @Override
        public void setVisible(boolean visible) {
            if (visible) {
                getWindowsZOrder();
                Animation.calculateAnimation();
                aSyncRenderer.startASyncRender();
                setDesktopsRender(true);
                startTime = System.currentTimeMillis();
                frames = 0;
            } else {
                aSyncRenderer.stopASyncRender();
                setDesktopsRender(false);
            }
            super.setVisible(visible);
        }
    };

    public static long startTime = 0;
    private static RegisterHotkey hotkey;
    private static Canvas canvas = new Canvas();
    private static Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
    private static List<Desktop> desktops = new ArrayList<Desktop>();
    private static int currentDesktop = 0;
    private static int desktopCount = 0;
    private static int frames;
    public ASyncRenderer aSyncRenderer = new ASyncRenderer(this, 60);
    static BufferStrategy buffer;
    static Graphics2D graphics;
    private static Thread animationThread;
    public static Position BACKGROUND_SIZE = new Position(0, (int) (SCREEN_SIZE.height * Options.currentDesktopZoomOut - (Options.sideDesktopMargin * (SCREEN_SIZE.width / SCREEN_SIZE.height))) / 2,
            SCREEN_SIZE.width - (int) ((SCREEN_SIZE.width * Options.currentDesktopZoomOut)) + (Options.sideDesktopMargin),
            SCREEN_SIZE.height - (int) ((SCREEN_SIZE.height * Options.currentDesktopZoomOut)) + (Options.sideDesktopMargin * (SCREEN_SIZE.width / SCREEN_SIZE.height)));
    public static Position[] dPositions = {new Position(), new Position(), new Position()};
    public static Position[] bPositions = {new Position(0, BACKGROUND_SIZE.y, BACKGROUND_SIZE.width, BACKGROUND_SIZE.height), new Position(0, BACKGROUND_SIZE.y, BACKGROUND_SIZE.width, BACKGROUND_SIZE.height),
            new Position(0, BACKGROUND_SIZE.y, BACKGROUND_SIZE.width, BACKGROUND_SIZE.height)};

    // private WinEventProc winEventProc = new WinEventProc() {
    // @Override
    // public void callback(HANDLE hWinEventHook, DWORD event, HWND hwnd, LONG idObject, LONG idChild, DWORD dwEventThread,
    // DWORD dwmsEventTime) {
    // if (aSyncRenderer.getStatus() && VirtualDesktopAccessor.INSTANCE.GetWindowDesktopNumber(hwnd) != -1) {
    // int desktopNumber = VirtualDesktopAccessor.INSTANCE.GetWindowDesktopNumber(hwnd);
    // byte[] title = new byte[512];
    // User32Extra.INSTANCE.GetWindowTextA(hwnd, title, 512);
    // System.out.println("Title: " + title + " DesktopNumber: " + desktopNumber);
    // setDesktopsRender(false);
    // getWindowsZOrder();
    // setDesktopsRender(true);
    // }
    // }
    // };
    private User32Extra.WndEnumProc getWindowsZOrderProc = new User32Extra.WndEnumProc() {
        public boolean callback(int hwnd, int lParam) {
            if (User32Extra.INSTANCE.IsWindowVisible(hwnd)) {
                int desktopNumber = VirtualDesktopAccessor.INSTANCE.GetWindowDesktopNumber(hwnd);
                if (desktopNumber != -1) {
                    RECT rect = new RECT();
                    User32Extra.INSTANCE.GetWindowRect(hwnd, rect);
                    byte[] buffer = new byte[1024];
                    User32Extra.INSTANCE.GetWindowTextA(hwnd, buffer, buffer.length);
                    String title = Native.toString(buffer);
                    if (title.equals("Desktop Overview") || title.equals("Settings")) {
                        return true;
                    }
                    boolean isMaximized = User32Extra.INSTANCE.IsZoomed(hwnd);
                    desktops.get(desktopNumber).addWindow(new Window(hwnd, rect, title, desktopNumber, isMaximized));
                }
            }
            return true;
        }
    };

    public DesktopOverview() throws UnexpectedException, InterruptedException {
        desktopCount = VirtualDesktopAccessor.INSTANCE.GetDesktopCount();
        currentDesktop = VirtualDesktopAccessor.INSTANCE.GetCurrentDesktopNumber();
        for (int i = 0; i < desktopCount; i++) {
            desktops.add(new Desktop(i, SCREEN_SIZE));
        }

        canvas.setSize(SCREEN_SIZE);
        overviewFrame.setUndecorated(true);
        overviewFrame.setLayout(new BorderLayout());
        overviewFrame.getContentPane().setBackground(Color.WHITE);
        overviewFrame.add(canvas);
        overviewFrame.pack();
        canvas.createBufferStrategy(2);
        buffer = canvas.getBufferStrategy();
        graphics = (Graphics2D) buffer.getDrawGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        mouseVelocityListener();
        hotkey = new RegisterHotkey(KEY.VK_TAB.code, 0x02);
        hotkey.setHotkeyListener(new HotkeyListener() {
            public void onKeyPress(MSG msg) {
                overviewFrame.setVisible(!overviewFrame.isVisible());
            }
        });

        new VirtualDesktopListener(new Listener() {
            @Override
            public void onMessage(MSG msg) {
                switch (msg.message) {
                    case VirtualDesktopListener.CURRENT_VIRTUAL_DESKTOP_CHANGED:
                        currentDesktop = msg.wParam.intValue();
                        break;
                    case VirtualDesktopListener.VIRTUAL_DESKTOP_CREATED:
                        desktopCount++;
                        desktops.add(new Desktop(desktopCount, SCREEN_SIZE));
                        break;
                    case VirtualDesktopListener.VIRTUAL_DESKTOP_DESTROYED:
                        desktopCount--;
                        desktops.remove(msg.lParam.intValue() + 1);
                        break;
                }
            }
        });

        canvas.addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                scrollAnimation(e.getWheelRotation());
            }
        });

        canvas.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                Animation.mousePressed = true;
                Animation.mousePos[0] = e.getX() - Animation.width;
                Animation.mousePos[1] = e.getY() - Animation.height;
                Animation.velocity = 0;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Animation.mousePressed = false;
            }
        });

        canvas.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if ((currentDesktop != 0 || Animation.width < Options.sideDesktopMargin || Animation.width > e.getX() - Animation.mousePos[0])
                        && (currentDesktop != desktopCount - 1 || Animation.width > -Options.sideDesktopMargin || Animation.width < e.getX() - Animation.mousePos[0])) {
                    Animation.width = e.getX() - Animation.mousePos[0];
                    Animation.height = e.getY() - Animation.mousePos[1];
                    if (Animation.lastPrevPos < System.currentTimeMillis() + 20) {
                        Animation.velocity = (Animation.prevPos - e.getX()) * 10;
                        Animation.prevPos = e.getX();
                        Animation.lastPrevPos = System.currentTimeMillis();
                    }
                    Animation.mouseDrag();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
            }
        });

        // new Thread(new Runnable() {
        // @Override
        // public void run() {
        // Ole32.INSTANCE.CoInitialize(null);
        // User32Extra.INSTANCE.SetWinEventHook(0x8002, 0x8002, (HMODULE) null, winEventProc, 0, 0, 0x1); //
        // WINEVENT_OUTOFCONTEXT | WINEVENT_SKIPOWNTHREAD = 0x00 | 0x01
        // MSG msg = new MSG();
        // User32.INSTANCE.GetMessage(msg, null, 0, 0);
        // }
        // }).start();
    }

    private void getWindowsZOrder() {
        for (Desktop desktop : desktops) {
            desktop.clearList();
        }
        User32Extra.INSTANCE.EnumWindows(getWindowsZOrderProc, 0);
    }

    @Override
    public void render() {
        if (Animation.desktopChanging)
            return;
        graphics.fillRect(0, 0, overviewFrame.getWidth(), overviewFrame.getHeight());
        graphics.drawImage(DesktopBackground.getBackground(currentDesktop - 1), bPositions[0].x, bPositions[0].y, bPositions[0].width, bPositions[0].height, null);
        graphics.drawImage(DesktopBackground.getBackground(currentDesktop), bPositions[1].x, bPositions[1].y, bPositions[1].width, bPositions[1].height, null);
        graphics.drawImage(DesktopBackground.getBackground(currentDesktop + 1), bPositions[2].x, bPositions[2].y, bPositions[2].width, bPositions[2].height, null);
        graphics.drawImage(desktops.get(currentDesktop).getImage(), dPositions[1].x, dPositions[1].y, dPositions[1].width, dPositions[1].height, null);
        if (currentDesktop != 0)
            graphics.drawImage(desktops.get(currentDesktop - 1).getImage(), dPositions[0].x, dPositions[0].y, dPositions[0].width, dPositions[0].height, null);
        if (currentDesktop != desktopCount - 1)
            graphics.drawImage(desktops.get(currentDesktop + 1).getImage(), dPositions[2].x, dPositions[2].y, dPositions[2].width, dPositions[2].height, null);
        buffer.show();
        frames++;
        // System.out.println((double) frames / ((System.currentTimeMillis() - startTime) / 1000));
    }

    public static boolean changeDesktop(int direction) {
        if (direction == 1) {
            if (currentDesktop != desktopCount - 1) {
                if (currentDesktop != 0) {
                    desktops.get(currentDesktop - 1).stopASyncRender();
                }
                currentDesktop++;
                if (currentDesktop != desktopCount - 1) {
                    desktops.get(currentDesktop + 1).startASyncRender();
                }
            } else {
                return false;
            }
        } else if (direction == -1) {
            if (currentDesktop != 0) {
                if (currentDesktop != desktopCount - 1) {
                    desktops.get(currentDesktop + 1).stopASyncRender();
                }
                currentDesktop--;
                if (currentDesktop != 0) {
                    desktops.get(currentDesktop - 1).startASyncRender();
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
        Animation.width = 0;
        setFrameRates();
        return true;
    }

    public void setDesktopsRender(boolean b) {
        desktops.get(currentDesktop).setASyncRender(b);
        if (currentDesktop != desktopCount - 1) {
            desktops.get(currentDesktop + 1).setASyncRender(b);
        }
        if (currentDesktop != 0) {
            desktops.get(currentDesktop - 1).setASyncRender(b);
        }
        setFrameRates();
    }

    public static void setFrameRates() {
        desktops.get(currentDesktop).setFrameRate(60);
        if (currentDesktop != desktopCount - 1) {
            desktops.get(currentDesktop + 1).setFrameRate(1);
        }
        if (currentDesktop != 0) {
            desktops.get(currentDesktop - 1).setFrameRate(1);
        }
    }

    public void mouseVelocityListener() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (Animation.velocity != 0) {
                        Animation.velocity = (int) (Animation.velocity / 1.030);
                        if (!Animation.mousePressed) {
                            if ((currentDesktop != 0 || Animation.width < Options.sideDesktopMargin) && (currentDesktop != desktopCount - 1 || Animation.width > -Options.sideDesktopMargin)) {
                                Animation.width -= (Animation.velocity * 0.2);
                                Animation.mouseVelocity();
                            }
                        }
                    }
                }
            }
        }).start();
    }

    public void scrollAnimation(int direction) {
        if (!changeDesktop(direction))
            return;
        if (animationThread != null && animationThread.isAlive()) {
            animationThread.interrupt();
        }
        animationThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int j = SCREEN_SIZE.width - (int) ((SCREEN_SIZE.width * Animation.currentDesktopZoomOut)) - (Options.sideDesktopMargin * 2);
                if (direction == 1) {
                    for (int i = j; i > 0; i--) {
                        Animation.width = i;
                        if (i % 10 == 0) {
                            try {
                                Thread.sleep(1);
                            } catch (InterruptedException e1) {
                                return;
                            }
                            if (animationThread.isInterrupted())
                                return;
                        }
                        Animation.calculateAnimation();
                    }
                    Animation.width = 0;
                } else if (direction == -1) {
                    for (int i = j; i > 0; i--) {
                        Animation.width = -i;
                        if (i % 10 == 0) {
                            try {
                                Thread.sleep(1);
                            } catch (InterruptedException e1) {
                                return;
                            }
                            if (animationThread.isInterrupted())
                                return;
                        }
                        Animation.calculateAnimation();
                    }
                    Animation.width = 0;
                }
            }
        });
        animationThread.start();
    }

    public JDialog getOptionsDialog() {
        return null;
    }

    public String getDescription() {
        return "Desktop Overview";
    }

    @Override
    public ASyncRenderer getASyncRenderer() {
        return aSyncRenderer;
    }

    @Override
    public void readFromJson() {
    }

    @Override
    public void writeToJson() {
    }

    @Override
    public String getName() {
        return "Desktop Overview";
    }

    @Override
    public void enable() {
        hotkey.register();
    }

    @Override
    public void disable() {
        hotkey.unRegister();
    }

    private static class Options {
        private static double currentDesktopZoomOut = 0.2;
        private static int sideDesktopMargin = 100;
        private static double sideDesktopZoomOut = 0.5;
    }

    public static class Position {
        public int x = 0;
        public int y = 0;
        public int width = 0;
        public int height = 0;

        public Position() {
        }

        public Position(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
    private static class Animation {
        public static double animationNormalizedPosition = 0;
        public static int width = 0;
        public static int height = 0;
        public static int animationDirection = 1;
        public static double currentDesktopZoomOut = 0;
        public static double rightDesktopZoomOut = 0;
        public static double leftDesktopZoomOut = 0;
        public static int[] mousePos = new int[2];
        public static int velocity = 0;
        public static int prevPos = 0;
        public static long lastPrevPos = 0;
        public static boolean mousePressed = false;
        public static boolean desktopChanging = false;

        public static void calculateAnimation() {
            if (width != 0) {
                animationNormalizedPosition = 1 - ((double) (DesktopOverview.SCREEN_SIZE.width - ((SCREEN_SIZE.width * Options.currentDesktopZoomOut) + (Options.sideDesktopMargin * 2)) - (width))
                        / (double) (DesktopOverview.SCREEN_SIZE.width - ((SCREEN_SIZE.width * Options.currentDesktopZoomOut) + (Options.sideDesktopMargin * 2))));
                if (width > 0)
                    animationDirection = 1;
                else
                    animationDirection = -1;
            } else
                animationNormalizedPosition = 0;
            currentDesktopZoomOut = (Options.currentDesktopZoomOut + (animationNormalizedPosition * (Options.sideDesktopZoomOut - Options.currentDesktopZoomOut) * animationDirection));
            if (animationDirection == 1) {
                leftDesktopZoomOut = (Options.sideDesktopZoomOut + (animationNormalizedPosition * (Options.currentDesktopZoomOut - Options.sideDesktopZoomOut)));
                rightDesktopZoomOut = (Options.sideDesktopZoomOut + ((animationNormalizedPosition) * (Options.currentDesktopZoomOut - Options.sideDesktopZoomOut)));
            } else {
                leftDesktopZoomOut = (Options.sideDesktopZoomOut + (-animationNormalizedPosition * (Options.currentDesktopZoomOut - Options.sideDesktopZoomOut)));
                rightDesktopZoomOut = (Options.sideDesktopZoomOut + ((-animationNormalizedPosition) * (Options.currentDesktopZoomOut - Options.sideDesktopZoomOut)));
            }

            DesktopOverview.dPositions[1].x = (int) ((SCREEN_SIZE.width * Animation.currentDesktopZoomOut) / 2);
            DesktopOverview.dPositions[1].y = (int) ((SCREEN_SIZE.height * Animation.currentDesktopZoomOut) / 2);
            DesktopOverview.dPositions[1].width = SCREEN_SIZE.width - (int) ((SCREEN_SIZE.width * Animation.currentDesktopZoomOut));
            DesktopOverview.dPositions[1].height = SCREEN_SIZE.height - (int) ((SCREEN_SIZE.height * Animation.currentDesktopZoomOut));

            DesktopOverview.dPositions[0].x = dPositions[1].x - ((SCREEN_SIZE.width - (int) ((SCREEN_SIZE.width * Animation.leftDesktopZoomOut))) + Options.sideDesktopMargin);
            DesktopOverview.dPositions[0].y = (int) ((SCREEN_SIZE.height * Animation.leftDesktopZoomOut) / 2);
            DesktopOverview.dPositions[0].width = SCREEN_SIZE.width - (int) ((SCREEN_SIZE.width * Animation.leftDesktopZoomOut));
            DesktopOverview.dPositions[0].height = SCREEN_SIZE.height - (int) ((SCREEN_SIZE.height * Animation.leftDesktopZoomOut));

            DesktopOverview.dPositions[2].x = dPositions[1].x + dPositions[1].width + Options.sideDesktopMargin;
            DesktopOverview.dPositions[2].y = (int) ((SCREEN_SIZE.height * Animation.rightDesktopZoomOut) / 2);
            DesktopOverview.dPositions[2].width = SCREEN_SIZE.width - (int) (((SCREEN_SIZE.width) * Animation.rightDesktopZoomOut));
            DesktopOverview.dPositions[2].height = SCREEN_SIZE.height - (int) ((SCREEN_SIZE.height * Animation.rightDesktopZoomOut));

            dPositions[0].x += width;
            dPositions[1].x += width;
            dPositions[2].x += width;

            DesktopOverview.bPositions[0].x = dPositions[0].x - (int) (Options.sideDesktopMargin * 0.5);
            DesktopOverview.bPositions[1].x = dPositions[1].x - (int) (Options.sideDesktopMargin * 0.5);
            DesktopOverview.bPositions[2].x = (dPositions[2].x - (int) (Options.sideDesktopMargin * 0.5));

        }

        public static void mouseDrag() {
            calculateAnimation();
            if (animationNormalizedPosition > 0.50) {
                desktopChanging = true;
                DesktopOverview.changeDesktop(-1);
                mousePos[0] += (SCREEN_SIZE.width - (int) ((SCREEN_SIZE.width * (leftDesktopZoomOut))) + (Options.sideDesktopMargin)) - 10;
                width = canvas.getMousePosition().x - mousePos[0];
                calculateAnimation();
                desktopChanging = false;
            } else if (animationNormalizedPosition < -0.50) {
                desktopChanging = true;
                DesktopOverview.changeDesktop(1);
                mousePos[0] -= (SCREEN_SIZE.width - (int) (((SCREEN_SIZE.width) * (rightDesktopZoomOut))) + (Options.sideDesktopMargin)) - 10;
                width = canvas.getMousePosition().x - mousePos[0];
                calculateAnimation();
                desktopChanging = false;
            }
        }

        public static void mouseVelocity() {
            calculateAnimation();
            if (animationNormalizedPosition > 1) {
                desktopChanging = true;
                DesktopOverview.changeDesktop(-1);
                calculateAnimation();
                desktopChanging = false;
            } else if (animationNormalizedPosition < -1) {
                desktopChanging = true;
                DesktopOverview.changeDesktop(1);
                calculateAnimation();
                desktopChanging = false;
            }
        }
    }
}
