package com.joel.inventoryviewerapi.dto.storage;

import lombok.*;

import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StorageRequestDTO {

    @NotNull
    private Integer baseId;

    @NotNull
    private Integer biomeId;

    @NotNull
    private Integer containerTypeId;

    @NotNull
    private Integer x;

    @NotNull
    private Integer y;

    @NotNull
    private Integer z;
}

