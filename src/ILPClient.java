import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ILPClient {
	private Socket clientEndPoint;
	private Scanner sc;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	
	public ILPClient() {
		// TODO Auto-generated constructor stub
		sc = new Scanner(System.in);
		
	}

	public void startRunning()  {

		//waiting for a user
		connectToServer();
		setupStreams();
		startMessaging();
		startListening();
		
	}
	
	private void connectToServer() {
		try {
			
			clientEndPoint = new Socket("127.0.0.1", 6666);
			System.out.println("Client connected to server: "+clientEndPoint.getRemoteSocketAddress());
		}catch(IOException e) {
			System.out.println("Client connection error : "+ e.getLocalizedMessage());
		}
	}
	
	private void setupStreams() {
		try {
			output = new ObjectOutputStream(clientEndPoint.getOutputStream());
			output.flush();
			input = new ObjectInputStream(clientEndPoint.getInputStream());
		}catch(IOException e) {
			System.out.println("Stream setup error: "+ e.getLocalizedMessage());
		}
		
	}
	
	private void startListening() {
		String message = "";
		do {
			try {
				 message = (String) input.readObject();
				 showMessage(message);
			}catch(ClassNotFoundException e) {
				System.out.println(e.getLocalizedMessage());
			}
			catch (IOException e) {
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
						System.out.print("Client: ");
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
	
	private void showMessage(String message) {
		System.out.println("Server: "+message);
	}
	

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ILPClient client = new ILPClient();
		client.startRunning();
	}
}
