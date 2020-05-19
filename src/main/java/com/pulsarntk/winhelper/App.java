package com.pulsarntk.winhelper;

import java.rmi.UnexpectedException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import com.pulsarntk.winhelper.itf.Feature;
import com.pulsarntk.winhelper.feature.taskbarscroll.TaskbarScroll;
import com.pulsarntk.winhelper.feature.desktopoverview.DesktopOverview;
import com.pulsarntk.winhelper.settings.Settings;
import com.formdev.flatlaf.FlatDarkLaf;
import com.sun.jna.platform.win32.WinDef.*;
import com.sun.jna.platform.win32.WinUser.MSG;
import com.pulsarntk.winhelper.lib.VirtualDesktopAccessor;
import com.pulsarntk.winhelper.lib.VirtualDesktopAccessor.VirtualDesktopListener;

public class App {
	public static ArrayList<Feature> features = new ArrayList<Feature>();

	public static void main(String[] args) throws UnexpectedException, InterruptedException {
		FlatDarkLaf.install();
		addFeature(new DesktopOverview());
	}

	public static void addFeature(Feature feature) {
		features.add(feature);
	}
}
