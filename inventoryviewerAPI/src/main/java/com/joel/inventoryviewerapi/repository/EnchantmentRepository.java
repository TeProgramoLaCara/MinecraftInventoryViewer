package com.joel.inventoryviewerapi.repository;

import com.joel.inventoryviewerapi.entity.Enchantment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnchantmentRepository extends JpaRepository<Enchantment, Integer> {
    boolean existsByName(String name);
    java.util.List<Enchantment> findByCategory(String category);
    java.util.Optional<Enchantment> findByName(String name);
}
