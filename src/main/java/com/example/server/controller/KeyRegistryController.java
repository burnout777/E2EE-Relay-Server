package com.example.server.controller;

import com.example.server.dto.PublicKeyDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class KeyRegistryController {

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody PublicKeyDTO key) {
        return ResponseEntity.ok("Public key stored.");
    }
}