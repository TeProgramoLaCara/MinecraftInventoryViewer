package com.joel.inventoryviewer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WandItem extends Item {

    public WandItem() {
        super(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        if (player == null) return InteractionResult.PASS;

        // Shift + Click Derecho en un bloque -> registrar contenedor
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be != null && be.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent()) {
                    be.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
                        JsonArray itemsArray = new JsonArray();
                        for (int i = 0; i < handler.getSlots(); i++) {
                            ItemStack slotStack = handler.getStackInSlot(i);
                            if (!slotStack.isEmpty()) {
                                JsonObject itemJson = new JsonObject();
                                
                                String techName = slotStack.getItem().getRegistryName() != null 
                                        ? slotStack.getItem().getRegistryName().toString() 
                                        : "minecraft:air";
                                
                                String displayName = slotStack.getHoverName().getString();
                                int qty = slotStack.getCount();

                                itemJson.addProperty("name", techName);
                                itemJson.addProperty("displayName", displayName);
                                itemJson.addProperty("quantity", qty);
                                itemsArray.add(itemJson);
                            }
                        }

                        String biomeName = level.getBiome(pos).unwrapKey()
                                .map(key -> key.location().toString())
                                .orElse("minecraft:plains");

                        String typeName = be.getType().getRegistryName() != null 
                                ? be.getType().getRegistryName().getPath().toUpperCase() 
                                : "CHEST";

                        String uuid = player.getUUID().toString();

                        CompletableFuture.runAsync(() -> {
                            try {
                                JsonObject playerJson = InventoryApiClient.getPlayerByUuid(uuid).join();
                                if (playerJson == null || !playerJson.has("activeBaseId") || playerJson.get("activeBaseId").isJsonNull()) {
                                    player.displayClientMessage(new TextComponent("§cNo tienes ninguna base seleccionada como activa. Presiona Click Derecho para seleccionar una."), false);
                                    return;
                                }
                                int activeBaseId = playerJson.get("activeBaseId").getAsInt();

                                if (SelectionHandler.activeBaseId != null && SelectionHandler.activeBaseId == activeBaseId && SelectionHandler.ACTIVE_BASE_STORAGES.contains(pos)) {
                                    // Already registered, toggle off (unregister)
                                    InventoryApiClient.unregisterStorage(activeBaseId, pos.getX(), pos.getY(), pos.getZ())
                                        .thenAccept(res -> {
                                            SelectionHandler.ACTIVE_BASE_STORAGES.remove(pos);
                                            player.displayClientMessage(new TextComponent("§e¡Contenedor " + typeName + " desregistrado de la base activa!"), false);
                                        }).exceptionally(ex -> {
                                            player.displayClientMessage(new TextComponent("§cError al desregistrar cofre: " + ex.getMessage()), false);
                                            return null;
                                        });
                                } else {
                                    // Not registered, register and sync
                                    InventoryApiClient.syncStorage(activeBaseId, pos.getX(), pos.getY(), pos.getZ(), typeName, biomeName, itemsArray)
                                        .thenAccept(res -> {
                                            player.displayClientMessage(new TextComponent("§a¡Contenedor " + typeName + " registrado y sincronizado en la base activa con " + itemsArray.size() + " tipos de ítems!"), false);
                                            
                                            if (SelectionHandler.activeBaseId != null && SelectionHandler.activeBaseId == activeBaseId) {
                                                if (!SelectionHandler.ACTIVE_BASE_STORAGES.contains(pos)) {
                                                    SelectionHandler.ACTIVE_BASE_STORAGES.add(pos);
                                                }
                                            }
                                        }).exceptionally(ex -> {
                                            player.displayClientMessage(new TextComponent("§cError al registrar cofre en la base: " + ex.getMessage()), false);
                                            return null;
                                        });
                                }
                            } catch (Exception ex) {
                                player.displayClientMessage(new TextComponent("§cError de red o base de datos al sincronizar."), false);
                            }
                        });
                    });
                    return InteractionResult.SUCCESS;
                }
            } else {
                BlockEntity be = level.getBlockEntity(pos);
                if (be != null && be.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent()) {
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        } else {
            // Click Derecho Normal abre la pantalla de selección de base activa
            if (level.isClientSide) {
                net.minecraft.client.Minecraft.getInstance().setScreen(new WandScreen());
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        // Click Derecho Normal en el aire abre la pantalla de selección
        if (!player.isShiftKeyDown()) {
            if (level.isClientSide) {
                net.minecraft.client.Minecraft.getInstance().setScreen(new WandScreen());
            }
            return InteractionResultHolder.success(player.getItemInHand(hand));
        }
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (SelectionHandler.activeBaseName != null) {
            tooltip.add(new TextComponent("§7Base Activa: §6§l" + SelectionHandler.activeBaseName));
        } else {
            tooltip.add(new TextComponent("§7Base Activa: §c§lNinguna"));
        }
        tooltip.add(new TextComponent(""));
        tooltip.add(new TextComponent("§8- Click Derecho para seleccionar base."));
        tooltip.add(new TextComponent("§8- Shift + Click Derecho sobre cofre para registrarlo."));
    }
}
