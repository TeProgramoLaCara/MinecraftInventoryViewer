package com.joel.inventoryviewerapi.controller;

import com.joel.inventoryviewerapi.dto.inventoryhistory.InventoryHistoryRequestDTO;
import com.joel.inventoryviewerapi.dto.inventoryhistory.InventoryHistoryResponseDTO;
import com.joel.inventoryviewerapi.mapper.inventoryhistory.InventoryHistoryMapper;
import com.joel.inventoryviewerapi.service.InventoryHistoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/inventory-histories")
public class InventoryHistoryController {

    private final InventoryHistoryService service;
    private final InventoryHistoryMapper mapper;

    public InventoryHistoryController(InventoryHistoryService service, InventoryHistoryMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<InventoryHistoryResponseDTO>> getAll() {
        return ResponseEntity.ok(service.findAll().stream().map(mapper::toDTO).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryHistoryResponseDTO> getById(@PathVariable Integer id) {
        var e = service.findById(id);
        if (e == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(mapper.toDTO(e));
    }

    @GetMapping("/search")
    public ResponseEntity<List<InventoryHistoryResponseDTO>> search(@RequestParam String query) {
        String q = query.toLowerCase();
        List<InventoryHistoryResponseDTO> list = service.findAll().stream()
                .map(mapper::toDTO)
                .filter(d -> (d.getChangeReason() != null && d.getChangeReason().toLowerCase().contains(q)) ||
                        (d.getChangeType() != null && d.getChangeType().toLowerCase().contains(q)))
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<InventoryHistoryResponseDTO> create(@Valid @RequestBody InventoryHistoryRequestDTO dto) {
        var saved = service.save(mapper.toEntity(dto));
        return ResponseEntity.ok(mapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryHistoryResponseDTO> update(@PathVariable Integer id, @Valid @RequestBody InventoryHistoryRequestDTO dto) {
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

