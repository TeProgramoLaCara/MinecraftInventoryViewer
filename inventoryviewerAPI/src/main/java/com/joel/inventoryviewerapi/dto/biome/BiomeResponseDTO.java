package com.joel.inventoryviewerapi.dto.biome;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BiomeResponseDTO {

    private Integer id;
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

