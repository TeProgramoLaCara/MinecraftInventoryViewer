package com.joel.inventoryviewerapi.dto.food;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodRequestDTO {

    @NotBlank
    private String name;

    private String displayName;

    @NotNull
    private Integer foodPoints;

    private Double saturation;
    private Double effectiveQuality;
    private Double saturationRatio;
}

