import java.io.Serializable;

public class MessageBody implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public byte[] message;
	public byte[] signature;
	
	public MessageBody() {
		// TODO Auto-generated constructor stub
	}

}
