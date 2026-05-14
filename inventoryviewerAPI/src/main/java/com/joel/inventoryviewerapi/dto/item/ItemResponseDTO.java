package com.joel.inventoryviewerapi.dto.item;

import java.util.List;

import com.joel.inventoryviewerapi.dto.itemenchantment.ItemEnchantmentResponseDTO;
import com.joel.inventoryviewerapi.dto.itemfood.ItemFoodResponseDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponseDTO {

    private Integer id;
    private String name;
    private String displayName;
    private Integer stackSize;
    private Integer maxDurability;

    private String enchantCategoriesJson;

    private List<ItemFoodResponseDTO> foodRelations;
    private List<ItemEnchantmentResponseDTO> enchantments;
}

