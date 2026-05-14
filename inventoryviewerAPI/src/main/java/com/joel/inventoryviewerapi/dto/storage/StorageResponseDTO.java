package com.joel.inventoryviewerapi.dto.storage;

import java.time.LocalDateTime;
import java.util.List;

import com.joel.inventoryviewerapi.dto.base.BaseResponseDTO;
import com.joel.inventoryviewerapi.dto.biome.BiomeResponseDTO;
import com.joel.inventoryviewerapi.dto.containertype.ContainerTypeResponseDTO;
import com.joel.inventoryviewerapi.dto.storageitem.StorageItemResponseDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StorageResponseDTO {

    private Integer id;

    private BaseResponseDTO base;
    private BiomeResponseDTO biome;
    private ContainerTypeResponseDTO containerType;

    private Integer x;
    private Integer y;
    private Integer z;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<StorageItemResponseDTO> items;
}

