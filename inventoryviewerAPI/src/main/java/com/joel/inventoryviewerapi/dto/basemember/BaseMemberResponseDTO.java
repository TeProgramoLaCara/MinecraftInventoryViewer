package com.joel.inventoryviewerapi.dto.basemember;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseMemberResponseDTO {

    private Integer id;
    private Integer baseId;
    private String baseName;
    private List<com.joel.inventoryviewerapi.dto.basetag.BaseTagResponseDTO> baseTags;
    private Integer playerId;
    private String role;
    private Integer invitedById;
    private LocalDateTime invitedAt;
    private Boolean accepted;
    private LocalDateTime acceptedAt;
    private String inviteToken;
    private LocalDateTime inviteExpiresAt;
    private String permissionsSet;
    private String note;
}

