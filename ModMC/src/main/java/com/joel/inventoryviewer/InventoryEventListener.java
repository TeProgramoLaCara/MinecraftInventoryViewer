package com.joel.inventoryviewer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = InventoryViewer.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class InventoryEventListener {

    // Keep track of which block a player recently opened
    private static final Map<UUID, BlockPos> playerOpenedContainer = new HashMap<>();

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getWorld();
        if (level.isClientSide) return; // Only process on server

        BlockPos pos = event.getPos();
        BlockEntity be = level.getBlockEntity(pos);
        if (be != null && be.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent()) {
            playerOpenedContainer.put(event.getPlayer().getUUID(), pos);
        }
    }

    @SubscribeEvent
    public static void onContainerClose(PlayerContainerEvent.Close event) {
        Player player = event.getPlayer();
        if (player.level.isClientSide) return;

        BlockPos pos = playerOpenedContainer.remove(player.getUUID());
        if (pos != null) {
            // Safe check for single-player (Shared JVM). In a real dedicated server, this needs packet sync.
            if (SelectionHandler.activeBaseId != null && SelectionHandler.ACTIVE_BASE_STORAGES.contains(pos)) {
                // Fetch container contents and resync
                BlockEntity be = player.level.getBlockEntity(pos);
                if (be != null && be.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent()) {
                    be.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
                        JsonArray itemsArray = new JsonArray();
                        for (int i = 0; i < handler.getSlots(); i++) {
                            ItemStack slotStack = handler.getStackInSlot(i);
                            if (!slotStack.isEmpty()) {
                                JsonObject itemJson = new JsonObject();
                                String techName = slotStack.getItem().getRegistryName() != null ? slotStack.getItem().getRegistryName().toString() : "minecraft:air";
                                String displayName = slotStack.getHoverName().getString();
                                int qty = slotStack.getCount();
                                itemJson.addProperty("name", techName);
                                itemJson.addProperty("displayName", displayName);
                                itemJson.addProperty("quantity", qty);
                                itemsArray.add(itemJson);
                            }
                        }

                        String biomeName = player.level.getBiome(pos).unwrapKey().map(key -> key.location().toString()).orElse("minecraft:plains");
                        String typeName = be.getType().getRegistryName() != null ? be.getType().getRegistryName().getPath().toUpperCase() : "CHEST";

                        CompletableFuture.runAsync(() -> {
                            try {
                                InventoryApiClient.syncStorage(SelectionHandler.activeBaseId, pos.getX(), pos.getY(), pos.getZ(), typeName, biomeName, itemsArray)
                                    .exceptionally(ex -> {
                                        System.out.println("Error auto-syncing container: " + ex.getMessage());
                                        return null;
                                    });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    });
                }
            }
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(net.minecraftforge.event.world.BlockEvent.BreakEvent event) {
        if (event.getWorld().isClientSide()) return;
        
        BlockPos pos = event.getPos();
        if (SelectionHandler.activeBaseId != null && SelectionHandler.ACTIVE_BASE_STORAGES.contains(pos)) {
            // Unregister storage from backend
            CompletableFuture.runAsync(() -> {
                try {
                    InventoryApiClient.unregisterStorage(SelectionHandler.activeBaseId, pos.getX(), pos.getY(), pos.getZ())
                        .thenAccept(res -> {
                            SelectionHandler.ACTIVE_BASE_STORAGES.remove(pos);
                            if (event.getPlayer() != null) {
                                event.getPlayer().displayClientMessage(new TextComponent("§eContenedor desregistrado de la base automáticamente."), false);
                            }
                        })
                        .exceptionally(ex -> {
                            System.out.println("Error unregistering storage: " + ex.getMessage());
                            return null;
                        });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
