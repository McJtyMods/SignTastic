package com.mcjty.signtastic.modules.signs;

import com.mcjty.signtastic.modules.signs.blocks.*;
import com.mcjty.signtastic.modules.signs.client.ClientSetup;
import com.mcjty.signtastic.modules.signs.client.SignGui;
import com.mcjty.signtastic.modules.signs.client.SignRenderer;
import com.mcjty.signtastic.modules.signs.items.SignConfiguratorItem;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.modules.IModule;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;

import static com.mcjty.signtastic.setup.Registration.*;

public class SignsModule implements IModule {

    public static final RegistryObject<SquareSignBlock> SQUARE_SIGN = BLOCKS.register("square_sign", SquareSignBlock::new);
    public static final RegistryObject<BlockItem> SQUARE_SIGN_ITEM = ITEMS.register("square_sign", () -> new BlockItem(SQUARE_SIGN.get(), createStandardProperties()));
    public static final RegistryObject<BlockEntityType<SquareSignTileEntity>> TYPE_SQUARE_SIGN = TILES.register("square_sign", () -> BlockEntityType.Builder.of(SquareSignTileEntity::new, SQUARE_SIGN.get()).build(null));

    public static final RegistryObject<BlockSignBlock> BLOCK_SIGN = BLOCKS.register("block_sign", () -> new BlockSignBlock(BlockSignTileEntity::new));
    public static final RegistryObject<BlockItem> BLOCK_SIGN_ITEM = ITEMS.register("block_sign", () -> new BlockItem(BLOCK_SIGN.get(), createStandardProperties()));
    public static final RegistryObject<BlockEntityType<BlockSignTileEntity>> TYPE_BLOCK_SIGN = TILES.register("block_sign", () -> BlockEntityType.Builder.of(BlockSignTileEntity::new, BLOCK_SIGN.get()).build(null));

    public static final RegistryObject<SlabSignBlock> SLAB_SIGN = BLOCKS.register("slab_sign", () -> new SlabSignBlock(SlabSignTileEntity::new));
    public static final RegistryObject<BlockItem> SLAB_SIGN_ITEM = ITEMS.register("slab_sign", () -> new BlockItem(SLAB_SIGN.get(), createStandardProperties()));
    public static final RegistryObject<BlockEntityType<SlabSignTileEntity>> TYPE_SLAB_SIGN = TILES.register("slab_sign", () -> BlockEntityType.Builder.of(SlabSignTileEntity::new, SLAB_SIGN.get()).build(null));

    public static final RegistryObject<MenuType<GenericContainer>> CONTAINER_SIGN = CONTAINERS.register("sign_container", GenericContainer::createContainerType);

    public static final RegistryObject<SignConfiguratorItem> SIGN_CONFIGURATOR = ITEMS.register("sign_configurator", SignConfiguratorItem::new);


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
}
