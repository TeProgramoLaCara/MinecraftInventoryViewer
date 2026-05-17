package com.joel.inventoryviewerapi.controller;

import com.joel.inventoryviewerapi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/catalog")
@RequiredArgsConstructor
@CrossOrigin("*")
public class CatalogController {

    private final ItemRepository itemRepository;
    private final BlockRepository blockRepository;
    private final BiomeRepository biomeRepository;
    private final EnchantmentRepository enchantmentRepository;
    private final FoodRepository foodRepository;

    @GetMapping("/items")
    public List<?> getItems() {
        return itemRepository.findAll();
    }

    @GetMapping("/blocks")
    public List<?> getBlocks() {
        return blockRepository.findAll();
    }

    @GetMapping("/biomes")
    public List<?> getBiomes() {
        return biomeRepository.findAll();
    }

    @GetMapping("/enchantments")
    public List<?> getEnchantments() {
        return enchantmentRepository.findAll();
    }

    @GetMapping("/food")
    public List<?> getFood() {
        return foodRepository.findAll();
    }
}
