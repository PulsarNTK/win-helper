package com.pulsarntk.winhelper;

import java.rmi.UnexpectedException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import com.pulsarntk.winhelper.itf.Feature;
import com.pulsarntk.winhelper.feature.*;
import com.pulsarntk.winhelper.settings.Settings;
import com.formdev.flatlaf.FlatDarkLaf;

public class App {
	public static ArrayList<Feature> features = new ArrayList<Feature>();

	public static void main(String[] args) throws UnexpectedException, InterruptedException {
		FlatDarkLaf.install();
		addFeature(new TaskbarWheel());
		addFeature(new TaskbarWheel());
		Settings settings = new Settings(features);
	}

	public static void addFeature(Feature feature) {
		features.add(feature);
	}
}
