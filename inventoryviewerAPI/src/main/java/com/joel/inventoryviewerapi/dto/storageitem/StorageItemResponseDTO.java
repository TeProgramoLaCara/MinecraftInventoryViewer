package com.joel.inventoryviewerapi.dto.storageitem;

import java.time.LocalDateTime;

import com.joel.inventoryviewerapi.dto.item.ItemResponseDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StorageItemResponseDTO {

    private Integer id;
    private ItemResponseDTO item;
    private Integer quantity;
    private LocalDateTime lastUpdate;
}

