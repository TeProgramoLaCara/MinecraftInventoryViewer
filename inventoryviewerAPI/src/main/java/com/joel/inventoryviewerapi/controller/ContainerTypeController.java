package com.joel.inventoryviewerapi.controller;

import com.joel.inventoryviewerapi.dto.containertype.ContainerTypeRequestDTO;
import com.joel.inventoryviewerapi.dto.containertype.ContainerTypeResponseDTO;
import com.joel.inventoryviewerapi.mapper.containertype.ContainerTypeMapper;
import com.joel.inventoryviewerapi.service.ContainerTypeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/container-types")
public class ContainerTypeController {

    private final ContainerTypeService service;
    private final ContainerTypeMapper mapper;

    public ContainerTypeController(ContainerTypeService service, ContainerTypeMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<ContainerTypeResponseDTO>> getAll() {
        return ResponseEntity.ok(service.findAll().stream().map(mapper::toDTO).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContainerTypeResponseDTO> getById(@PathVariable Integer id) {
        var e = service.findById(id);
        if (e == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(mapper.toDTO(e));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ContainerTypeResponseDTO>> search(@RequestParam String query) {
        String q = query.toLowerCase();
        List<ContainerTypeResponseDTO> list = service.findAll().stream()
                .map(mapper::toDTO)
                .filter(d -> (d.getName() != null && d.getName().toLowerCase().contains(q)) ||
                        (d.getDescription() != null && d.getDescription().toLowerCase().contains(q)))
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<ContainerTypeResponseDTO> create(@Valid @RequestBody ContainerTypeRequestDTO dto) {
        var saved = service.save(mapper.toEntity(dto));
        return ResponseEntity.ok(mapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContainerTypeResponseDTO> update(@PathVariable Integer id, @Valid @RequestBody ContainerTypeRequestDTO dto) {
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

