package com.joel.inventoryviewerapi.dto.food;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodResponseDTO {

    private Integer id;
    private String name;
    private String displayName;
    private Integer foodPoints;
    private Double saturation;
    private Double effectiveQuality;
    private Double saturationRatio;
}

