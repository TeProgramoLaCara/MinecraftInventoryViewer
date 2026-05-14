package com.joel.inventoryviewerapi.mapper.basemember;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.joel.inventoryviewerapi.entity.BaseMember;
import com.joel.inventoryviewerapi.dto.basemember.BaseMemberResponseDTO;

@Mapper(componentModel = "spring")
public interface BaseMemberMapper {

    @Mapping(target = "baseId", source = "base.id")
    @Mapping(target = "playerId", source = "player.id")
    @Mapping(target = "invitedById", source = "invitedBy.id")
    BaseMemberResponseDTO toDto(BaseMember entity);

    // Si necesitas la conversión inversa, mapea baseId/playerId manualmente en el servicio
}
