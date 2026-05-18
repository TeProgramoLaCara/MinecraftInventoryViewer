package com.joel.inventoryviewerapi.repository;

import com.joel.inventoryviewerapi.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Integer> {
    List<Player> findByNameContainingIgnoreCaseOrUuidContainingIgnoreCase(String name, String uuid);
    java.util.Optional<Player> findByUuid(String uuid);
}
