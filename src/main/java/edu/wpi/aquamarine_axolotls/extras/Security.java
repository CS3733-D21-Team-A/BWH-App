package edu.wpi.aquamarine_axolotls.extras;

import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.NtpTimeProvider;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import javafx.util.Pair;

import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Security {
	private Security(){} //NO INSTANTIATION >:(

	final static private Charset CHARSET = StandardCharsets.ISO_8859_1;

	final static private DatabaseController dbController = DatabaseController.getInstance();
	final static private SecretGenerator secretGenerator = new DefaultSecretGenerator();
	final static private CodeVerifier codeVerifier;

	static {
		TimeProvider tp;

		try {
			tp = new NtpTimeProvider("pool.ntp.org");
		} catch (UnknownHostException e) {
			System.out.println("Unable to establish connection with pool.ntp.org, reverting to system time for 2FA");
			tp = new SystemTimeProvider();
		}
		codeVerifier = new DefaultCodeVerifier(new DefaultCodeGenerator(), tp);
	}


	/**
	 * Enables TOTP 2FA for the provided user
	 * @return User's TOTP secret and a QR code for it
	 * @param username Username of user to enable TOTP 2FA for
	 * @throws SQLException Something went wrong.
	 */
	public static Pair<String,byte[]> enableTOTP(String username) throws SQLException, QrGenerationException {
		String secret = secretGenerator.generate();
		dbController.editUser(username, new HashMap<String,String>() {{
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
		return codeVerifier.isValidCode(dbController.getUserByUsername(username).get("TOTPSECRET"), passcode);
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
		dbController.editUser(username, new HashMap<String,String>() {{
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

		if (!dbController.checkUserExists(username)) return false;
		Map<String,String> userMap = dbController.getUserByUsername(username);

		md.update(userMap.get("SALT").getBytes(CHARSET));

		String hash = new String(md.digest(password.getBytes(CHARSET)), CHARSET);

		return hash.equals(userMap.get("PASSWORD"));
	}
}
