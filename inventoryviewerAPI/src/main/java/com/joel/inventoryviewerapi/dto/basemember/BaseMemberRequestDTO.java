package com.joel.inventoryviewerapi.dto.basemember;

import lombok.*;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaseMemberRequestDTO {

    @NotNull
    private Integer baseId;

    @NotNull
    private Integer playerId;

    @NotBlank
    private String role; // owner/admin/member/viewer

    private Integer invitedByPlayerId;
    private Boolean accepted;
    private String permissionsSet;
    private String note;
}

