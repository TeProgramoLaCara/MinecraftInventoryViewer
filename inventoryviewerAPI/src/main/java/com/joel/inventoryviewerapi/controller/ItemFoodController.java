package com.joel.inventoryviewerapi.controller;

import com.joel.inventoryviewerapi.dto.itemfood.ItemFoodRequestDTO;
import com.joel.inventoryviewerapi.dto.itemfood.ItemFoodResponseDTO;
import com.joel.inventoryviewerapi.mapper.itemfood.ItemFoodMapper;
import com.joel.inventoryviewerapi.service.ItemFoodService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/item-foods")
public class ItemFoodController {

    private final ItemFoodService service;
    private final ItemFoodMapper mapper;

    public ItemFoodController(ItemFoodService service, ItemFoodMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<ItemFoodResponseDTO>> getAll() {
        return ResponseEntity.ok(service.findAll().stream().map(mapper::toDTO).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemFoodResponseDTO> getById(@PathVariable Integer id) {
        var e = service.findById(id);
        if (e == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(mapper.toDTO(e));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemFoodResponseDTO>> search(@RequestParam String query) {
        String q = query.toLowerCase();
        List<ItemFoodResponseDTO> list = service.findAll().stream()
                .map(mapper::toDTO)
                .filter(d -> (d.getFood() != null && d.getFood().getName() != null && d.getFood().getName().toLowerCase().contains(q)))
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<ItemFoodResponseDTO> create(@Valid @RequestBody ItemFoodRequestDTO dto) {
        var saved = service.save(mapper.toEntity(dto));
        return ResponseEntity.ok(mapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemFoodResponseDTO> update(@PathVariable Integer id, @Valid @RequestBody ItemFoodRequestDTO dto) {
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

