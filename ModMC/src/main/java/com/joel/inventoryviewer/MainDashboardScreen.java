package com.joel.inventoryviewer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

public class MainDashboardScreen extends Screen {
    private enum Tab { BASES, CATALOG, PLAYER }
    private Tab currentTab = Tab.BASES;
    private JsonArray data = new JsonArray();
    private Integer expandedBaseId = null;
    
    // Tag management state
    private EditBox tagEdit;
    private EditBox searchBox;
    private Button saveTagBtn;
    private Button basesBtn;
    private Button catalogBtn;
    private Button playerBtn;
    private Integer activeBaseForTag = null;
    private Integer editingTagId = null;
    private Integer tagIdToDelete = null;
    
    private int currentColorIdx = 0;
    private final int[] colors = {0xFFFFFF, 0xFF5555, 0x55FF55, 0x5555FF, 0xFFAA00, 0xFF55FF};
    private final String[] colorNames = {"Blanco", "Rojo", "Verde", "Azul", "Oro", "Rosa"};

    // Catalog state
    private String catalogCategory = "items";
    private final String[] categories = {"items", "blocks", "biomes", "enchantments", "food"};
    private final String[] categoryNames = {"Items", "Bloques", "Biomas", "Encantamientos", "Comida"};
    private JsonObject selectedCatalogItem = null;
    private int catalogPage = 0;
    private final int ITEMS_PER_PAGE = 50;

    private final int PANEL_X = 15;
    private final int PANEL_Y = 40;
    private double scrollAmount = 0;
    private int maxScroll = 0;

    // Bases management state
    private EditBox baseNameEdit;
    private Button baseSaveBtn;
    private Integer editingBaseId = null; // null = create new base
    private int baseItemPage = 0;

    public MainDashboardScreen() {
        super(new TextComponent("Dashboard de Inventario"));
        refreshData();
    }

    private void refreshData() {
        if (currentTab == Tab.BASES) {
            InventoryApiClient.getBases().thenAccept(json -> {
                Minecraft.getInstance().execute(() -> {
                    this.data = json;
                });
            });
        } else if (currentTab == Tab.PLAYER) {
            String playerName = Minecraft.getInstance().getUser().getName();
            InventoryApiClient.searchPlayer(playerName).thenAccept(jsonArray -> {
                Minecraft.getInstance().execute(() -> {
                    if (jsonArray.size() == 0) {
                        String uuid = Minecraft.getInstance().getUser().getUuid();
                        InventoryApiClient.createPlayer(playerName, uuid).thenAccept(newPlayer -> {
                            Minecraft.getInstance().execute(() -> {
                                com.google.gson.JsonArray arr = new com.google.gson.JsonArray();
                                arr.add(newPlayer);
                                this.data = arr;
                            });
                        });
                    } else {
                        this.data = jsonArray;
                    }
                });
            });
        } else if (currentTab == Tab.CATALOG) {
            InventoryApiClient.getCatalog(catalogCategory).thenAccept(json -> {
                Minecraft.getInstance().execute(() -> {
                    this.data = json;
                    this.selectedCatalogItem = null;
                    this.scrollAmount = 0;
                    this.catalogPage = 0;
                });
            });
        }
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        this.basesBtn = new Button(centerX - 155, 10, 100, 20, new TextComponent("Bases"), b -> switchTab(Tab.BASES));
        this.catalogBtn = new Button(centerX - 50, 10, 100, 20, new TextComponent("Catálogo"), b -> switchTab(Tab.CATALOG));
        this.playerBtn = new Button(centerX + 55, 10, 100, 20, new TextComponent("Jugador"), b -> switchTab(Tab.PLAYER));
        addRenderableWidget(this.basesBtn);
        addRenderableWidget(this.catalogBtn);
        addRenderableWidget(this.playerBtn);
        updateTabButtonLabels();

        // Tag Editor
        this.tagEdit = new EditBox(this.font, 0, 0, 80, 14, new TextComponent("Tag"));
        this.tagEdit.visible = false;
        addRenderableWidget(this.tagEdit);

        this.saveTagBtn = new Button(0, 0, 60, 14, new TextComponent("Guardar"), b -> {
            if (activeBaseForTag != null && !tagEdit.getValue().isEmpty()) {
                if (editingTagId == null) {
                    InventoryApiClient.addTagToBase(activeBaseForTag, tagEdit.getValue(), "PRIVATE", colors[currentColorIdx])
                        .thenAccept(res -> Minecraft.getInstance().execute(() -> { refreshData(); resetTagEditor(); }));
                } else {
                    InventoryApiClient.updateTag(editingTagId, activeBaseForTag, tagEdit.getValue(), "PRIVATE", colors[currentColorIdx])
                        .thenAccept(res -> Minecraft.getInstance().execute(() -> { refreshData(); resetTagEditor(); }));
                }
            }
        });
        this.saveTagBtn.visible = false;
        addRenderableWidget(this.saveTagBtn);

        // Search Box for Catalog
        this.searchBox = new EditBox(this.font, PANEL_X + 10, PANEL_Y + 23, 150, 14, new TextComponent("Buscar..."));
        this.searchBox.visible = (currentTab == Tab.CATALOG);
        this.searchBox.setResponder(s -> { this.catalogPage = 0; this.scrollAmount = 0; });
        addRenderableWidget(this.searchBox);

        // Bases Edit Box
        this.baseNameEdit = new EditBox(this.font, PANEL_X + 10, PANEL_Y + 15, 120, 14, new TextComponent("Nombre Base"));
        this.baseNameEdit.visible = (currentTab == Tab.BASES);
        addRenderableWidget(this.baseNameEdit);

        this.baseSaveBtn = new Button(PANEL_X + 135, PANEL_Y + 12, 50, 20, new TextComponent("Guardar"), b -> {
            String name = baseNameEdit.getValue().trim();
            if (!name.isEmpty()) {
                if (editingBaseId == null) {
                    InventoryApiClient.createBase(name, "Creada desde Dashboard").thenAccept(baseJson -> {
                        int baseId = baseJson.get("id").getAsInt();
                        String uuid = Minecraft.getInstance().player.getUUID().toString();
                        InventoryApiClient.getPlayerByUuid(uuid).thenAccept(p -> {
                            if (p != null) {
                                int pId = p.get("id").getAsInt();
                                InventoryApiClient.addMemberToBase(baseId, pId, "OWNER").join();
                                InventoryApiClient.addTagToBase(baseId, "Principal", "SYSTEM", 0xFFFFFF).join();
                            }
                            Minecraft.getInstance().execute(this::refreshData);
                        });
                    });
                } else {
                    InventoryApiClient.updateBase(editingBaseId, name, "").thenAccept(res -> {
                        Minecraft.getInstance().execute(this::refreshData);
                    });
                    editingBaseId = null;
                }
                baseNameEdit.setValue("");
                baseSaveBtn.setMessage(new TextComponent("Guardar"));
            }
        });
        this.baseSaveBtn.visible = (currentTab == Tab.BASES);
        addRenderableWidget(this.baseSaveBtn);
    }

