package com.example.server.repository;

import com.example.server.model.StoredMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StoredMessageRepository extends JpaRepository<StoredMessage, Long> {
    List<StoredMessage> findByRecipient(String recipient);
}