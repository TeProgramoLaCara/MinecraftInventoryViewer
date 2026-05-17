package com.joel.inventoryviewerapi.controller;

import com.joel.inventoryviewerapi.dto.block.BlockRequestDTO;
import com.joel.inventoryviewerapi.dto.block.BlockResponseDTO;
import com.joel.inventoryviewerapi.mapper.block.BlockMapper;
import com.joel.inventoryviewerapi.service.BlockService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/blocks")
public class BlockController {

    private final BlockService service;
    private final BlockMapper mapper;

    public BlockController(BlockService service, BlockMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<BlockResponseDTO>> getAll() {
        return ResponseEntity.ok(service.findAll().stream().map(mapper::toDTO).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlockResponseDTO> getById(@PathVariable Integer id) {
        var e = service.findById(id);
        if (e == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(mapper.toDTO(e));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<BlockResponseDTO> getByName(@PathVariable String name) {
        return service.findByName(name)
                .map(mapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<BlockResponseDTO>> search(@RequestParam String query) {
        String q = query.toLowerCase();
        List<BlockResponseDTO> list = service.findAll().stream()
                .map(mapper::toDTO)
                .filter(d -> (d.getName() != null && d.getName().toLowerCase().contains(q)) ||
                        (d.getDisplayName() != null && d.getDisplayName().toLowerCase().contains(q)))
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<BlockResponseDTO> create(@Valid @RequestBody BlockRequestDTO dto) {
        var saved = service.save(mapper.toEntity(dto));
        return ResponseEntity.ok(mapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BlockResponseDTO> update(@PathVariable Integer id, @Valid @RequestBody BlockRequestDTO dto) {
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

