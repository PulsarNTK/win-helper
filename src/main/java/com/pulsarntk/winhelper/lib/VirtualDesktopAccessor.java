package com.pulsarntk.winhelper.lib;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.*;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinUser.MSG;

public interface VirtualDesktopAccessor extends Library {
    VirtualDesktopAccessor INSTANCE = (VirtualDesktopAccessor) Native.load("lib/VirtualDesktopAccessorModified.dll", VirtualDesktopAccessor.class);

    int GetCurrentDesktopNumber();

    int GetDesktopCount();

    int GetWindowDesktopNumber(HWND window);

    int GetWindowDesktopNumber(int window);

    int IsWindowOnCurrentVirtualDesktop(HWND window);

    BOOL MoveWindowToDesktopNumber(HWND window, int number);

    void GoToDesktopNumber(int number);

    void RegisterPostMessageHook(int listener, int messageOffset);

    void UnregisterPostMessageHook(int hwnd);

    /**
     * @return 1 if pinned, 0 if not pinned, -1 if not valid
     */
    int IsPinnedWindow(HWND hwnd);

    /**
     * 
     * @param hwnd of the window to be pinned
     */
    void PinWindow(HWND hwnd);

    /**
     * 
     * @param hwnd of the window to be unpinned
     */
    void UnPinWindow(HWND hwnd);

    /**
     * 
     * @return 1 if pinned, 0 if not pinned, -1 if not valid
     */
    int IsPinnedApp(HWND hwnd);

    void PinApp(HWND hwnd);

    void UnPinApp(HWND hwnd);

    int IsWindowOnDesktopNumber(HWND window, int number);

    /**
     * Call this during taskbar created message.
     */
    void RestartVirtualDesktopAccessor();

    /**
     * Is the window shown in Alt+Tab list?
     */
    int ViewIsShownInSwitchers(HWND hwnd);

    /**
     * Is the window visible?
     */
    int ViewIsVisible(HWND hwnd);

    /**
     * Get thumbnail handle for a window, possibly peek preview of Alt+Tab
     */
    HWND ViewGetThumbnailHwnd(HWND hwnd);

    /**
     * Set focus like Alt+Tab switcher
     */
    void ViewSetFocus(HWND hwnd);

    /**
     * Get focused window thumbnail handle
     */
    HWND ViewGetFocused();

    /**
     * Switch to window like Alt+Tab switcher
     */
    void ViewSwitchTo(HWND hwnd);

    /***
     * Get windows in Z-order (NOT alt-tab order)
     */
    int ViewGetByZOrder(HWND windows, UINT count, BOOL onlySwitcherWindows, BOOL onlyCurrentDesktop); //

    /**
     * Get windows in alt tab order
     */
    int ViewGetByLastActivationOrder(HWND windows, UINT count, BOOL onlySwitcherWindows, BOOL onlyCurrentDesktop);

    int ViewGetLastActivationTimestamp(HWND windows); // Get last activation timestamp

    public class VirtualDesktopListener extends Thread {
        public static final int CURRENT_VIRTUAL_DESKTOP_CHANGED = 0;
        public static final int VIEW_VIRTUAL_DESKTOP_CHANGED = 1;
        public static final int VIRTUAL_DESKTOP_DESTROYED = 2;
        public static final int VIRTUAL_DESKTOP_DESTROY_FAILED = 3;
        public static final int VIRTUAL_DESKTOP_DESTROY_BEGIN = 4;
        public static final int VIRTUAL_DESKTOP_CREATED = 5;

        Listener listener;
        public int threadId = 0;

        public VirtualDesktopListener(Listener listener) {
            this.listener = listener;
            synchronized (this) {
                this.start();
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void run() {
            synchronized (this) {
                threadId = Kernel32.INSTANCE.GetCurrentThreadId();
                notify();
            }
            while (!isInterrupted()) {
                MSG msg = new MSG();
                User32.INSTANCE.GetMessage(msg, null, 0, 0);
                listener.onMessage(msg);
            }
            super.run();
        }

        public void unRegister() {
            VirtualDesktopAccessor.INSTANCE.UnregisterPostMessageHook(threadId);
        }

        public void register() {
            VirtualDesktopAccessor.INSTANCE.RegisterPostMessageHook(threadId, 0);
        }

        public interface Listener {
            void onMessage(MSG msg);
        }
    }
}
