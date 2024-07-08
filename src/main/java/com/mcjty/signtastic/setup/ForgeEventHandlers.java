package com.mcjty.signtastic.setup;

import com.mcjty.signtastic.modules.signs.blocks.AbstractSignTileEntity;
import com.mcjty.signtastic.modules.signs.items.SignConfiguratorItem;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class ForgeEventHandlers {

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
        if (event.getItemStack().getItem() instanceof SignConfiguratorItem) {
            BlockPos pos = event.getHitVec().getBlockPos();
            Level world = event.getLevel();
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof AbstractSignTileEntity) {
                event.setUseBlock(TriState.FALSE);
                event.setUseItem(TriState.TRUE);
            }
        }
    }
}
