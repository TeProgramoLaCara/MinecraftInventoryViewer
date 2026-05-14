package com.joel.inventoryviewerapi.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "base_member")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class BaseMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "base_id")
    private Base base;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private Player player;

    @Column(length = 32)
    private String role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_by")
    private Player invitedBy;

    private LocalDateTime invitedAt;

    private Boolean accepted;

    private LocalDateTime acceptedAt;

    @Column(length = 128)
    private String inviteToken;

    private LocalDateTime inviteExpiresAt;

    @Column(columnDefinition = "text")
    private String permissionsSet;

    @Column(columnDefinition = "text")
    private String note;
}
