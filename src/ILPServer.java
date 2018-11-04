import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ILPServer implements Runnable{
	
	private ServerSocket serverSocket;
	
	public ILPServer(int portNo) {
	
		try {
			serverSocket = new ServerSocket(portNo);
	
		}catch(IOException e) {
			System.out.println("Server Initialization error: "+ e.getLocalizedMessage());
		}
			
	}
	
	/**
	 * implemented method of Runnable interface
	 */
	public void run() {
		
		System.out.println("Server Started");
		//waiting for new user
		while(true) {
			try {
				System.out.println("Waiting for a connection on port: "+serverSocket.getLocalPort());
				Socket server = serverSocket.accept();
				System.out.println("Client connected: "+server.getRemoteSocketAddress());
			}catch(IOException e) {
				System.out.println("Server connection error: "+ e.getLocalizedMessage());
			}
			
		}

	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ILPServer server = new ILPServer(6666);
		
		Thread serverThread = new Thread(server, "Server-Thread");
		serverThread.start();
		
	}

}
