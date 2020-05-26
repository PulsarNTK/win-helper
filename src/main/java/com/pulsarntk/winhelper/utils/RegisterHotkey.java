package com.pulsarntk.winhelper.utils;

import com.sun.jna.platform.win32.User32;
import java.rmi.UnexpectedException;
import java.util.stream.Stream;
import com.pulsarntk.winhelper.lib.User32Extra;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinUser.MSG;

public class RegisterHotkey extends Thread {
        private HotkeyListener listener = new HotkeyListener() {
                public void onKeyPress(MSG msg) {
                        return;
                }
        };
        public int vKCode;
        public int fsModifiers;
        public boolean status;
        private int threadId;

        public RegisterHotkey(int vKCode, int fsModifiers) throws UnexpectedException, InterruptedException {
                this.vKCode = vKCode;
                this.fsModifiers = fsModifiers;
                this.start();
        }

        @Override
        public void run() {
                threadId = Kernel32.INSTANCE.GetCurrentThreadId();
                MSG msg = new MSG();
                while (true) {
                        User32.INSTANCE.GetMessage(msg, null, 0x312, 0x315);
                        switch (msg.message) {
                                case 0x0312:
                                        listener.onKeyPress(msg);
                                        break;
                                case 0x0313:
                                        if (!status) {
                                                if (User32Extra.INSTANCE.RegisterHotKey(null, threadId, this.fsModifiers, this.vKCode)) {
                                                        status = true;
                                                } else {
                                                        System.out.println("An error occured when REGISTER hotkey!");
                                                }
                                        }
                                        break;
                                case 0x0315:

                                        if (!status) {
                                                if (!User32Extra.INSTANCE.UnregisterHotKey(null, threadId)) {
                                                        status = false;
                                                } else {
                                                        System.out.println("An error occured when UNREGISTER hotkey!");
                                                }
                                        }
                                        break;
                                default:
                                        System.out.println(msg.message);
                                        break;
                        }
                }
        }

        public boolean register() {
                return User32Extra.INSTANCE.PostThreadMessageA(threadId, 0x313, 0, 0);
        }

        public boolean unRegister() {
                return User32Extra.INSTANCE.PostThreadMessageA(threadId, 0x315, 0, 0);
        }

        public void setHotkeyListener(HotkeyListener listener) {
                this.listener = listener;
        }

        public interface HotkeyListener {
                public void onKeyPress(MSG msg);
        }

