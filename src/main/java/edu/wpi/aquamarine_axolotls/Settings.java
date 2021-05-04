package edu.wpi.aquamarine_axolotls;

import java.util.prefs.*;

public class Settings {
	private Settings(){} //NO INSTANTIATION >:(

	public final static Preferences prefs = Preferences.userNodeForPackage(Settings.class);

	final public static String API_KEY = "Google Maps API Key";
}
