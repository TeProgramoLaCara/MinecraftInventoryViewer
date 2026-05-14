package com.joel.inventoryviewerapi.dto.block;

import java.time.LocalDateTime;

import com.joel.inventoryviewerapi.dto.item.ItemResponseDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlockResponseDTO {

    private Integer id;
    private String sourceId;
    private String name;
    private String displayName;
    private Double hardness;
    private Double resistance;
    private Integer minStateId;
    private Integer maxStateId;
    private Boolean diggable;
    private Boolean transparent;
    private Integer filterLight;
    private Integer emitLight;
    private String boundingBox;
    private Integer defaultState;
    private String material;
    private LocalDateTime createdAt;

    private ItemResponseDTO item;
}

