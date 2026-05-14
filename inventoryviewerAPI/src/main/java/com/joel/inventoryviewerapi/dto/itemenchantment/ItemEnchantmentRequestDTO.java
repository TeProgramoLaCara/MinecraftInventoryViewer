package com.joel.inventoryviewerapi.dto.itemenchantment;

import lombok.*;

import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemEnchantmentRequestDTO {

    @NotNull
    private Integer itemId;

    @NotNull
    private Integer enchantmentId;

    @NotNull
    private Integer maxAllowedLevel;
}

