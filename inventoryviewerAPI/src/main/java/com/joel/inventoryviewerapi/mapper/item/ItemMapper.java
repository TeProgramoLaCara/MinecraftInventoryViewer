package com.joel.inventoryviewerapi.mapper.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.joel.inventoryviewerapi.entity.Item;
import com.joel.inventoryviewerapi.dto.item.ItemRequestDTO;
import com.joel.inventoryviewerapi.dto.item.ItemResponseDTO;

@Mapper(componentModel = "spring", uses = {
        com.joel.inventoryviewerapi.mapper.itemfood.ItemFoodMapper.class,
        com.joel.inventoryviewerapi.mapper.itemenchantment.ItemEnchantmentMapper.class
})
public interface ItemMapper {

    Item toEntity(ItemRequestDTO dto);

    @Mapping(target = "foodRelations", source = "itemFoods")
    @Mapping(target = "enchantments", source = "itemEnchantments")
    ItemResponseDTO toDTO(Item entity);
}
