package com.joel.inventoryviewerapi.controller;

import com.joel.inventoryviewerapi.dto.playeractivitylog.PlayerActivityLogRequestDTO;
import com.joel.inventoryviewerapi.dto.playeractivitylog.PlayerActivityLogResponseDTO;
import com.joel.inventoryviewerapi.mapper.playeractivitylog.PlayerActivityLogMapper;
import com.joel.inventoryviewerapi.service.PlayerActivityLogService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/player-activity-logs")
public class PlayerActivityLogController {

    private final PlayerActivityLogService service;
    private final PlayerActivityLogMapper mapper;

    public PlayerActivityLogController(PlayerActivityLogService service, PlayerActivityLogMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<PlayerActivityLogResponseDTO>> getAll() {
        return ResponseEntity.ok(service.findAll().stream().map(mapper::toDTO).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerActivityLogResponseDTO> getById(@PathVariable Integer id) {
        var e = service.findById(id);
        if (e == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(mapper.toDTO(e));
    }

    @GetMapping("/search")
    public ResponseEntity<List<PlayerActivityLogResponseDTO>> search(@RequestParam String query) {
        String q = query.toLowerCase();
        List<PlayerActivityLogResponseDTO> list = service.findAll().stream()
                .map(mapper::toDTO)
                .filter(d -> (d.getAction() != null && d.getAction().toLowerCase().contains(q)))
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<PlayerActivityLogResponseDTO> create(@Valid @RequestBody PlayerActivityLogRequestDTO dto) {
        var saved = service.save(mapper.toEntity(dto));
        return ResponseEntity.ok(mapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlayerActivityLogResponseDTO> update(@PathVariable Integer id, @Valid @RequestBody PlayerActivityLogRequestDTO dto) {
        var entity = mapper.toEntity(dto);
        entity.setId(id);
        var saved = service.save(entity);
        return ResponseEntity.ok(mapper.toDTO(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

