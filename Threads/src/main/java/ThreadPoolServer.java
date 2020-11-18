import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadPoolServer {
    public static void main(String args[]) throws Exception {

        StringList strings = new StringList();
        Lock mutex = new ReentrantLock();

        if (args.length != 2) {
            System.out.println("Usage: ThreadedServer <port> <bound>");
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
        ServerSocket server = new ServerSocket(port);
        
        int bound = Integer.parseInt(args[1]);
        Executor pool = Executors.newFixedThreadPool(bound);;
        
        System.out.println("Server Started on port "+ port +" with a max bound of "+bound+"...");
        while (true) {
            System.out.println("Accepting a Request...");
            Socket sock = server.accept();
            System.out.println("Request Accepted!");
            
            // create the object with the socket and reference to the list (important)
            Performer performer = new Performer(sock, strings, mutex);
            // run the methods in a new pool thread
            pool.execute(performer);
        }
    }
}
