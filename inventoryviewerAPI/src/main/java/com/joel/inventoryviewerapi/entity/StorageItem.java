package com.joel.inventoryviewerapi.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "storage_item", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"storage_id", "item_id"})
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class StorageItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "storage_id")
    private Storage storage;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private Integer quantity;

    private LocalDateTime lastUpdate;
}
