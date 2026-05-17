package com.joel.inventoryviewer;

import net.minecraft.core.BlockPos;
import java.util.ArrayList;
import java.util.List;

public class SelectionHandler {
    private static final List<BlockPos> SELECTED_BLOCKS = new ArrayList<>();

    public static void addBlock(BlockPos pos) {
        if (!SELECTED_BLOCKS.contains(pos)) {
            SELECTED_BLOCKS.add(pos);
        }
    }

    public static void removeBlock(BlockPos pos) {
        SELECTED_BLOCKS.remove(pos);
    }

    public static List<BlockPos> getSelectedBlocks() {
        return new ArrayList<>(SELECTED_BLOCKS);
    }

    public static void clearSelection() {
        SELECTED_BLOCKS.clear();
    }
}
