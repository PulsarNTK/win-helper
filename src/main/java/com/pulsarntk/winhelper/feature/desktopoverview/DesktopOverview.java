package com.pulsarntk.winhelper.feature.desktopoverview;

import java.awt.Color;
import java.rmi.UnexpectedException;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import com.pulsarntk.winhelper.itf.Configurable;
import com.pulsarntk.winhelper.itf.Feature;
import com.pulsarntk.winhelper.lib.User32Extra;
import com.pulsarntk.winhelper.lib.VirtualDesktopAccessor;
import com.pulsarntk.winhelper.utils.RegisterHotkey;
import com.pulsarntk.winhelper.utils.RegisterHotkey.VKMap;
import com.pulsarntk.winhelper.utils.RegisterHotkey.HotkeyListener;
import com.sun.jna.platform.win32.WinUser.MSG;
import java.awt.*;
import com.pulsarntk.winhelper.lib.VirtualDesktopAccessor;
import com.sun.jna.platform.win32.WinDef.*;
import java.awt.Graphics;
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


public class DesktopOverview extends Canvas implements Feature {
    private RegisterHotkey hotkey;
    private JFrame frame = new JFrame("Desktop Overview");
    private List<WindowInfo> wList = new ArrayList<WindowInfo>();
    private List<Integer> order = new ArrayList<Integer>();
    private Dimension size = getToolkit().getDefaultToolkit().getScreenSize();
    private Dimension margin = new Dimension((int) size.getWidth() / 100 * 10, (int) size.getHeight() / 100 * 10);
    private List<Desktop> desktops = new ArrayList<Desktop>();
    int currentDesktop = 0;


    public DesktopOverview() throws UnexpectedException, InterruptedException {
        frame.setUndecorated(true);
        // frame.setAlwaysOnTop(true);
        frame.setSize(size);
        frame.add(this);
        frame.setBackground(new Color(0, 0, 0, 50));

        hotkey = new RegisterHotkey(VKMap.VK_TAB, 0x02);
        hotkey.setHotkeyListener(new HotkeyListener() {
            public void onKeyPress(MSG msg) {
                frame.setVisible(!frame.isVisible());
            }
        });

        addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                int rotation = e.getWheelRotation();
                if (rotation == -1) {
                    if (currentDesktop != 0) {
                        currentDesktop--;
                    }
                } else if (rotation == 1) {
                    if (currentDesktop != VirtualDesktopAccessor.INSTANCE.GetDesktopCount() - 1) {
                        currentDesktop++;
                    }
                }
                System.out.println(currentDesktop);
            }
        });

        int dCount = VirtualDesktopAccessor.INSTANCE.GetDesktopCount();
        for (int i = 0; i < dCount; i++) {
            desktops.add(new Desktop(i, size));
        }
        this.currentDesktop = VirtualDesktopAccessor.INSTANCE.GetCurrentDesktopNumber();
        getWindowsZOrder();

        while (true) {
            frame.repaint();
        }
    }

    private void getWindowsZOrder() {
        order.clear();
        wList.clear();
        int top = User32Extra.INSTANCE.GetTopWindow(0);
        while (top != 0) {
            order.add(top);
            top = User32Extra.INSTANCE.GetWindow(top, User32Extra.GW_HWNDNEXT);
        }
        User32Extra.INSTANCE.EnumWindows(new User32Extra.WndEnumProc() {
            public boolean callback(int hwnd, int lParam) {
                if (User32Extra.INSTANCE.IsWindowVisible(hwnd)) {
                    int desktopNumber = VirtualDesktopAccessor.INSTANCE.GetWindowDesktopNumber(hwnd);
                    if (desktopNumber != -1) {
                        RECT r = new RECT();
                        User32Extra.INSTANCE.GetWindowRect(hwnd, r);
                        byte[] buffer = new byte[1024];
                        User32Extra.INSTANCE.GetWindowTextA(hwnd, buffer, buffer.length);
                        boolean isMaximized = User32Extra.INSTANCE.IsZoomed(hwnd);
                        String title = Native.toString(buffer);
                        wList.add(new WindowInfo(hwnd, r, title, desktopNumber, isMaximized));
                        desktops.get(desktopNumber).addWindow(new WindowInfo(hwnd, r, title, desktopNumber, isMaximized));
                    }
                }
                return true;
            }
        }, 0);
        Collections.sort(wList, new Comparator<WindowInfo>() {
            public int compare(WindowInfo o1, WindowInfo o2) {
                return order.indexOf(o2.hwnd) - order.indexOf(o1.hwnd);
            }
        });
        for (Desktop desktop : desktops) {
            Collections.sort(desktop.wList, new Comparator<WindowInfo>() {
                public int compare(WindowInfo o1, WindowInfo o2) {
                    return order.indexOf(o2.hwnd) - order.indexOf(o1.hwnd);
                }
            });
        }
    }

    public void paint(Graphics g) {
        g.clearRect(0, 0, getWidth(), getHeight());
        g.drawImage(desktops.get(currentDesktop).render(), 0, 0, null);
    }

    public JPanel getSettingsPanel() {
        return null;
    }

    public String getDescription() {
        return null;
    }
}
