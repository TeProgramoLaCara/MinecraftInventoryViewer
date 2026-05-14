package com.joel.inventoryviewerapi.repository;

import com.joel.inventoryviewerapi.entity.StorageItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StorageItemRepository extends JpaRepository<StorageItem, Integer> {
    List<StorageItem> findByItemNameContainingIgnoreCaseOrStorageBaseNameContainingIgnoreCase(String itemName, String baseName);
}
