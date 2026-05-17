package com.joel.inventoryviewerapi.controller;

import com.joel.inventoryviewerapi.dto.base.BaseRequestDTO;
import com.joel.inventoryviewerapi.dto.base.BaseResponseDTO;
import com.joel.inventoryviewerapi.mapper.base.BaseMapper;
import com.joel.inventoryviewerapi.service.BaseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bases")
public class BaseController {

    private final BaseService service;
    private final BaseMapper mapper;

    public BaseController(BaseService service, BaseMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<BaseResponseDTO>> getAll() {
        List<BaseResponseDTO> list = service.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponseDTO> getById(@PathVariable Integer id) {
        var entity = service.findById(id);
        if (entity == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(mapper.toDTO(entity));
    }

    @GetMapping("/search")
    public ResponseEntity<List<BaseResponseDTO>> search(@RequestParam String query) {
        String q = query.toLowerCase();
        List<BaseResponseDTO> list = service.findAll().stream()
                .map(mapper::toDTO)
                .filter(d -> (d.getName() != null && d.getName().toLowerCase().contains(q)) ||
                        (d.getDescription() != null && d.getDescription().toLowerCase().contains(q)))
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<BaseResponseDTO> create(@Valid @RequestBody BaseRequestDTO dto) {
        var entity = mapper.toEntity(dto);
        var saved = service.save(entity);
        return ResponseEntity.ok(mapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponseDTO> update(@PathVariable Integer id, @Valid @RequestBody BaseRequestDTO dto) {
        var entity = mapper.toEntity(dto);
        // This assumes BaseService has an 'update' method that takes the ID and the entity
        var updatedEntity = service.update(id, entity);
        return ResponseEntity.ok(mapper.toDTO(updatedEntity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

