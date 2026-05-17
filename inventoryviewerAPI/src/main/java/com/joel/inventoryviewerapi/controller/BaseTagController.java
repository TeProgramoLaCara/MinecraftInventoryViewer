package com.joel.inventoryviewerapi.controller;

import com.joel.inventoryviewerapi.dto.basetag.BaseTagRequestDTO;
import com.joel.inventoryviewerapi.dto.basetag.BaseTagResponseDTO;
import com.joel.inventoryviewerapi.mapper.basetag.BaseTagMapper;
import com.joel.inventoryviewerapi.service.BaseService;
import com.joel.inventoryviewerapi.service.BaseTagService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/base-tags")
public class BaseTagController {

    private final BaseTagService service;
    private final BaseTagMapper mapper;
    private final BaseService baseService;

    public BaseTagController(BaseTagService service, BaseTagMapper mapper, BaseService baseService) {
        this.service = service;
        this.mapper = mapper;
        this.baseService = baseService;
    }

    @PostMapping
    public ResponseEntity<BaseTagResponseDTO> create(@Valid @RequestBody BaseTagRequestDTO dto) {
        var entity = com.joel.inventoryviewerapi.entity.BaseTag.builder()
                .base(baseService.findById(dto.getBaseId()))
                .tag(dto.getTag())
                .scope(dto.getScope() != null ? dto.getScope() : "PRIVATE")
                .color(dto.getColor() != null ? dto.getColor() : 0xFFFFFF)
                .build();
        
        var saved = service.save(entity);
        return ResponseEntity.ok(mapper.toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseTagResponseDTO> update(@PathVariable Integer id, @Valid @RequestBody BaseTagRequestDTO dto) {
        var entity = service.findById(id);
        if (entity == null) return ResponseEntity.notFound().build();
        
        entity.setTag(dto.getTag());
        entity.setScope(dto.getScope());
        entity.setColor(dto.getColor());
        
        var saved = service.save(entity);
        return ResponseEntity.ok(mapper.toDto(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
