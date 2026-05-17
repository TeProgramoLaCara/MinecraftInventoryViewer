package com.joel.inventoryviewer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.core.BlockPos;
import java.util.List;

public class WandScreen extends Screen {
    private EditBox nameEdit;
    private EditBox descriptionEdit;
    private final List<BlockPos> selectedBlocks;

    public WandScreen() {
        super(new TextComponent("Gestión de Bases"));
        this.selectedBlocks = SelectionHandler.getSelectedBlocks();
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.nameEdit = new EditBox(this.font, centerX - 100, centerY - 60, 200, 20, new TextComponent("Nombre de la Base"));
        this.nameEdit.setValue("Mi Base");
        this.addRenderableWidget(this.nameEdit);

        this.descriptionEdit = new EditBox(this.font, centerX - 100, centerY - 30, 200, 20, new TextComponent("Descripción"));
        this.descriptionEdit.setValue("Descripción de la base");
        this.addRenderableWidget(this.descriptionEdit);

        this.addRenderableWidget(new Button(centerX - 100, centerY + 10, 200, 20, new TextComponent("Crear Base con Selección"), button -> {
            String name = this.nameEdit.getValue();
            String desc = this.descriptionEdit.getValue();
            String playerName = this.minecraft.player.getName().getString();
            String uuid = this.minecraft.player.getUUID().toString();

            InventoryApiClient.searchPlayer(playerName).thenAccept(players -> {
                if (players.size() == 0) {
                    InventoryApiClient.createPlayer(playerName, uuid).thenAccept(newPlayer -> {
                        int playerId = newPlayer.get("id").getAsInt();
                        createBaseWithCreator(name, desc, playerId);
                    });
                } else {
                    int playerId = players.get(0).getAsJsonObject().get("id").getAsInt();
                    createBaseWithCreator(name, desc, playerId);
                }
            });
        }));

        this.addRenderableWidget(new Button(centerX - 100, centerY + 40, 200, 20, new TextComponent("Limpiar Selección"), button -> {
            SelectionHandler.clearSelection();
            this.onClose();
        }));
    }

    @Override
    public void render(com.mojang.blaze3d.vertex.PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);
        drawCenteredString(poseStack, this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        drawString(poseStack, this.font, "Cofres seleccionados: " + selectedBlocks.size(), this.width / 2 - 100, this.height / 2 - 80, 0xAAAAAA);
        super.render(poseStack, mouseX, mouseY, partialTick);
    }

    private void createBaseWithCreator(String name, String desc, int playerId) {
        InventoryApiClient.createBase(name, desc).thenAccept(baseJson -> {
            int baseId = baseJson.get("id").getAsInt();
            
            // Add creator as owner
            InventoryApiClient.addMemberToBase(baseId, playerId, "OWNER");
            // Add default tag
            InventoryApiClient.addTagToBase(baseId, "Principal", "SYSTEM", 0xFFFFFF);

            for (BlockPos pos : selectedBlocks) {
                String biomeName = "Plains";
                if (this.minecraft.level != null) {
                    biomeName = this.minecraft.level.getBiome(pos).unwrapKey().map(key -> key.location().toString()).orElse("minecraft:plains");
                }
                InventoryApiClient.createStorage(baseId, "CHEST", pos.getX(), pos.getY(), pos.getZ(), biomeName);
            }
            SelectionHandler.clearSelection();
            this.minecraft.execute(() -> {
                this.minecraft.player.displayClientMessage(new TextComponent("§aBase '" + name + "' creada y asignada a ti."), false);
                this.onClose();
            });
        });
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
