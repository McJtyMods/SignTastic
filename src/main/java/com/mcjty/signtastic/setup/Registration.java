package com.mcjty.signtastic.setup;


import com.mcjty.signtastic.SignTastic;
import com.mcjty.signtastic.modules.signs.SignsModule;
import com.mcjty.signtastic.modules.signs.data.SignData;
import com.mcjty.signtastic.modules.signs.data.SignSettings;
import mcjty.lib.api.infusable.ItemInfusable;
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
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

import static com.mcjty.signtastic.SignTastic.MODID;

public class Registration {

    public static final DeferredBlocks BLOCKS = DeferredBlocks.create(MODID);
    public static final DeferredItems ITEMS = DeferredItems.create(MODID);
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(Registries.MENU, MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MODID);
    public static final DeferredRegister.DataComponents REGISTRAR = DeferredRegister.createDataComponents(MODID);

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        TILES.register(bus);
        CONTAINERS.register(bus);
        TABS.register(bus);
        ATTACHMENT_TYPES.register(bus);
    }

    public static final Supplier<AttachmentType<SignSettings>> SIGNSETTINGS = ATTACHMENT_TYPES.register(
            "hotkeys", () -> AttachmentType.builder(SignSettings::new)
                    .serialize(SignSettings.CODEC)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SignSettings>> ITEM_SIGNSETTINGS = REGISTRAR.registerComponentType(
            "signsettings",
            builder -> builder
                    .persistent(SignSettings.CODEC)
                    .networkSynchronized(SignSettings.STREAM_CODEC));

    public static final Supplier<AttachmentType<SignData>> SIGNDATA = ATTACHMENT_TYPES.register(
            "hotkeys", () -> AttachmentType.builder(() -> new SignData())
                    .serialize(SignData.CODEC)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SignData>> ITEM_SIGNDATA = REGISTRAR.registerComponentType(
            "signdata",
            builder -> builder
                    .persistent(SignData.CODEC)
                    .networkSynchronized(SignData.STREAM_CODEC));

    public static Item.Properties createStandardProperties() {
        return SignTastic.setup.defaultProperties();
    }

    public static DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = TABS.register("signtastic", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + MODID))
            .icon(() -> new ItemStack(SignsModule.SQUARE_SIGN_ITEM.get()))
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .displayItems((featureFlags, output) -> {
                SignTastic.setup.populateTab(output);
            })
            .build());
}
