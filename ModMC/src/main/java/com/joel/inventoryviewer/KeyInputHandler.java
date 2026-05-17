package com.joel.inventoryviewer;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = InventoryViewer.MODID, value = Dist.CLIENT)
public class KeyInputHandler {
    public static final KeyMapping BASE_INFO_KEY = new KeyMapping(
            "key.inventoryviewer.base_info",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "key.categories.inventoryviewer"
    );

    public static void register() {
        ClientRegistry.registerKeyBinding(BASE_INFO_KEY);
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        if (BASE_INFO_KEY.consumeClick()) {
            Minecraft.getInstance().setScreen(new MainDashboardScreen());
        }
    }
}
