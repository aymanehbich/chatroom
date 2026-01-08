package src.client;

import java.io.*;

public class MessageReceiver extends Thread {
    private BufferedReader serverIn;
    
    public MessageReceiver(BufferedReader serverIn) {
        this.serverIn = serverIn;
    }
    
    public void run() {
        try {
            String msg;
            while ((msg = serverIn.readLine()) != null) {
                System.out.println(msg);
            }
        } catch (IOException e) {
            System.out.println("Disconnected");
        }
    }
}