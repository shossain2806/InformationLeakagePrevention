import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;

public class DSA {

	public static KeyPair buildKeyPair() {
		
		try {
			KeyPairGenerator pairgen = KeyPairGenerator.getInstance("DSA");
			SecureRandom random = new SecureRandom();
			pairgen.initialize(512, random);
			return pairgen.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}

	}
	
	public static byte[] signatureByte(byte[] message,PrivateKey privateKey) {
		try {
			Signature sign = Signature.getInstance("DSA");
			sign.initSign(privateKey);
			sign.update(message);

			byte[] signature = sign.sign();
			return signature;
		}catch(Exception e) {
			e.printStackTrace();
			return new byte[0];
		}
		
	}
	
	public static boolean verifyWith(PublicKey publicKey, byte[] message, byte[] signature) {
		try {
			Signature verifyalg = Signature.getInstance("DSA");
			verifyalg.initVerify(publicKey);
			
			verifyalg.update(message);
			return verifyalg.verify(signature);
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
