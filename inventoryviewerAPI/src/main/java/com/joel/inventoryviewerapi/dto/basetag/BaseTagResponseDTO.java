package com.joel.inventoryviewerapi.dto.basetag;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseTagResponseDTO {

    private Integer id;
    private Integer baseId; // en vez de BaseResponseDTO base
    private String tag;
    private String scope;
    private Integer color;
}

