package edu.wpi.aquamarine_axolotls.extras;

import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import javafx.util.Pair;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.HashMap;
import java.util.Map;

public class Security {
	private Security(){} //NO INSTANTIATION >:(

	final static private Charset CHARSET = StandardCharsets.ISO_8859_1;

	final static private DatabaseController DB_CONTROLLER = DatabaseController.getInstance();

	final static private SecretGenerator SECRET_GENERATOR = new DefaultSecretGenerator();
	final static private CodeVerifier CODE_VERIFIER = new DefaultCodeVerifier(new DefaultCodeGenerator(), new SystemTimeProvider());


	/**
	 * Enables TOTP 2FA for the provided user (Assumes provided user is already in the database)
	 * @return User's TOTP secret and a QR code for it
	 * @param username Username of user to enable TOTP 2FA for
	 * @throws SQLException Something went wrong.
	 */
	public static Pair<String,byte[]> enableTOTP(String username) throws SQLException, QrGenerationException {
		String secret = SECRET_GENERATOR.generate();
		DB_CONTROLLER.editUser(username, new HashMap<String,String>() {{
			put("TOTPSECRET",secret);
			put("MFAENABLED","true");
		}});
		return new Pair<>(secret, makeQrCode(username,secret));
	}

	/**
	 * TOTP 2FA verification
	 * @param username username of user to verify
	 * @param passcode passcode to attempt verification with
	 * @return if verification was successful
	 * @throws SQLException Something went wrong.
	 */
	public static boolean verifyTOTP(String username, String passcode) throws SQLException {
		return CODE_VERIFIER.isValidCode(DB_CONTROLLER.getUserByUsername(username).get("TOTPSECRET"), passcode);
	}

	/**
	 * Generate a TOTP QR code based on the provided username and secret
	 * @param username username of user the TOTP secret is for
	 * @param secret TOTP secret
	 * @return QR code as byte array
	 * @throws QrGenerationException Something went wrong.
	 */
	private static byte[] makeQrCode(String username, String secret) throws QrGenerationException {
		QrData qrData = new QrData.Builder()
			.label(username)
			.secret(secret)
			.issuer("BWH")
			.build();

		return new ZxingPngQrGenerator().generate(qrData);
	}

	/**
	 * disable TOTP for the provided user
	 * @param username username of user to disable TOTP 2FA for
	 * @throws SQLException Something went wrong.
	 */
	public static void disableTOTP(String username) throws SQLException {
		DB_CONTROLLER.editUser(username, new HashMap<String,String>() {{
			put("TOTPSECRET","");
			put("MFAENABLED","false");
		}});
	}

	/**
	 * Adds salt and hash for provided password into the provided user map
	 * @param user map to add salt and hash into
	 * @param password password to hash
	 */
	public static void addHashedPassword(Map<String,String> user, String password) {
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[16];
		random.nextBytes(salt);

		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-512");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.out.println("SOMETHING WENT WRONG THIS SHOULD NEVER HAPPEN");
		}
		md.update(salt);

		String saltString = new String(salt, CHARSET);

		String hash = new String(md.digest(password.getBytes(CHARSET)), CHARSET);

		user.put("SALT",saltString);
		user.put("PASSWORD",hash);
	}

	/**
	 * Verify if the provided username and password combination exists for account validation
	 * @param username account username
	 * @param password account password
	 * @return credentials were valid
	 * @throws SQLException Something went wrong.
	 */
	public static boolean secureVerifyAccount(String username, String password) throws SQLException {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-512");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.out.println("SOMETHING WENT WRONG THIS SHOULD NEVER HAPPEN");
		}

		if (!DB_CONTROLLER.checkUserExistsByUsername(username)) return false;
		Map<String,String> userMap = DB_CONTROLLER.getUserByUsername(username);

		md.update(userMap.get("SALT").getBytes(CHARSET));

		String hash = new String(md.digest(password.getBytes(CHARSET)), CHARSET);

		return hash.equals(userMap.get("PASSWORD"));
	}

	/**
	 * Generates a single-use verification code
	 * @return a single-use verification code
	 */
	public static String generateOneTimeSecurityCode() { //credit: https://stackoverflow.com/questions/46261055/how-to-generate-a-securerandom-string-of-length-n-in-java
		SecureRandom random = new SecureRandom();
		byte[] bytes = new byte[16];
		random.nextBytes(bytes);
		Encoder encoder = Base64.getUrlEncoder().withoutPadding();
		return encoder.encodeToString(bytes);
	}
}
