package com.pulsarntk.winhelper;
import java.util.Map.Entry;

import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;
import com.pulsarntk.winhelper.virtualdesktopaccessor.VirtualDesktopAccessor;

public class App {

	public static void main(String[] args) {
		System.out.println(Integer.toString(new VirtualDesktopAccessor().GetDesktopCount()));
	}
}
