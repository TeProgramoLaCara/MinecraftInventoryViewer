package com.joel.inventoryviewerapi.controller;

import com.joel.inventoryviewerapi.dto.enchantment.EnchantmentRequestDTO;
import com.joel.inventoryviewerapi.dto.enchantment.EnchantmentResponseDTO;
import com.joel.inventoryviewerapi.mapper.enchantment.EnchantmentMapper;
import com.joel.inventoryviewerapi.service.EnchantmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/enchantments")
public class EnchantmentController {

    private final EnchantmentService service;
    private final EnchantmentMapper mapper;

    public EnchantmentController(EnchantmentService service, EnchantmentMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<EnchantmentResponseDTO>> getAll() {
        return ResponseEntity.ok(service.findAll().stream().map(mapper::toDTO).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnchantmentResponseDTO> getById(@PathVariable Integer id) {
        var e = service.findById(id);
        if (e == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(mapper.toDTO(e));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<EnchantmentResponseDTO> getByName(@PathVariable String name) {
        return service.findByName(name)
                .map(mapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<EnchantmentResponseDTO>> search(@RequestParam String query) {
        String q = query.toLowerCase();
        List<EnchantmentResponseDTO> list = service.findAll().stream()
                .map(mapper::toDTO)
                .filter(d -> (d.getName() != null && d.getName().toLowerCase().contains(q)) ||
                        (d.getDisplayName() != null && d.getDisplayName().toLowerCase().contains(q)))
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<EnchantmentResponseDTO> create(@Valid @RequestBody EnchantmentRequestDTO dto) {
        var saved = service.save(mapper.toEntity(dto));
        return ResponseEntity.ok(mapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnchantmentResponseDTO> update(@PathVariable Integer id, @Valid @RequestBody EnchantmentRequestDTO dto) {
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

