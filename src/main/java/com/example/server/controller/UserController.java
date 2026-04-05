package com.example.server.controller;

import com.example.server.dto.KeyBundle;
import com.example.server.dto.RotationRequest;
import com.example.server.model.PreKey;
import com.example.server.model.User;
import com.example.server.service.KeyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final KeyService keyService;

    public UserController(KeyService keyService) {
        this.keyService = keyService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        if (keyService.userExists(user.getUsername())) {
            return ResponseEntity.badRequest().body("Username taken");
        }
        keyService.registerUser(user);
        return ResponseEntity.ok("User registered successfully.");
    }

    @PostMapping("/keys/upload")
    public ResponseEntity<String> uploadPreKeys(@RequestBody List<PreKey> keys) {
        keyService.uploadPreKeys(keys);
        return ResponseEntity.ok("Pre-keys uploaded.");
    }


    @GetMapping("/keys/{username}")
    public ResponseEntity<?> getKeys(@PathVariable String username, @RequestParam(required = false) Long timestamp) {
        if (!keyService.userExists(username)) {
            return ResponseEntity.status(404).body("User '" + username + "' does not exist in database.");
        }

        KeyBundle bundle = keyService.getUserKeys(username, timestamp);
        if (bundle == null) {
            return ResponseEntity.status(404).body("User exists, but no KeyBundle could be generated.");
        }

        return ResponseEntity.ok(bundle);
    }

    @GetMapping("/{username}/count")
    public ResponseEntity<Integer> getPreKeyCount(@PathVariable String username) {
        return ResponseEntity.ok(keyService.getPreKeyCount(username));
    }

    @PostMapping("/keys/rotate")
    public ResponseEntity<String> rotateKey(@RequestBody RotationRequest req) {
        keyService.rotateIdentityKey(
                req.username(),
                req.newKey(),
                req.oldKey(),
                req.transitionTimestamp()
        );

        return ResponseEntity.ok("Rotation complete and block mined.");
    }

    @GetMapping("/ledger/audit")
    public ResponseEntity<String> auditLedger() {
        return keyService.isLedgerSecure() ?
                ResponseEntity.ok("Audit: PASSED.") :
                ResponseEntity.status(409).body("Audit: FAILED.");
    }
}