    private void updateTabButtonLabels() {
        if (basesBtn != null) {
            basesBtn.setMessage(new TextComponent(currentTab == Tab.BASES ? "§6§l[ BASES ]" : "§eBases"));
        }
        if (catalogBtn != null) {
            catalogBtn.setMessage(new TextComponent(currentTab == Tab.CATALOG ? "§6§l[ CATÁLOGO ]" : "§eCatálogo"));
        }
        if (playerBtn != null) {
            playerBtn.setMessage(new TextComponent(currentTab == Tab.PLAYER ? "§6§l[ JUGADOR ]" : "§eJugador"));
        }
    }

    private void switchTab(Tab tab) {
        this.currentTab = tab;
        this.data = new com.google.gson.JsonArray();
        this.expandedBaseId = null;
        this.scrollAmount = 0;
        this.selectedCatalogItem = null;
        this.catalogPage = 0;
        if (this.searchBox != null) {
            this.searchBox.visible = (tab == Tab.CATALOG);
            this.searchBox.setValue("");
        }
        if (this.baseNameEdit != null) {
            this.baseNameEdit.visible = (tab == Tab.BASES);
            this.baseNameEdit.setValue("");
        }
        if (this.baseSaveBtn != null) {
            this.baseSaveBtn.visible = (tab == Tab.BASES);
            this.baseSaveBtn.setMessage(new TextComponent("Guardar"));
        }
        editingBaseId = null;
        baseItemPage = 0;
        updateTabButtonLabels();
        resetTagEditor();
        refreshData();
    }

    private void resetTagEditor() {
        this.activeBaseForTag = null;
        this.editingTagId = null;
        this.tagIdToDelete = null;
        if (this.tagEdit != null) { this.tagEdit.setValue(""); this.tagEdit.visible = false; }
        if (this.saveTagBtn != null) this.saveTagBtn.visible = false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        this.scrollAmount = Mth.clamp(this.scrollAmount - delta * 15, 0, Math.max(0, maxScroll));
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.searchBox != null && this.searchBox.isFocused()) {
            if (keyCode == 256) { // ESC key
                this.onClose();
                return true;
            }
            return this.searchBox.keyPressed(keyCode, scanCode, modifiers);
        }
        if (this.baseNameEdit != null && this.baseNameEdit.isFocused()) {
            if (keyCode == 256) { // ESC key
                this.onClose();
                return true;
            }
            return this.baseNameEdit.keyPressed(keyCode, scanCode, modifiers);
        }
        if (this.tagEdit != null && this.tagEdit.isFocused()) {
            if (keyCode == 256) { // ESC key
                this.onClose();
                return true;
            }
            return this.tagEdit.keyPressed(keyCode, scanCode, modifiers);
        }
        // Cerrar la GUI si no se está escribiendo y se presiona la tecla 'B'
        if (keyCode == 66) { // 'B' key (GLFW_KEY_B)
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);
        int pW = width - 30, pH = height - 50;
        
        // Fondo translúcido premium
        fill(poseStack, PANEL_X, PANEL_Y, PANEL_X + pW, PANEL_Y + pH, 0x66000000);
        fill(poseStack, PANEL_X, PANEL_Y, PANEL_X + pW, PANEL_Y + 1, 0xFF888888);
        fill(poseStack, PANEL_X, PANEL_Y + pH - 1, PANEL_X + pW, PANEL_Y + pH, 0xFF888888);