        public static class VKMap {
                public static final int VK_ABNT_C1 /*               */ = 0xC1; // Abnt C1
                public static final int VK_ABNT_C2 /*               */ = 0xC2; // Abnt C2
                public static final int VK_ADD /*                   */ = 0x6B; // Numpad +
                public static final int VK_ATTN /*                  */ = 0xF6; // Attn
                public static final int VK_BACK /*                  */ = 0x08; // Backspace
                public static final int VK_CANCEL /*                */ = 0x03; // Break
                public static final int VK_CLEAR /*                 */ = 0x0C; // Clear
                public static final int VK_CRSEL /*                 */ = 0xF7; // Cr Sel
                public static final int VK_DECIMAL /*               */ = 0x6E; // Numpad .
                public static final int VK_DIVIDE /*                */ = 0x6F; // Numpad /
                public static final int VK_EREOF /*                 */ = 0xF9; // Er Eof
                public static final int VK_ESCAPE /*                */ = 0x1B; // Esc
                public static final int VK_EXECUTE /*               */ = 0x2B; // Execute
                public static final int VK_EXSEL /*                 */ = 0xF8; // Ex Sel
                public static final int VK_ICO_CLEAR /*             */ = 0xE6; // IcoClr
                public static final int VK_ICO_HELP /*              */ = 0xE3; // IcoHlp
                public static final int VK_KEY_0 /*                 */ = 0x30; // ('0') 0
                public static final int VK_KEY_1 /*                 */ = 0x31; // ('1') 1
                public static final int VK_KEY_2 /*                 */ = 0x32; // ('2') 2
                public static final int VK_KEY_3 /*                 */ = 0x33; // ('3') 3
                public static final int VK_KEY_4 /*                 */ = 0x34; // ('4') 4
                public static final int VK_KEY_5 /*                 */ = 0x35; // ('5') 5
                public static final int VK_KEY_6 /*                 */ = 0x36; // ('6') 6
                public static final int VK_KEY_7 /*                 */ = 0x37; // ('7') 7
                public static final int VK_KEY_8 /*                 */ = 0x38; // ('8') 8
                public static final int VK_KEY_9 /*                 */ = 0x39; // ('9') 9
                public static final int VK_KEY_A /*                 */ = 0x41; // ('A') A
                public static final int VK_KEY_B /*                 */ = 0x42; // ('B') B
                public static final int VK_KEY_C /*                 */ = 0x43; // ('C') C
                public static final int VK_KEY_D /*                 */ = 0x44; // ('D') D
                public static final int VK_KEY_E /*                 */ = 0x45; // ('E') E
                public static final int VK_KEY_F /*                 */ = 0x46; // ('F') F
                public static final int VK_KEY_G /*                 */ = 0x47; // ('G') G
                public static final int VK_KEY_H /*                 */ = 0x48; // ('H') H
                public static final int VK_KEY_I /*                 */ = 0x49; // ('I') I
                public static final int VK_KEY_J /*                 */ = 0x4A; // ('J') J
                public static final int VK_KEY_K /*                 */ = 0x4B; // ('K') K
                public static final int VK_KEY_L /*                 */ = 0x4C; // ('L') L
                public static final int VK_KEY_M /*                 */ = 0x4D; // ('M') M
                public static final int VK_KEY_N /*                 */ = 0x4E; // ('N') N
                public static final int VK_KEY_O /*                 */ = 0x4F; // ('O') O
                public static final int VK_KEY_P /*                 */ = 0x50; // ('P') P
                public static final int VK_KEY_Q /*                 */ = 0x51; // ('Q') Q
                public static final int VK_KEY_R /*                 */ = 0x52; // ('R') R
                public static final int VK_KEY_S /*                 */ = 0x53; // ('S') S
                public static final int VK_KEY_T /*                 */ = 0x54; // ('T') T
                public static final int VK_KEY_U /*                 */ = 0x55; // ('U') U
                public static final int VK_KEY_V /*                 */ = 0x56; // ('V') V
                public static final int VK_KEY_W /*                 */ = 0x57; // ('W') W
                public static final int VK_KEY_X /*                 */ = 0x58; // ('X') X
                public static final int VK_KEY_Y /*                 */ = 0x59; // ('Y') Y
                public static final int VK_KEY_Z /*                 */ = 0x5A; // ('Z') Z
                public static final int VK_MULTIPLY /*              */ = 0x6A; // Numpad *
                public static final int VK_NONAME /*                */ = 0xFC; // NoName
                public static final int VK_NUMPAD0 /*               */ = 0x60; // Numpad 0
                public static final int VK_NUMPAD1 /*               */ = 0x61; // Numpad 1
                public static final int VK_NUMPAD2 /*               */ = 0x62; // Numpad 2
                public static final int VK_NUMPAD3 /*               */ = 0x63; // Numpad 3
                public static final int VK_NUMPAD4 /*               */ = 0x64; // Numpad 4
                public static final int VK_NUMPAD5 /*               */ = 0x65; // Numpad 5
                public static final int VK_NUMPAD6 /*               */ = 0x66; // Numpad 6
                public static final int VK_NUMPAD7 /*               */ = 0x67; // Numpad 7
                public static final int VK_NUMPAD8 /*               */ = 0x68; // Numpad 8
                public static final int VK_NUMPAD9 /*               */ = 0x69; // Numpad 9
                public static final int VK_OEM_1 /*                 */ = 0xBA; // OEM_1 (: ;)
                public static final int VK_OEM_102 /*               */ = 0xE2; // OEM_102 (> <)
                public static final int VK_OEM_2 /*                 */ = 0xBF; // OEM_2 (? /)
                public static final int VK_OEM_3 /*                 */ = 0xC0; // OEM_3 (~ `)
                public static final int VK_OEM_4 /*                 */ = 0xDB; // OEM_4 ({ [)
                public static final int VK_OEM_5 /*                 */ = 0xDC; // OEM_5 (| \)
                public static final int VK_OEM_6 /*                 */ = 0xDD; // OEM_6 (} ])
                public static final int VK_OEM_7 /*                 */ = 0xDE; // OEM_7 (" ')
                public static final int VK_OEM_8 /*                 */ = 0xDF; // OEM_8 (§ !)
                public static final int VK_OEM_ATTN /*              */ = 0xF0; // Oem Attn
                public static final int VK_OEM_AUTO /*              */ = 0xF3; // Auto
                public static final int VK_OEM_AX /*                */ = 0xE1; // Ax
                public static final int VK_OEM_BACKTAB /*           */ = 0xF5; // Back Tab
                public static final int VK_OEM_CLEAR /*             */ = 0xFE; // OemClr
                public static final int VK_OEM_COMMA /*             */ = 0xBC; // OEM_COMMA (< ,)
                public static final int VK_OEM_COPY /*              */ = 0xF2; // Copy
                public static final int VK_OEM_CUSEL /*             */ = 0xEF; // Cu Sel
                public static final int VK_OEM_ENLW /*              */ = 0xF4; // Enlw
                public static final int VK_OEM_FINISH /*            */ = 0xF1; // Finish
                public static final int VK_OEM_FJ_LOYA /*           */ = 0x95; // Loya
                public static final int VK_OEM_FJ_MASSHOU /*        */ = 0x93; // Mashu
                public static final int VK_OEM_FJ_ROYA /*           */ = 0x96; // Roya
                public static final int VK_OEM_FJ_TOUROKU /*        */ = 0x94; // Touroku
                public static final int VK_OEM_JUMP /*              */ = 0xEA; // Jump
                public static final int VK_OEM_MINUS /*             */ = 0xBD; // OEM_MINUS (_ -)
                public static final int VK_OEM_PA1 /*               */ = 0xEB; // OemPa1
                public static final int VK_OEM_PA2 /*               */ = 0xEC; // OemPa2
                public static final int VK_OEM_PA3 /*               */ = 0xED; // OemPa3
                public static final int VK_OEM_PERIOD /*            */ = 0xBE; // OEM_PERIOD (> .)
                public static final int VK_OEM_PLUS /*              */ = 0xBB; // OEM_PLUS (+ =)
                public static final int VK_OEM_RESET /*             */ = 0xE9; // Reset
                public static final int VK_OEM_WSCTRL /*            */ = 0xEE; // WsCtrl
                public static final int VK_PA1 /*                   */ = 0xFD; // Pa1
                public static final int VK_PACKET /*                */ = 0xE7; // Packet
                public static final int VK_PLAY /*                  */ = 0xFA; // Play
                public static final int VK_PROCESSKEY /*            */ = 0xE5; // Process
                public static final int VK_RETURN /*                */ = 0x0D; // Enter
                public static final int VK_SELECT /*                */ = 0x29; // Select
                public static final int VK_SEPARATOR /*             */ = 0x6C; // Separator
                public static final int VK_SPACE /*                 */ = 0x20; // Space
                public static final int VK_SUBTRACT /*              */ = 0x6D; // Num -
                public static final int VK_TAB /*                   */ = 0x09; // Tab
                public static final int VK_ZOOM /*                  */ = 0xFB; // Zoom
                public static final int VK__none_ /*                */ = 0xFF; // no VK mapping
                public static final int VK_ACCEPT /*                */ = 0x1E; // Accept
                public static final int VK_APPS /*                  */ = 0x5D; // Context Menu
                public static final int VK_BROWSER_BACK /*          */ = 0xA6; // Browser Back
                public static final int VK_BROWSER_FAVORITES /*     */ = 0xAB; // Browser Favorites
                public static final int VK_BROWSER_FORWARD /*       */ = 0xA7; // Browser Forward
                public static final int VK_BROWSER_HOME /*          */ = 0xAC; // Browser Home
                public static final int VK_BROWSER_REFRESH /*       */ = 0xA8; // Browser Refresh
                public static final int VK_BROWSER_SEARCH /*        */ = 0xAA; // Browser Search
                public static final int VK_BROWSER_STOP /*          */ = 0xA9; // Browser Stop
                public static final int VK_CAPITAL /*               */ = 0x14; // Caps Lock
                public static final int VK_CONVERT /*               */ = 0x1C; // Convert
                public static final int VK_DELETE /*                */ = 0x2E; // Delete
                public static final int VK_DOWN /*                  */ = 0x28; // Arrow Down
                public static final int VK_END /*                   */ = 0x23; // End
                public static final int VK_F1 /*                    */ = 0x70; // F1
                public static final int VK_F10 /*                   */ = 0x79; // F10
                public static final int VK_F11 /*                   */ = 0x7A; // F11
                public static final int VK_F12 /*                   */ = 0x7B; // F12
                public static final int VK_F13 /*                   */ = 0x7C; // F13
                public static final int VK_F14 /*                   */ = 0x7D; // F14
                public static final int VK_F15 /*                   */ = 0x7E; // F15
                public static final int VK_F16 /*                   */ = 0x7F; // F16
                public static final int VK_F17 /*                   */ = 0x80; // F17
                public static final int VK_F18 /*                   */ = 0x81; // F18
                public static final int VK_F19 /*                   */ = 0x82; // F19
                public static final int VK_F2 /*                    */ = 0x71; // F2
                public static final int VK_F20 /*                   */ = 0x83; // F20
                public static final int VK_F21 /*                   */ = 0x84; // F21
                public static final int VK_F22 /*                   */ = 0x85; // F22
                public static final int VK_F23 /*                   */ = 0x86; // F23
                public static final int VK_F24 /*                   */ = 0x87; // F24
                public static final int VK_F3 /*                    */ = 0x72; // F3
                public static final int VK_F4 /*                    */ = 0x73; // F4
                public static final int VK_F5 /*                    */ = 0x74; // F5
                public static final int VK_F6 /*                    */ = 0x75; // F6
                public static final int VK_F7 /*                    */ = 0x76; // F7
                public static final int VK_F8 /*                    */ = 0x77; // F8
                public static final int VK_F9 /*                    */ = 0x78; // F9
                public static final int VK_ /*                      */ = 0x18; //
                public static final int VK_HELP /*                  */ = 0x2F; // Help
                public static final int VK_HOME /*                  */ = 0x24; // Home
                public static final int VK_ICO_00 /*                */ = 0xE4; // Ico00 *
                public static final int VK_INSERT /*                */ = 0x2D; // Insert
                public static final int VK_JUNJA /*                 */ = 0x17; // Junja
                public static final int VK_KANA /*                  */ = 0x15; // Kana
                public static final int VK_KANJI /*                 */ = 0x19; // Kanji
                public static final int VK_LAUNCH_APP1 /*           */ = 0xB6; // App1
                public static final int VK_LAUNCH_APP2 /*           */ = 0xB7; // App2
                public static final int VK_LAUNCH_MAIL /*           */ = 0xB4; // Mail
                public static final int VK_LAUNCH_MEDIA_SELECT /*   */ = 0xB5; // Media
                public static final int VK_LBUTTON /*               */ = 0x01; // Left Button **
                public static final int VK_LCONTROL /*              */ = 0xA2; // Left Ctrl
                public static final int VK_LEFT /*                  */ = 0x25; // Arrow Left
                public static final int VK_LMENU /*                 */ = 0xA4; // Left Alt
                public static final int VK_LSHIFT /*                */ = 0xA0; // Left Shift
                public static final int VK_LWIN /*                  */ = 0x5B; // Left Win
                public static final int VK_MBUTTON /*               */ = 0x04; // Middle Button **
                public static final int VK_MEDIA_NEXT_TRACK /*      */ = 0xB0; // Next Track
                public static final int VK_MEDIA_PLAY_PAUSE /*      */ = 0xB3; // Play / Pause
                public static final int VK_MEDIA_PREV_TRACK /*      */ = 0xB1; // Previous Track
                public static final int VK_MEDIA_STOP /*            */ = 0xB2; // Stop
                public static final int VK_MODECHANGE /*            */ = 0x1F; // Mode Change
                public static final int VK_NEXT /*                  */ = 0x22; // Page Down
                public static final int VK_NONCONVERT /*            */ = 0x1D; // Non Convert
                public static final int VK_NUMLOCK /*               */ = 0x90; // Num Lock
                public static final int VK_OEM_FJ_JISHO /*          */ = 0x92; // Jisho
                public static final int VK_PAUSE /*                 */ = 0x13; // Pause
                public static final int VK_PRINT /*                 */ = 0x2A; // Print
                public static final int VK_PRIOR /*                 */ = 0x21; // Page Up
                public static final int VK_RBUTTON /*               */ = 0x02; // Right Button **
                public static final int VK_RCONTROL /*              */ = 0xA3; // Right Ctrl
                public static final int VK_RIGHT /*                 */ = 0x27; // Arrow Right
                public static final int VK_RMENU /*                 */ = 0xA5; // Right Alt
                public static final int VK_RSHIFT /*                */ = 0xA1; // Right Shift
                public static final int VK_RWIN /*                  */ = 0x5C; // Right Win
                public static final int VK_SCROLL /*                */ = 0x91; // Scrol Lock
                public static final int VK_SLEEP /*                 */ = 0x5F; // Sleep
                public static final int VK_SNAPSHOT /*              */ = 0x2C; // Print Screen
                public static final int VK_UP /*                    */ = 0x26; // Arrow Up
                public static final int VK_VOLUME_DOWN /*           */ = 0xAE; // Volume Down
                public static final int VK_VOLUME_MUTE /*           */ = 0xAD; // Volume Mute
                public static final int VK_VOLUME_UP /*             */ = 0xAF; // Volume Up
                public static final int VK_XBUTTON1 /*              */ = 0x05; // X Button 1 **
                public static final int VK_XBUTTON2 /*              */ = 0x06; // X Button 2 **

        }

