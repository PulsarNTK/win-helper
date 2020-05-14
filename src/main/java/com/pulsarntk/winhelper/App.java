package com.pulsarntk.winhelper;

import java.rmi.UnexpectedException;
import com.sun.jna.platform.win32.WinUser.MSG;
import com.pulsarntk.winhelper.hotkey.RegisterHotkey;
import com.pulsarntk.winhelper.hotkey.VKMap;
import com.pulsarntk.winhelper.plugins.TaskbarWhell;

public class App {

	public static void main(String[] args) throws UnexpectedException, InterruptedException {
		new TaskbarWhell().install();

	}

	public static int lParamToVK(int lParam) {
		return lParam >> 16;
	}
}
