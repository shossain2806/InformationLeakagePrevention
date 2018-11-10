import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class ILPServer extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ServerSocket serverSocket;
	private Socket client;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Scanner sc;
	private KeyPair keyPair;
	private PublicKey clientDSAPublicKey;
	private JTextArea textArea;
	public ILPServer(int portNo) {
		super.setTitle("Server");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
     	getContentPane().setLayout(null);
     	int width = 400;
		int height = 500;
		//
		textArea = new JTextArea();
		int originXTextArea = 10;
		int originYTextArea = 10;
		int widhtTextArea = width - 20;
		int heightTextArea = height - 20;
		textArea.setEditable(false);
	    JScrollPane scroll = new JScrollPane(textArea);
	    scroll.setBounds(originXTextArea, originYTextArea, widhtTextArea, heightTextArea);
	    textArea.setLineWrap(true);
	    textArea.setWrapStyleWord(true);
	    getContentPane().add(scroll);
	    setLocationRelativeTo ( null );

	  	setSize(width,height);  
		setVisible(true); 
	  		
	  	
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
			System.out.println("Client connected: " + client.getRemoteSocketAddress());
			setupStreams();
			sharePublicKeys();
			startListening();

		} catch (IOException e) {
			System.out.println("Server connection error: " + e.getLocalizedMessage());
		} 
	}
	
	public void sharePublicKeys() {
		try {
			output.writeObject(keyPair.getPublic());
			clientDSAPublicKey = (PublicKey) input.readObject();
		}catch(Exception e) {
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
		textArea.append("Client: " +message+"\n");
	}

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ILPServer server = new ILPServer(6666);
		server.startRunning();
	}

}
