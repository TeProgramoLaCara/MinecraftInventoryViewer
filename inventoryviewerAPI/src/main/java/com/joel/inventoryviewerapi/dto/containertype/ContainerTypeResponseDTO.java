package com.joel.inventoryviewerapi.dto.containertype;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContainerTypeResponseDTO {

    private Integer id;
    private String name;
    private Integer slots;
    private String description;
    private String icon;
}

