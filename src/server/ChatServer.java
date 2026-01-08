package src.server;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

import src.database.DatabaseManager;

public class ChatServer {
    private static Map<String, ClientHandler> clients = new ConcurrentHashMap<>();
    
    // Future: Add DatabaseManager here
    private static DatabaseManager dbManager;

    public static void main(String[] args) {
        System.out.println("Chat Server starting on port " + ServerConfig.PORT);
        
        // Future: Initialize database
        dbManager = new DatabaseManager();

        try (ServerSocket serverSocket = new ServerSocket(ServerConfig.PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                
                if (clients.size() >= ServerConfig.MAX_CLIENTS) {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println("ERROR:Server full. Please try again later.");
                    socket.close();
                    continue;
                }
                
                ClientHandler client = new ClientHandler(socket, dbManager);
                client.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Future: Close database
        finally {
            if (dbManager != null) {
                dbManager.close();
            }
        }
    }

    // Add client to the map with username as key
    static void addClient(String username, ClientHandler client) {
        clients.put(username, client);
        System.out.println("[Server] " + username + " added to clients list");
    }

    static void broadcast(String msg, ClientHandler sender) {
        for (ClientHandler c : clients.values()) {
            if (c != sender) {
                c.send(msg);
            }
        }
    }

    // Send private message to specific user
    static boolean sendPrivateMessage(String recipient, String msg) {
        ClientHandler recipientClient = clients.get(recipient);
        if (recipientClient != null) {
            recipientClient.send(msg);
            return true;
        }
        return false;
    }

    // Get list of online users
    static String getOnlineUsers(String currentUser) {
        StringBuilder users = new StringBuilder("Online users: ");
        for (String username : clients.keySet()) {
            if (!username.equals(currentUser)) {
                users.append(username).append(", ");
            }
        }
        if (users.toString().endsWith(", ")) {
            users.setLength(users.length() - 2);
        }
        return users.toString();
    }

    // Changed: Accept String username instead of ClientHandler
    static void remove(String username) {
        clients.remove(username);
        System.out.println("[Server] " + username + " removed from clients list");
    }
}