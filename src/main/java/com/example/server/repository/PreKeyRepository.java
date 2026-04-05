package com.example.server.repository;


import com.example.server.model.PreKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreKeyRepository extends JpaRepository<PreKey, Long> {
    PreKey findFirstByUsername(String username);
    long countByUsername(String username);

}