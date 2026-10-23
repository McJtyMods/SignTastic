package com.mcjty.signtastic.setup;


import com.mcjty.signtastic.SignTastic;
import com.mcjty.signtastic.modules.signs.SignsModule;
import com.mcjty.signtastic.modules.signs.data.SignData;
import com.mcjty.signtastic.modules.signs.data.SignSettings;
import mcjty.lib.blocks.RBlock;
import mcjty.lib.blocks.RBlockRegistry;
import mcjty.lib.setup.DeferredBlocks;
import mcjty.lib.setup.DeferredItems;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.minecraft.resources.Identifier;
import mcjty.lib.setup.DeferredHolder;
import mcjty.lib.setup.DeferredRegister;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.mcjty.signtastic.SignTastic.MODID;

public class Registration {

    public static final RBlockRegistry RBLOCKS = new RBlockRegistry(MODID, supplier -> SignTastic.setup.addTabItem(supplier));
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(Registries.MENU, MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MODID);

    public static void register() {
        RBLOCKS.register();
        CONTAINERS.register();
        TABS.register();
        COMPONENTS.register();
    }

    public static final AttachmentType<SignSettings> SIGNSETTINGS = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(MODID, "signsettings"), builder -> builder.initializer(SignSettings::new).persistent(SignSettings.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SignSettings>> ITEM_SIGNSETTINGS = COMPONENTS.registerComponentType(
            "signsettings",
            builder -> builder
                    .persistent(SignSettings.CODEC)
                    .networkSynchronized(SignSettings.STREAM_CODEC));

    public static final AttachmentType<SignData> SIGNDATA = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(MODID, "signdata"), builder -> builder.initializer(SignData::new).persistent(SignData.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SignData>> ITEM_SIGNDATA = COMPONENTS.registerComponentType(
            "signdata",
            builder -> builder
                    .persistent(SignData.CODEC)
                    .networkSynchronized(SignData.STREAM_CODEC));

    public static Item.Properties createStandardProperties() {
        return SignTastic.setup.defaultProperties();
    }

    public static DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = TABS.register("signtastic", () -> net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + MODID))
            .icon(() -> new ItemStack(SignsModule.SQUARE_SIGN.item().get()))
            .displayItems((featureFlags, output) -> {
                SignTastic.setup.populateTab(output);
            })
            .build());
}
