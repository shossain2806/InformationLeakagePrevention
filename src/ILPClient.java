import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;

import java.util.Scanner;


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
		dsaKeypair = DSA.buildKeyPair();

	}

	public void startRunning() {

		// waiting for a user
		connectToServer();
		setupStreams();
		sharePublicKeys();
		startMessaging();

	}

	private void connectToServer() {
		try {

			clientEndPoint = new Socket("127.0.0.1", 6666);

			System.out.println("Client connected to server: " + clientEndPoint.getRemoteSocketAddress());
		} catch (IOException e) {
			System.out.println("Client connection error : " + e.getLocalizedMessage());
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
	
	public void sharePublicKeys() {
		try {
			output.writeObject(dsaKeypair.getPublic());
			sharedRSAPublicKey = (PublicKey) input.readObject();
			
		}catch(Exception e) {
			e.printStackTrace();
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
						byte[] messageByte = message.getBytes();

						byte[] signature = DSA.signatureByte(messageByte, dsaKeypair.getPrivate());
						byte[] encrypted = RSA.encrypt(sharedRSAPublicKey, messageByte);
						
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



	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ILPClient client = new ILPClient();
		client.startRunning();
	}

}
