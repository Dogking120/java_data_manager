package basic_api;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public final class Cryptography {
    public static byte[] PBKDF2Encrypt(char[] password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
    	KeySpec spec = new PBEKeySpec(password, salt, 65536, 128);
    	SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    	byte[] hash = factory.generateSecret(spec).getEncoded();
		return hash;
    }
    public static byte[] genSalt() {
        byte[] salt = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        return salt;
    }
    public static PrivateKey getPrivateKey(Path certfile, String password) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(new FileInputStream(certfile.toFile()), password.toCharArray());

		return (PrivateKey) keyStore.getKey("rsa", password.toCharArray());
    }
    public static String decryptWithPrivateKey(PrivateKey key, byte[] encryptedMessageBytes) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher decryptCipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
        decryptCipher.init(Cipher.DECRYPT_MODE, key);
        
        byte[] decryptedMessageBytes = decryptCipher.doFinal(encryptedMessageBytes);
        return new String(decryptedMessageBytes, StandardCharsets.UTF_8);
    }
    public static SSLContext createSSLContext(Path certfile, char[] password) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, KeyManagementException {
    	
        SSLContext sslContext;

	    KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
	    keyStore.load(new FileInputStream(certfile.toFile()), password);
	
	    KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
	    keyManagerFactory.init(keyStore, password);
	
	    TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
	    trustManagerFactory.init(keyStore);
	
	    sslContext = SSLContext.getInstance("TLS");
	    sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
        
	    return sslContext;	
    }
}
