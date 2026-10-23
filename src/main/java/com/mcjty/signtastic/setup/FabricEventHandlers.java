package com.mcjty.signtastic.setup;
import com.mcjty.signtastic.modules.signs.items.SignConfiguratorItem;
import com.mcjty.signtastic.modules.signs.blocks.AbstractSignTileEntity;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
public final class FabricEventHandlers {
    public static void register() {
        UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
            if (!player.isSpectator() && player.getItemInHand(hand).getItem() instanceof SignConfiguratorItem item
                    && level.getBlockEntity(hit.getBlockPos()) instanceof AbstractSignTileEntity) {
                return item.useOn(new UseOnContext(player, hand, hit));
            }
            return InteractionResult.PASS;
        });
    }
}
