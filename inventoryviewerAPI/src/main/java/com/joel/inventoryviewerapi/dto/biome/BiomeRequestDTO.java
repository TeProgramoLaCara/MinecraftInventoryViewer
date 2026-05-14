package com.joel.inventoryviewerapi.dto.biome;

import lombok.*;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BiomeRequestDTO {

    @NotBlank
    private String name;

    private String displayName;
    private String category;
    private Double temperature;
    private String precipitation;
    private Double depth;
    private String dimension;
    private String color;
    private Double rainfall;
}

