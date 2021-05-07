package edu.wpi.aquamarine_axolotls.extras;

import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;

import java.io.IOException;
import java.sql.SQLException;

public class Security {
	private Security(){} //NO INSTANTIATION >:(

	private final static SecretGenerator secretGenerator = new DefaultSecretGenerator();
	private final static CodeVerifier codeVerifier = new DefaultCodeVerifier(new DefaultCodeGenerator(), new SystemTimeProvider()); //TODO: NTP time provider instead?

	// dbController can't be final because the database connection can be changed, so we need to be able to update it
	//private static DatabaseController dbController;

	/*static {
		try {
			dbController = DatabaseController.getInstance();
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}*/


	public static String generateSecret() {
		return secretGenerator.generate();
	}

	public static boolean verifyTOTP(String username, String passcode) throws SQLException {
		//codeVerifier.isValidCode(dbController.getUserByUsername(username).get("TOTPSECRET"), passcode);
		return false;
	}
}
