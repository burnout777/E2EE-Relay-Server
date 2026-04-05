package com.example.server.chainJ;

import com.example.server.repository.BlockRepository;
import com.example.server.model.Block;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * ChainJ: My Lightweight Java Blockchain Library.
 * Handles the immutable recording of retired keys.
 */

@Component
public class ChainJ {

    private final BlockRepository blockRepository;
    private static final DateTimeFormatter HASH_PRECISION = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    public ChainJ(BlockRepository blockRepository) {
        this.blockRepository = blockRepository;
    }

    /**
     * Appends a permanent record of a key activation.
     * @param username The owner
     * @param publicKey The key that became active
     * @param activationTime The exact moment this key started being used
     */
    public void appendToLedger(String username, String publicKey, LocalDateTime activationTime) {
        Block lastBlock = blockRepository.findTopByOrderByIdDesc();
        String previousHash = (lastBlock != null) ? lastBlock.getCurrentHash() : "0".repeat(64);
        long nextId = (lastBlock != null) ? lastBlock.getId() + 1 : 0;

        Block newBlock = new Block();
        newBlock.setId(nextId);
        newBlock.setPreviousHash(previousHash);
        newBlock.setUsername(username);
        newBlock.setRetiredPublicKey(publicKey); // Named 'retired' because it's in the archive
        newBlock.setValidFrom(activationTime);

        newBlock.setCurrentHash(calculateHash(newBlock));
        blockRepository.save(newBlock);

        System.out.println(">>> [ChainJ] Block #" + nextId + " mined. Key activation recorded at " + activationTime);
    }

    private String calculateHash(Block block) {
        try {
            String input = block.getId() +
                    block.getPreviousHash() +
                    block.getUsername() +
                    block.getRetiredPublicKey() +
                    block.getValidFrom().format(HASH_PRECISION);

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("ChainJ Hashing Failed", e);
        }
    }

    public boolean isValidChain() {
        List<Block> chain = blockRepository.findAll();
        if (chain.isEmpty()) return true;

        for (int i = 0; i < chain.size(); i++) {
            Block current = chain.get(i);

            if (!current.getCurrentHash().equals(calculateHash(current))) return false;

            if (i > 0) {
                Block previous = chain.get(i - 1);
                if (!current.getPreviousHash().equals(previous.getCurrentHash())) return false;
            }
        }
        return true;
    }

    /**
     * Point-in-Time lookup.
     * This is only called if KeyService determines the timestamp is older than the current key.
     */
    public Optional<String> getIdentityKeyAtTime(String username, LocalDateTime timestamp) {

        return blockRepository.findFirstByUsernameAndValidFromLessThanEqualOrderByValidFromDesc(username, timestamp)
                .map(Block::getRetiredPublicKey);
    }
}
