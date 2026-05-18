package com.joel.inventoryviewer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = "inventoryviewer", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class SelectionHandler {
    public static Integer activeBaseId = null;
    public static String activeBaseName = null;
    public static final List<BlockPos> ACTIVE_BASE_STORAGES = new ArrayList<>();
    private static boolean fetching = false;

    // Keep compatibility fields
    private static final List<BlockPos> SELECTED_BLOCKS = new ArrayList<>();
    public static void addBlock(BlockPos pos) { if (!SELECTED_BLOCKS.contains(pos)) SELECTED_BLOCKS.add(pos); }
    public static void removeBlock(BlockPos pos) { SELECTED_BLOCKS.remove(pos); }
    public static List<BlockPos> getSelectedBlocks() { return new ArrayList<>(SELECTED_BLOCKS); }
    public static void clearSelection() { SELECTED_BLOCKS.clear(); }

    @SubscribeEvent
    public static void onRenderLevelLast(RenderLevelLastEvent event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        // Check if player is holding WandItem in main hand or off hand
        boolean holdingWand = player.getMainHandItem().getItem() instanceof WandItem || 
                              player.getOffhandItem().getItem() instanceof WandItem;
        
        if (!holdingWand) return;

        // Auto-fetch active base and storages asynchronously if not loaded in this session yet
        if (activeBaseId == null && !fetching) {
            fetching = true;
            String uuid = player.getUUID().toString();
            InventoryApiClient.getPlayerByUuid(uuid).thenAccept(playerJson -> {
                if (playerJson != null && playerJson.has("activeBaseId") && !playerJson.get("activeBaseId").isJsonNull()) {
                    activeBaseId = playerJson.get("activeBaseId").getAsInt();
                    
                    // Fetch storages
                    InventoryApiClient.getStoragesByBaseId(activeBaseId).thenAccept(storages -> {
                        ACTIVE_BASE_STORAGES.clear();
                        for (int i = 0; i < storages.size(); i++) {
                            JsonObject s = storages.get(i).getAsJsonObject();
                            int sx = s.get("x").getAsInt();
                            int sy = s.get("y").getAsInt();
                            int sz = s.get("z").getAsInt();
                            ACTIVE_BASE_STORAGES.add(new BlockPos(sx, sy, sz));
                        }
                    });

                    // Fetch active base name from memberships
                    if (playerJson.has("memberships")) {
                        JsonArray memberships = playerJson.getAsJsonArray("memberships");
                        for (int i = 0; i < memberships.size(); i++) {
                            JsonObject member = memberships.get(i).getAsJsonObject();
                            if (member.has("baseId") && member.get("baseId").getAsInt() == activeBaseId) {
                                activeBaseName = member.get("baseName").getAsString();
                                break;
                            }
                        }
                    }
                }
                fetching = false;
            }).exceptionally(ex -> {
                fetching = false;
                return null;
            });
        }

        if (ACTIVE_BASE_STORAGES.isEmpty()) return;

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource bufferSource = mc.renderBuffers().bufferSource();
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());

        Vec3 camera = mc.gameRenderer.getMainCamera().getPosition();

        for (BlockPos pos : ACTIVE_BASE_STORAGES) {
            double x = pos.getX() - camera.x;
            double y = pos.getY() - camera.y;
            double z = pos.getZ() - camera.z;
            
            poseStack.pushPose();
            poseStack.translate(x, y, z);
            
            // Draw unit gold wireframe box exactly centered on the container coordinates
            LevelRenderer.renderLineBox(poseStack, vertexConsumer, 0, 0, 0, 1, 1, 1, 1.0F, 0.66F, 0.0F, 1.0F);
            
            poseStack.popPose();
        }
        
        // CRITICAL: Flush the buffer immediately so it renders with the current valid ModelView matrix!
        if (bufferSource instanceof net.minecraft.client.renderer.MultiBufferSource.BufferSource) {
            ((net.minecraft.client.renderer.MultiBufferSource.BufferSource) bufferSource).endBatch(RenderType.lines());
        }
    }
}
