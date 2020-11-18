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
			    if(json.getString("MessageType").equalsIgnoreCase("ready")) { // someone sends the ready message
			    	serverThread.readyPlayers++; // increase the number of players that are ready
			    	System.out.println(json.getString("username").toString() + " is ready!"); // tell the user that someone is ready
			    	if(serverThread.readyPlayers == serverThread.players) { // if everyone is ready
			    		serverThread.gameStarted = true; // the game is started
			    		System.out.println("Game has been started! Are you the host? (Yes/No)"); // ask who is the host
			    	}
			    }else if(json.getString("MessageType").equalsIgnoreCase("chat")) { // message is a chat
			    	System.out.println("[" + json.getString("username").toString() + "] " + json.getString("message").toString());	
			    }else if(json.getString("MessageType").equalsIgnoreCase("host")) { // message is a chat
			    	System.out.println("[" + json.getString("username").toString() + "] " + json.getString("message").toString());	
			    }else {
			    	System.out.println("Unknown message recieved!");
			    }
			    
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
