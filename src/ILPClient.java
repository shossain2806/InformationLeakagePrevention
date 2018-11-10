import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.Scanner;

import javax.crypto.Cipher;

public class ILPClient {
	private Socket clientEndPoint;
	private Scanner sc;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private PublicKey sharedRSAPublicKey;
	KeyPair dsaKeypair;

	public ILPClient() {
		// TODO Auto-generated constructor stub
		sc = new Scanner(System.in);

		try {
			KeyPairGenerator pairgen = KeyPairGenerator.getInstance("DSA");
			SecureRandom random = new SecureRandom();
			pairgen.initialize(512, random);
			dsaKeypair = pairgen.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public void startRunning() {

		// waiting for a user
		connectToServer();
		setupStreams();
		startMessaging();

	}

	private void connectToServer() {
		try {

			clientEndPoint = new Socket("127.0.0.1", 6666);

			ObjectInputStream ois = new ObjectInputStream(clientEndPoint.getInputStream());
			sharedRSAPublicKey = (PublicKey) ois.readObject();

			ObjectOutputStream oos = new ObjectOutputStream(clientEndPoint.getOutputStream());
			oos.writeObject(dsaKeypair.getPublic());

			System.out.println("Client connected to server: " + clientEndPoint.getRemoteSocketAddress());
		} catch (IOException e) {
			System.out.println("Client connection error : " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void setupStreams() {
		try {
			output = new ObjectOutputStream(clientEndPoint.getOutputStream());
			input = new ObjectInputStream(clientEndPoint.getInputStream());
		} catch (IOException e) {
			System.out.println("Stream setup error: " + e.getLocalizedMessage());
		}
	}

	private void startMessaging() {
		Runnable r = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String message = "";
				do {
					System.out.print("Client: ");
					message = sc.nextLine();

					try {
						byte[] messageBye = message.getBytes();

						Signature sign = Signature.getInstance("DSA");
						sign.initSign(dsaKeypair.getPrivate());

						sign.update(messageBye);

						byte[] signature = sign.sign();

						byte[] encrypted = encrypt(sharedRSAPublicKey, messageBye);
						MessageBody messageBody = new MessageBody();
						messageBody.message = encrypted;
						messageBody.signature = signature;
						output.writeObject(messageBody);
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				} while (!message.equals("end"));
			}
		};
		new Thread(r).start();
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

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ILPClient client = new ILPClient();
		client.startRunning();
	}

}
