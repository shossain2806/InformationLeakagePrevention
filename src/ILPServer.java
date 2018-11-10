import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Scanner;


public class ILPServer {

	private ServerSocket serverSocket;
	private Socket client;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Scanner sc;
	private KeyPair keyPair;
	private PublicKey clientDSAPublicKey;

	public ILPServer(int portNo) {

		try {
			serverSocket = new ServerSocket(portNo, 1);
			sc = new Scanner(System.in);
			keyPair = RSA.buildKeyPair();
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
			output = new ObjectOutputStream(client.getOutputStream());
			input = new ObjectInputStream(client.getInputStream());
		} catch (IOException e) {
			System.out.println("Stream setup error: " + e.getLocalizedMessage());
		}

	}

	private void startListening() {

		do {
			try {

				MessageBody messageBody = (MessageBody) input.readObject();
		
				byte[] decrypted = RSA.decrypt(keyPair.getPrivate(), messageBody.message);
				byte[] signature = messageBody.signature;

				boolean verified = DSA.verifyWith(clientDSAPublicKey, decrypted, signature);

				if (verified) {
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

	

	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ILPServer server = new ILPServer(6666);
		server.startRunning();
	}

}
