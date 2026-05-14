package com.joel.inventoryviewerapi.repository;

import com.joel.inventoryviewerapi.entity.InventoryHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryHistoryRepository extends JpaRepository<InventoryHistory, Integer> {}
