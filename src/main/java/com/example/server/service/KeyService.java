package com.example.server.service;

import com.example.server.chainJ.ChainJ;
import com.example.server.dto.KeyBundle;
import com.example.server.model.PreKey;
import com.example.server.model.User;
import com.example.server.repository.PreKeyRepository;
import com.example.server.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
public class KeyService {

    private final UserRepository userRepository;
    private final PreKeyRepository preKeyRepository;
    private final ChainJ chainJ;
    private final PasswordEncoder passwordEncoder;

    public KeyService(UserRepository userRepository, PreKeyRepository preKeyRepository,
                      ChainJ chainJ, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.preKeyRepository = preKeyRepository;
        this.chainJ = chainJ;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean userExists(String username) {
        return userRepository.existsById(username);
    }

    @Transactional
    public void registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        LocalDateTime now = LocalDateTime.now(java.time.ZoneOffset.UTC);
        user.setLastRotation(now);
        userRepository.save(user);

    }

    public void uploadPreKeys(List<PreKey> keys) {
        preKeyRepository.saveAll(keys);
    }

    @Transactional
    public KeyBundle getUserKeys(String username, Long timestampMillis) {
        User user = userRepository.findByUsername(username);
        if (user == null) return null;

        if (timestampMillis == null) return getCurrentBundle(username);

        LocalDateTime messageTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestampMillis), ZoneOffset.UTC);

        if (!messageTime.isBefore(user.getLastRotation())) {
            return new KeyBundle(user.getPublicKey(), null);
        }

        Optional<String> historicalKey = chainJ.getIdentityKeyAtTime(username, messageTime);

        return historicalKey
                .map(key -> new KeyBundle(key, null))
                .orElseGet(() -> {
                    System.err.println("Critical: No historical key found for timestamp " + messageTime);
                    return new KeyBundle(user.getPublicKey(), null); // Emergency fallback
                });
    }

    private KeyBundle getCurrentBundle(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) return null;

        PreKey preKeyEntity = preKeyRepository.findFirstByUsername(username);
        String preKeyString = null;

        if (preKeyEntity != null) {
            preKeyString = preKeyEntity.getPreKeyVal();
            preKeyRepository.delete(preKeyEntity);
        }

        return new KeyBundle(user.getPublicKey(), preKeyString);
    }

    @Transactional
    public void rotateIdentityKey(String username, String newKey, String oldKey, String transitionTimeMillis) {
        User user = userRepository.findByUsername(username);
        if (user == null) return;

        long millis = Long.parseLong(transitionTimeMillis);
        LocalDateTime transitionTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneOffset.UTC);

        chainJ.appendToLedger(username, oldKey, user.getLastRotation());

        user.setPublicKey(newKey);
        user.setLastRotation(transitionTime);
        userRepository.save(user);
    }

    public int getPreKeyCount(String username) {
        return (int) preKeyRepository.countByUsername(username);
    }

    public boolean isLedgerSecure() {
        return chainJ.isValidChain();
    }
}