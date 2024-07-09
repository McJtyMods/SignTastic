package com.mcjty.signtastic.modules.signs.items;

import com.mcjty.signtastic.SignTastic;
import com.mcjty.signtastic.modules.signs.blocks.AbstractSignTileEntity;
import com.mcjty.signtastic.modules.signs.data.SignSettings;
import com.mcjty.signtastic.setup.Registration;
import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.tooltips.ITooltipSettings;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.util.Lazy;

import javax.annotation.Nonnull;
import java.util.List;

import static mcjty.lib.builder.TooltipBuilder.*;

public class SignConfiguratorItem extends Item implements ITooltipSettings {

    private final Lazy<TooltipBuilder> tooltipBuilder = Lazy.of(() -> new TooltipBuilder()
            .info(key("message.signtastic.shiftmessage"))
            .infoShift(header(), gold()));

    public SignConfiguratorItem() {
        super(SignTastic.setup.defaultProperties().stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, List<Component> list, TooltipFlag flags) {
        super.appendHoverText(itemStack, context, list, flags);
        tooltipBuilder.get().makeTooltip(BuiltInRegistries.ITEM.getKey(this), itemStack, list, flags);
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player) {
        return super.doesSneakBypassUse(stack, world, pos, player);
    }

    @Override
    @Nonnull
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!level.isClientSide()) {
            BlockPos pos = context.getClickedPos();
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof AbstractSignTileEntity) {
                AbstractSignTileEntity sign = (AbstractSignTileEntity) blockEntity;
                if (context.getPlayer().isCrouching()) {
                    // Paste
                    SignSettings settings = context.getItemInHand().get(Registration.ITEM_SIGNSETTINGS);
                    if (settings == null) {
                        context.getPlayer().sendSystemMessage(Component.literal("There are no copied settings!").withStyle(ChatFormatting.RED));
                    } else {
                        context.getPlayer().sendSystemMessage(Component.literal("Pasted settings to sign!").withStyle(ChatFormatting.GREEN));
                        sign.setSettings(settings);
                    }
                } else {
                    // Copy
                    SignSettings settings = sign.getSettings();
                    context.getItemInHand().set(Registration.ITEM_SIGNSETTINGS, settings);
                    context.getPlayer().sendSystemMessage(Component.literal("Copied settings from sign!").withStyle(ChatFormatting.GREEN));
                }
            }
        }
        return InteractionResult.SUCCESS;
    }
}
