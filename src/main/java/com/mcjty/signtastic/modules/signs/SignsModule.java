package com.mcjty.signtastic.modules.signs;

import com.mcjty.signtastic.modules.signs.blocks.*;
import com.mcjty.signtastic.modules.signs.items.SignConfiguratorItem;
import com.mcjty.signtastic.setup.Registration;
import mcjty.lib.blocks.RBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.items.BaseBlockItem;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import mcjty.lib.setup.DeferredItem;

import java.util.function.Supplier;

import static com.mcjty.signtastic.setup.Registration.*;
import static mcjty.lib.datagen.DataGen.has;

public class SignsModule implements IModule {
    @Override public void init() {}
    @Override public void initClient() {}

    public static final RBlock<SquareSignBlock, BlockItem, SquareSignTileEntity> SQUARE_SIGN = RBLOCKS.registerBlock("square_sign",
            SquareSignTileEntity.class,
            properties -> new SquareSignBlock(properties, SquareSignTileEntity::new),
            (block, properties) -> new BaseBlockItem(block.get(), properties.useBlockDescriptionPrefix()),
            SquareSignTileEntity::new
    );
    public static final RBlock<BlockSignBlock, BlockItem, BlockSignTileEntity> BLOCK_SIGN = RBLOCKS.registerBlock("block_sign",
            BlockSignTileEntity.class,
            properties -> new BlockSignBlock(properties, BlockSignTileEntity::new),
            (block, properties) -> new BaseBlockItem(block.get(), properties.useBlockDescriptionPrefix()),
            BlockSignTileEntity::new
    );
    public static final RBlock<SlabSignBlock, BlockItem, SlabSignTileEntity> SLAB_SIGN = RBLOCKS.registerBlock("slab_sign",
            SlabSignTileEntity.class,
            properties -> new SlabSignBlock(properties, SlabSignTileEntity::new),
            (block, properties) -> new BaseBlockItem(block.get(), properties.useBlockDescriptionPrefix()),
            SlabSignTileEntity::new
    );

    public static final Supplier<MenuType<GenericContainer>> CONTAINER_SIGN = CONTAINERS.register("sign_container", GenericContainer::createContainerType);

    public static final DeferredItem<SignConfiguratorItem> SIGN_CONFIGURATOR = RBLOCKS.registerItem("sign_configurator", SignConfiguratorItem::new);


    @Override
    public void initConfig() {

    }

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider provider) {
        dataGen.add(Dob.builder()
                .message("block.signtastic.square_sign", "Square sign")
                .message("block.signtastic.block_sign", "Block sign")
                .message("block.signtastic.slab_sign", "Slab sign")
                .message("item.signtastic.sign_configurator", "Sign Configurator")
                .message("itemGroup.signtastic", "SignTastic")
                .message("message.signtastic.shiftmessage", "<Press Shift>")
                .message("message.signtastic.square_sign.header", "A square flat sign that can be heavily configured")
                .message("message.signtastic.block_sign.header", "A block shaped sign that can be heavily configured")
                .message("message.signtastic.slab_sign.header", "A slab shaped sign that can be heavily configured")
                .message("message.signtastic.sign_configurator.header", "Use this to copy a sign configuration")
                .message("message.signtastic.sign_configurator.gold", "Right click to copy, Sneak right click to paste")
        );
        dataGen.add(
                Dob.blockBuilder(SQUARE_SIGN)
                        .parentedItem("block/square_sign")
                        .standardLoot(Registration.ITEM_SIGNDATA.get(), Registration.ITEM_SIGNSETTINGS.get())
                        .blockState(p -> p.orientedBlock(SQUARE_SIGN, DataGenHelper.screenModel(p, "square_sign", p.modLoc("block/screenframe_icon"), 13)))
                        .shaped(builder -> builder
                                        .define('S', ItemTags.SIGNS)
                                        .unlockedBy("paper", has(Items.PAPER)),
                                "ppp", "pSp", "ppp"),
                Dob.blockBuilder(BLOCK_SIGN)
                        .parentedItem("block/block_sign")
                        .standardLoot(Registration.ITEM_SIGNDATA.get(), Registration.ITEM_SIGNSETTINGS.get())
                        .blockState(p -> p.orientedBlock(BLOCK_SIGN, DataGenHelper.screenModel(p, "block_sign", p.modLoc("block/screenframe_icon"), 0)))
                        .shaped(builder -> builder
                                        .define('S', ItemTags.SIGNS)
                                        .define('P', ItemTags.PLANKS)
                                        .unlockedBy("paper", has(Items.PAPER)),
                                "pPp", "PSP", "pPp"),
                Dob.blockBuilder(SLAB_SIGN)
                        .parentedItem("block/slab_sign")
                        .standardLoot(Registration.ITEM_SIGNDATA.get(), Registration.ITEM_SIGNSETTINGS.get())
                        .blockState(p -> p.orientedBlock(SLAB_SIGN, DataGenHelper.screenModel(p, "slab_sign", p.modLoc("block/screenframe_icon"), 8)))
                        .shaped(builder -> builder
                                        .define('S', ItemTags.SIGNS)
                                        .define('P', ItemTags.PLANKS)
                                        .unlockedBy("paper", has(Items.PAPER)),
                                "   ", "PSP", "pPp"),
                Dob.itemBuilder(SIGN_CONFIGURATOR)
                        .generatedItem("signtastic:item/sign_configurator")
                        .shaped(builder -> builder
                                        .define('S', SQUARE_SIGN.item().get())
                                        .unlockedBy("iron", has(Items.IRON_INGOT)),
                                "S  ", " i ", "  i")
        );
    }
}
