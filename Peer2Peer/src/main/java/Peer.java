import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

import org.json.*;

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
	protected int points;
	protected final int winningPoints = 5;
	private boolean ready;
	private File qFile;
	
	public Peer(BufferedReader bufReader, String username, ServerThread serverThread){
		this.username = username;
		this.bufferedReader = bufReader;
		this.serverThread = serverThread;
		this.points = 0;
		this.ready = false;
		qFile = new File("./src/main/java/questions.json");
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
				}else if(ready && !serverThread.gameStarted) { // ready but game not yet started
					send = generateMessage("chat", message);
				}else if(ready && serverThread.gameStarted && serverThread.currentHost == -1) { // they are ready and the game has been started, for picking the host section
					
					if(message.equalsIgnoreCase("yes")){ // if they say yes to current host
						serverThread.currentHost = 1;
						send = generateMessage("host", "is the game host!");
						System.out.println("You are now the host!");
						System.out.println("Would you like to ask a question? (Yes/No)");
					}else { // they dont want to be a host
						serverThread.currentHost = 0;
						System.out.println("You are not the host! Please wait for a question to be asked!");
					}
					
				}else if(serverThread.currentHost == 1 && ready && serverThread.gameStarted) { // if game is started and user is host
					if(message.equalsIgnoreCase("yes")) {
						// get a question from the json list
						String[] questionArray = getQuestion();
						
						//parse the question array
						String question = questionArray[0];
						String answer = questionArray[1];
						// send the question
						send = generateMessage("question", question);
					}
				}else if(serverThread.currentHost == 0 && ready && serverThread.gameStarted) { // if game is started and user is not the host
					
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
	
	// gets a question from the Json file
	private String[] getQuestion() {
		// set up temp variables
		String[] temp = new String[2];
		String tempJson = "";
		
		// try reading the file of questions
		try {
			Scanner read = new Scanner(qFile);
			while(read.hasNextLine()) {
				tempJson = tempJson + read.nextLine();
			}
		}catch(FileNotFoundException y ) {
			System.out.println("Cannot find queston file! Quitting!");
			System.exit(1);
		}
		
		// turn the file into a json array
		JSONTokener json = new JSONTokener(tempJson);
		JSONArray jsonQList = new JSONArray(json);
		
		// get a random int for a random question to be asked
		Random rand = new Random();
		int randIndex = rand.nextInt(jsonQList.length());
		
		// get the question at that random index
		JSONObject question = (JSONObject)jsonQList.get(randIndex); 
		
		// fix the question into the string array
		temp[0] = question.getString("question");
		temp[1] = question.getString("answer");
		
		//return the string array
		return temp;
	}
	
	// allows some encapsulation allowing the serialization to change
	private String generateMessage(String type, String message) {
		return "{'MessageType': '"+ type +"','username': '"+ username +"','message':'" + message + "'}";
	}
}
