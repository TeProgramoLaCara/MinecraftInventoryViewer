package com.joel.inventoryviewerapi.repository;

import com.joel.inventoryviewerapi.entity.Biome;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BiomeRepository extends JpaRepository<Biome, Integer> {
    boolean existsByName(String name);
    java.util.Optional<Biome> findByName(String name);
}
