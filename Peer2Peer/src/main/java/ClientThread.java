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
	Socket currentSocket;
	String currentUsername;
	
	public ClientThread(Socket socket, ServerThread in, String usernameIn) throws IOException {
		bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.serverThread = in;
		this.currentSocket = socket;
		this.currentUsername = usernameIn;
	}
	public void run() {
		while (true) {
			try {
			    JSONObject json = new JSONObject(bufferedReader.readLine());
			    if(json.getString("MessageType").equalsIgnoreCase("ready")) { // someone sends the ready message
			    	serverThread.readyPlayers++; // increase the number of players that are ready
			    	System.out.println(json.getString("username").toString() + " is ready!"); // tell the user that someone is ready
			    	serverThread.checkReady();
			    }else if(json.getString("MessageType").equalsIgnoreCase("chat")) { // message is a chat
			    	System.out.println("[" + json.getString("username").toString() + "] " + json.getString("message").toString());	
			    }else if(json.getString("MessageType").equalsIgnoreCase("host")) { // message is a chat
			    	System.out.println("[" + json.getString("username").toString() + "] " + json.getString("message").toString());	
			    }else if(json.getString("MessageType").equalsIgnoreCase("question")) { // message is a chat
			    	System.out.println(json.getString("username").toString() + " asked: " + json.getString("message").toString());
			    	System.out.print("Your answer:");
			    }else if(json.getString("MessageType").equalsIgnoreCase("answer")) { // message is a chat
			    	System.out.println(json.getString("username").toString() + " guessed: " + json.getString("message").toString());
			    	if(serverThread.currentHost == 1) {
			    		serverThread.calculateAnswer(json.getString("message").toString(), json.getString("username").toString());
			    	}
			    }else if(json.getString("MessageType").equalsIgnoreCase("incorrect") && json.getString("username").equalsIgnoreCase(currentUsername)) { // user guess not right
			    	System.out.println("Wrong answer try again!");
			    }else if(json.getString("MessageType").equalsIgnoreCase("correct") && json.getString("username").equalsIgnoreCase(currentUsername)) { // user guess is right
			    	System.out.println("You got it right!");
			    	serverThread.currentHost = 1;
			    	System.out.println("Would you like to ask a question? (Yes/No)");
			    }else if(json.getString("MessageType").equalsIgnoreCase("correct") && !json.getString("username").equalsIgnoreCase(currentUsername)){ // someone else guessed right
			    	System.out.println(json.getString("username").toString() + " made a correct guess!");	
			    }else if(json.getString("MessageType").equalsIgnoreCase("incorrect") && !json.getString("username").equalsIgnoreCase(currentUsername)){ // someone else guessed wrong
			    	System.out.println(json.getString("username").toString() + " made an incorrect guess!");	
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
