package com.joel.inventoryviewerapi.dto.storageitem;

import lombok.*;

import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StorageItemRequestDTO {

    @NotNull
    private Integer storageId;

    @NotNull
    private Integer itemId;

    @NotNull
    private Integer quantity;
}

