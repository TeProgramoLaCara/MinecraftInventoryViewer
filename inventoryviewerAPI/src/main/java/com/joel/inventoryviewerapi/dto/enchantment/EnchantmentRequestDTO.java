package com.joel.inventoryviewerapi.dto.enchantment;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnchantmentRequestDTO {

    @NotBlank
    private String name;

    private String displayName;

    @NotNull
    private Integer maxLevel;

    private Boolean treasureOnly;
    private Boolean curse;
    private String category;
    private Integer weight;
    private Boolean tradeable;
    private Boolean discoverable;

    // JSON en entidad → aquí lo dejamos como String o List<String> según luego quieras mapearlo
    private String excludeJson;
}

