package com.joel.inventoryviewerapi.mapper.playeractivitylog;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.joel.inventoryviewerapi.entity.PlayerActivityLog;
import com.joel.inventoryviewerapi.dto.playeractivitylog.PlayerActivityLogRequestDTO;
import com.joel.inventoryviewerapi.dto.playeractivitylog.PlayerActivityLogResponseDTO;

@Mapper(componentModel = "spring")
public interface PlayerActivityLogMapper {

    @Mapping(target = "player", expression = "java(mapPlayer(dto.getPlayerId()))")
    PlayerActivityLog toEntity(PlayerActivityLogRequestDTO dto);

    PlayerActivityLogResponseDTO toDTO(PlayerActivityLog entity);

    default com.joel.inventoryviewerapi.entity.Player mapPlayer(Integer playerId) {
        if (playerId == null) return null;
        com.joel.inventoryviewerapi.entity.Player p = new com.joel.inventoryviewerapi.entity.Player();
        p.setId(playerId);
        return p;
    }
}
