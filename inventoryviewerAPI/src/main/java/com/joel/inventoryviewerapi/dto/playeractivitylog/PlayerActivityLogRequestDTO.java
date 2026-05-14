package com.joel.inventoryviewerapi.dto.playeractivitylog;

import lombok.*;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerActivityLogRequestDTO {

    @NotNull
    private Integer playerId;

    @NotBlank
    private String action;
}

