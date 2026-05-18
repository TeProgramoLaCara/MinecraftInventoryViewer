package com.joel.inventoryviewerapi.repository;

import com.joel.inventoryviewerapi.entity.Storage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorageRepository extends JpaRepository<Storage, Integer> {
    java.util.Optional<Storage> findByBaseIdAndXAndYAndZ(Integer baseId, Integer x, Integer y, Integer z);
}
