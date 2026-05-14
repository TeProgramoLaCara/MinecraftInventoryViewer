package com.joel.inventoryviewerapi.mapper.base;

import org.mapstruct.Mapper;
import java.util.List;

import com.joel.inventoryviewerapi.entity.Base;
import com.joel.inventoryviewerapi.dto.base.BaseRequestDTO;
import com.joel.inventoryviewerapi.dto.base.BaseResponseDTO;

@Mapper(componentModel = "spring", uses = {
        com.joel.inventoryviewerapi.mapper.basemember.BaseMemberMapper.class,
        com.joel.inventoryviewerapi.mapper.basetag.BaseTagMapper.class
})
public interface BaseMapper {

    Base toEntity(BaseRequestDTO dto);

    BaseResponseDTO toDTO(Base entity);

    List<BaseResponseDTO> toDTOList(List<Base> entities);
}
