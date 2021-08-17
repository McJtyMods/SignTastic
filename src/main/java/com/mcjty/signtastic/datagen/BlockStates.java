package com.mcjty.signtastic.datagen;

import com.mcjty.signtastic.SignTastic;
import com.mcjty.signtastic.modules.signs.SignsModule;
import mcjty.lib.datagen.BaseBlockStateProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import static net.minecraftforge.client.model.generators.ModelProvider.BLOCK_FOLDER;

public class BlockStates extends BaseBlockStateProvider {

    public BlockStates(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, SignTastic.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        ModelFile squareSign = screenModel("square_sign", modLoc("block/screenframe_icon"), 13);
        orientedBlock(SignsModule.SQUARE_SIGN.get(), squareSign);
        ModelFile blockSign = screenModel("block_sign", modLoc("block/screenframe_icon"), 0);
        orientedBlock(SignsModule.BLOCK_SIGN.get(), blockSign);
        ModelFile slabSign = screenModel("slab_sign", modLoc("block/screenframe_icon"), 8);
        orientedBlock(SignsModule.SLAB_SIGN.get(), slabSign);
    }

    public ModelFile screenModel(String modelName, ResourceLocation texture, int offset) {
        BlockModelBuilder model = models().getBuilder(BLOCK_FOLDER + "/" + modelName)
                .parent(models().getExistingFile(mcLoc("block")));
        model.element().from(0, 0, offset).to(16, 16, 16)
                .face(Direction.DOWN).cullface(Direction.DOWN).texture("#side").end()
                .face(Direction.UP).cullface(Direction.UP).texture("#side").end()
                .face(Direction.EAST).cullface(Direction.EAST).texture("#side").end()
                .face(Direction.WEST).cullface(Direction.WEST).texture("#side").end()
                .face(Direction.NORTH).texture("#front").end()
                .face(Direction.SOUTH).cullface(Direction.SOUTH).texture("#side").end()
                .end()
                .texture("side", new ResourceLocation("minecraft", "block/oak_planks"))
                .texture("particle", new ResourceLocation("minecraft", "block/oak_planks"))
                .texture("front", texture);
        return model;
    }

}
