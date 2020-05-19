package com.pulsarntk.winhelper.feature.desktopoverview;

import java.rmi.UnexpectedException;
import java.util.ArrayList;
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
import com.pulsarntk.winhelper.utils.RegisterHotkey.VKMap;
import com.pulsarntk.winhelper.utils.RegisterHotkey.HotkeyListener;
import com.sun.jna.platform.win32.WinUser.MSG;
import java.awt.*;
import com.pulsarntk.winhelper.lib.VirtualDesktopAccessor;
import com.pulsarntk.winhelper.lib.VirtualDesktopAccessor.VirtualDesktopListener;
import com.sun.jna.platform.win32.WinDef.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;

import javax.swing.JFrame;


import com.sun.jna.Memory;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HBITMAP;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinGDI;
import com.sun.jna.platform.win32.WinGDI.BITMAPINFO;
import com.sun.jna.platform.win32.WinNT.HANDLE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import com.pulsarntk.winhelper.feature.desktopoverview.utils.WindowCapture;
import com.pulsarntk.winhelper.feature.desktopoverview.utils.WindowInfo;
import java.awt.image.BufferStrategy;

public class DesktopOverview extends JFrame implements Feature, Renderable, ASyncRenderable {
    private RegisterHotkey hotkey;
    private Canvas canvas = new Canvas();
    private Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
    private Dimension margin = new Dimension(SCREEN_SIZE.width / 100 * 10, SCREEN_SIZE.height / 100 * 10);
    private Dimension animation = new Dimension(0, 0);
    private List<Desktop> desktops = new ArrayList<Desktop>();
    int currentDesktop = 0;
    int desktopCount = 0;
    Thread renderThread;
    int frames;
    public ASyncRenderer aSyncRenderer = new ASyncRenderer(this);

    public DesktopOverview() throws UnexpectedException, InterruptedException {
        super("Desktop Overview");
        desktopCount = VirtualDesktopAccessor.INSTANCE.GetDesktopCount();
        currentDesktop = VirtualDesktopAccessor.INSTANCE.GetCurrentDesktopNumber();
        for (int i = 0; i < desktopCount; i++) {
            desktops.add(new Desktop(i, SCREEN_SIZE));
        }
        getWindowsZOrder();

        canvas.setSize(SCREEN_SIZE);
        setUndecorated(true);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);
        add(canvas);
        pack();
        canvas.createBufferStrategy(2);


        hotkey = new RegisterHotkey(VKMap.VK_TAB, 0x02);
        hotkey.setHotkeyListener(new HotkeyListener() {
            public void onKeyPress(MSG msg) {
                setVisible(!isVisible());
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
                int rotation = e.getWheelRotation();
                if (rotation == -1) {
                    if (currentDesktop != 0) {
                        currentDesktop--;
                    }
                } else if (rotation == 1) {
                    if (currentDesktop != desktopCount - 1) {
                        currentDesktop++;
                    }
                }
                System.out.println(currentDesktop);
            }
        });


    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            aSyncRenderer.startASyncRender();
            desktops.get(currentDesktop).getASyncRenderer().startASyncRender();
        } else {
            aSyncRenderer.stopASyncRender();
            desktops.get(currentDesktop).getASyncRenderer().stopASyncRender();
        }
        super.setVisible(visible);
    }

    private void getWindowsZOrder() {
        User32Extra.INSTANCE.EnumWindows(new User32Extra.WndEnumProc() {
            public boolean callback(int hwnd, int lParam) {
                if (User32Extra.INSTANCE.IsWindowVisible(hwnd)) {
                    int desktopNumber = VirtualDesktopAccessor.INSTANCE.GetWindowDesktopNumber(hwnd);
                    if (desktopNumber != -1) {
                        RECT rect = new RECT();
                        User32Extra.INSTANCE.GetWindowRect(hwnd, rect);
                        byte[] buffer = new byte[1024];
                        User32Extra.INSTANCE.GetWindowTextA(hwnd, buffer, buffer.length);
                        String title = Native.toString(buffer);
                        boolean isMaximized = User32Extra.INSTANCE.IsZoomed(hwnd);
                        desktops.get(desktopNumber).addWindowInfo(new WindowInfo(hwnd, rect, title, desktopNumber, isMaximized));
                    }
                }
                return true;
            }
        }, 0);
    }

    @Override
    public void render() {
        BufferStrategy buffer = canvas.getBufferStrategy();
        Graphics2D g2d = (Graphics2D) buffer.getDrawGraphics();
        desktops.get(currentDesktop).getASyncRenderer().startASyncRender();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.clearRect(0, 0, getWidth(), getHeight());
        g2d.drawImage(desktops.get(currentDesktop).getImage(), margin.width, margin.height, SCREEN_SIZE.width - (margin.width * 2), SCREEN_SIZE.height - (margin.height * 2), null);
        buffer.show();
        System.out.println(frames++);
    }

    public JPanel getSettingsPanel() {
        return null;
    }

    public String getDescription() {
        return null;
    }

    @Override
    public ASyncRenderer getASyncRenderer() {
        return aSyncRenderer;
    }

}
