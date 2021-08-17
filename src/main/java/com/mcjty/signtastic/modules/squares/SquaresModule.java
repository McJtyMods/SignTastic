package com.mcjty.signtastic.modules.squares;

import com.mcjty.signtastic.modules.squares.blocks.SquareSignBlock;
import com.mcjty.signtastic.modules.squares.blocks.SquareSignTileEntity;
import com.mcjty.signtastic.modules.squares.client.SignGui;
import com.mcjty.signtastic.modules.squares.client.SquareSignRenderer;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.modules.IModule;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static com.mcjty.signtastic.setup.Registration.*;

public class SquaresModule implements IModule {

    public static final RegistryObject<SquareSignBlock> SQUARE_SIGN = BLOCKS.register("square_sign", () -> new SquareSignBlock(SquareSignTileEntity::new));
    public static final RegistryObject<BlockItem> SQUARE_SIGN_ITEM = ITEMS.register("square_sign", () -> new BlockItem(SQUARE_SIGN.get(), createStandardProperties()));
    public static final RegistryObject<TileEntityType<SquareSignTileEntity>> TYPE_SQUARE_SIGN = TILES.register("square_sign", () -> TileEntityType.Builder.of(SquareSignTileEntity::new, SQUARE_SIGN.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> CONTAINER_SQUARE_SIGN = CONTAINERS.register("square_sign", GenericContainer::createContainerType);

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            SignGui.register();
        });
        SquareSignRenderer.register();
    }

    @Override
    public void initConfig() {

    }
}
