import java.io.IOException;
import java.net.Socket;

public class ILPClient {

	public ILPClient() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ILPClient client = new ILPClient();
		try {
			Socket scktClient = new Socket("127.0.0.1", 6666);
			System.out.println("Client connected to server: "+scktClient.getRemoteSocketAddress());
		}catch(IOException e) {
			System.out.println("Client connection error : "+ e.getLocalizedMessage());
		}
		
	}

}
