import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * This is the main class for the peer2peer program.
 * It starts a client with a username and port. Next the peer can decide who to listen to. 
 * So this peer2peer application is basically a subscriber model, we can "blurt" out to anyone who wants to listen and 
 * we can decide who to listen to. We cannot limit in here who can listen to us. So we talk publicly but listen to only the other peers
 * we are interested in. 
 * 
 */

public class Peer {
	private String username;
	private BufferedReader bufferedReader;
	private ServerThread serverThread;
	protected boolean isHost;
	protected int points;
	protected final int winningPoints = 5;
	private boolean ready;
	
	public Peer(BufferedReader bufReader, String username, ServerThread serverThread){
		this.username = username;
		this.bufferedReader = bufReader;
		this.serverThread = serverThread;
		this.isHost = false;
		this.points = 0;
		this.ready = false;
	}
	/**
	 * Main method saying hi and also starting the Server thread where other peers can subscribe to listen
	 *
	 * @param args[0] username
	 * @param args[1] port for server
	 */
	public static void main (String[] args) throws Exception {

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		String username = args[0];
		System.out.println("Hello " + username + " and welcome! Your port will be " + args[1]);

		// starting the Server Thread, which waits for other peers to want to connect
		ServerThread serverThread = new ServerThread(args[1]);
		serverThread.start();
		Peer peer = new Peer(bufferedReader, args[0], serverThread);
		peer.updateListenToPeers();
	}
	
	/**
	 * User is asked to define who they want to subscribe/listen to
	 * Per default we listen to no one
	 *
	 */
	public void updateListenToPeers() throws Exception {
		System.out.println("> Who do you want to listen to? Enter host:port");
		String input = bufferedReader.readLine();
		String[] setupValue = input.split(" ");
		for (int i = 0; i < setupValue.length; i++) {
			String[] address = setupValue[i].split(":");
			Socket socket = null;
			try {
				socket = new Socket(address[0], Integer.valueOf(address[1]));
				new ClientThread(socket, serverThread).start();
			} catch (Exception c) {
				if (socket != null) {
					socket.close();
				} else {
					System.out.println("Cannot connect, wrong input");
					System.out.println("Exiting: I know really user friendly");
					System.exit(0);
				}
			}
		}

		askForInput();
	}
	
	/**
	 * Client waits for user to input their message or quit
	 *
	 * @param bufReader bufferedReader to listen for user entries
	 * @param username name of this peer
	 * @param serverThread server thread that is waiting for peers to sign up
	 */
	public void askForInput() throws Exception {
		try {
			System.out.println("> Type ready when ready to begin the game (exit to exit)");
			while(true) {
				String send = "";
				String message = bufferedReader.readLine();
				if(message.equalsIgnoreCase("exit")){ // they want to exit
					System.out.println("Now exiting!");
					break;
				}else if(message.equalsIgnoreCase("ready") && !ready) {// if they type ready
					System.out.println("Ready to play game! You can chat in the meantime!");
					send = generateMessage("ready", message);
					serverThread.readyPlayers++;
					ready = true;
					serverThread.checkReady();
				}else if(ready && !serverThread.gameStarted) { // if ready can chat
					send = generateMessage("chat", message);
				}else if(ready && serverThread.gameStarted) { // they are ready and the game has been started
					if(message.equalsIgnoreCase("yes")){ // if they say yes to current host
						serverThread.currentHost = true;
						send = generateMessage("host", "is the game host!");	
					}else {
						serverThread.currentHost = false;
						System.out.println("You are not the host! Please wait for a question to be asked!");
					}
				}else {
					System.out.println("Unknown command!");
				}
				
				// if there is something to send
				if(send.length() > 0) {
					serverThread.sendMessage(send);
				}
			}
			System.exit(0);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// allows some encapsulation allowing the serialization to change
	private String generateMessage(String type, String message) {
		return "{'MessageType': '"+ type +"','username': '"+ username +"','message':'" + message + "'}";
	}
}
