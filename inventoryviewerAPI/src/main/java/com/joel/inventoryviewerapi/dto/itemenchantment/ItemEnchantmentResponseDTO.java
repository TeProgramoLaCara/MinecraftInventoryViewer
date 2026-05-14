package com.joel.inventoryviewerapi.dto.itemenchantment;

import com.joel.inventoryviewerapi.dto.enchantment.EnchantmentResponseDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemEnchantmentResponseDTO {

    private Integer id;
    private EnchantmentResponseDTO enchantment;
    private Integer maxAllowedLevel;
}

