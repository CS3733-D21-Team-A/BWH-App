package edu.wpi.aquamarine_axolotls;

import java.util.prefs.*;

public class Settings {
	private Settings(){} //NO INSTANTIATION >:(

	public final static Preferences prefs = Preferences.userNodeForPackage(Settings.class);

	final public static String API_KEY = "Google Maps API Key";
	final public static String USE_CLIENT_SERVER_DATABASE = "Use Client Server Databse";
	final public static String USER_TYPE = "User Type";
	final public static String USER_NAME = "Username";
	final public static String USER_FIRST_NAME = "User First Name";
}
