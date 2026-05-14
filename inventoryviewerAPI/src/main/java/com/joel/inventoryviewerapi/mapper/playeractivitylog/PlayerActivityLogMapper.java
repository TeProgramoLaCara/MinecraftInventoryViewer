package com.joel.inventoryviewerapi.mapper.playeractivitylog;

import org.mapstruct.Mapper;

import com.joel.inventoryviewerapi.entity.PlayerActivityLog;
import com.joel.inventoryviewerapi.dto.playeractivitylog.PlayerActivityLogRequestDTO;
import com.joel.inventoryviewerapi.dto.playeractivitylog.PlayerActivityLogResponseDTO;

@Mapper(componentModel = "spring")
public interface PlayerActivityLogMapper {

    PlayerActivityLog toEntity(PlayerActivityLogRequestDTO dto);

    PlayerActivityLogResponseDTO toDTO(PlayerActivityLog entity);
}
