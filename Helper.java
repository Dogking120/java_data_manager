package extras;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Base64.Encoder;

import basic_api.Cryptography;
import basic_api.UserAuth;

public final class Helper {

	public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException {
		System.out.println("Starting Helper.java!");
		printUserAuth(genPasswordHashAndSalt("password".toCharArray()));
	}
	
	private static UserAuth genPasswordHashAndSalt(char[] password) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] salt = Cryptography.genSalt();
		byte[] hash = Cryptography.PBKDF2Encrypt(password, salt);
		UserAuth ua = new UserAuth();
		Encoder encoder = Base64.getEncoder();
		ua.key = "Not applicable";
		ua.hash = encoder.encodeToString(hash);
		ua.salt = encoder.encodeToString(salt);
		return ua;
	}
	private static void printUserAuth(UserAuth ua) {
		System.out.println(String.format("Key: %s\nHash: %s\nSalt: %s", ua.key, ua.hash, ua.salt));
	}
}
