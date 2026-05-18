package com.joel.inventoryviewerapi.service;

import com.joel.inventoryviewerapi.entity.Player;
import com.joel.inventoryviewerapi.repository.PlayerRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PlayerService {

    private final PlayerRepository repository;

    public PlayerService(PlayerRepository repository) {
        this.repository = repository;
    }

    public List<Player> findAll() { return repository.findAll(); }

    public Player findById(Integer id) { return repository.findById(id).orElse(null); }

    public Player save(Player entity) { return repository.save(entity); }

    public void delete(Integer id) { repository.deleteById(id); }

    public List<Player> search(String query) {
        return repository.findByNameContainingIgnoreCaseOrUuidContainingIgnoreCase(query, query);
    }

    public java.util.Optional<Player> findByUuid(String uuid) {
        return repository.findByUuid(uuid);
    }
}
