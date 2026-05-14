package com.joel.inventoryviewerapi.dto.enchantment;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnchantmentResponseDTO {

    private Integer id;
    private String name;
    private String displayName;
    private Integer maxLevel;
    private Boolean treasureOnly;
    private Boolean curse;
    private String category;
    private Integer weight;
    private Boolean tradeable;
    private Boolean discoverable;

    private String excludeJson;
}

