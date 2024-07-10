package com.mcjty.signtastic.modules.signs;

import com.mcjty.signtastic.modules.signs.blocks.*;
import com.mcjty.signtastic.modules.signs.client.SignGui;
import com.mcjty.signtastic.modules.signs.client.SignRenderer;
import com.mcjty.signtastic.modules.signs.items.SignConfiguratorItem;
import mcjty.lib.blocks.RBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.function.Supplier;

import static com.mcjty.signtastic.setup.Registration.*;
import static mcjty.lib.datagen.DataGen.has;

public class SignsModule implements IModule {

    public static final RBlock<SquareSignBlock, BlockItem, SquareSignTileEntity> SQUARE_SIGN = RBLOCKS.registerBlock("square_sign",
            SquareSignTileEntity.class,
            () -> new SquareSignBlock(SquareSignTileEntity::new),
            block -> new BlockItem(block.get(), createStandardProperties()),
            SquareSignTileEntity::new
    );
    public static final RBlock<BlockSignBlock, BlockItem, BlockSignTileEntity> BLOCK_SIGN = RBLOCKS.registerBlock("block_sign",
            BlockSignTileEntity.class,
            () -> new BlockSignBlock(BlockSignTileEntity::new),
            block -> new BlockItem(block.get(), createStandardProperties()),
            BlockSignTileEntity::new
    );
    public static final RBlock<SlabSignBlock, BlockItem, SlabSignTileEntity> SLAB_SIGN = RBLOCKS.registerBlock("slab_sign",
            SlabSignTileEntity.class,
            () -> new SlabSignBlock(SlabSignTileEntity::new),
            block -> new BlockItem(block.get(), createStandardProperties()),
            SlabSignTileEntity::new
    );

    public static final Supplier<MenuType<GenericContainer>> CONTAINER_SIGN = CONTAINERS.register("sign_container", GenericContainer::createContainerType);

    public static final DeferredItem<SignConfiguratorItem> SIGN_CONFIGURATOR = RBLOCKS.registerItem("sign_configurator", SignConfiguratorItem::new);


    public SignsModule(IEventBus bus, Dist dist) {
    }

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            SignGui.register();
        });
        SignRenderer.register();
    }

    @Override
    public void initConfig(IEventBus bus) {

    }

    @Override
    public void initDatagen(DataGen dataGen) {
        dataGen.add(
                Dob.blockBuilder(SQUARE_SIGN)
                        .parentedItem("block/square_sign")
                        .standardLoot(SQUARE_SIGN.be())
                        .blockState(p -> p.orientedBlock(SQUARE_SIGN, DataGenHelper.screenModel(p, "square_sign", p.modLoc("block/screenframe_icon"), 13)))
                        .shaped(builder -> builder
                                        .define('S', ItemTags.SIGNS)
                                        .unlockedBy("paper", has(Items.PAPER)),
                                "ppp", "pSp", "ppp"),
                Dob.blockBuilder(BLOCK_SIGN)
                        .parentedItem("block/block_sign")
                        .standardLoot(BLOCK_SIGN.be())
                        .blockState(p -> p.orientedBlock(BLOCK_SIGN, DataGenHelper.screenModel(p, "block_sign", p.modLoc("block/screenframe_icon"), 0)))
                        .shaped(builder -> builder
                                        .define('S', ItemTags.SIGNS)
                                        .define('P', ItemTags.PLANKS)
                                        .unlockedBy("paper", has(Items.PAPER)),
                                "pPp", "PSP", "pPp"),
                Dob.blockBuilder(SLAB_SIGN)
                        .parentedItem("block/slab_sign")
                        .standardLoot(SLAB_SIGN.be())
                        .blockState(p -> p.orientedBlock(SLAB_SIGN, DataGenHelper.screenModel(p, "slab_sign", p.modLoc("block/screenframe_icon"), 8)))
                        .shaped(builder -> builder
                                        .define('S', ItemTags.SIGNS)
                                        .define('P', ItemTags.PLANKS)
                                        .unlockedBy("paper", has(Items.PAPER)),
                                "   ", "PSP", "pPp"),
                Dob.itemBuilder(SIGN_CONFIGURATOR)
                        .generatedItem("item/sign_configurator")
                        .shaped(builder -> builder
                                        .define('S', SQUARE_SIGN.item())
                                        .unlockedBy("iron", has(Items.IRON_INGOT)),
                                "S  ", " i ", "  i")
        );
    }
}
