package com.mcjty.signtastic.modules.signs.items;

import com.mcjty.signtastic.SignTastic;
import com.mcjty.signtastic.modules.signs.SignSettings;
import com.mcjty.signtastic.modules.signs.blocks.AbstractSignTileEntity;
import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.tooltips.ITooltipSettings;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
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
    public void appendHoverText(ItemStack itemStack, World world, List<ITextComponent> list, ITooltipFlag flags) {
        super.appendHoverText(itemStack, world, list, flags);
        tooltipBuilder.get().makeTooltip(getRegistryName(), itemStack, list, flags);
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) {
        return super.doesSneakBypassUse(stack, world, pos, player);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World level = context.getLevel();
        if (!level.isClientSide()) {
            BlockPos pos = context.getClickedPos();
            TileEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof AbstractSignTileEntity) {
                AbstractSignTileEntity sign = (AbstractSignTileEntity) blockEntity;
                if (context.getPlayer().isCrouching()) {
                    CompoundNBT tag = context.getItemInHand().getTag();
                    if (tag == null || !tag.contains("settings")) {
                        context.getPlayer().sendMessage(new StringTextComponent("There are no copied settings!").withStyle(TextFormatting.RED), Util.NIL_UUID);
                    } else {
                        context.getPlayer().sendMessage(new StringTextComponent("Pasted settings to sign!").withStyle(TextFormatting.GREEN), Util.NIL_UUID);
                        CompoundNBT settingsTag = tag.getCompound("settings");
                        sign.getSettings().read(settingsTag);
                        sign.markDirtyClient();
                    }
                } else {
                    // Copy
                    SignSettings settings = sign.getSettings();
                    CompoundNBT tag = new CompoundNBT();
                    settings.write(tag);
                    context.getItemInHand().getOrCreateTag().put("settings", tag);
                    context.getPlayer().sendMessage(new StringTextComponent("Copied settings from sign!").withStyle(TextFormatting.GREEN), Util.NIL_UUID);
                }
            }
        }
        return ActionResultType.SUCCESS;
    }
}