        if (maxScroll > 0) {
            int scrollBarH = (int) ((pH / (double) (pH + maxScroll)) * pH);
            int scrollBarY = PANEL_Y + (int) ((scrollAmount / maxScroll) * (pH - scrollBarH));
            fill(poseStack, PANEL_X + pW - 4, scrollBarY, PANEL_X + pW - 2, scrollBarY + scrollBarH, 0xAA888888);
        }

        double scale = Minecraft.getInstance().getWindow().getGuiScale();
        int scissorY = PANEL_Y + 2;
        int scissorHeight = pH - 4;
        
        // Si estamos en catálogo, el listado empieza debajo del buscador (y = PANEL_Y + 42)
        if (currentTab == Tab.CATALOG) {
            scissorY = PANEL_Y + 42;
            scissorHeight = pH - 44;
        }
        
        RenderSystem.enableScissor(
            (int) (PANEL_X * scale), 
            (int) (Minecraft.getInstance().getWindow().getGuiScaledHeight() - (scissorY + scissorHeight)) * (int) scale, 
            (int) (pW * scale), 
            (int) scissorHeight * (int) scale
        );

        int drawY = (int) (PANEL_Y + 10 - scrollAmount);
        if (currentTab == Tab.CATALOG) {
            drawY = (int) (PANEL_Y + 42 - scrollAmount);
        }

        switch (currentTab) {
            case BASES -> renderBases(poseStack, PANEL_X + 10, drawY, mouseX, mouseY);
            case CATALOG -> renderCatalog(poseStack, PANEL_X + 10, drawY, mouseX, mouseY);
            case PLAYER -> renderPlayer(poseStack, PANEL_X + 10, drawY, mouseX, mouseY);
        }
        
        RenderSystem.disableScissor();

        // Renderizar los botones de categoría fijos arriba en el catálogo (fuera de la tijera de scroll)
        if (currentTab == Tab.CATALOG) {
            renderCatalogTabs(poseStack);
        }

