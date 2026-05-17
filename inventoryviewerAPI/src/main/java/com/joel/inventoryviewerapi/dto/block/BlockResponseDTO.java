package com.joel.inventoryviewerapi.dto.block;

import com.joel.inventoryviewerapi.dto.item.ItemResponseDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlockResponseDTO {

    private Integer id;
    private String name;
    private String displayName;
    private Double hardness;
    private Double resistance;
    private Boolean transparent;
    private Integer filterLight;
    private Integer emitLight;
    private String material;

    private ItemResponseDTO item;
}
