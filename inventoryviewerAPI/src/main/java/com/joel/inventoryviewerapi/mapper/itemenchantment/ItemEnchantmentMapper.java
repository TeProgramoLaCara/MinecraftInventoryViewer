package com.joel.inventoryviewerapi.mapper.itemenchantment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.joel.inventoryviewerapi.entity.ItemEnchantment;
import com.joel.inventoryviewerapi.dto.itemenchantment.ItemEnchantmentRequestDTO;
import com.joel.inventoryviewerapi.dto.itemenchantment.ItemEnchantmentResponseDTO;

@Mapper(componentModel = "spring", uses = {
        com.joel.inventoryviewerapi.mapper.enchantment.EnchantmentMapper.class
})
public interface ItemEnchantmentMapper {

    ItemEnchantment toEntity(ItemEnchantmentRequestDTO dto);

    @Mapping(target = "enchantment", source = "enchantment")
    ItemEnchantmentResponseDTO toDTO(ItemEnchantment entity);
}
