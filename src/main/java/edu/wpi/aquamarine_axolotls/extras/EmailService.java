package edu.wpi.aquamarine_axolotls.extras;

import java.util.Map;

public class EmailService {
	private EmailService(){} //no instantiation allowed

	/**
	 * Sends an account creation confirmation email to the provided user
	 * @param email address to send email to
	 * @param username username of account associated with email
	 * @param firstName of user associated with account
	 * @return if email sent successfully
	 */
	public static void sendAccountCreationEmail(String email, String username, String firstName) {
		//TODO: IMPLEMENT THIS
	}

	/**
	 * Sends an account reset email to the provided user
	 * @param email address to send email to
	 * @param username username of account associated with email
	 * @param resetCode reset code to send to user
	 */
	public static void sendAccountResetEmail(String email, String username, String resetCode) {
		//TODO: IMPLEMENT THIS
	}

	/**
	 * Sends a service request confirmation email to the provided address
	 * @param email address to send email to
	 * @param username username of account associated with email
	 * @param requestValues values of request to send in email
	 */
	public static void sendServiceRequestConfirmation(String email, String username, Map<String,String> requestValues) {
		//TODO: IMPLEMENT THIS
	}
}
