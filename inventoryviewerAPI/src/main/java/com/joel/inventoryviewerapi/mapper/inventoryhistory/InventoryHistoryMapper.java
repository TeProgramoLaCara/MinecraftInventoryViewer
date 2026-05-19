package com.joel.inventoryviewerapi.mapper.inventoryhistory;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.joel.inventoryviewerapi.entity.InventoryHistory;
import com.joel.inventoryviewerapi.dto.inventoryhistory.InventoryHistoryRequestDTO;
import com.joel.inventoryviewerapi.dto.inventoryhistory.InventoryHistoryResponseDTO;

@Mapper(componentModel = "spring", uses = {
        com.joel.inventoryviewerapi.mapper.item.ItemMapper.class
})
public interface InventoryHistoryMapper {

    @Mapping(target = "storage", expression = "java(mapStorage(dto.getStorageId()))")
    @Mapping(target = "item", expression = "java(mapItem(dto.getItemId()))")
    @Mapping(target = "actor", expression = "java(mapActor(dto.getActorId()))")
    InventoryHistory toEntity(InventoryHistoryRequestDTO dto);

    @Mapping(target = "item", source = "item")
    InventoryHistoryResponseDTO toDTO(InventoryHistory entity);

    default com.joel.inventoryviewerapi.entity.Storage mapStorage(Integer storageId) {
        if (storageId == null) return null;
        com.joel.inventoryviewerapi.entity.Storage s = new com.joel.inventoryviewerapi.entity.Storage();
        s.setId(storageId);
        return s;
    }

    default com.joel.inventoryviewerapi.entity.Item mapItem(Integer itemId) {
        if (itemId == null) return null;
        com.joel.inventoryviewerapi.entity.Item i = new com.joel.inventoryviewerapi.entity.Item();
        i.setId(itemId);
        return i;
    }

    default com.joel.inventoryviewerapi.entity.Player mapActor(Integer actorId) {
        if (actorId == null) return null;
        com.joel.inventoryviewerapi.entity.Player p = new com.joel.inventoryviewerapi.entity.Player();
        p.setId(actorId);
        return p;
    }
}
