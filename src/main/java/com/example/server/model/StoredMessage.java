package com.example.server.model;

import jakarta.persistence.*;

@Entity
@Table(name = "messages")
public class StoredMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String sender;

    @Column(nullable = false, length = 50)
    private String recipient;

    @Column(length = 10000)
    private String cipherText;

    @Column(length = 32)
    private String iv;

    @Column(length = 255)
    private String ephemeralPublicKey;

    private String signature;

    @Column(length = 32)
    private String timestamp;

    private String salt;

    public StoredMessage() {}

    public StoredMessage(String sender,
                         String recipient,
                         String cipherText,
                         String iv,
                         String ephemeralKey,
                         String signature,
                         String salt,
                         String timestamp) {
        this.sender = sender;
        this.recipient = recipient;
        this.cipherText = cipherText;
        this.iv = iv;
        this.ephemeralPublicKey = ephemeralKey;
        this.signature = signature;
        this.timestamp = timestamp;
        this.salt = salt;
    }

    public Long getId() { return id; }
    public String getSender() { return sender; }
    public String getRecipient() { return recipient; }
    public String getCipherText() { return cipherText; }
    public String getIv() { return iv; }
    public String getEphemeralPublicKey() { return ephemeralPublicKey; }
    public String getSignature() { return signature; }
    public String getTimestamp() { return timestamp; }
    public String getSalt() { return salt; }

}