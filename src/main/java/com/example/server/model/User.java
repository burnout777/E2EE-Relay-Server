package com.example.server.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "app_users")
public class User {

    @Id
    private String username;

    @Column(unique = true, nullable = false)
    private String password;

    @Lob
    private String publicKey;

    private LocalDateTime lastRotation;

    public User() {
    }

    public User(String username, String publicKey, LocalDateTime lastRotation) {
        this.username = username;
        this.publicKey = publicKey;
        this.lastRotation = lastRotation;
    }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }

    public LocalDateTime getLastRotation() { return lastRotation; }
    public void setLastRotation(LocalDateTime lastRotation) { this.lastRotation = lastRotation; }


}