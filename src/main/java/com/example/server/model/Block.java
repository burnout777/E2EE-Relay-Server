package com.example.server.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "block")
public class Block {

    @Id
    private Long id;

    private String previousHash;
    private String currentHash;
    private String username;
    private String retiredPublicKey;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;

    public Block() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPreviousHash() { return previousHash; }
    public void setPreviousHash(String previousHash) { this.previousHash = previousHash; }

    public String getCurrentHash() { return currentHash; }
    public void setCurrentHash(String currentHash) { this.currentHash = currentHash; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRetiredPublicKey() { return retiredPublicKey; }
    public void setRetiredPublicKey(String retiredPublicKey) { this.retiredPublicKey = retiredPublicKey; }

    public LocalDateTime getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDateTime validFrom) { this.validFrom = validFrom; }

    public LocalDateTime getValidTo() { return validTo; }
    public void setValidTo(LocalDateTime validTo) { this.validTo = validTo; }
}