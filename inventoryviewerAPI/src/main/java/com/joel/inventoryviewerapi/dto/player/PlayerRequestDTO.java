package com.joel.inventoryviewerapi.dto.player;

import lombok.*;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerRequestDTO {

    @NotBlank
    private String uuid;

    @NotBlank
    private String name;
}

