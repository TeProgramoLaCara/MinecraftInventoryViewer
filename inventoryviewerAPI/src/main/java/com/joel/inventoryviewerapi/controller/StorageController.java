package com.joel.inventoryviewerapi.controller;

import com.joel.inventoryviewerapi.dto.storage.StorageRequestDTO;
import com.joel.inventoryviewerapi.dto.storage.StorageResponseDTO;
import com.joel.inventoryviewerapi.mapper.storage.StorageMapper;
import com.joel.inventoryviewerapi.service.StorageService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/storages")
public class StorageController {

    private final StorageService service;
    private final StorageMapper mapper;

    public StorageController(StorageService service, StorageMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<StorageResponseDTO>> getAll() {
        return ResponseEntity.ok(service.findAll().stream().map(mapper::toDTO).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StorageResponseDTO> getById(@PathVariable Integer id) {
        var e = service.findById(id);
        if (e == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(mapper.toDTO(e));
    }

    @GetMapping("/search")
    public ResponseEntity<List<StorageResponseDTO>> search(@RequestParam String query) {
        String q = query.toLowerCase();
        List<StorageResponseDTO> list = service.findAll().stream()
                .map(mapper::toDTO)
                .filter(d -> (d.getBase() != null && d.getBase().getName() != null && d.getBase().getName().toLowerCase().contains(q)) ||
                        (d.getBiome() != null && d.getBiome().getName() != null && d.getBiome().getName().toLowerCase().contains(q)))
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<StorageResponseDTO> create(@Valid @RequestBody StorageRequestDTO dto) {
        var saved = service.save(mapper.toEntity(dto));
        return ResponseEntity.ok(mapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StorageResponseDTO> update(@PathVariable Integer id, @Valid @RequestBody StorageRequestDTO dto) {
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

