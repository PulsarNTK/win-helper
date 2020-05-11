package com.pulsarntk.winhelper.virtualdesktopaccessor;

public class VirtualDesktopAccessor {

    static {
        try {
            System.loadLibrary("lib/VirtualDesktopAccessor");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Native code library failed to load./n" + e);
            System.exit(1);
        }
    }


    public native int GetDesktopCount();


}