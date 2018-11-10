import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

public class RSA {

	public static KeyPair buildKeyPair() {
		try {
			final int keySize = 2048;
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(keySize);
			return keyPairGenerator.genKeyPair();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}

	}
	
	public static byte[] encrypt(PublicKey publicKey, byte[] message) {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);

			return cipher.doFinal(message);
		} catch (Exception e) {
			e.printStackTrace();
			return new byte[0];
		}

	}
	
	public static byte[] decrypt(PrivateKey privateKey, byte[] encrypted) {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			return cipher.doFinal(encrypted);
		} catch (Exception e) {
			e.printStackTrace();
			return new byte[0];
		}

	}
}
