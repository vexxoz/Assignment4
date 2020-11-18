import java.net.*;
import java.io.*;

class Server {
    public static void main(String args[]) throws Exception {

        StringList strings = new StringList();

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
            Performer performer = new Performer(sock, strings);
            performer.doPerform();
        }
    }
}