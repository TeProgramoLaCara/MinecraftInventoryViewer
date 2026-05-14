package com.joel.inventoryviewerapi.dto.playeractivitylog;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerActivityLogResponseDTO {

    private Integer id;
    private String action;
    private LocalDateTime timestamp;
}

