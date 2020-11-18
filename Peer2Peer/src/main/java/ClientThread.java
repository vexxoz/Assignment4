import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.json.*;

/**
 * Client 
 * This is the Client thread class, there is a client thread for each peer we are listening to.
 * We are constantly listening and if we get a message we print it. 
 */

public class ClientThread extends Thread {
	private BufferedReader bufferedReader;
	private ServerThread serverThread;
	
	public ClientThread(Socket socket, ServerThread in) throws IOException {
		bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.serverThread = in;
	}
	public void run() {
		while (true) {
			try {
			    JSONObject json = new JSONObject(bufferedReader.readLine());
			    if(json.getString("MessageType").equalsIgnoreCase("ready")) {
			    	serverThread.readyPlayers++;
			    }
			    // just print the response to allow for easier reading and debugging
			    System.out.println(json.toString());
			} catch (Exception e) {
				interrupt();
				break;
			}
		}
	}
	
//	private String decodeProto() {
//		
//	}

}
