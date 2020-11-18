import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.io.*;

class Performer implements Runnable{

    StringList state;
    Socket sock;
    protected Lock mutex;

    public Performer(Socket sock, StringList strings, Lock mutexIn) {
        this.sock = sock;    
        this.state = strings;
        this.mutex = mutexIn;
    }

    public void doPerform() {
        
        BufferedReader in = null;
        PrintWriter out = null;
        try {

            in = new BufferedReader(
                        new InputStreamReader(sock.getInputStream()));
            out = new PrintWriter(sock.getOutputStream(), true);
            out.println("Enter text (. to disconnect):");

            boolean done = false;
            while (!done) {
                String str = in.readLine();

                if (str == null || str.equals("."))
                    done = true;
                else if(str.contains("add")) {
                	mutex.lock();
                	try {
	                	// get the string without space or add
	                	String input = str.split("add")[1].split(" ")[1];
	                	state.add(input);
	                	out.println("Server state is now: " + state.toString());
                	}catch(ArrayIndexOutOfBoundsException e) {
                		out.println("No word was given to add!");
                	}finally {
                		mutex.unlock();
                	}
                }
                else if(str.contains("remove")) {
                	mutex.lock();
                	try {
                		String input = str.split("remove")[1].split(" ")[1];
	                	int index = Integer.parseInt(input);
	                	if(index < state.size()) {
	                		String word = state.getIndex(index);
	                		state.remove(index);
	                		out.println("Removed " + word);
	                	}else {
	                		out.println("No such index");
	                	}
                	}catch(NumberFormatException e) {
                		out.println("No number sent please send a number for an index");
                	}catch(ArrayIndexOutOfBoundsException e) {
                		out.println("No number sent please send a number for an index");
                	}finally {
                		mutex.unlock();
                	}
                }
                else if(str.contains("display")) {
                	out.println("Server state is now: " + state.toString());	
                }
                else if(str.contains("reverse")) {
                	mutex.lock();
                	try {
	                	String input = str.split("reverse")[1].split(" ")[1];
	                	int index = Integer.parseInt(input);
	                	
	                	if(index < state.size()) {
	                	
	                		String word = state.getIndex(index);
	                		String newWord = "";
	                		for(int i=word.length()-1;i>=0;i--) {
	                			newWord = newWord + word.charAt(i);
	                		}
	                		
	                		state.set(index, newWord);
	                		out.println("Server state is now: " + state.toString());	
	                	}else {
	                		out.println("No such index");
	                	}
                	}catch(NumberFormatException e) {
                		out.println("No number sent please send a number for an index");
                	}catch(ArrayIndexOutOfBoundsException e) {
                		out.println("No number sent please send a number for an index");
                	}finally {
                		mutex.unlock();
                	}
                }
                else if(str.contains("count")) {
                	mutex.lock();
                	try {
	                	List<Integer> response = new ArrayList<Integer>();
	                	for(int i=0;i<state.size();i++) {
	                		response.add(state.getIndex(i).length());
	                	}
	                	out.println("Server state count is: " + response.toString());
                	}finally {
                		mutex.unlock();
                	}
                }
                else {
                    out.println("Unknown Command try: add <String>, remove <int>, display, count, reverse <int>");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
            try {
                in.close();
            } catch (IOException e) {e.printStackTrace();}
            try {
                sock.close();
            } catch (IOException e) {e.printStackTrace();}
        }
    }

	@Override
	public void run() {
		doPerform();
	}
}
