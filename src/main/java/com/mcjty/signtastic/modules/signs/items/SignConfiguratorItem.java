package com.mcjty.signtastic.modules.signs.items;

import com.mcjty.signtastic.SignTastic;
import com.mcjty.signtastic.modules.signs.SignSettings;
import com.mcjty.signtastic.modules.signs.blocks.AbstractSignTileEntity;
import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.tooltips.ITooltipSettings;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.Lazy;

import java.util.List;

import static mcjty.lib.builder.TooltipBuilder.*;

public class SignConfiguratorItem extends Item implements ITooltipSettings {

    private final Lazy<TooltipBuilder> tooltipBuilder = () -> new TooltipBuilder()
            .info(key("message.signtastic.shiftmessage"))
            .infoShift(header(), gold());

    public SignConfiguratorItem() {
        super(new Properties()
                .stacksTo(1)
                .tab(SignTastic.setup.getTab()));
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Level world, List<Component> list, TooltipFlag flags) {
        super.appendHoverText(itemStack, world, list, flags);
        tooltipBuilder.get().makeTooltip(getRegistryName(), itemStack, list, flags);
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player) {
        return super.doesSneakBypassUse(stack, world, pos, player);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!level.isClientSide()) {
            BlockPos pos = context.getClickedPos();
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof AbstractSignTileEntity sign) {
                if (context.getPlayer().isCrouching()) {
                    CompoundTag tag = context.getItemInHand().getTag();
                    if (tag == null || !tag.contains("settings")) {
                        context.getPlayer().sendMessage(new TextComponent("There are no copied settings!").withStyle(ChatFormatting.RED), Util.NIL_UUID);
                    } else {
                        context.getPlayer().sendMessage(new TextComponent("Pasted settings to sign!").withStyle(ChatFormatting.GREEN), Util.NIL_UUID);
                        CompoundTag settingsTag = tag.getCompound("settings");
                        sign.getSettings().read(settingsTag);
                        sign.markDirtyClient();
                    }
                } else {
                    // Copy
                    SignSettings settings = sign.getSettings();
                    CompoundTag tag = new CompoundTag();
                    settings.write(tag);
                    context.getItemInHand().getOrCreateTag().put("settings", tag);
                    context.getPlayer().sendMessage(new TextComponent("Copied settings from sign!").withStyle(ChatFormatting.GREEN), Util.NIL_UUID);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }
}
