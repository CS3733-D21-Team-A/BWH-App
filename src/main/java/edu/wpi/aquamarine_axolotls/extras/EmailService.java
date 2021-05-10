package edu.wpi.aquamarine_axolotls.extras;

import java.util.Map;

import java.io.IOException;
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import com.sendgrid.helpers.mail.objects.Personalization;
import edu.wpi.aquamarine_axolotls.Settings;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;


import java.sql.*;
import java.util.List;
import java.util.Map;

import static edu.wpi.aquamarine_axolotls.Settings.EMAIL_API_KEY;
import static edu.wpi.aquamarine_axolotls.Settings.PREFERENCES;

public class EmailService {
	private EmailService(){} //no instantiation allowed

	private static final String ACCOUNT_CONFIRM_TEMPLATE = "d-ae1015ad10034e53845d03c85698f60a";

	public static void main(String[] args) throws IOException {
		sendAccountCreationEmail("meetite@gmail.com","nyoma","N'yoma");
	}


	/**
	 * Sends the provided email
	 * @param mail email to send
	 */
	private static void send(Mail mail) throws IOException {
		Request request = new Request();
		request.setMethod(Method.POST);
		request.setEndpoint("mail/send");
		request.setBody(mail.build());

		Response response = new SendGrid(PREFERENCES.get(EMAIL_API_KEY,null)).api(request);
		System.out.println(response.getStatusCode());
		System.out.println(response.getBody());
		System.out.println(response.getHeaders());
	}



	/**
	 * Sends an account creation confirmation email to the provided user
	 * @param email address to send email to
	 * @param username username of account associated with email
	 * @param firstName of user associated with account
	 * @return if email sent successfully
	 */
	public static void sendAccountCreationEmail(String email, String username, String firstName) throws IOException {
		//TODO: IMPLEMENT THIS

		Mail mail = new Mail();
		mail.setFrom(new Email("groupasofteng@gmail.com"));
		mail.setTemplateId(ACCOUNT_CONFIRM_TEMPLATE);

		Personalization personalization = new Personalization();
		personalization.addTo(new Email(email));
		personalization.addDynamicTemplateData("username", username);
		personalization.addDynamicTemplateData("first_name", firstName);

		mail.addPersonalization(personalization);

		send(mail);
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