        public enum KEY {
                NONE(0x0), LBUTTON(0x1), RBUTTON(0x2), CANCEL(0x3), MBUTTON(0x4), XBUTTON1(0x5), XBUTTON2(0x6), //
                VK_0x07(0x7), BACK(0x8), TAB(0x9), VK_0x0A(0xa), VK_0x0B(0xb), CLEAR(0xc), RETURN(0xd), VK_0x0E(0xe), //
                VK_0x0F(0xf), SHIFT(0x10), CONTROL(0x11), MENU(0x12), PAUSE(0x13), CAPITAL(0x14), //
                KANA_HANGUEL_HANGUL(0x15), IME_ON(0x16), JUNJA(0x17), FINAL(0x18), HANJA_KANJI(0x19), IME_OFF(0x1a), //
                ESCAPE(0x1b), CONVERT(0x1c), NONCONVERT(0x1d), ACCEPT(0x1e), MODECHANGE(0x1f), SPACE(0x20), PRIOR(0x21), //
                NEXT(0x22), END(0x23), HOME(0x24), LEFT(0x25), UP(0x26), RIGHT(0x27), DOWN(0x28), SELECT(0x29), //
                PRINT(0x2a), EXECUTE(0x2b), SNAPSHOT(0x2c), INSERT(0x2d), DELETE(0x2e), HELP(0x2f), _0(0x30), _1(0x31), //
                _2(0x32), _3(0x33), _4(0x34), _5(0x35), _6(0x36), _7(0x37), _8(0x38), _9(0x39), VK_0x3A(0x3a), //
                VK_0x3B(0x3b), VK_0x3C(0x3c), VK_0x3D(0x3d), VK_0x3E(0x3e), VK_0x3F(0x3f), VK_0x40(0x40), A(0x41), //
                B(0x42), C(0x43), D(0x44), E(0x45), F(0x46), G(0x47), H(0x48), //
                I(0x49), J(0x4a), K(0x4b), L(0x4c), M(0x4d), N(0x4e), O(0x4f), //
                P(0x50), Q(0x51), R(0x52), S(0x53), T(0x54), U(0x55), V(0x56), //
                W(0x57), X(0x58), Y(0x59), Z(0x5a), LWIN(0x5b), RWIN(0x5c), APPS(0x5d), //
                VK_0x5E(0x5e), SLEEP(0x5f), NUMPAD0(0x60), NUMPAD1(0x61), NUMPAD2(0x62), NUMPAD3(0x63), NUMPAD4(0x64), //
                NUMPAD5(0x65), NUMPAD6(0x66), NUMPAD7(0x67), NUMPAD8(0x68), NUMPAD9(0x69), MULTIPLY(0x6a), //
                ADD(0x6b), SEPARATOR(0x6c), SUBTRACT(0x6d), DECIMAL(0x6e), DIVIDE(0x6f), F1(0x70), F2(0x71), //
                F3(0x72), F4(0x73), F5(0x74), F6(0x75), F7(0x76), F8(0x77), F9(0x78), F10(0x79), F11(0x7a), F12(0x7b), //
                F13(0x7c), F14(0x7d), F15(0x7e), F16(0x7f), F17(0x80), F18(0x81), F19(0x82), F20(0x83), F21(0x84), //
                F22(0x85), F23(0x86), F24(0x87), VK_0x88(0x88), VK_0x89(0x89), VK_0x8A(0x8a), VK_0x8B(0x8b), VK_0x8C(0x8c), //
                VK_0x8D(0x8d), VK_0x8E(0x8e), VK_0x8F(0x8f), NUMLOCK(0x90), SCROLL(0x91), VK_0x92(0x92), VK_0x93(0x93), //
                VK_0x94(0x94), VK_0x95(0x95), VK_0x96(0x96), VK_0x97(0x97), VK_0x98(0x98), VK_0x99(0x99), VK_0x9A(0x9a), //
                VK_0x9B(0x9b), VK_0x9C(0x9c), VK_0x9D(0x9d), VK_0x9E(0x9e), VK_0x9F(0x9f), LSHIFT(0xa0), RSHIFT(0xa1), //
                LCONTROL(0xa2), RCONTROL(0xa3), LMENU(0xa4), RMENU(0xa5), BROWSER_BACK(0xa6), BROWSER_FORWARD(0xa7), //
                BROWSER_REFRESH(0xa8), BROWSER_STOP(0xa9), BROWSER_SEARCH(0xaa), BROWSER_FAVORITES(0xab), //
                BROWSER_HOME(0xac), VOLUME_MUTE(0xad), VOLUME_DOWN(0xae), VOLUME_UP(0xaf), MEDIA_NEXT_TRACK(0xb0), //
                MEDIA_PREV_TRACK(0xb1), MEDIA_STOP(0xb2), MEDIA_PLAY_PAUSE(0xb3), LAUNCH_MAIL(0xb4), //
                LAUNCH_MEDIA_SELECT(0xb5), LAUNCH_APP1(0xb6), LAUNCH_APP2(0xb7), VK_0xB8(0xb8), VK_0xB9(0xb9), //
                OEM_1(0xba), OEM_PLUS(0xbb), OEM_COMMA(0xbc), OEM_MINUS(0xbd), OEM_PERIOD(0xbe), OEM_2(0xbf), //
                OEM_3(0xc0), VK_0xC1(0xc1), VK_0xC2(0xc2), VK_0xC3(0xc3), VK_0xC4(0xc4), VK_0xC5(0xc5), VK_0xC6(0xc6), //
                VK_0xC7(0xc7), VK_0xC8(0xc8), VK_0xC9(0xc9), VK_0xCA(0xca), VK_0xCB(0xcb), VK_0xCC(0xcc), VK_0xCD(0xcd), //
                VK_0xCE(0xce), VK_0xCF(0xcf), VK_0xD0(0xd0), VK_0xD1(0xd1), VK_0xD2(0xd2), VK_0xD3(0xd3), VK_0xD4(0xd4), //
                VK_0xD5(0xd5), VK_0xD6(0xd6), VK_0xD7(0xd7), VK_0xD8(0xd8), VK_0xD9(0xd9), VK_0xDA(0xda), OEM_4(0xdb), //
                OEM_5(0xdc), OEM_6(0xdd), OEM_7(0xde), OEM_8(0xdf), VK_0xE0(0xe0), VK_0xE1(0xe1), OEM_102(0xe2), //
                VK_0xE3(0xe3), VK_0xE4(0xe4), PROCESSKEY(0xe5), VK_0xE6(0xe6), PACKET(0xe7), VK_0xE8(0xe8), //
                VK_0xE9(0xe9), VK_0xEA(0xea), VK_0xEB(0xeb), VK_0xEC(0xec), VK_0xED(0xed), VK_0xEE(0xee), VK_0xEF(0xef), //
                VK_0xF0(0xf0), VK_0xF1(0xf1), VK_0xF2(0xf2), VK_0xF3(0xf3), VK_0xF4(0xf4), VK_0xF5(0xf5), ATTN(0xf6), //
                CRSEL(0xf7), EXSEL(0xf8), EREOF(0xf9), PLAY(0xfa), ZOOM(0xfb), NONAME(0xfc), PA1(0xfd), OEM_CLEAR(0xfe);//

                public int code;

                KEY(int code) {
                        this.code = code;
                }

                public static KEY[] getDefinedKeys() {
                        return Stream.of(values()).filter(key -> key.name().length() < 3 || key.name().substring(0, 3).compareTo("VK_") != 0).toArray(KEY[]::new);
                }

        }
}
