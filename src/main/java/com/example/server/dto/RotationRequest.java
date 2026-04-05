package com.example.server.dto;

import java.time.LocalDateTime;

public record RotationRequest(
        String username,
        String newKey,
        String oldKey,
        String transitionTimestamp
) {}