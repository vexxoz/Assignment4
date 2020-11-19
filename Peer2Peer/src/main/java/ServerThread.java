import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

/**
 * SERVER
 * This is the ServerThread class that has a socket where we accept clients contacting us.
 * We save the clients ports connecting to the server into a List in this class. 
 * When we wand to send a message we send it to all the listening ports
 */

public class ServerThread extends Thread{
	private ServerSocket serverSocket;
	private Set<Socket> listeningSockets = new HashSet<Socket>();
	protected int players;
	protected int readyPlayers;
	protected boolean gameStarted;
	protected int currentHost;
	protected String currentAnswer;
	protected final int winningPoints = 2;
	protected int points;
	
	public ServerThread(String portNum) throws IOException {
		serverSocket = new ServerSocket(Integer.valueOf(portNum));
		this.players = 1;
		this.readyPlayers = 0;
		this.gameStarted = false;
		this.currentHost = -1;
		this.currentAnswer = "";
		this.points = 0;
	}
	
	/**
	 * Starting the thread, we are waiting for clients wanting to talk to us, then save the socket in a list
	 */
	public void run() {
		try {
			while (true) {
				Socket sock = serverSocket.accept();
				listeningSockets.add(sock);
				// add other player count
				players++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sending the message to the OutputStream for each socket that we saved
	 */
	void sendMessage(String message) {
		try {
			for (Socket s : listeningSockets) {
				PrintWriter out = new PrintWriter(s.getOutputStream(), true);
				out.println(message);
		     }
		} catch(Exception e) {
			e.printStackTrace();
		}
	}	
	
	
	void checkReady() {
    	if(readyPlayers == players) { // if everyone is ready
    		gameStarted = true; // the game is started
    		System.out.println("Game has been started! Are you the host? (Yes/No)"); // ask who is the host
    	}
	}
	
	// get the answer and the port of the person who answered as a way to tell them they were right
	void calculateAnswer(String in, String username) {
		if(in.equalsIgnoreCase(currentAnswer)) {
			sendMessage(generateMessage("correct", "Answer is correct!", username));
			currentHost = 0;
			System.out.println(username + " made a correct guess!");
			System.out.println(username + " is now the host!");	
		}else {
			sendMessage(generateMessage("incorrect", "Answer is not correct!", username));
			System.out.println(username + " made an incorrect guess!");
		}
	}
	
	void checkWin(String username) {
		System.out.println("Checking ur win! You have: " + points);
		if(points == winningPoints) {
			System.out.println("YOU Won the game!");
			generateMessage("end", "won the game!", username);
			System.exit(0);
		}
	}
	
	// allows some encapsulation allowing the serialization to change
	private String generateMessage(String type, String message, String username) {
		return "{'MessageType': '"+ type +"','username': '"+ username +"','message':'" + message + "'}";
	}
}
