package com.joel.inventoryviewerapi.dto.basetag;

import lombok.*;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaseTagRequestDTO {

    @NotNull
    private Integer baseId;

    @NotBlank
    private String tag;

    private String scope;
    private String color;
}

