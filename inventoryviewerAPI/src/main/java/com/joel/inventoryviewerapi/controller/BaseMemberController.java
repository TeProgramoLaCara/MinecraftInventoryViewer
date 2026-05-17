package com.joel.inventoryviewerapi.controller;

import com.joel.inventoryviewerapi.dto.basemember.BaseMemberRequestDTO;
import com.joel.inventoryviewerapi.dto.basemember.BaseMemberResponseDTO;
import com.joel.inventoryviewerapi.mapper.basemember.BaseMemberMapper;
import com.joel.inventoryviewerapi.service.BaseMemberService;
import com.joel.inventoryviewerapi.service.BaseService;
import com.joel.inventoryviewerapi.service.PlayerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/base-members")
public class BaseMemberController {

    private final BaseMemberService service;
    private final BaseMemberMapper mapper;
    private final BaseService baseService;
    private final PlayerService playerService;

    public BaseMemberController(BaseMemberService service, BaseMemberMapper mapper, BaseService baseService, PlayerService playerService) {
        this.service = service;
        this.mapper = mapper;
        this.baseService = baseService;
        this.playerService = playerService;
    }

    @PostMapping
    public ResponseEntity<BaseMemberResponseDTO> create(@Valid @RequestBody BaseMemberRequestDTO dto) {
        var entity = com.joel.inventoryviewerapi.entity.BaseMember.builder()
                .base(baseService.findById(dto.getBaseId()))
                .player(playerService.findById(dto.getPlayerId()))
                .role(dto.getRole())
                .accepted(dto.getAccepted() != null ? dto.getAccepted() : false)
                .permissionsSet(dto.getPermissionsSet())
                .note(dto.getNote())
                .build();
        
        var saved = service.save(entity);
        return ResponseEntity.ok(mapper.toDto(saved));
    }
}
