package edu.wpi.aquamarine_axolotls;

import java.util.prefs.*;

public class Settings { //functionally static
	private Settings(){} //NO INSTANTIATION >:(

	public final static Preferences prefs = Preferences.userNodeForPackage(Aapp.class);

	final public static String API_KEY = "Google Maps API Key";
}
