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
import java.sql.SQLException;
import java.util.Collections;

public class Security {
	private Security(){} //NO INSTANTIATION >:(

	final static private DatabaseController dbController = DatabaseController.getInstance();
	private final static SecretGenerator secretGenerator = new DefaultSecretGenerator();
	private final static CodeVerifier codeVerifier;

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
		dbController.editUser(username, Collections.singletonMap("TOTPSECRET",secret));
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
}
