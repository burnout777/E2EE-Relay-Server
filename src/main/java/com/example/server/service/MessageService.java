package com.example.server.service;

import com.example.server.dto.EncryptedMessageDTO;
import com.example.server.model.StoredMessage;
import com.example.server.repository.StoredMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MessageService {

    private final StoredMessageRepository messageRepository;

    public MessageService(StoredMessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Transactional
    public StoredMessage processAndSaveMessage(EncryptedMessageDTO dto, String authenticatedSender) {
        String sender = (authenticatedSender != null && !"Unknown".equals(authenticatedSender))
                ? authenticatedSender
                : dto.getSender();

        StoredMessage stored = new StoredMessage(
                sender,
                dto.getRecipientUser(),
                dto.getCipherText(),
                dto.getIv(),
                dto.getEphemeralPublicKey(),
                dto.getSignature(),
                dto.getSalt(),
                dto.getTimestamp()
        );

        return messageRepository.save(stored);

    }

    public List<EncryptedMessageDTO> getMessagesForUser(String username) {
        List<StoredMessage> entities = messageRepository.findByRecipient(username);

        return entities.stream().map(entity -> {
            EncryptedMessageDTO dto = new EncryptedMessageDTO();
            dto.setRecipientUser(entity.getRecipient());
            dto.setCipherText(entity.getCipherText());
            dto.setIv(entity.getIv());
            dto.setEphemeralPublicKey(entity.getEphemeralPublicKey());
            dto.setSignature(entity.getSignature());
            dto.setSender(entity.getSender());
            dto.setSalt(entity.getSalt());
            dto.setTimestamp(entity.getTimestamp());
            return dto;
        }).toList();
    }

    @Transactional
    public void deleteMessagesForUser(String username) {
        List<StoredMessage> messages = messageRepository.findByRecipient(username);
        messageRepository.deleteAll(messages);
    }
}