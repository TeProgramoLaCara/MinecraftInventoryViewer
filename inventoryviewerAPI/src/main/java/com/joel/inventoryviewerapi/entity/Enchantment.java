package com.joel.inventoryviewerapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "enchantment")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Enchantment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 128)
    private String name;

    private String displayName;

    private Integer maxLevel;

    private Boolean treasureOnly;

    private Boolean curse;

    private String category;

    private Integer weight;

    private Boolean tradeable;

    private Boolean discoverable;

    @Column(columnDefinition = "json")
    private String exclude;
}
