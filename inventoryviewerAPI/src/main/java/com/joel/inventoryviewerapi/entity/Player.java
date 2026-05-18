package com.joel.inventoryviewerapi.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "player")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 36)
    private String uuid;

    @Column(nullable = false, length = 64)
    private String name;

    private LocalDateTime createdAt;

    @Column(name = "active_base_id")
    private Integer activeBaseId;

    @OneToMany(mappedBy = "player", fetch = FetchType.LAZY)
    private List<PlayerActivityLog> activityLogs;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BaseMember> memberships;

    @OneToMany(mappedBy = "actor", fetch = FetchType.LAZY)
    private List<InventoryHistory> inventoryActions;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
