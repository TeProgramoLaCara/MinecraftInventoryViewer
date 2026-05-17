package com.joel.inventoryviewerapi.repository;

import com.joel.inventoryviewerapi.entity.Block;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockRepository extends JpaRepository<Block, Integer> {
    boolean existsByName(String name);
    java.util.Optional<com.joel.inventoryviewerapi.entity.Block> findByName(String name);
}
