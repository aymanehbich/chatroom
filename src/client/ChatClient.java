package src.client;
import java.io.*;
import java.net.*;

public class ChatClient {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 5000);
            System.out.println("Connected! Waiting for other client...\n");
            
            BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter serverOut = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
            
            // Start message receiver thread
            MessageReceiver receiver = new MessageReceiver(serverIn);
            receiver.start();
            
            // Main thread sends messages
            String msg;
            while ((msg = keyboard.readLine()) != null) {
                if (msg.equalsIgnoreCase("quit")) break;
                serverOut.println(msg);
            }
            
            socket.close();
        } catch (IOException e) {
            System.out.println("Could not connect. Is the server running?");
        }
    }
}
