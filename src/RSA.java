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
			System.out.println("before encryption,plain text:"+ new String(message));
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] encryped = cipher.doFinal(message);
			System.out.println("encrypted text:"+ new String(encryped));
			return encryped;
		} catch (Exception e) {
			e.printStackTrace();
			return new byte[0];
		}

	}
	
	public static byte[] decrypt(PrivateKey privateKey, byte[] encrypted) {
		try {
			System.out.println("before decryption,encrypted text:"+ new String(encrypted));
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] decrypted = cipher.doFinal(encrypted);
			System.out.println("decrypted text:"+ new String(decrypted));
 			return decrypted;
		} catch (Exception e) {
			e.printStackTrace();
			return new byte[0];
		}

	}
}
