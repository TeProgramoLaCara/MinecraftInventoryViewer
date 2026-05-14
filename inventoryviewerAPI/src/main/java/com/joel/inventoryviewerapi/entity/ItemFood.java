package com.joel.inventoryviewerapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "item_food", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"item_id", "food_id"})
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ItemFood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id")
    private Food food;
}
