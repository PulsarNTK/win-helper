package com.pulsarntk.winhelper.utils.hotkey;

import com.sun.jna.platform.win32.User32;
import java.rmi.UnexpectedException;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinUser.MSG;

public class RegisterHotkey extends Thread {
    private HotkeyListener listener = new HotkeyListener() {
        public void onKeyPress(MSG msg) {
            return;
        }
    };
    private int vKCode;
    private int fsModifiers;
    public boolean status;
    private int threadId = Kernel32.INSTANCE.GetCurrentThreadId();

    public RegisterHotkey(int vKCode, int fsModifiers) throws UnexpectedException, InterruptedException {
        this.vKCode = vKCode;
        this.fsModifiers = fsModifiers;
        this.start();
        synchronized (this) {
            this.wait();
            if (!this.status) {
                this.interrupt();
                throw new UnexpectedException("An error occured when RegisterHotkey");
            }
        }
    }

    @Override
    public void run() {
        threadId = Kernel32.INSTANCE.GetCurrentThreadId();
        synchronized (this) {
            status = User32.INSTANCE.RegisterHotKey(null, threadId, this.fsModifiers, this.vKCode);
            this.notify();
        }
        MSG msg = new MSG();
        while (!this.isInterrupted()) {
            User32.INSTANCE.GetMessage(msg, null, 0, 0);
            listener.onKeyPress(msg);
        }
    }

    public void unRegisterHotkey() {
        this.interrupt();
    }

    public void setHotkeyListener(HotkeyListener listener) {
        this.listener = listener;
    }

    public interface HotkeyListener {
        public void onKeyPress(MSG msg);
    }
}
