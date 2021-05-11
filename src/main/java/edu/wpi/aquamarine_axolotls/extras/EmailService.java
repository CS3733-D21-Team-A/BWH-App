package edu.wpi.aquamarine_axolotls.extras;

import java.util.Map;

import java.io.IOException;
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;

import com.sendgrid.helpers.mail.objects.Personalization;
import edu.wpi.aquamarine_axolotls.Settings;

import static edu.wpi.aquamarine_axolotls.Settings.EMAIL_API_KEY;
import static edu.wpi.aquamarine_axolotls.Settings.PREFERENCES;

public class EmailService {
	private EmailService(){} //no instantiation allowed

	private static final String ACCOUNT_CONFIRM_TEMPLATE = "d-ae1015ad10034e53845d03c85698f60a";
	private static final String ACCOUNT_RESET_TEMPLATE = "d-73eda03e75b843fca186dd142c4a7611";
	private static final String SERVICE_REQUEST_TEMPLATE = "d-4ba0ff1157d945d48834039618450d01";


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
	}



	/**
	 * Sends an account creation confirmation email to the provided user
	 * @param email address to send email to
	 * @param username username of account associated with email
	 * @param firstName of user associated with account
	 */
	public static void sendAccountCreationEmail(String email, String username, String firstName) throws IOException {
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
	public static void sendAccountResetEmail(String email, String username, String resetCode) throws IOException {
		Mail mail = new Mail();
		mail.setFrom(new Email("groupasofteng@gmail.com"));
		mail.setTemplateId(ACCOUNT_RESET_TEMPLATE);

		Personalization personalization = new Personalization();
		personalization.addTo(new Email(email));
		personalization.addDynamicTemplateData("username", username);
		personalization.addDynamicTemplateData("resetCode", resetCode);

		mail.addPersonalization(personalization);

		send(mail);
	}

	/**
	 * Sends a service request confirmation email to the provided address
	 * @param email address to send email to
	 * @param username username of account associated with email
	 * @param requestValues values of request to send in email
	 */
	public static void sendServiceRequestConfirmation(String email, String username, Map<String,String> requestValues) throws IOException {
		Mail mail = new Mail();
		mail.setFrom(new Email("groupasofteng@gmail.com"));
		mail.setTemplateId(SERVICE_REQUEST_TEMPLATE);

		StringBuilder details = new StringBuilder();

		for (String key : requestValues.keySet()) {
			details.append("<b>");
			details.append(key);
			details.append("</b>: ");
			details.append(requestValues.get(key));
			details.append("<br>");
		}

		Personalization personalization = new Personalization();
		personalization.addTo(new Email(email));
		personalization.addDynamicTemplateData("username", username);
		personalization.addDynamicTemplateData("details", details.toString());

		mail.addPersonalization(personalization);

		send(mail);
	}
}
