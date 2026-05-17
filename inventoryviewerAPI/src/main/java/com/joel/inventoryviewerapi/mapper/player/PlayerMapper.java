package com.joel.inventoryviewerapi.mapper.player;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.joel.inventoryviewerapi.entity.Player;
import com.joel.inventoryviewerapi.dto.player.PlayerRequestDTO;
import com.joel.inventoryviewerapi.dto.player.PlayerResponseDTO;

@Mapper(componentModel = "spring", uses = {
        com.joel.inventoryviewerapi.mapper.playeractivitylog.PlayerActivityLogMapper.class,
        com.joel.inventoryviewerapi.mapper.basemember.BaseMemberMapper.class
})
public interface PlayerMapper {

    Player toEntity(PlayerRequestDTO dto);

    @Mapping(target = "activityLogs", source = "activityLogs")
    @Mapping(target = "memberships", source = "memberships")
    PlayerResponseDTO toDTO(Player entity);
}
