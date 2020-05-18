package com.pulsarntk.winhelper.lib;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.*;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinUser.MSG;

public interface VirtualDesktopAccessor extends Library {
    VirtualDesktopAccessor INSTANCE = (VirtualDesktopAccessor) Native.load("lib/VirtualDesktopAccessor.dll", VirtualDesktopAccessor.class);

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
        public static enum MESSAGE {
            CURRENT_VIRTUAL_DESKTOP_CHANGED, VIEW_VIRTUAL_DESKTOP_CHANGED, VIRTUAL_DESKTOP_DESTROYED, VIRTUAL_DESKTOP_DESTROY_FAILED, VIRTUAL_DESKTOP_DESTROY_BEGIN, VIRTUAL_DESKTOP_CREATED
        }

        Listener listener;

        public VirtualDesktopListener(Listener listener) {
            this.listener = listener;
            this.start();
        }

        @Override
        public void run() {
            VirtualDesktopAccessor.INSTANCE.RegisterPostMessageHook(Kernel32.INSTANCE.GetCurrentThreadId(), 0);
            while (!isInterrupted()) {
                MSG msg = new MSG();
                User32.INSTANCE.GetMessage(msg, null, 0, 0);
                listener.onMessage(msg);
            }
            super.run();
        }

        public interface Listener {
            void onMessage(MSG msg);
        }
    }
}