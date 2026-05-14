package com.joel.inventoryviewerapi.repository;

import com.joel.inventoryviewerapi.entity.PlayerActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerActivityLogRepository extends JpaRepository<PlayerActivityLog, Integer> {}
