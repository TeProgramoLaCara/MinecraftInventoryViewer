package com.joel.inventoryviewer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class WandScreen extends Screen {
    private EditBox nameEdit;
    private int playerId = -1;
    private final List<BaseInfo> bases = new ArrayList<>();
    private int currentPage = 0;
    private boolean loaded = false;

    public WandScreen() {
        super(new TextComponent("Varita de Selección"));
    }

    @Override
    protected void init() {
        this.clearWidgets();
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        if (!loaded) {
            String playerName = this.minecraft.player.getName().getString();
            String uuid = this.minecraft.player.getUUID().toString();

            // Load player and bases asynchronously once
            InventoryApiClient.getPlayerByUuid(uuid).thenAccept(playerJson -> {
                if (playerJson == null) {
                    // Create player
                    InventoryApiClient.createPlayer(playerName, uuid).thenAccept(newPlayer -> {
                        this.playerId = newPlayer.get("id").getAsInt();
                        loadBasesList();
                    });
                } else {
                    this.playerId = playerJson.get("id").getAsInt();
                    
                    // Fetch active base details if present
                    if (playerJson.has("activeBaseId") && !playerJson.get("activeBaseId").isJsonNull()) {
                        SelectionHandler.activeBaseId = playerJson.get("activeBaseId").getAsInt();
                    }
                    
                    loadBasesList();
                }
            });
            return;
        }

        // --- Render UI Elements (when loaded) ---

        // Input Field for new base name
        this.nameEdit = new EditBox(this.font, centerX - 100, centerY - 80, 200, 16, new TextComponent("Nombre de la Base"));
        this.nameEdit.setValue("Mi Base");
        this.addRenderableWidget(this.nameEdit);

        // Button to create new base
        this.addRenderableWidget(new Button(centerX - 100, centerY - 60, 200, 18, new TextComponent("Crear y Activar Base"), button -> {
            String name = this.nameEdit.getValue().trim();
            if (name.isEmpty()) return;

            InventoryApiClient.createBase(name, "Creada desde Varita").thenAccept(baseJson -> {
                int baseId = baseJson.get("id").getAsInt();
                
                // Add player as owner
                InventoryApiClient.addMemberToBase(baseId, playerId, "OWNER").join();
                
                // Add default principal tag
                InventoryApiClient.addTagToBase(baseId, "Principal", "SYSTEM", 0xFFFFFF).join();
                
                // Log activity in database
                InventoryApiClient.logPlayerActivity(playerId, "Creó la base '" + name + "' desde la varita").join();
                
                // Set as active
                InventoryApiClient.setActiveBase(playerId, baseId).join();
                SelectionHandler.activeBaseId = baseId;
                SelectionHandler.activeBaseName = name;
                SelectionHandler.ACTIVE_BASE_STORAGES.clear();

                this.minecraft.execute(() -> {
                    this.minecraft.player.displayClientMessage(new TextComponent("§aBase '" + name + "' creada y seleccionada como activa."), false);
                    this.loaded = false; // Trigger reload of list
                    this.init();
                });
            });
        }));

        // Render bases items for the current page
        int startIdx = currentPage * 3;
        for (int i = 0; i < 3; i++) {
            int idx = startIdx + i;
            if (idx < bases.size()) {
                BaseInfo base = bases.get(idx);
                final int finalBaseId = base.id;
                final String finalBaseName = base.name;
                boolean isActive = SelectionHandler.activeBaseId != null && SelectionHandler.activeBaseId == base.id;

                Button actBtn = new Button(centerX + 30, centerY - 15 + i * 22, 70, 16, new TextComponent(isActive ? "Activa" : "Activar"), button -> {
                    setActiveBase(finalBaseId, finalBaseName);
                });
                if (isActive) {
                    actBtn.active = false;
                }
                this.addRenderableWidget(actBtn);
            }
        }

        // Paging buttons
        if (bases.size() > 3) {
            this.addRenderableWidget(new Button(centerX - 100, centerY + 65, 30, 16, new TextComponent("<-"), button -> {
                if (currentPage > 0) {
                    currentPage--;
                    this.init();
                }
            }));

            this.addRenderableWidget(new Button(centerX + 70, centerY + 65, 30, 16, new TextComponent("->"), button -> {
                if ((currentPage + 1) * 3 < bases.size()) {
                    currentPage++;
                    this.init();
                }
            }));
        }
    }

    private void loadBasesList() {
        InventoryApiClient.getPlayerByUuid(this.minecraft.player.getUUID().toString()).thenAccept(playerJson -> {
            this.bases.clear();
            if (playerJson != null && playerJson.has("memberships")) {
                JsonArray memberships = playerJson.getAsJsonArray("memberships");
                for (int i = 0; i < memberships.size(); i++) {
                    JsonObject member = memberships.get(i).getAsJsonObject();
                    if (member.has("accepted") && member.get("accepted").getAsBoolean()) {
                        int baseId = member.get("baseId").getAsInt();
                        String baseName = member.get("baseName").getAsString();
                        
                        this.bases.add(new BaseInfo(baseId, baseName));
                        
                        // Set active base name cache
                        if (SelectionHandler.activeBaseId != null && SelectionHandler.activeBaseId == baseId) {
                            SelectionHandler.activeBaseName = baseName;
                        }
                    }
                }
            }

            // Sync storages of active base once loaded if cache is empty
            if (SelectionHandler.activeBaseId != null && SelectionHandler.ACTIVE_BASE_STORAGES.isEmpty()) {
                InventoryApiClient.getStoragesByBaseId(SelectionHandler.activeBaseId).thenAccept(storages -> {
                    SelectionHandler.ACTIVE_BASE_STORAGES.clear();
                    for (int i = 0; i < storages.size(); i++) {
                        JsonObject s = storages.get(i).getAsJsonObject();
                        int sx = s.get("x").getAsInt();
                        int sy = s.get("y").getAsInt();
                        int sz = s.get("z").getAsInt();
                        SelectionHandler.ACTIVE_BASE_STORAGES.add(new BlockPos(sx, sy, sz));
                    }
                });
            }

            this.loaded = true;
            this.minecraft.execute(this::init);
        });
    }

    private void setActiveBase(int baseId, String baseName) {
        InventoryApiClient.setActiveBase(playerId, baseId).thenAccept(res -> {
            SelectionHandler.activeBaseId = baseId;
            SelectionHandler.activeBaseName = baseName;
            
            // Load and cache all chest locations for highlight outlines
            InventoryApiClient.getStoragesByBaseId(baseId).thenAccept(storages -> {
                this.minecraft.execute(() -> {
                    SelectionHandler.ACTIVE_BASE_STORAGES.clear();
                    for (int i = 0; i < storages.size(); i++) {
                        JsonObject s = storages.get(i).getAsJsonObject();
                        int sx = s.get("x").getAsInt();
                        int sy = s.get("y").getAsInt();
                        int sz = s.get("z").getAsInt();
                        SelectionHandler.ACTIVE_BASE_STORAGES.add(new BlockPos(sx, sy, sz));
                    }
                    this.minecraft.player.displayClientMessage(new TextComponent("§aBase '" + baseName + "' seleccionada como activa."), true);
                    this.init(); // Redraw UI
                });
            });
        });
    }

    @Override
    public void render(com.mojang.blaze3d.vertex.PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);

        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int panelW = 220;
        int panelH = 200;
        int x = centerX - panelW / 2;
        int y = centerY - panelH / 2;

        // Draw translucent dark background box with thin gold border
        fill(poseStack, x, y, x + panelW, y + panelH, 0xDD111111);
        fill(poseStack, x, y, x + panelW, y + 1, 0xFFFFAA00); // Top border
        fill(poseStack, x, y + panelH - 1, x + panelW, y + panelH, 0xFFFFAA00); // Bottom border
        fill(poseStack, x, y, x + 1, y + panelH, 0xFFFFAA00); // Left border
        fill(poseStack, x + panelW - 1, y, x + panelW, y + panelH, 0xFFFFAA00); // Right border

        // Header Title
        drawCenteredString(poseStack, this.font, "§6§lGESTIÓN DE VARITA", centerX, y + 8, 0xFFFFFF);

        if (!loaded) {
            drawCenteredString(poseStack, this.font, "Cargando bases...", centerX, centerY, 0xAAAAAA);
            super.render(poseStack, mouseX, mouseY, partialTick);
            return;
        }

        // "Crear Nueva Base" section label
        drawString(poseStack, this.font, "§eCrear Nueva Base:", x + 10, y + 24, 0xFFFFFF);

        // Section divider line
        int sepY = centerY - 32;
        fill(poseStack, x + 10, sepY, x + panelW - 10, sepY + 1, 0x33FFFFFF);

        // "Seleccionar Base Activa" section label
        drawString(poseStack, this.font, "§eSeleccionar Base Activa:", x + 10, sepY + 8, 0xFFFFFF);

        // Render bases items text
        int startIdx = currentPage * 3;
        for (int i = 0; i < 3; i++) {
            int idx = startIdx + i;
            int itemY = centerY - 12 + i * 22;
            if (idx < bases.size()) {
                BaseInfo base = bases.get(idx);
                boolean isActive = SelectionHandler.activeBaseId != null && SelectionHandler.activeBaseId == base.id;

                if (isActive) {
                    drawString(poseStack, this.font, "§6§l▶ " + base.name, x + 12, itemY, 0xFFFFFF);
                } else {
                    drawString(poseStack, this.font, "§7- " + base.name, x + 12, itemY, 0xFFFFFF);
                }
            }
        }

        // Render Page Number if paged
        if (bases.size() > 3) {
            int totalPages = (int) Math.ceil(bases.size() / 3.0);
            drawCenteredString(poseStack, this.font, "Pág. " + (currentPage + 1) + " / " + totalPages, centerX, centerY + 69, 0xAAAAAA);
        }

        super.render(poseStack, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.nameEdit != null && this.nameEdit.isFocused()) {
            if (keyCode == 256) { // ESC key
                this.onClose();
                return true;
            }
            return this.nameEdit.keyPressed(keyCode, scanCode, modifiers);
        }
        if (keyCode == 69) { // 'E' key
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private static class BaseInfo {
        int id;
        String name;

        BaseInfo(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
