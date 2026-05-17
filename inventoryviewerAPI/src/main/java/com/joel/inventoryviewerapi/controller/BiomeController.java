package com.joel.inventoryviewerapi.controller;

import com.joel.inventoryviewerapi.dto.biome.BiomeRequestDTO;
import com.joel.inventoryviewerapi.dto.biome.BiomeResponseDTO;
import com.joel.inventoryviewerapi.mapper.biome.BiomeMapper;
import com.joel.inventoryviewerapi.service.BiomeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/biomes")
public class BiomeController {

    private final BiomeService service;
    private final BiomeMapper mapper;

    public BiomeController(BiomeService service, BiomeMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<BiomeResponseDTO>> getAll() {
        return ResponseEntity.ok(service.findAll().stream().map(mapper::toDTO).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BiomeResponseDTO> getById(@PathVariable Integer id) {
        var e = service.findById(id);
        if (e == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(mapper.toDTO(e));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<BiomeResponseDTO> getByName(@PathVariable String name) {
        return service.findByName(name)
                .map(mapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<BiomeResponseDTO>> search(@RequestParam String query) {
        String q = query.toLowerCase();
        List<BiomeResponseDTO> list = service.findAll().stream()
                .map(mapper::toDTO)
                .filter(d -> (d.getName() != null && d.getName().toLowerCase().contains(q)) ||
                        (d.getDisplayName() != null && d.getDisplayName().toLowerCase().contains(q)))
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<BiomeResponseDTO> create(@Valid @RequestBody BiomeRequestDTO dto) {
        var saved = service.save(mapper.toEntity(dto));
        return ResponseEntity.ok(mapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BiomeResponseDTO> update(@PathVariable Integer id, @Valid @RequestBody BiomeRequestDTO dto) {
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

