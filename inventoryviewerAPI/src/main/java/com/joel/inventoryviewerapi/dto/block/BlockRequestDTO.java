package com.joel.inventoryviewerapi.dto.block;

import lombok.*;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlockRequestDTO {

    @NotBlank
    private String name;

    private String displayName;
    private Double hardness;
    private Double resistance;
    private Boolean transparent;
    private Integer filterLight;
    private Integer emitLight;
    private String material;

    private Integer itemId; // relación opcional a item
}
