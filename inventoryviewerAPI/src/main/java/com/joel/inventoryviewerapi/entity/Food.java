package com.joel.inventoryviewerapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "food")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 128)
    private String name;

    private String displayName;

    private Integer foodPoints;

    private Double saturation;

    private Double effectiveQuality;

    private Double saturationRatio;
}
