package com.joel.inventoryviewerapi.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "player_activity_log")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class PlayerActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private Player player;

    @Column(columnDefinition = "text")
    private String action;

    private LocalDateTime timestamp;
}
