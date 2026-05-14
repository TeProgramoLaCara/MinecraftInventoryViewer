package com.joel.inventoryviewerapi.mapper.enchantment;

import org.mapstruct.Mapper;

import com.joel.inventoryviewerapi.entity.Enchantment;
import com.joel.inventoryviewerapi.dto.enchantment.EnchantmentRequestDTO;
import com.joel.inventoryviewerapi.dto.enchantment.EnchantmentResponseDTO;

@Mapper(componentModel = "spring")
public interface EnchantmentMapper {

    Enchantment toEntity(EnchantmentRequestDTO dto);

    EnchantmentResponseDTO toDTO(Enchantment entity);
}
