package com.joel.inventoryviewerapi.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "storage", uniqueConstraints = {
        @UniqueConstraint(name = "ux_storage_coords", columnNames = {"base_id", "x", "y", "z"})
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Storage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "base_id")
    private Base base;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "biome_id")
    private Biome biome;

    private Integer x;
    private Integer y;
    private Integer z;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "container_type_id")
    private ContainerType containerType;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "storage", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<StorageItem> storageItems;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
