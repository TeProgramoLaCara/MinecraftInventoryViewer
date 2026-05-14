package com.joel.inventoryviewerapi.controller;

import com.joel.inventoryviewerapi.dto.food.FoodRequestDTO;
import com.joel.inventoryviewerapi.dto.food.FoodResponseDTO;
import com.joel.inventoryviewerapi.mapper.food.FoodMapper;
import com.joel.inventoryviewerapi.service.FoodService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/food")
public class FoodController {

    private final FoodService service;
    private final FoodMapper mapper;

    public FoodController(FoodService service, FoodMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping("/all")
    public ResponseEntity<List<FoodResponseDTO>> getAll() {
        return ResponseEntity.ok(service.findAll().stream().map(mapper::toDTO).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodResponseDTO> getById(@PathVariable Integer id) {
        var e = service.findById(id);
        if (e == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(mapper.toDTO(e));
    }

    @GetMapping("/search")
    public ResponseEntity<List<FoodResponseDTO>> search(@RequestParam String query) {
        String q = query.toLowerCase();
        List<FoodResponseDTO> list = service.findAll().stream()
                .map(mapper::toDTO)
                .filter(d -> (d.getName() != null && d.getName().toLowerCase().contains(q)) ||
                        (d.getDisplayName() != null && d.getDisplayName().toLowerCase().contains(q)))
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<FoodResponseDTO> create(@Valid @RequestBody FoodRequestDTO dto) {
        var saved = service.save(mapper.toEntity(dto));
        return ResponseEntity.ok(mapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoodResponseDTO> update(@PathVariable Integer id, @Valid @RequestBody FoodRequestDTO dto) {
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

