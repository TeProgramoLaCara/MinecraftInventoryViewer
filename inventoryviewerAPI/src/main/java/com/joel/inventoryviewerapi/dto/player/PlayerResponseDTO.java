package com.joel.inventoryviewerapi.dto.player;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import com.joel.inventoryviewerapi.dto.basemember.BaseMemberResponseDTO;
import com.joel.inventoryviewerapi.dto.playeractivitylog.PlayerActivityLogResponseDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerResponseDTO {

    private Integer id;
    private String uuid;
    private String name;
    private LocalDateTime createdAt;

    private List<PlayerActivityLogResponseDTO> activityLogs;
    private List<BaseMemberResponseDTO> memberships;
}

