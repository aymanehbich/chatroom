package src.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import src.models.Message;
import src.models.User;
import src.server.ServerConfig;

/**
 * Database Manager - Add implementation when ready
 * This is a placeholder to show where database code will go
 */
public class DatabaseManager {
    
    // Future: Add connection pool or connection management
    private Connection connection;
    
    public DatabaseManager() {
        // Future: Initialize database connection
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(
                ServerConfig.DB_URL,
                ServerConfig.DB_USER,
                ServerConfig.DB_PASSWORD
            );
            System.out.println("[DB] Connected to database");
        } catch (ClassNotFoundException e) {
            System.err.println("[DB] MySQL JDBC Driver not found");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("[DB] Connection failed");
            e.printStackTrace();
        }
    }

    // Check if username exists
    public boolean userExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error checking user: " + e.getMessage());
        }
        return false;
    }
    
    // Register new user
    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (username, password, joined_at) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setTimestamp(3, Timestamp.valueOf(user.getJoinedAt()));
            stmt.executeUpdate();
            System.out.println("[DB] Registered user: " + user.getUsername());
            return true;
        } catch (SQLException e) {
            System.err.println("[DB] Error registering user: " + e.getMessage());
            return false;
        }
    }
    
    // Login - verify username and password
    public boolean authenticateUser(String username, String password) {
        String sql = "SELECT password FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return storedPassword.equals(password);
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error authenticating user: " + e.getMessage());
        }
        return false;
    }
    
    public void saveUser(User user) {
        // Future: INSERT INTO users (username, joined_at) VALUES (?, ?)
        String sql = "INSERT INTO users (username, joined_at) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setTimestamp(2, Timestamp.valueOf(user.getJoinedAt()));
            stmt.executeUpdate();
            System.out.println("[DB] Saved user: " + user.getUsername());
        } catch (SQLException e) {
            System.err.println("[DB] Error saving user: " + e.getMessage());
        }
    }
    
    public void saveMessage(Message message) {
        // Future: INSERT INTO messages (sender, content, timestamp) VALUES (?, ?, ?)
        String sql = "INSERT INTO messages (sender, content, timestamp) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, message.getSender());
            stmt.setString(2, message.getContent());
            stmt.setTimestamp(3, Timestamp.valueOf(message.getTimestamp()));
            stmt.executeUpdate();
            System.out.println("[DB] Saved message from: " + message.getSender());
        } catch (SQLException e) {
            System.err.println("[DB] Error saving message: " + e.getMessage());
        }
    }
    
    public void close() {
        // Future: Close database connection
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connection closed");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error closing connection");
        }
    }
}
