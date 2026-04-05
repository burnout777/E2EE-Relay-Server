package com.example.server.repository;

import com.example.server.model.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {
    Block findTopByOrderByIdDesc();

    Optional<Block> findFirstByUsernameAndValidFromLessThanEqualOrderByValidFromDesc(String username, LocalDateTime validFrom);
}