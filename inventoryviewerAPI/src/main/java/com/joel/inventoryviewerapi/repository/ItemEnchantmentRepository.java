package com.joel.inventoryviewerapi.repository;

import com.joel.inventoryviewerapi.entity.ItemEnchantment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemEnchantmentRepository extends JpaRepository<ItemEnchantment, Integer> {
}
