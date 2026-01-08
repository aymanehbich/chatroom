package src.server;

import src.database.DatabaseManager;
import src.models.Message;
import src.models.User;
import src.utils.MessageProtocol;

import java.io.*;
import java.net.*;


public class ClientHandler extends Thread {
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private User user;
    private DatabaseManager dbManager;

    public ClientHandler(Socket socket, DatabaseManager dbManager) throws IOException {
        this.socket = socket;
        this.dbManager = dbManager;
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new PrintWriter(socket.getOutputStream(), true);
    }

    void send(String msg) {
        output.println(msg);
    }

    public void run() {
        try {
            // Ask if user wants to login or register
            output.println(MessageProtocol.createPrompt("Enter 1 to Login, 2 to Register:"));
            String choice = input.readLine();
            
            if (choice == null || (!choice.equals("1") && !choice.equals("2"))) {
                output.println(MessageProtocol.createError("Invalid choice"));
                socket.close();
                return;
            }
            
            // Get username
            output.println(MessageProtocol.createPrompt("Enter username:"));
            String username = input.readLine();
            
            if (username == null || username.trim().isEmpty()) {
                output.println(MessageProtocol.createError("Invalid username"));
                socket.close();
                return;
            }
            
            // Get password
            output.println(MessageProtocol.createPrompt("Enter password:"));
            String password = input.readLine();
            
            if (password == null || password.trim().isEmpty()) {
                output.println(MessageProtocol.createError("Invalid password"));
                socket.close();
                return;
            }
            
            username = username.trim();
            password = password.trim();
            
            // Handle Login (1) or Register (2)
            if (choice.equals("1")) {
                if (!dbManager.authenticateUser(username, password)) {
                    output.println(MessageProtocol.createError("Invalid username or password"));
                    socket.close();
                    return;
                }
                output.println(MessageProtocol.createSystem("Login successful!"));
                user = new User(username);
            } else {
                if (dbManager.userExists(username)) {
                    output.println(MessageProtocol.createError("Username already exists"));
                    socket.close();
                    return;
                }
                user = new User(username, password);
                if (!dbManager.registerUser(user)) {
                    output.println(MessageProtocol.createError("Registration failed"));
                    socket.close();
                    return;
                }
                output.println(MessageProtocol.createSystem("Registration successful!"));
            }
            
            // Add client to server's client list
            ChatServer.addClient(user.getUsername(), this);

            System.out.println(user.getUsername() + " connected");

            // Show instructions - UPDATED
            output.println(MessageProtocol.createSystem("Commands:"));
            output.println(MessageProtocol.createSystem("  /users - List online users"));
            output.println(MessageProtocol.createSystem("  @username message - Send message to user"));

            ChatServer.broadcast(MessageProtocol.createSystem(user.getUsername() + " joined the chat"), this);

            // Chat loop
            String msg;
            while ((msg = input.readLine()) != null) {
                if (msg.trim().isEmpty()) continue;
                
                // Handle /users command
                if (msg.equals("/users")) {
                    output.println(MessageProtocol.createSystem(ChatServer.getOnlineUsers(user.getUsername())));
                    continue;
                }
                
                // Require @username for all messages
                if (msg.startsWith("@")) {
                    int spaceIndex = msg.indexOf(' ');
                    if (spaceIndex != -1) {
                        String recipient = msg.substring(1, spaceIndex);
                        String privateMsg = msg.substring(spaceIndex + 1);
                        
                        boolean sent = ChatServer.sendPrivateMessage(
                            recipient, 
                            MessageProtocol.createMessage(user.getUsername(), privateMsg)
                        );
                        
                        if (sent) {
                            output.println(MessageProtocol.createSystem("Message sent to " + recipient));
                            Message message = new Message(user.getUsername(), "@" + recipient + ": " + privateMsg);
                            dbManager.saveMessage(message);
                        } else {
                            output.println(MessageProtocol.createError("User " + recipient + " not found or offline"));
                        }
                    } else {
                        output.println(MessageProtocol.createError("Usage: @username message"));
                    }
                } else {
                    // Reject messages without @username
                    output.println(MessageProtocol.createError("Please specify a recipient: @username message"));
                    // output.println(MessageProtocol.createSystem("Use /users to see who's online"));
                }
            }
        } catch (IOException e) {
            System.out.println(user != null ? user.getUsername() + " disconnected unexpectedly" : "Client disconnected");
        } finally {
            if (user != null) {
                ChatServer.remove(user.getUsername());
                ChatServer.broadcast(MessageProtocol.createSystem(user.getUsername() + " left the chat"), this);
            }
            try { socket.close(); } catch (IOException ignored) {}
        }
    }
}