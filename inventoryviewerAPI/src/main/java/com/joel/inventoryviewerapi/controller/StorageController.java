package com.joel.inventoryviewerapi.controller;

import com.joel.inventoryviewerapi.dto.storage.StorageRequestDTO;
import com.joel.inventoryviewerapi.dto.storage.StorageResponseDTO;
import com.joel.inventoryviewerapi.mapper.storage.StorageMapper;
import com.joel.inventoryviewerapi.service.StorageService;
import com.joel.inventoryviewerapi.repository.*;
import com.joel.inventoryviewerapi.entity.*;
import jakarta.validation.Valid;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/storages")
public class StorageController {

    private final StorageService service;
    private final StorageMapper mapper;
    
    private final StorageRepository storageRepository;
    private final BaseRepository baseRepository;
    private final BiomeRepository biomeRepository;
    private final ContainerTypeRepository containerTypeRepository;
    private final ItemRepository itemRepository;
    private final StorageItemRepository storageItemRepository;

    public StorageController(
            StorageService service,
            StorageMapper mapper,
            StorageRepository storageRepository,
            BaseRepository baseRepository,
            BiomeRepository biomeRepository,
            ContainerTypeRepository containerTypeRepository,
            ItemRepository itemRepository,
            StorageItemRepository storageItemRepository
    ) {
        this.service = service;
        this.mapper = mapper;
        this.storageRepository = storageRepository;
        this.baseRepository = baseRepository;
        this.biomeRepository = biomeRepository;
        this.containerTypeRepository = containerTypeRepository;
        this.itemRepository = itemRepository;
        this.storageItemRepository = storageItemRepository;
    }

    @GetMapping
    public ResponseEntity<List<StorageResponseDTO>> getAll() {
        return ResponseEntity.ok(service.findAll().stream().map(mapper::toDTO).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StorageResponseDTO> getById(@PathVariable Integer id) {
        var e = service.findById(id);
        if (e == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(mapper.toDTO(e));
    }

    @GetMapping("/base/{baseId}")
    public ResponseEntity<List<StorageResponseDTO>> getByBaseId(@PathVariable Integer baseId) {
        List<StorageResponseDTO> list = service.findAll().stream()
                .filter(s -> s.getBase() != null && s.getBase().getId().equals(baseId))
                .map(mapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/search")
    public ResponseEntity<List<StorageResponseDTO>> search(@RequestParam String query) {
        String q = query.toLowerCase();
        List<StorageResponseDTO> list = service.findAll().stream()
                .map(mapper::toDTO)
                .filter(d -> (d.getBase() != null && d.getBase().getName() != null && d.getBase().getName().toLowerCase().contains(q)) ||
                        (d.getBiome() != null && d.getBiome().getName() != null && d.getBiome().getName().toLowerCase().contains(q)))
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<StorageResponseDTO> create(@Valid @RequestBody StorageRequestDTO dto) {
        var saved = service.save(mapper.toEntity(dto));
        return ResponseEntity.ok(mapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StorageResponseDTO> update(@PathVariable Integer id, @Valid @RequestBody StorageRequestDTO dto) {
        var entity = mapper.toEntity(dto);
        entity.setId(id);
        var saved = service.save(entity);
        return ResponseEntity.ok(mapper.toDTO(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // --- Bulk Single Chest Sync Endpoint ---
    
    @PostMapping("/sync")
    @Transactional
    public ResponseEntity<?> syncStorage(@Valid @RequestBody SyncStorageRequest request) {
        // 1. Get Base
        var baseOpt = baseRepository.findById(request.baseId);
        if (baseOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Base no encontrada con ID: " + request.baseId);
        }
        var base = baseOpt.get();

        // 2. Get or Create Biome
        var biome = biomeRepository.findByName(request.biomeName).orElseGet(() -> {
            String dispName = request.biomeName;
            if (dispName.contains(":")) {
                dispName = dispName.substring(dispName.indexOf(":") + 1);
            }
            // Capitalize display name
            if (dispName.length() > 0) {
                dispName = dispName.substring(0, 1).toUpperCase() + dispName.substring(1).replace("_", " ");
            }
            var b = Biome.builder()
                    .name(request.biomeName)
                    .displayName(dispName)
                    .build();
            return biomeRepository.save(b);
        });

        // 3. Get or Create ContainerType
        var containerType = containerTypeRepository.findByName(request.typeName).orElseGet(() -> {
            var ct = ContainerType.builder()
                    .name(request.typeName)
                    .slots(27)
                    .description("Auto-created container type")
                    .build();
            return containerTypeRepository.save(ct);
        });

        // 4. Find or Create Storage
        var storage = storageRepository.findByBaseIdAndXAndYAndZ(request.baseId, request.x, request.y, request.z)
                .orElseGet(() -> {
                    var s = new Storage();
                    s.setBase(base);
                    s.setX(request.x);
                    s.setY(request.y);
                    s.setZ(request.z);
                    return s;
                });

        storage.setBiome(biome);
        storage.setContainerType(containerType);
        
        // Save the storage entity
        final Storage savedStorage = storageRepository.save(storage);

        // 5. Clear all existing items inside this storage to prevent stale items
        storageItemRepository.deleteAll(storageItemRepository.findAll().stream()
                .filter(si -> si.getStorage() != null && si.getStorage().getId().equals(savedStorage.getId()))
                .collect(java.util.stream.Collectors.toList()));
        storageItemRepository.flush();
        
        if (savedStorage.getStorageItems() != null) {
            savedStorage.getStorageItems().clear();
        }

        // 6. Bulk add the new scanned items
        if (request.items != null) {
            java.util.Map<String, SyncItem> aggregatedItems = new java.util.HashMap<>();
            for (var itemReq : request.items) {
                if (aggregatedItems.containsKey(itemReq.name)) {
                    var existing = aggregatedItems.get(itemReq.name);
                    existing.quantity += itemReq.quantity;
                } else {
                    aggregatedItems.put(itemReq.name, new SyncItem(itemReq.name, itemReq.displayName, itemReq.quantity));
                }
            }

            for (var itemReq : aggregatedItems.values()) {
                // Find or Create Item in overall catalog
                var item = itemRepository.findByName(itemReq.name).orElseGet(() -> {
                    return itemRepository.save(Item.builder().name(itemReq.name).displayName(itemReq.displayName).stackSize(64).build());
                });

                // Link to storage
                var storageItem = StorageItem.builder()
                        .storage(savedStorage)
                        .item(item)
                        .quantity(itemReq.quantity)
                        .lastUpdate(java.time.LocalDateTime.now())
                        .build();

                storageItemRepository.save(storageItem);
            }
        }

        // Re-load and return the fully populated synced storage DTO
        var finalStorage = storageRepository.findById(savedStorage.getId()).orElse(savedStorage);
        return ResponseEntity.ok(mapper.toDTO(finalStorage));
    }

    // --- Bulk Sync Request DTOs ---

    @DeleteMapping("/base/{baseId}/coords")
    @Transactional
    public ResponseEntity<?> deleteStorageByCoords(@PathVariable Integer baseId, @RequestParam Integer x, @RequestParam Integer y, @RequestParam Integer z) {
        var opt = storageRepository.findByBaseIdAndXAndYAndZ(baseId, x, y, z);
        if (opt.isPresent()) {
            storageRepository.delete(opt.get());
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class SyncStorageRequest {
        public Integer baseId;
        public Integer x;
        public Integer y;
        public Integer z;
        public String typeName;
        public String biomeName;
        public List<SyncItem> items;
    }

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class SyncItem {
        public String name;
        public String displayName;
        public Integer quantity;
    }
}
