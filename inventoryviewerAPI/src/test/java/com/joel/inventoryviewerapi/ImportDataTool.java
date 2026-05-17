package com.joel.inventoryviewerapi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joel.inventoryviewerapi.entity.*;
import com.joel.inventoryviewerapi.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@SpringBootTest
class ImportDataTool {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager entityManager;

    @Autowired private BiomeRepository biomeRepository;
    @Autowired private ItemRepository itemRepository;
    @Autowired private FoodRepository foodRepository;
    @Autowired private EnchantmentRepository enchantmentRepository;
    @Autowired private BlockRepository blockRepository;
    @Autowired private ItemFoodRepository itemFoodRepository;
    @Autowired private ItemEnchantmentRepository itemEnchantmentRepository;

    private final String JSON_PATH = "C:\\MinecraftInventoryViewer\\JSON\\";

    @Test
    @Transactional
    @Commit
    void importAllData() throws IOException {
        // Configurar Jackson para ser flexible
        objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        System.out.println("--- INICIANDO RESET E IMPORTACIÓN ---");

        clearDatabase();

        importBiomes();
        importEnchantments();
        importItems();
        importFoods();
        importBlocks();

        linkItemsWithData();

        System.out.println("--- PROCESO FINALIZADO CON ÉXITO ---");
    }

    private void linkItemsWithData() throws IOException {
        System.out.println(">> Vinculando items con comidas y encantamientos...");
        List<Item> allItems = itemRepository.findAll();
        
        for (Item item : allItems) {
            // 1. Vincular con Comida
            foodRepository.findByName(item.getName()).ifPresent(food -> {
                ItemFood itemFood = ItemFood.builder()
                        .item(item)
                        .food(food)
                        .build();
                itemFoodRepository.save(itemFood);
            });

            // 2. Vincular con Encantamientos por categorías
            if (item.getEnchantCategories() != null && !item.getEnchantCategories().equals("null")) {
                List<String> categories = objectMapper.readValue(item.getEnchantCategories(), new TypeReference<List<String>>() {});
                for (String category : categories) {
                    List<Enchantment> enchantments = enchantmentRepository.findByCategory(category);
                    for (Enchantment enc : enchantments) {
                        ItemEnchantment ie = ItemEnchantment.builder()
                                .item(item)
                                .enchantment(enc)
                                .maxAllowedLevel(enc.getMaxLevel())
                                .build();
                        itemEnchantmentRepository.save(ie);
                    }
                }
            }
        }
        System.out.println(">> Relaciones creadas.");
    }

    private void clearDatabase() {
        System.out.println(">> Limpiando tablas existentes...");
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();

        String[] tables = {
            "inventory_history", "storage_item", "storage", "base_member",
            "base_tag", "base", "player_activity_log", "player",
            "item_enchantment", "item_food", "item", "block",
            "biome", "food", "enchantment"
        };

        for (String table : tables) {
            entityManager.createNativeQuery("TRUNCATE TABLE " + table).executeUpdate();
        }

        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
        System.out.println(">> Base de datos vaciada.");
    }

    private void importBiomes() throws IOException {
        List<Biome> biomes = objectMapper.readValue(new File(JSON_PATH + "biomes.json"), new TypeReference<List<Biome>>() {});
        int count = 0;
        for (Biome b : biomes) {
            if (!biomeRepository.existsByName(b.getName())) {
                b.setId(null);
                biomeRepository.save(b);
                count++;
            }
        }
        System.out.println(">> Biomas importados: " + count);
    }

    private void importEnchantments() throws IOException {
        List<Map<String, Object>> data = objectMapper.readValue(new File(JSON_PATH + "enchantments.json"), new TypeReference<List<Map<String, Object>>>() {});
        int count = 0;
        for (Map<String, Object> map : data) {
            String name = (String) map.get("name");
            if (!enchantmentRepository.existsByName(name)) {
                Enchantment e = Enchantment.builder()
                        .name(name)
                        .displayName((String) map.get("displayName"))
                        .maxLevel((Integer) map.get("maxLevel"))
                        .treasureOnly((Boolean) map.get("treasureOnly"))
                        .curse((Boolean) map.get("curse"))
                        .category((String) map.get("category"))
                        .weight((Integer) map.get("weight"))
                        .tradeable((Boolean) map.get("tradeable"))
                        .discoverable((Boolean) map.get("discoverable"))
                        .exclude(objectMapper.writeValueAsString(map.get("exclude")))
                        .build();
                enchantmentRepository.save(e);
                count++;
            }
        }
        System.out.println(">> Encantamientos importados: " + count);
    }

    private void importItems() throws IOException {
        List<Map<String, Object>> data = objectMapper.readValue(new File(JSON_PATH + "items.json"), new TypeReference<List<Map<String, Object>>>() {});
        int count = 0;
        for (Map<String, Object> map : data) {
            String name = (String) map.get("name");
            if (!itemRepository.existsByName(name)) {
                Item item = Item.builder()
                        .name(name)
                        .displayName((String) map.get("displayName"))
                        .stackSize((Integer) map.get("stackSize"))
                        .maxDurability((Integer) map.get("maxDurability"))
                        .enchantCategories(objectMapper.writeValueAsString(map.get("enchantCategories")))
                        .build();
                itemRepository.save(item);
                count++;
            }
        }
        System.out.println(">> Items importados: " + count);
    }

    private void importFoods() throws IOException {
        List<Food> foods = objectMapper.readValue(new File(JSON_PATH + "foods.json"), new TypeReference<List<Food>>() {});
        int count = 0;
        for (Food f : foods) {
            if (!foodRepository.existsByName(f.getName())) {
                f.setId(null);
                foodRepository.save(f);
                count++;
            }
        }
        System.out.println(">> Comidas importadas: " + count);
    }

    private void importBlocks() throws IOException {
        List<Map<String, Object>> data = objectMapper.readValue(new File(JSON_PATH + "blocks.json"), new TypeReference<List<Map<String, Object>>>() {});
        int count = 0;
        for (Map<String, Object> map : data) {
            String name = (String) map.get("name");
            if (!blockRepository.existsByName(name)) {
                Block block = Block.builder()
                        .name(name)
                        .displayName((String) map.get("displayName"))
                        .hardness(map.get("hardness") != null ? Double.valueOf(map.get("hardness").toString()) : null)
                        .resistance(map.get("resistance") != null ? Double.valueOf(map.get("resistance").toString()) : null)
                        .transparent((Boolean) map.get("transparent"))
                        .filterLight((Integer) map.get("filterLight"))
                        .emitLight((Integer) map.get("emitLight"))
                        .material((String) map.get("material"))
                        .build();
                
                itemRepository.findByName(name).ifPresent(block::setItem);
                blockRepository.save(block);
                count++;
            }
        }
        System.out.println(">> Bloques importados: " + count);
    }
}
