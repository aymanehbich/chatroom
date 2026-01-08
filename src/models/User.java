package src.models;

import java.time.LocalDateTime;

public class User {
    private String username;
    private String password;
    private LocalDateTime joinedAt;
    
    public User(String username) {
        this.username = username;
        this.joinedAt = LocalDateTime.now();
    }
    
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.joinedAt = LocalDateTime.now();
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }
}