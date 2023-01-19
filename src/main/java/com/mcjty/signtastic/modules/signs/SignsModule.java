package com.mcjty.signtastic.modules.signs;

import com.mcjty.signtastic.modules.signs.blocks.*;
import com.mcjty.signtastic.modules.signs.client.ClientSetup;
import com.mcjty.signtastic.modules.signs.client.SignGui;
import com.mcjty.signtastic.modules.signs.client.SignRenderer;
import com.mcjty.signtastic.modules.signs.items.SignConfiguratorItem;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;

import static com.mcjty.signtastic.SignTastic.tab;
import static com.mcjty.signtastic.setup.Registration.*;
import static mcjty.lib.datagen.DataGen.has;

public class SignsModule implements IModule {

    public static final RegistryObject<SquareSignBlock> SQUARE_SIGN = BLOCKS.register("square_sign", () -> new SquareSignBlock(SquareSignTileEntity::new));
    public static final RegistryObject<BlockItem> SQUARE_SIGN_ITEM = ITEMS.register("square_sign", tab(() -> new BlockItem(SQUARE_SIGN.get(), createStandardProperties())));
    public static final RegistryObject<BlockEntityType<SquareSignTileEntity>> TYPE_SQUARE_SIGN = TILES.register("square_sign", () -> BlockEntityType.Builder.of(SquareSignTileEntity::new, SQUARE_SIGN.get()).build(null));

    public static final RegistryObject<BlockSignBlock> BLOCK_SIGN = BLOCKS.register("block_sign", () -> new BlockSignBlock(BlockSignTileEntity::new));
    public static final RegistryObject<BlockItem> BLOCK_SIGN_ITEM = ITEMS.register("block_sign", tab(() -> new BlockItem(BLOCK_SIGN.get(), createStandardProperties())));
    public static final RegistryObject<BlockEntityType<BlockSignTileEntity>> TYPE_BLOCK_SIGN = TILES.register("block_sign", () -> BlockEntityType.Builder.of(BlockSignTileEntity::new, BLOCK_SIGN.get()).build(null));

    public static final RegistryObject<SlabSignBlock> SLAB_SIGN = BLOCKS.register("slab_sign", () -> new SlabSignBlock(SlabSignTileEntity::new));
    public static final RegistryObject<BlockItem> SLAB_SIGN_ITEM = ITEMS.register("slab_sign", tab(() -> new BlockItem(SLAB_SIGN.get(), createStandardProperties())));
    public static final RegistryObject<BlockEntityType<SlabSignTileEntity>> TYPE_SLAB_SIGN = TILES.register("slab_sign", () -> BlockEntityType.Builder.of(SlabSignTileEntity::new, SLAB_SIGN.get()).build(null));

    public static final RegistryObject<MenuType<GenericContainer>> CONTAINER_SIGN = CONTAINERS.register("sign_container", GenericContainer::createContainerType);

    public static final RegistryObject<SignConfiguratorItem> SIGN_CONFIGURATOR = ITEMS.register("sign_configurator", tab(SignConfiguratorItem::new));


    public SignsModule() {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::onTextureStitch);
        });
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
    public void initConfig() {

    }

    @Override
    public void initDatagen(DataGen dataGen) {
        dataGen.add(
                Dob.blockBuilder(SQUARE_SIGN)
                        .parentedItem("block/square_sign")
                        .standardLoot(TYPE_SQUARE_SIGN)
                        .blockState(p -> p.orientedBlock(SQUARE_SIGN.get(), DataGenHelper.screenModel(p, "square_sign", p.modLoc("block/screenframe_icon"), 13)))
                        .shaped(builder -> builder.shaped(SQUARE_SIGN.get())
                                        .define('S', ItemTags.SIGNS)
                                        .unlockedBy("paper", has(Items.PAPER)),
                                "ppp", "pSp", "ppp"),
                Dob.blockBuilder(BLOCK_SIGN)
                        .parentedItem("block/block_sign")
                        .standardLoot(TYPE_BLOCK_SIGN)
                        .blockState(p -> p.orientedBlock(BLOCK_SIGN.get(), DataGenHelper.screenModel(p, "block_sign", p.modLoc("block/screenframe_icon"), 0)))
                        .shaped(builder -> builder.shaped(BLOCK_SIGN.get())
                                        .define('S', ItemTags.SIGNS)
                                        .define('P', ItemTags.PLANKS)
                                        .unlockedBy("paper", has(Items.PAPER)),
                                "pPp", "PSP", "pPp"),
                Dob.blockBuilder(SLAB_SIGN)
                        .parentedItem("block/slab_sign")
                        .standardLoot(TYPE_SLAB_SIGN)
                        .blockState(p -> p.orientedBlock(SLAB_SIGN.get(), DataGenHelper.screenModel(p, "slab_sign", p.modLoc("block/screenframe_icon"), 8)))
                        .shaped(builder -> builder.shaped(SLAB_SIGN.get())
                                        .define('S', ItemTags.SIGNS)
                                        .define('P', ItemTags.PLANKS)
                                        .unlockedBy("paper", has(Items.PAPER)),
                                "   ", "PSP", "pPp"),
                Dob.itemBuilder(SIGN_CONFIGURATOR)
                        .generatedItem("item/sign_configurator")
                        .shaped(builder -> builder.shaped(SIGN_CONFIGURATOR.get())
                                        .define('S', SQUARE_SIGN.get())
                                        .unlockedBy("iron", has(Items.IRON_INGOT)),
                                "S  ", " i ", "  i")
        );
    }
}
