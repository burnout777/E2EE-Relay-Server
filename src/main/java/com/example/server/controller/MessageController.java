package com.example.server.controller;

import com.example.server.dto.EncryptedMessageDTO;
import com.example.server.model.StoredMessage;
import com.example.server.service.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.List;

@Controller
public class MessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MessageController(SimpMessagingTemplate messagingTemplate, MessageService messageService) {
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
    }

    @MessageMapping("/chat")
    public void relayMessage(@Payload String rawJsonPayload, Principal principal) {
        try {
            EncryptedMessageDTO messageDto = objectMapper.readValue(rawJsonPayload, EncryptedMessageDTO.class);
            String authName = (principal != null) ? principal.getName() : "Unknown";

            StoredMessage savedMsg = messageService.processAndSaveMessage(messageDto, authName);

            messageDto.setSender(savedMsg.getSender());

            System.out.println("Relaying message from " + savedMsg.getSender() + " to " + messageDto.getRecipientUser());
            messagingTemplate.convertAndSendToUser(
                    messageDto.getRecipientUser(),
                    "/queue/messages",
                    messageDto
            );

        } catch (Exception e) {
            System.err.println("Relay failed: " + e.getMessage());
        }
    }

    @GetMapping("/api/messages/{username}")
    @ResponseBody
    public ResponseEntity<List<EncryptedMessageDTO>> getHistory(@PathVariable String username) {
        return ResponseEntity.ok(messageService.getMessagesForUser(username));
    }
}