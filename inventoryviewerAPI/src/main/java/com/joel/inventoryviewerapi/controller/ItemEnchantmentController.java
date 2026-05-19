package com.joel.inventoryviewerapi.controller;

import com.joel.inventoryviewerapi.dto.itemenchantment.ItemEnchantmentRequestDTO;
import com.joel.inventoryviewerapi.dto.itemenchantment.ItemEnchantmentResponseDTO;
import com.joel.inventoryviewerapi.mapper.itemenchantment.ItemEnchantmentMapper;
import com.joel.inventoryviewerapi.service.ItemEnchantmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/item-enchantments")
public class ItemEnchantmentController {

    private final ItemEnchantmentService service;
    private final ItemEnchantmentMapper mapper;

    public ItemEnchantmentController(ItemEnchantmentService service, ItemEnchantmentMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<ItemEnchantmentResponseDTO>> getAll() {
        return ResponseEntity.ok(service.findAll().stream().map(mapper::toDTO).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemEnchantmentResponseDTO> getById(@PathVariable Integer id) {
        var e = service.findById(id);
        if (e == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(mapper.toDTO(e));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemEnchantmentResponseDTO>> search(@RequestParam String query) {
        String q = query.toLowerCase();
        List<ItemEnchantmentResponseDTO> list = service.findAll().stream()
                .map(mapper::toDTO)
                .filter(d -> (d.getEnchantment() != null && d.getEnchantment().getName() != null && d.getEnchantment().getName().toLowerCase().contains(q)))
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<ItemEnchantmentResponseDTO> create(@Valid @RequestBody ItemEnchantmentRequestDTO dto) {
        var saved = service.save(mapper.toEntity(dto));
        return ResponseEntity.ok(mapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemEnchantmentResponseDTO> update(@PathVariable Integer id, @Valid @RequestBody ItemEnchantmentRequestDTO dto) {
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

