package com.joel.inventoryviewer;

import com.mojang.logging.LogUtils;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("inventoryviewer")
public class InventoryViewer
{
    public static final String MODID = "inventoryviewer";
    private static final Logger LOGGER = LogUtils.getLogger();

    // Deferred register for items
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Item> WAND = ITEMS.register("wand", WandItem::new);

    public InventoryViewer()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the items
        ITEMS.register(modEventBus);

        // Register the setup method for modloading
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::clientSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(SelectionHandler.class);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        LOGGER.info("Inventory Viewer Setup Started");
    }

    private void clientSetup(final FMLClientSetupEvent event)
    {
        KeyInputHandler.register();
    }
}
