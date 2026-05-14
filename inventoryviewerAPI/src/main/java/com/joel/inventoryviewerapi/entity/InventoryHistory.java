package com.joel.inventoryviewerapi.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_history")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class InventoryHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "storage_id")
    private Storage storage;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private Integer oldQuantity;

    private Integer newQuantity;

    @Column(columnDefinition = "text")
    private String changeReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id")
    private Player actor;

    @Column(length = 16)
    private String changeType; // enum in DB; keep as String in entity

    private LocalDateTime timestamp;
}
