package com.joel.inventoryviewerapi.repository;

import com.joel.inventoryviewerapi.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findByNameContainingIgnoreCaseOrDisplayNameContainingIgnoreCase(String name, String displayName);
    boolean existsByName(String name);
    java.util.Optional<Item> findByName(String name);
}
