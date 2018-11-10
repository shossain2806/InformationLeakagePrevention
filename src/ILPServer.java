import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Scanner;

import javax.crypto.Cipher;

public class ILPServer {

	private ServerSocket serverSocket;
	private Socket client;
	private DataOutputStream output;
	private DataInputStream input;
	private Scanner sc;
	private KeyPair keyPair;
	private PublicKey clientDSAPublicKey;

	public ILPServer(int portNo) {

		try {
			serverSocket = new ServerSocket(portNo, 1);
			sc = new Scanner(System.in);
			keyPair = buildKeyPair();
		} catch (IOException e) {
			System.out.println("Server Initialization error: " + e.getLocalizedMessage());
		}

	}

	public void startRunning() {

		// waiting for a user
		try {
			System.out.println("Waiting for a connection on port: " + serverSocket.getLocalPort());
			client = serverSocket.accept();

			ObjectOutputStream objOutput = new ObjectOutputStream(client.getOutputStream());
			objOutput.writeObject(keyPair.getPublic());

			ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
			clientDSAPublicKey = (PublicKey) ois.readObject();

			System.out.println("Client connected: " + client.getRemoteSocketAddress());

			setupStreams();
			startListening();

		} catch (IOException e) {
			System.out.println("Server connection error: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void setupStreams() {

		try {
			output = new DataOutputStream(client.getOutputStream());
			input = new DataInputStream(client.getInputStream());
		} catch (IOException e) {
			System.out.println("Stream setup error: " + e.getLocalizedMessage());
		}

	}

	private void startListening() {

		do {
			try {

				int len = input.readInt();
				byte[] encrypted = new byte[len];

				input.readFully(encrypted, 0, len);

				byte[] decrypted = decrypt(keyPair.getPrivate(), encrypted);

				int lenSig = input.readInt();
				byte[] signature = new byte[lenSig];

				input.readFully(signature, 0, lenSig);

				Signature verifyalg = Signature.getInstance("DSA");
				verifyalg.initVerify(clientDSAPublicKey);

				verifyalg.update(decrypted);
				if (verifyalg.verify(signature)) {
					String message = new String(decrypted);
					showMessage(message);
				} else {
					showMessage("not verified message");
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}

		} while (true);

	}

	void showMessage(String message) {
		System.out.println("Client: " + message);
	}

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

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ILPServer server = new ILPServer(6666);
		server.startRunning();
	}

}
