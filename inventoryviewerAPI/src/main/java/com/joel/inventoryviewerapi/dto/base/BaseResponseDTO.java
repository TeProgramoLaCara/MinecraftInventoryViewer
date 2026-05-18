package com.joel.inventoryviewerapi.dto.base;

import java.time.LocalDateTime;
import java.util.List;

import com.joel.inventoryviewerapi.dto.basemember.BaseMemberResponseDTO;
import com.joel.inventoryviewerapi.dto.basetag.BaseTagResponseDTO;
import com.joel.inventoryviewerapi.dto.storage.StorageResponseDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponseDTO {

    private Integer id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<BaseMemberResponseDTO> members;
    private List<BaseTagResponseDTO> tags;
}

