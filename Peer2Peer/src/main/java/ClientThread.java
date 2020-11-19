import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import org.json.JSONObject;

import PtoP.Proto.Comms.broadcast;

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
	InputStream bufIn;
	
	public ClientThread(Socket socket, ServerThread in, String usernameIn) throws IOException {
		bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.serverThread = in;
		this.currentSocket = socket;
		this.currentUsername = usernameIn;
		this.bufIn = currentSocket.getInputStream();
	}
	public void run() {
		while (true) {
			try {
			    broadcast message = broadcast.parseDelimitedFrom(bufIn);;
			    if(message.getMessageType().equalsIgnoreCase("ready")) { // someone sends the ready message
			    	serverThread.readyPlayers++; // increase the number of players that are ready
			    	System.out.println(message.getUsername() + " is ready!"); // tell the user that someone is ready
			    	serverThread.checkReady();
			    }
			    
			    else if(message.getMessageType().equalsIgnoreCase("chat")) { // message is a chat
			    	System.out.println("[" + message.getUsername() + "] " + message.getData());	
			    }
			    
			    else if(message.getMessageType().equalsIgnoreCase("host")) { // someone else is host
			    	System.out.println("[" + message.getUsername() + "] " + message.getData());	
			    	serverThread.currentHost = 0;
			    }
			    
			    else if(message.getMessageType().equalsIgnoreCase("question")) { // message is a chat
			    	System.out.println(message.getUsername() + " asked: " + message.getData());
			    	System.out.print("Your answer:");
			    }
			    
			    else if(message.getMessageType().equalsIgnoreCase("answer")) { // someone sends an answer
//			    	System.out.println(message.getUsername() + " guessed: " + message.getData());
			    	if(serverThread.currentHost == 1) {
			    		serverThread.calculateAnswer(message.getData(), message.getUsername());
			    	}
			    }
			    
			    /// RESPONSES FROM SERVER IF ANS WAS RIGHT 
			    else if(message.getMessageType().equalsIgnoreCase("incorrect") && message.getUsername().equalsIgnoreCase(currentUsername)) { // user guess not right
			    	System.out.println("Wrong answer try again!");
			    	System.out.print("Your answer:");
			    }
			    
			    else if(message.getMessageType().equalsIgnoreCase("correct") && message.getUsername().equalsIgnoreCase(currentUsername)) { // user guess is right
			    	System.out.println("You got it right!");
			    	serverThread.currentHost = 1;
			    	serverThread.points++;
			    	serverThread.checkWin(currentUsername);
			    	System.out.println("Would you like to ask a question? (Yes/No)");
			    }
			    
			    else if(message.getMessageType().equalsIgnoreCase("correct") && !message.getUsername().equalsIgnoreCase(currentUsername)){ // someone else guessed right
			    	System.out.println(message.getUsername() + " made a correct guess!");
			    	System.out.println(message.getUsername() + " is now the host!");	
			    }
			    
			    else if(message.getMessageType().equalsIgnoreCase("incorrect") && !message.getUsername().equalsIgnoreCase(currentUsername)){ // someone else guessed wrong
			    	System.out.println(message.getUsername() + " made an incorrect guess!");	
			    }
			    
			    else if(message.getMessageType().equalsIgnoreCase("end") && !message.getUsername().equalsIgnoreCase(currentUsername)){ // someone else won
			    	System.out.println(message.getUsername() + " " + message.getData());
			    	System.exit(0);
			    }
			    
			    else {
			    	System.out.println("Unknown message recieved!");
			    }
			    
			} catch (Exception e) {
				interrupt();
				break;
			}
		}
	}

}
