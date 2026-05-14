package com.joel.inventoryviewerapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "biome")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Biome {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 64)
    private String name;

    private String displayName;

    private String category;

    private Double temperature;

    private String precipitation;

    private Double depth;

    private String dimension;

    private Integer color;

    private Double rainfall;
}
