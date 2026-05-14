package com.joel.inventoryviewerapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "container_type")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ContainerType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 64)
    private String name;

    private Integer slots;

    @Column(columnDefinition = "text")
    private String description;

    private String icon;
}
