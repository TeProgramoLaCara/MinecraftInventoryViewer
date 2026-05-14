package com.joel.inventoryviewerapi.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "block")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Block {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "source_id", nullable = false, unique = true)
    private Integer sourceId;

    @Column(nullable = false, unique = true, length = 64)
    private String name;

    private String displayName;

    private Double hardness;

    private Double resistance;

    private Integer minStateId;

    private Integer maxStateId;

    private Boolean diggable;

    private Boolean transparent;

    private Integer filterLight;

    private Integer emitLight;

    private String boundingBox;

    private Integer defaultState;

    private String material;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private LocalDateTime createdAt;
}
