package com.joel.inventoryviewerapi.controller;

import com.joel.inventoryviewerapi.dto.storageitem.StorageItemRequestDTO;
import com.joel.inventoryviewerapi.dto.storageitem.StorageItemResponseDTO;
import com.joel.inventoryviewerapi.mapper.storageitem.StorageItemMapper;
import com.joel.inventoryviewerapi.service.StorageItemService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/storage-items")
public class StorageItemController {

    private final StorageItemService service;
    private final StorageItemMapper mapper;

    public StorageItemController(StorageItemService service, StorageItemMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<StorageItemResponseDTO>> getAll() {
        return ResponseEntity.ok(service.findAll().stream().map(mapper::toDTO).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StorageItemResponseDTO> getById(@PathVariable Integer id) {
        var e = service.findById(id);
        if (e == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(mapper.toDTO(e));
    }

    @GetMapping("/search")
    public ResponseEntity<List<StorageItemResponseDTO>> search(@RequestParam String query) {
        return ResponseEntity.ok(service.search(query).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<StorageItemResponseDTO> create(@Valid @RequestBody StorageItemRequestDTO dto) {
        var saved = service.save(mapper.toEntity(dto));
        return ResponseEntity.ok(mapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StorageItemResponseDTO> update(@PathVariable Integer id, @Valid @RequestBody StorageItemRequestDTO dto) {
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

