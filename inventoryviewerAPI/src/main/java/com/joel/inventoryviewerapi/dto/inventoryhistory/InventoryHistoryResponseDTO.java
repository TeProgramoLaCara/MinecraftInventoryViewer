package com.joel.inventoryviewerapi.dto.inventoryhistory;

import java.time.LocalDateTime;

import com.joel.inventoryviewerapi.dto.item.ItemResponseDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryHistoryResponseDTO {

    private Integer id;
    private ItemResponseDTO item;
    private Integer oldQuantity;
    private Integer newQuantity;
    private String changeReason;
    private String changeType;
    private LocalDateTime timestamp;
}

