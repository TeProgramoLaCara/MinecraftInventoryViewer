package com.joel.inventoryviewerapi.dto.inventoryhistory;

import lombok.*;

import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryHistoryRequestDTO {

    @NotNull
    private Integer storageId;

    @NotNull
    private Integer itemId;

    @NotNull
    private Integer oldQuantity;

    @NotNull
    private Integer newQuantity;

    private String changeReason;

    @NotNull
    private Integer actorId;

    @NotNull
    private String changeType; // enum en entidad
}

