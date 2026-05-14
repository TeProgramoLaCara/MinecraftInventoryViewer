package com.joel.inventoryviewerapi.dto.containertype;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContainerTypeRequestDTO {

    @NotBlank
    private String name;

    @NotNull
    private Integer slots;

    private String description;
    private String icon;
}

