package edu.wpi.aquamarine_axolotls;

import java.util.prefs.*;

public class Settings {
	private Settings(){} //NO INSTANTIATION >:(

	public final static Preferences PREFERENCES = Preferences.userNodeForPackage(Settings.class);

	final public static String GOOGLE_MAPS_API_KEY = "Google Maps API Key";
	final public static String USE_CLIENT_SERVER_DATABASE = "Use Client Server Database";
	final public static String USER_TYPE = "User Type";
	final public static String USER_NAME = "Username";
	final public static String USER_FIRST_NAME = "User First Name";
	final public static String INSTANCE_ID = "Instance ID";
	final public static String EMAIL_API_KEY = "SendGrid Email API Key";
}
