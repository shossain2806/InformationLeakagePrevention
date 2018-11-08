import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ILPServer {
	
	private ServerSocket serverSocket;
	private Socket client;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Scanner sc;
	public ILPServer(int portNo) {
	
		try {
			serverSocket = new ServerSocket(portNo,1);
			sc = new Scanner(System.in);
			
		}catch(IOException e) {
			System.out.println("Server Initialization error: "+ e.getLocalizedMessage());
		}
		
	}
	
	public void startRunning() {

		//waiting for a user
		try {
			System.out.println("Waiting for a connection on port: "+serverSocket.getLocalPort());
		    client = serverSocket.accept();
		    System.out.println("Client connected: "+client.getRemoteSocketAddress());
			setupStreams();
			startMessaging();
			startListening();
			
		}catch(IOException e) {
			System.out.println("Server connection error: "+ e.getLocalizedMessage());
		}
	}
	
	private void setupStreams() {
		try {
			output = new ObjectOutputStream(client.getOutputStream());
			output.flush();
			input = new ObjectInputStream(client.getInputStream());
		}catch(IOException e) {
			System.out.println("Stream setup error: "+ e.getLocalizedMessage());
		}
		
	}
	
	private void startListening() throws IOException{
		String message = "";
		do {
			try {
				 message = (String) input.readObject();
				 showMessage(message);
			}catch(ClassNotFoundException e) {
				System.out.println(e.getLocalizedMessage());
			}
		}while(!message.equals("end"));
		
	}
	

	private void startMessaging() {
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String message = "";
				do {
					try {
						System.out.print("Server: ");
						message = sc.nextLine();
						output.writeObject(message);
						output.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}while(!message.equals("end"));
			}
		};
		new Thread(r).start();
	}
	
	void showMessage(String message) {
		System.out.println("Client: "+message);
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ILPServer server = new ILPServer(6666);
		server.startRunning();
	}

}
