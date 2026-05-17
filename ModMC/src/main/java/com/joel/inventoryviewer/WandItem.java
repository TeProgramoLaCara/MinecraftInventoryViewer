package com.joel.inventoryviewer;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;

public class WandItem extends Item {

    public WandItem() {
        super(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        if (player != null && player.isShiftKeyDown()) {
            if (level.isClientSide) {
                BlockEntity be = level.getBlockEntity(pos);
                // Check if it's a container (we'll check for block entity presence on client)
                if (be != null) {
                    SelectionHandler.addBlock(pos);
                    player.displayClientMessage(new TextComponent("Cofre añadido a la selección en: " + pos.toShortString()), true);
                } else {
                    player.displayClientMessage(new TextComponent("Este bloque no es un contenedor."), true);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (player.isShiftKeyDown()) {
             return InteractionResultHolder.pass(player.getItemInHand(hand));
        }

        if (level.isClientSide) {
            net.minecraft.client.Minecraft.getInstance().setScreen(new WandScreen());
        }
        
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}
