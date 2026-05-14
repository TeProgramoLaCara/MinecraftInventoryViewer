package com.joel.inventoryviewerapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "item_enchantment", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"item_id", "enchantment_id"})
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ItemEnchantment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "enchantment_id")
    private Enchantment enchantment;

    private Integer maxAllowedLevel;
}
