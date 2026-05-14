package com.joel.inventoryviewerapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "base_tag")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class BaseTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "base_id")
    private Base base;

    @Column(nullable = false, length = 64)
    private String tag;

    @Column(length = 32)
    private String scope;

    private Integer color;
}