        super.render(poseStack, mouseX, mouseY, partialTick);
    }

    private void renderCatalogTabs(PoseStack poseStack) {
        int catX = PANEL_X + 10;
        int catY = PANEL_Y + 5;
        
        for (int i = 0; i < categories.length; i++) {
            int tw = font.width(categoryNames[i]) + 10;
            fill(poseStack, catX, catY, catX + tw, catY + 14, catalogCategory.equals(categories[i]) ? 0xFFFFAA00 : 0x44FFFFFF);
            if (catalogCategory.equals(categories[i])) {
                // Dibujar sin sombra para evitar el efecto repetido/borroso en texto negro
                font.draw(poseStack, categoryNames[i], catX + 5, catY + 3, 0x000000);
            } else {
                drawString(poseStack, font, categoryNames[i], catX + 5, catY + 3, 0xFFFFFF);
            }
            catX += tw + 5;
        }

        // Paginación calculada en base al filtro del buscador
        java.util.List<JsonObject> filtered = new java.util.ArrayList<>();
        String query = searchBox != null ? searchBox.getValue().trim().toLowerCase() : "";
        for (JsonElement el : data) {
            JsonObject item = el.getAsJsonObject();
            String name = item.has("name") && !item.get("name").isJsonNull() ? item.get("name").getAsString() : "";
            String dName = item.has("displayName") && !item.get("displayName").isJsonNull() ? item.get("displayName").getAsString() : "";
            if (query.isEmpty() || name.toLowerCase().contains(query) || dName.toLowerCase().contains(query)) {
                filtered.add(item);
            }
        }

        int totalItems = filtered.size();
        int totalPages = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);
        if (totalPages == 0) totalPages = 1;
        
        int pgX = width - 100;
        int pgY = PANEL_Y + 5;
        
        fill(poseStack, pgX, pgY, pgX + 15, pgY + 14, 0x44FFFFFF);
        drawString(poseStack, font, "<", pgX + 5, pgY + 3, 0xFFFFFF);
        
        String pgStr = (catalogPage + 1) + "/" + totalPages;
        drawCenteredString(poseStack, font, pgStr, pgX + 35, pgY + 3, 0xFFFFFF);
        
        fill(poseStack, pgX + 55, pgY, pgX + 70, pgY + 14, 0x44FFFFFF);
        drawString(poseStack, font, ">", pgX + 60, pgY + 3, 0xFFFFFF);
    }

    private void renderBases(PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        y = PANEL_Y + 40 - (int)scrollAmount;
        int startY = y;
        
        int leftW = 160;
        int rightX = PANEL_X + leftW + 10;
        
        for (JsonElement e : data) {
            JsonObject base = e.getAsJsonObject();
            int id = base.get("id").getAsInt();
            String name = base.get("name").getAsString();
            boolean isSelected = (expandedBaseId != null && expandedBaseId == id);
            
            fill(poseStack, x, y, x + leftW - 10, y + 16, isSelected ? 0x66FFFFFF : 0x22FFFFFF);
            drawString(poseStack, font, (isSelected ? "▶ " : "") + name, x + 5, y + 4, isSelected ? 0xFFFFAA00 : 0xFFFFFF);
            y += 18;
        }
        
        this.maxScroll = Math.max(0, (y - startY) - (height - 80));

        com.mojang.blaze3d.systems.RenderSystem.disableScissor();

        if (expandedBaseId != null) {
            JsonObject selectedBase = null;
            for (JsonElement e : data) {
                if (e.getAsJsonObject().get("id").getAsInt() == expandedBaseId) {
                    selectedBase = e.getAsJsonObject();
                    break;
                }
            }
            if (selectedBase != null) {
                int ry = PANEL_Y + 10;
                drawString(poseStack, font, "§6§l" + selectedBase.get("name").getAsString(), rightX, ry, 0xFFFFFF);
                
                int btnY = ry + 20;
                boolean hoverEdit = mouseX >= rightX && mouseX < rightX + 50 && mouseY >= btnY && mouseY < btnY + 14;
                fill(poseStack, rightX, btnY, rightX + 50, btnY + 14, hoverEdit ? 0x66FFFFFF : 0x44FFFFFF);
                drawString(poseStack, font, "Editar", rightX + 10, btnY + 3, 0xFFFFFF);

                boolean hoverDel = mouseX >= rightX + 60 && mouseX < rightX + 110 && mouseY >= btnY && mouseY < btnY + 14;
                fill(poseStack, rightX + 60, btnY, rightX + 110, btnY + 14, hoverDel ? 0x66FF5555 : 0x44FF5555);
                drawString(poseStack, font, "Borrar", rightX + 70, btnY + 3, 0xFFFFFF);
                
                ry = btnY + 25;
                drawString(poseStack, font, "§eInventario Agrupado:", rightX, ry, 0xFFFFFF);
                ry += 15;
                
                java.util.Map<String, Integer> itemsAgg = new java.util.HashMap<>();
                if (selectedBase.has("storages")) {
                    for (JsonElement s : selectedBase.getAsJsonArray("storages")) {
                        JsonObject st = s.getAsJsonObject();
                        if (st.has("items")) {
                            for (JsonElement it : st.getAsJsonArray("items")) {
                                JsonObject itemObj = it.getAsJsonObject();
                                String dName = itemObj.getAsJsonObject("item").get("displayName").getAsString();
                                int qty = itemObj.get("quantity").getAsInt();
                                itemsAgg.put(dName, itemsAgg.getOrDefault(dName, 0) + qty);
                            }
                        }
                    }
                }
                
                if (itemsAgg.isEmpty()) {
                    drawString(poseStack, font, "§7No hay ítems.", rightX, ry, 0xFFFFFF);
                } else {
                    java.util.List<java.util.Map.Entry<String, Integer>> list = new java.util.ArrayList<>(itemsAgg.entrySet());
                    int totalItems = list.size();
                    int itemsPerPage = 10;
                    int startIdx = baseItemPage * itemsPerPage;
                    int endIdx = Math.min(startIdx + itemsPerPage, totalItems);
                    
                    for (int i = startIdx; i < endIdx; i++) {
                        java.util.Map.Entry<String, Integer> entry = list.get(i);
                        drawString(poseStack, font, "x" + entry.getValue() + " " + entry.getKey(), rightX, ry, 0x55FFFF);
                        ry += 12;
                    }
                    
                    if (totalItems > itemsPerPage) {
                        int pgY = PANEL_Y + height - 70;
                        fill(poseStack, rightX, pgY, rightX + 20, pgY + 14, 0x44FFFFFF);
                        drawString(poseStack, font, "<-", rightX + 4, pgY + 3, 0xFFFFFF);
                        
                        fill(poseStack, rightX + 30, pgY, rightX + 50, pgY + 14, 0x44FFFFFF);
                        drawString(poseStack, font, "->", rightX + 34, pgY + 3, 0xFFFFFF);
                    }
                }
            }
        }
    }

    private void renderCatalog(PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        int startY = y;
        int listWidth = selectedCatalogItem != null ? (width - 30) - 160 : (width - 50);

        // Filtrado local en el cliente por buscador
        java.util.List<JsonObject> filtered = new java.util.ArrayList<>();
        String query = searchBox != null ? searchBox.getValue().trim().toLowerCase() : "";
        for (JsonElement el : data) {
            JsonObject item = el.getAsJsonObject();
            String name = item.has("name") && !item.get("name").isJsonNull() ? item.get("name").getAsString() : "";
            String dName = item.has("displayName") && !item.get("displayName").isJsonNull() ? item.get("displayName").getAsString() : "";
            if (query.isEmpty() || name.toLowerCase().contains(query) || dName.toLowerCase().contains(query)) {
                filtered.add(item);
            }
        }

        int totalItems = filtered.size();
        int startIdx = catalogPage * ITEMS_PER_PAGE;
        int endIdx = Math.min(startIdx + ITEMS_PER_PAGE, totalItems);

        for (int i = startIdx; i < endIdx; i++) {
            JsonObject item = filtered.get(i);
            String name = item.has("name") && !item.get("name").isJsonNull() ? item.get("name").getAsString() : "Desconocido";
            String dName = item.has("displayName") && !item.get("displayName").isJsonNull() ? item.get("displayName").getAsString() : name;
            String techName = "minecraft:" + name.toLowerCase();

            if (y > PANEL_Y - 30 && y < PANEL_Y + height - 50 + 30) {
                boolean isHovered = mouseX >= x && mouseX < x + listWidth && mouseY >= y && mouseY < y + 20;
                int rowBgColor = (selectedCatalogItem != null && selectedCatalogItem.equals(item)) 
                    ? 0x66FFFFFF 
                    : (isHovered ? 0x44FFFFFF : 0x22FFFFFF);
                fill(poseStack, x, y, x + listWidth, y + 20, rowBgColor);
                
                ItemStack stack = getStackFromTechnicalName(techName);
                Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(stack, x + 2, y + 2);
                
                drawString(poseStack, font, font.plainSubstrByWidth(dName, listWidth - 60), x + 24, y + 6, 0xFFFFFF);

                int btnX = x + listWidth - 25;
                fill(poseStack, btnX, y + 3, btnX + 20, y + 17, 0x55AAAAAA);
                drawString(poseStack, font, "[ i ]", btnX + 2, y + 6, 0xFFFF55);
            }
            y += 22;
        }
        
        this.maxScroll = Math.max(0, (y - startY) - (height - 94));

        // Deshabilitar tijera de scroll para que no corte el panel de información ni el item model
        com.mojang.blaze3d.systems.RenderSystem.disableScissor();

        // Panel de información premium translúcido
        if (selectedCatalogItem != null) {
            int pX = PANEL_X + width - 30 - 150;
            fill(poseStack, pX, PANEL_Y + 10, pX + 145, PANEL_Y + height - 60, 0x77000000);
            
            String name = selectedCatalogItem.has("name") && !selectedCatalogItem.get("name").isJsonNull() ? selectedCatalogItem.get("name").getAsString() : "Desconocido";
            String dName = selectedCatalogItem.has("displayName") && !selectedCatalogItem.get("displayName").isJsonNull() ? selectedCatalogItem.get("displayName").getAsString() : name;
            String techName = "minecraft:" + name.toLowerCase();

            ItemStack stack = getStackFromTechnicalName(techName);
            PoseStack modelView = RenderSystem.getModelViewStack();
            modelView.pushPose();
            modelView.translate(pX + 64, PANEL_Y + 30, 100.0F);
            modelView.scale(2.0F, 2.0F, 1.0F);
            RenderSystem.applyModelViewMatrix();
            Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(stack, 0, 0);
            modelView.popPose();
            RenderSystem.applyModelViewMatrix();

            int infoY = PANEL_Y + 75;
            drawCenteredString(poseStack, font, "§6" + dName, pX + 72, infoY, 0xFFFFFF);
            infoY += 15;

            for (String key : selectedCatalogItem.keySet()) {
                if (key.equals("itemFoods") || key.equals("itemEnchantments")) continue;
                if (key.equals("id") || key.equals("name") || key.equals("createdAt") || key.equals("sourceId")) continue;
                
                if (key.equals("displayName")) {
                    drawString(poseStack, font, "§7name: §f" + dName, pX + 5, infoY, 0xFFFFFF);
                    infoY += 12;
                    continue;
                }

                // Ocultar campos vacíos o nulos dinámicamente
                if (selectedCatalogItem.get(key).isJsonNull()) continue;
                
                String val = "";
                JsonElement jsonEl = selectedCatalogItem.get(key);
                if (jsonEl.isJsonArray()) {
                    java.util.List<String> list = new java.util.ArrayList<>();
                    for (JsonElement el : jsonEl.getAsJsonArray()) {
                        list.add(el.getAsString());
                    }
                    val = String.join(", ", list);
                } else {
                    String rawStr = jsonEl.getAsString();
                    if (rawStr.trim().startsWith("[") && rawStr.trim().endsWith("]")) {
                        try {
                            com.google.gson.JsonArray arr = new com.google.gson.Gson().fromJson(rawStr, com.google.gson.JsonArray.class);
                            java.util.List<String> list = new java.util.ArrayList<>();
                            for (JsonElement el : arr) {
                                list.add(el.getAsString());
                            }
                            val = String.join(", ", list);
                        } catch (Exception ex) {
                            val = rawStr;
                        }
                    } else {
                        val = rawStr;
                    }
                }

                if (val.equals("null") || val.equals("N/A") || val.equals("[]") || val.isEmpty() || val.equals("0") || val.equals("0.0")) continue;

                // Envoltura automática multilínea para listas largas de elementos
                int maxLineW = 135;
                if (font.width(val) > maxLineW) {
                    drawString(poseStack, font, "§7" + key + ":", pX + 5, infoY, 0xFFFFFF);
                    infoY += 11;
                    java.util.List<String> lines = new java.util.ArrayList<>();
                    String[] tokens = val.split(", ");
                    String currentLine = "";
                    for (String token : tokens) {
                        String test = currentLine.isEmpty() ? token : currentLine + ", " + token;
                        if (font.width(test) > maxLineW - 10) {
                            lines.add(currentLine);
                            currentLine = token;
                        } else {
                            currentLine = test;
                        }
                    }
                    if (!currentLine.isEmpty()) lines.add(currentLine);
                    
                    for (String line : lines) {
                        drawString(poseStack, font, "  §f" + font.plainSubstrByWidth(line, maxLineW - 10), pX + 5, infoY, 0xFFFFFF);
                        infoY += 11;
                    }
                } else {
                    String txt = "§7" + key + ": §f" + font.plainSubstrByWidth(val, 130);
                    drawString(poseStack, font, txt, pX + 5, infoY, 0xFFFFFF);
                    infoY += 12;
                }
            }
        }
    }

    private ItemStack getStackFromTechnicalName(String techName) {
        try {
            if (catalogCategory.equals("biomes")) return new ItemStack(Items.GRASS_BLOCK);
            if (catalogCategory.equals("enchantments")) return new ItemStack(Items.ENCHANTED_BOOK);
            
            ResourceLocation rl = new ResourceLocation(techName);
            Item item = ForgeRegistries.ITEMS.getValue(rl);
            if (item != null && item != Items.AIR) return new ItemStack(item);
            
            Block block = ForgeRegistries.BLOCKS.getValue(rl);
            if (block != null && block != Blocks.AIR) return new ItemStack(block);
        } catch (Exception ex) {}
        return new ItemStack(Items.BARRIER);
    }

    private void renderPlayer(PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        if (data.size() == 0) return;
        int startY = y;
        JsonObject p = data.get(0).getAsJsonObject();
        String pName = p.has("name") && !p.get("name").isJsonNull() ? p.get("name").getAsString() : "Desconocido";
        String pDate = p.has("createdAt") && !p.get("createdAt").isJsonNull() ? p.get("createdAt").getAsString() : "Hoy";
        drawString(poseStack, font, "§6§lJUGADOR: §r" + pName, x, y, 0xFFFFFF);
        drawString(poseStack, font, "§7Miembro desde: " + pDate, x, y + 12, 0x888888);
        y += 35;
        drawString(poseStack, font, "§6Actividad Reciente:", x, y, 0xFFFFFF);
        y += 15;
        if (p.has("activityLogs") && p.get("activityLogs").isJsonArray()) {
            JsonArray logs = p.getAsJsonArray("activityLogs");
            for (int i = 0; i < Math.min(logs.size(), 3); i++) {
                JsonObject log = logs.get(i).getAsJsonObject();
                String act = log.has("action") && !log.get("action").isJsonNull() ? log.get("action").getAsString() : (log.has("activityType") ? log.get("activityType").getAsString() : "Acción");
                String timeRaw = log.has("timestamp") && !log.get("timestamp").isJsonNull() ? log.get("timestamp").getAsString() : (log.has("activityDate") ? log.get("activityDate").getAsString() : "---");
                String time = timeRaw.contains(" ") ? timeRaw.split(" ")[0] : (timeRaw.contains("T") ? timeRaw.split("T")[0] : timeRaw);
                drawString(poseStack, font, " §8- §7" + act + " (" + time + ")", x + 5, y, 0xAAAAAA);
                y += 12;
            }
        }
        y += 20;
        drawString(poseStack, font, "§6Tus Bases:", x, y, 0xFFFFFF);
        y += 18;
        if (p.has("memberships")) {
            for (JsonElement m : p.getAsJsonArray("memberships")) {
                JsonObject member = m.getAsJsonObject();
                int bId = member.get("baseId").getAsInt();
                String bName = member.has("baseName") && !member.get("baseName").isJsonNull() ? member.get("baseName").getAsString() : "Base";
                fill(poseStack, x - 5, y - 2, width - 35, y + 12, (expandedBaseId != null && expandedBaseId == bId) ? 0x33FFFFFF : 0x11FFFFFF);
                drawString(poseStack, font, (expandedBaseId != null && expandedBaseId == bId ? "[-] " : "[+] ") + bName, x, y, 0xFFFFFF);
                if (expandedBaseId != null && expandedBaseId == bId) {
                    y += 18;
                    int tagX = x + 10;
                    if (member.has("baseTags")) {
                        for (JsonElement t : member.getAsJsonArray("baseTags")) {
                            JsonObject tag = t.getAsJsonObject();
                            int tId = tag.get("id").getAsInt();
                            String tName = tag.get("tag").getAsString();
                            int tColor = tag.has("color") && !tag.get("color").isJsonNull() ? tag.get("color").getAsInt() : 0xFFFFFF;
                            int nameW = font.width("[" + tName + "]");
                            int tagW = nameW + (tagIdToDelete != null && tagIdToDelete == tId ? 55 : 35);
                            if (tagX + tagW > width - 50) { tagX = x + 10; y += 14; }
                            fill(poseStack, tagX, y - 1, tagX + tagW, y + 10, 0x44000000);
                            drawString(poseStack, font, "[" + tName + "]", tagX + 2, y, tColor);
                            if (tagIdToDelete != null && tagIdToDelete == tId) drawString(poseStack, font, "§c[SI?]", tagX + nameW + 5, y, 0xFFFFFF);
                            else { drawString(poseStack, font, "§e[E]", tagX + nameW + 5, y, 0xFFFFFF); drawString(poseStack, font, "§c[X]", tagX + nameW + 20, y, 0xFFFFFF); }
                            tagX += tagW + 10;
                        }
                    }
                    y += 20; // Separar de las etiquetas
                    // Botón para activar el editor de nueva etiqueta
                    boolean isHoveredAdd = mouseX >= x + 10 && mouseX < x + 120 && mouseY >= y && mouseY < y + 12;
                    drawString(poseStack, font, isHoveredAdd ? "§e§n[+ Nueva Etiqueta]" : "§a[+ Nueva Etiqueta]", x + 10, y, 0xFFFFFF);
                    
                    if (activeBaseForTag != null && activeBaseForTag == bId) {
                        y += 16; // Saltar de línea para el formulario
                        
                        // Título del formulario: Nueva Etiqueta o Editando Etiqueta
                        String formTitle = (editingTagId == null) ? "§6§lNueva Etiqueta:" : "§6§lEditando Etiqueta:";
                        drawString(poseStack, font, formTitle, x + 10, y, 0xFFFFFF);
                        
                        y += 14; // Saltar de línea para los inputs
                        
                        // Posicionar la caja de texto
                        tagEdit.x = x + 10;
                        tagEdit.y = y - 1;
                        tagEdit.visible = true;
                        
                        // Posicionar el botón de guardar dinámicamente con CREAR o ACTUALIZAR
                        String btnText = (editingTagId == null) ? "CREAR" : "ACTUALIZAR";
                        saveTagBtn.setMessage(new net.minecraft.network.chat.TextComponent(btnText));
                        saveTagBtn.x = x + 95;
                        saveTagBtn.y = y - 2;
                        saveTagBtn.visible = true;
                        
                        // Selector de color
                        int colX = x + 160;
                        fill(poseStack, colX, y - 2, colX + 55, y + 12, 0x44000000);
                        drawCenteredString(poseStack, font, "Color", colX + 27, y, colors[currentColorIdx]);
                        
                        y += 10; // Espaciado extra si está abierto el editor
                    } else {
                        // Si no está activo para esta base, nos aseguramos de no mostrarlo aquí
                        if (activeBaseForTag != null && activeBaseForTag == bId) {
                            tagEdit.visible = false;
                            saveTagBtn.visible = false;
                        }
                    }
                    y += 10;
                }
                y += 20;
            }
        }
        this.maxScroll = Math.max(0, (y - startY) - (height - 80));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int drawY = (int) (PANEL_Y + 10 - scrollAmount);
        int x = PANEL_X + 10;
        
        if (mouseY < PANEL_Y || mouseY > PANEL_Y + (height - 50)) return super.mouseClicked(mouseX, mouseY, button);

        if (currentTab == Tab.BASES) {
            int y = PANEL_Y + 40 - (int)scrollAmount;
            int leftW = 160;
            for (JsonElement e : data) {
                int id = e.getAsJsonObject().get("id").getAsInt();
                if (mouseX >= x && mouseX < x + leftW - 10 && mouseY >= y && mouseY < y + 16) { 
                    expandedBaseId = id; 
                    baseItemPage = 0;
                    resetTagEditor();
                    return true; 
                }
                y += 18;
            }
            
            if (expandedBaseId != null) {
                int rightX = PANEL_X + leftW + 10;
                int ry = PANEL_Y + 10;
                int btnY = ry + 20;
                
                if (mouseY >= btnY && mouseY < btnY + 14) {
                    if (mouseX >= rightX && mouseX < rightX + 50) {
                        editingBaseId = expandedBaseId;
                        for (JsonElement e : data) {
                            if (e.getAsJsonObject().get("id").getAsInt() == expandedBaseId) {
                                baseNameEdit.setValue(e.getAsJsonObject().get("name").getAsString());
                                break;
                            }
                        }
                        baseSaveBtn.setMessage(new TextComponent("Act."));
                        return true;
                    }
                    if (mouseX >= rightX + 60 && mouseX < rightX + 110) {
                        InventoryApiClient.deleteBase(expandedBaseId).thenAccept(res -> {
                            Minecraft.getInstance().execute(() -> {
                                expandedBaseId = null;
                                refreshData();
                            });
                        });
                        return true;
                    }
                }
                
                int pgY = PANEL_Y + height - 70;
                if (mouseY >= pgY && mouseY < pgY + 14) {
                    if (mouseX >= rightX && mouseX < rightX + 20 && baseItemPage > 0) {
                        baseItemPage--; return true;
                    }
                    if (mouseX >= rightX + 30 && mouseX < rightX + 50) {
                        baseItemPage++; return true;
                    }
                }
            }
        } else if (currentTab == Tab.CATALOG) {
            // Detección de clics fija para los botones de categoría
            int catY = PANEL_Y + 5;
            int catX = x;
            for (int i = 0; i < categories.length; i++) {
                int tw = font.width(categoryNames[i]) + 10;
                if (mouseX >= catX && mouseX < catX + tw && mouseY >= catY && mouseY < catY + 14) {
                    catalogCategory = categories[i];
                    this.data = new com.google.gson.JsonArray();
                    if (this.searchBox != null) this.searchBox.setValue("");
                    refreshData();
                    return true;
                }
                catX += tw + 5;
            }

            int listWidth = selectedCatalogItem != null ? (width - 30) - 160 : (width - 50);

            // Paginación clics
            int pgX = width - 100;
            int pgY = PANEL_Y + 5;
            
            if (mouseY >= pgY && mouseY < pgY + 14) {
                if (mouseX >= pgX && mouseX < pgX + 15) {
                    if (catalogPage > 0) { catalogPage--; scrollAmount = 0; selectedCatalogItem = null; }
                    return true;
                }
                if (mouseX >= pgX + 55 && mouseX < pgX + 70) {
                    // Paginación calculada en base al filtro del buscador
                    java.util.List<JsonObject> filtered = new java.util.ArrayList<>();
                    String query = searchBox != null ? searchBox.getValue().trim().toLowerCase() : "";
                    for (JsonElement el : data) {
                        JsonObject item = el.getAsJsonObject();
                        String name = item.has("name") && !item.get("name").isJsonNull() ? item.get("name").getAsString() : "";
                        String dName = item.has("displayName") && !item.get("displayName").isJsonNull() ? item.get("displayName").getAsString() : "";
                        if (query.isEmpty() || name.toLowerCase().contains(query) || dName.toLowerCase().contains(query)) {
                            filtered.add(item);
                        }
                    }
                    int totalPages = (int) Math.ceil((double) filtered.size() / ITEMS_PER_PAGE);
                    if (catalogPage < totalPages - 1) { catalogPage++; scrollAmount = 0; selectedCatalogItem = null; }
                    return true;
                }
            }

            // Listado de clics (desplazado a PANEL_Y + 42 por el buscador)
            int y = (int) (PANEL_Y + 42 - scrollAmount);
            
            java.util.List<JsonObject> filtered = new java.util.ArrayList<>();
            String query = searchBox != null ? searchBox.getValue().trim().toLowerCase() : "";
            for (JsonElement el : data) {
                JsonObject item = el.getAsJsonObject();
                String name = item.has("name") && !item.get("name").isJsonNull() ? item.get("name").getAsString() : "";
                String dName = item.has("displayName") && !item.get("displayName").isJsonNull() ? item.get("displayName").getAsString() : "";
                if (query.isEmpty() || name.toLowerCase().contains(query) || dName.toLowerCase().contains(query)) {
                    filtered.add(item);
                }
            }

            int totalItems = filtered.size();
            int startIdx = catalogPage * ITEMS_PER_PAGE;
            int endIdx = Math.min(startIdx + ITEMS_PER_PAGE, totalItems);

            for (int i = startIdx; i < endIdx; i++) {
                if (y > PANEL_Y - 30 && y < PANEL_Y + (height - 50) + 30) {
                    if (mouseX >= x && mouseX < x + listWidth && mouseY >= y && mouseY < y + 20) {
                        selectedCatalogItem = filtered.get(i);
                        return true;
                    }
                }
                y += 22;
            }
        } else if (currentTab == Tab.PLAYER && data.size() > 0) {
            int y = drawY;
            JsonObject p = data.get(0).getAsJsonObject();
            y += 35; y += 15;
            if (p.has("activityLogs") && p.get("activityLogs").isJsonArray()) y += Math.min(p.getAsJsonArray("activityLogs").size(), 3) * 12;
            else y += 12;
            y += 20; y += 18;
            for (JsonElement m : p.getAsJsonArray("memberships")) {
                JsonObject member = m.getAsJsonObject();
                int bId = member.get("baseId").getAsInt();
                if (mouseY >= y && mouseY < y + 12) { expandedBaseId = (expandedBaseId != null && expandedBaseId == bId) ? null : bId; resetTagEditor(); return true; }
                if (expandedBaseId != null && expandedBaseId == bId) {
                    y += 18;
                    int tagX = x + 10;
                    for (JsonElement t : member.getAsJsonArray("baseTags")) {
                        JsonObject tag = t.getAsJsonObject();
                        int tId = tag.get("id").getAsInt();
                        int nameW = font.width(tag.get("tag").getAsString()) + 2; // Fixed tag text size mapping
                        int tagW = nameW + (tagIdToDelete != null && tagIdToDelete == tId ? 55 : 35);
                        if (tagX + tagW > width - 50) { tagX = x + 10; y += 14; }
                        if (tagIdToDelete != null && tagIdToDelete == tId) {
                            if (mouseX >= tagX + nameW + 5 && mouseX < tagX + tagW && mouseY >= y && mouseY < y + 10) {
                                InventoryApiClient.deleteTag(tId).thenAccept(v -> Minecraft.getInstance().execute(() -> { refreshData(); resetTagEditor(); })); return true;
                            }
                        } else {
                            if (mouseX >= tagX + nameW + 5 && mouseX < tagX + nameW + 18 && mouseY >= y && mouseY < y + 10) {
                                activeBaseForTag = bId; editingTagId = tId; tagEdit.setValue(tag.get("tag").getAsString()); tagEdit.setFocus(true); return true;
                            }
                            if (mouseX >= tagX + nameW + 20 && mouseX < tagX + nameW + 33 && mouseY >= y && mouseY < y + 10) {
                                tagIdToDelete = tId; return true;
                            }
                        }
                        tagX += tagW + 10;
                    }
                    y += 20;
                    if (mouseX >= x + 10 && mouseX < x + 120 && mouseY >= y && mouseY < y + 12) {
                        if (activeBaseForTag != null && activeBaseForTag == bId && editingTagId == null) {
                            resetTagEditor();
                        } else {
                            activeBaseForTag = bId; editingTagId = null; tagEdit.setValue(""); tagEdit.setFocus(true);
                        }
                        return true;
                    }
                    
                    if (activeBaseForTag != null && activeBaseForTag == bId) {
                        y += 16;
                        y += 14;
                        if (mouseX >= x + 160 && mouseX < x + 215 && mouseY >= y - 2 && mouseY < y + 12) {
                            currentColorIdx = (currentColorIdx + 1) % colors.length;
                            return true;
                        }
                        y += 10;
                    }
                    y += 10;
                }
                y += 20;
            }
        }
        tagIdToDelete = null;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
