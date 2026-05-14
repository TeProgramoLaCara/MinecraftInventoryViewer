package com.joel.inventoryviewerapi.repository;

import com.joel.inventoryviewerapi.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<Food, Integer> {}
