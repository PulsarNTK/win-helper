package com.pulsarntk.winhelper.utils;

import com.sun.jna.platform.win32.User32;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;
import com.pulsarntk.winhelper.lib.Kernel32Extra;
import com.pulsarntk.winhelper.lib.User32Extra;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinUser.MSG;

public class RegisterHotkey {

    private static Map<Integer, RegisterHotkey> hotkeys = new HashMap<Integer, RegisterHotkey>();
    public static Thread thread;
    public static int threadId;

    static {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                threadId = Kernel32.INSTANCE.GetCurrentThreadId();
                MSG msg = new MSG();
                while (true) {
                    User32.INSTANCE.GetMessage(msg, null, 0x312, 0x315);
                    RegisterHotkey rh = hotkeys.get(msg.wParam.intValue());
                    if (rh != null) {
                        synchronized (rh) {
                            switch (msg.message) {
                                case 0x0312:
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            rh.listener.onKeyPress(msg);
                                        }
                                    }).start();
                                    break;
                                case 0x0313:
                                    if (User32Extra.INSTANCE.RegisterHotKey(null, rh.id, rh.fsModifiers, rh.vKCode)) {
                                        rh.status = true;
                                    } else {
                                        System.out.println("An error occured when REGISTER hotkey! -> GetLastError:" + Kernel32Extra.INSTANCE.GetLastError());
                                    }
                                    break;
                                case 0x0315:
                                    if (User32Extra.INSTANCE.UnregisterHotKey(null, rh.id)) {
                                        rh.status = false;
                                    } else {
                                        System.out.println("An error occured when UNREGISTER hotkey!");
                                    }
                                    break;
                                default:
                                    System.out.println(msg.message);
                                    break;
                            }
                            rh.notify();
                        }
                    }
                }
            }
        });
        thread.start();
    }

    private HotkeyListener listener = new HotkeyListener() {
        public void onKeyPress(MSG msg) {
            return;
        }
    };
    public int vKCode;
    public int fsModifiers;
    public int id;
    public boolean status = false;

    public RegisterHotkey(int vKCode, int fsModifiers) {
        this.vKCode = vKCode;
        this.fsModifiers = fsModifiers;
        do {
            this.id = new Random().nextInt(Integer.MAX_VALUE);
        } while (hotkeys.get(this.id) != null);
        hotkeys.put(this.id, this);
    }

    public boolean register() {
        if (!status) {
            synchronized (this) {
                if (User32Extra.INSTANCE.PostThreadMessageA(threadId, 0x313, this.id, 0)) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            return status;
        }
        return true;
    }

    public boolean unRegister() {
        if (status) {
            synchronized (this) {
                if (User32Extra.INSTANCE.PostThreadMessageA(threadId, 0x315, this.id, 0)) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            return !status;
        }
        return true;
    }

    public void setHotkeyListener(HotkeyListener listener) {
        this.listener = listener;
    }

    public void remove() {
        unRegister();
        hotkeys.remove(this.id);
    }

    public interface HotkeyListener {
        public void onKeyPress(MSG msg);
    }

    public enum KEY {
        VK_KEY(0x0), //
        VK_LBUTTON(0x1), //
        VK_RBUTTON(0x2), //
        VK_CANCEL(0x3), //
        VK_MBUTTON(0x4), //
        VK_XBUTTON1(0x5), //
        VK_XBUTTON2(0x6), //
        VK_0x07(0x7), //
        VK_BACK(0x8), //
        VK_TAB(0x9), //
        VK_0x0A(0xa), //
        VK_0x0B(0xb), //
        VK_CLEAR(0xc), //
        VK_RETURN(0xd), //
        VK_0x0E(0xe), //
        VK_0x0F(0xf), //
        VK_SHIFT(0x10), //
        VK_CONTROL(0x11), //
        VK_MENU(0x12), //
        VK_PAUSE(0x13), //
        VK_CAPITAL(0x14), //
        VK_KANA_HANGUEL_HANGUL(0x15), //
        VK_IME_ON(0x16), //
        VK_JUNJA(0x17), //
        VK_FINAL(0x18), //
        VK_HANJA_KANJI(0x19), //
        VK_IME_OFF(0x1a), //
        VK_ESCAPE(0x1b), //
        VK_CONVERT(0x1c), //
        VK_NONCONVERT(0x1d), //
        VK_ACCEPT(0x1e), //
        VK_MODECHANGE(0x1f), //
        VK_SPACE(0x20), //
        VK_PRIOR(0x21), //
        VK_NEXT(0x22), //
        VK_END(0x23), //
        VK_HOME(0x24), //
        VK_LEFT(0x25), //
        VK_UP(0x26), //
        VK_RIGHT(0x27), //
        VK_DOWN(0x28), //
        VK_SELECT(0x29), //
        VK_PRINT(0x2a), //
        VK_EXECUTE(0x2b), //
        VK_SNAPSHOT(0x2c), //
        VK_INSERT(0x2d), //
        VK_DELETE(0x2e), //
        VK_HELP(0x2f), //
        VK_0(0x30), //
        VK_1(0x31), //
        VK_2(0x32), //
        VK_3(0x33), //
        VK_4(0x34), //
        VK_5(0x35), //
        VK_6(0x36), //
        VK_7(0x37), //
        VK_8(0x38), //
        VK_9(0x39), //
        VK_0x3A(0x3a), //
        VK_0x3B(0x3b), //
        VK_0x3C(0x3c), //
        VK_0x3D(0x3d), //
        VK_0x3E(0x3e), //
        VK_0x3F(0x3f), //
        VK_0x40(0x40), //
        VK_A(0x41), //
        VK_B(0x42), //
        VK_C(0x43), //
        VK_D(0x44), //
        VK_E(0x45), //
        VK_F(0x46), //
        VK_G(0x47), //
        VK_H(0x48), //
        VK_I(0x49), //
        VK_J(0x4a), //
        VK_K(0x4b), //
        VK_L(0x4c), //
        VK_M(0x4d), //
        VK_N(0x4e), //
        VK_O(0x4f), //
        VK_P(0x50), //
        VK_Q(0x51), //
        VK_R(0x52), //
        VK_S(0x53), //
        VK_T(0x54), //
        VK_U(0x55), //
        VK_V(0x56), //
        VK_W(0x57), //
        VK_X(0x58), //
        VK_Y(0x59), //
        VK_Z(0x5a), //
        VK_LWIN(0x5b), //
        VK_RWIN(0x5c), //
        VK_APPS(0x5d), //
        VK_0x5E(0x5e), //
        VK_SLEEP(0x5f), //
        VK_NUMPAD0(0x60), //
        VK_NUMPAD1(0x61), //
        VK_NUMPAD2(0x62), //
        VK_NUMPAD3(0x63), //
        VK_NUMPAD4(0x64), //
        VK_NUMPAD5(0x65), //
        VK_NUMPAD6(0x66), //
        VK_NUMPAD7(0x67), //
        VK_NUMPAD8(0x68), //
        VK_NUMPAD9(0x69), //
        VK_MULTIPLY(0x6a), //
        VK_ADD(0x6b), //
        VK_SEPARATOR(0x6c), //
        VK_SUBTRACT(0x6d), //
        VK_DECIMAL(0x6e), //
        VK_DIVIDE(0x6f), //
        VK_F1(0x70), //
        VK_F2(0x71), //
        VK_F3(0x72), //
        VK_F4(0x73), //
        VK_F5(0x74), //
        VK_F6(0x75), //
        VK_F7(0x76), //
        VK_F8(0x77), //
        VK_F9(0x78), //
        VK_F10(0x79), //
        VK_F11(0x7a), //
        VK_F12(0x7b), //
        VK_F13(0x7c), //
        VK_F14(0x7d), //
        VK_F15(0x7e), //
        VK_F16(0x7f), //
        VK_F17(0x80), //
        VK_F18(0x81), //
        VK_F19(0x82), //
        VK_F20(0x83), //
        VK_F21(0x84), //
        VK_F22(0x85), //
        VK_F23(0x86), //
        VK_F24(0x87), //
        VK_0x88(0x88), //
        VK_0x89(0x89), //
        VK_0x8A(0x8a), //
        VK_0x8B(0x8b), //
        VK_0x8C(0x8c), //
        VK_0x8D(0x8d), //
        VK_0x8E(0x8e), //
        VK_0x8F(0x8f), //
        VK_NUMLOCK(0x90), //
        VK_SCROLL(0x91), //
        VK_0x92(0x92), //
        VK_0x93(0x93), //
        VK_0x94(0x94), //
        VK_0x95(0x95), //
        VK_0x96(0x96), //
        VK_0x97(0x97), //
        VK_0x98(0x98), //
        VK_0x99(0x99), //
        VK_0x9A(0x9a), //
        VK_0x9B(0x9b), //
        VK_0x9C(0x9c), //
        VK_0x9D(0x9d), //
        VK_0x9E(0x9e), //
        VK_0x9F(0x9f), //
        VK_LSHIFT(0xa0), //
        VK_RSHIFT(0xa1), //
        VK_LCONTROL(0xa2), //
        VK_RCONTROL(0xa3), //
        VK_LMENU(0xa4), //
        VK_RMENU(0xa5), //
        VK_BROWSER_BACK(0xa6), //
        VK_BROWSER_FORWARD(0xa7), //
        VK_BROWSER_REFRESH(0xa8), //
        VK_BROWSER_STOP(0xa9), //
        VK_BROWSER_SEARCH(0xaa), //
        VK_BROWSER_FAVORITES(0xab), //
        VK_BROWSER_HOME(0xac), //
        VK_VOLUME_MUTE(0xad), //
        VK_VOLUME_DOWN(0xae), //
        VK_VOLUME_UP(0xaf), //
        VK_MEDIA_NEXT_TRACK(0xb0), //
        VK_MEDIA_PREV_TRACK(0xb1), //
        VK_MEDIA_STOP(0xb2), //
        VK_MEDIA_PLAY_PAUSE(0xb3), //
        VK_LAUNCH_MAIL(0xb4), //
        VK_LAUNCH_MEDIA_SELECT(0xb5), //
        VK_LAUNCH_APP1(0xb6), //
        VK_LAUNCH_APP2(0xb7), //
        VK_0xB8(0xb8), //
        VK_0xB9(0xb9), //
        VK_OEM_1(0xba), //
        VK_OEM_PLUS(0xbb), //
        VK_OEM_COMMA(0xbc), //
        VK_OEM_MINUS(0xbd), //
        VK_OEM_PERIOD(0xbe), //
        VK_OEM_2(0xbf), //
        VK_OEM_3(0xc0), //
        VK_0xC1(0xc1), //
        VK_0xC2(0xc2), //
        VK_0xC3(0xc3), //
        VK_0xC4(0xc4), //
        VK_0xC5(0xc5), //
        VK_0xC6(0xc6), //
        VK_0xC7(0xc7), //
        VK_0xC8(0xc8), //
        VK_0xC9(0xc9), //
        VK_0xCA(0xca), //
        VK_0xCB(0xcb), //
        VK_0xCC(0xcc), //
        VK_0xCD(0xcd), //
        VK_0xCE(0xce), //
        VK_0xCF(0xcf), //
        VK_0xD0(0xd0), //
        VK_0xD1(0xd1), //
        VK_0xD2(0xd2), //
        VK_0xD3(0xd3), //
        VK_0xD4(0xd4), //
        VK_0xD5(0xd5), //
        VK_0xD6(0xd6), //
        VK_0xD7(0xd7), //
        VK_0xD8(0xd8), //
        VK_0xD9(0xd9), //
        VK_0xDA(0xda), //
        VK_OEM_4(0xdb), //
        VK_OEM_5(0xdc), //
        VK_OEM_6(0xdd), //
        VK_OEM_7(0xde), //
        VK_OEM_8(0xdf), //
        VK_0xE0(0xe0), //
        VK_0xE1(0xe1), //
        VK_OEM_102(0xe2), //
        VK_0xE3(0xe3), //
        VK_0xE4(0xe4), //
        VK_PROCESSKEY(0xe5), //
        VK_0xE6(0xe6), //
        VK_PACKET(0xe7), //
        VK_0xE8(0xe8), //
        VK_0xE9(0xe9), //
        VK_0xEA(0xea), //
        VK_0xEB(0xeb), //
        VK_0xEC(0xec), //
        VK_0xED(0xed), //
        VK_0xEE(0xee), //
        VK_0xEF(0xef), //
        VK_0xF0(0xf0), //
        VK_0xF1(0xf1), //
        VK_0xF2(0xf2), //
        VK_0xF3(0xf3), //
        VK_0xF4(0xf4), //
        VK_0xF5(0xf5), //
        VK_ATTN(0xf6), //
        VK_CRSEL(0xf7), //
        VK_EXSEL(0xf8), //
        VK_EREOF(0xf9), //
        VK_PLAY(0xfa), //
        VK_ZOOM(0xfb), //
        VK_NONAME(0xfc), //
        VK_PA1(0xfd), //
        VK_OEM_CLEAR(0xfe); //

        public int code;

        KEY(int code) {
            this.code = code;
        }

        public static KEY[] getDefinedKeys() {
            return Stream.of(values()).filter(key -> key.name().length() < 5 || !key.name().substring(0, 5).equals("VK_0x")).toArray(KEY[]::new);
        }

        @Override
        public String toString() {
            return name().substring(3);
        }

    }
}
