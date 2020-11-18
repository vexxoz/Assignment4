import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadedServer {
    public static void main(String args[]) throws Exception {

        StringList strings = new StringList();
        Lock mutex = new ReentrantLock();

        if (args.length != 1) {
            System.out.println("Usage: ThreadedServer <port>");
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
        ServerSocket server = new ServerSocket(port);
        System.out.println("Server Started on port "+ port +"...");
        while (true) {
            System.out.println("Accepting a Request...");
            Socket sock = server.accept();
            System.out.println("Request Accepted!");
            
            // create the object with the socket and reference to the list (important)
            Performer performer = new Performer(sock, strings, mutex);
            // run the methods in a new thread
            new Thread(performer).start();
        }
    }
}
