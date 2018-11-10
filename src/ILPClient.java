import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;

import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ILPClient extends JFrame {
	private Socket clientEndPoint;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private PublicKey sharedRSAPublicKey;
	private JTextField textField;
	private JButton button;
	private int portNo;
	KeyPair dsaKeypair;

	public ILPClient(int port) {
		// TODO Auto-generated constructor stub
		portNo = port;
		dsaKeypair = DSA.buildKeyPair();
		configUI();
	}

	private void configUI() {

		super.setTitle("Client");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		int width = 400;
		int height = 200;
		// configure search button
		int buttonWidth = 100;
		int buttonHeight = 40;
		button = new JButton("Connect");// create button
		button.setBounds((width - buttonWidth) / 2, (height - buttonHeight) / 2, buttonWidth, buttonHeight);
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(clientEndPoint == null) {
					String host = JOptionPane.showInputDialog("Enter server address");
					boolean connected = connectToServer(host,portNo);
					if (connected) {
						button.setText("Send"); 
						textField.setEnabled(true);
						startRunning();
					}
					else {
						JOptionPane.showMessageDialog(null, "Try Again!");
					}
				}else {
					String message = textField.getText();
					sendMessage(message);
					textField.setText("");
				}
				
			}

		});
		button.setEnabled(true);
		getContentPane().add(button);

		// configure textField
		textField = new JTextField();
		int widthText = width - 40;
		int originX = (width - widthText) / 2;
		int heightText = 40;
		int padding = 20;
		int originY = button.getBounds().y - heightText - padding;
		textField.setBounds(originX, originY, widthText, heightText);
		textField.setEnabled(false);
		getContentPane().add(textField);

		//
		JLabel label = new JLabel("Send Message:");
		label.setBounds(originX, originY - 20, 100, 20);
		getContentPane().add(label);

		setSize(width, height);
		setVisible(true);
	}

	private void startRunning() {
	
		setupStreams();
		sharePublicKeys();
	}
	
	private boolean connectToServer(String host, int portNo) {
		
		if (host == null) {
			return false;
		}
		try {

			clientEndPoint = new Socket();
			clientEndPoint.connect(new InetSocketAddress(host, portNo), 3000);

			System.out.println("Client connected to server: " + clientEndPoint.getRemoteSocketAddress());
			return true;
		} catch (IOException e) {
			
			System.out.println("Client connection error : " + e.getLocalizedMessage());
			clientEndPoint = null;
			return false;
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

	private void sharePublicKeys() {
		try {
			output.writeObject(dsaKeypair.getPublic());
			sharedRSAPublicKey = (PublicKey) input.readObject();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void sendMessage(String message) {
		try {
			byte[] messageByte = message.getBytes();

			byte[] signature = DSA.signatureByte(messageByte, dsaKeypair.getPrivate());
			byte[] encrypted = RSA.encrypt(sharedRSAPublicKey, messageByte);

			MessageBody messageBody = new MessageBody();
			messageBody.message = encrypted;
			messageBody.signature = signature;

			output.writeObject(messageBody);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ILPClient client = new ILPClient(6666);
	}

}
