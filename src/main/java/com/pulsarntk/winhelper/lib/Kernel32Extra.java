package com.pulsarntk.winhelper.lib;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.win32.W32APIOptions;
import com.sun.jna.Pointer;

public interface Kernel32Extra extends Kernel32 {
    Kernel32Extra INSTANCE = (Kernel32Extra) Native.load("kernel32", Kernel32Extra.class, W32APIOptions.DEFAULT_OPTIONS);

    boolean VirtualFreeEx(HANDLE hProcess, Pointer lpAddress, int dwSize, int dwFreeType);

    int GlobalAddAtomA(char[] chars);

    int GlobalDeleteAtom(int atom);

    int WaitForSingleObject(int handle, int timeout);

    int GlobalAlloc(int flags, int dwBytes);

    int GlobalLock(int point);

}
