package com.mcjty.signtastic.modules.signs;

import mcjty.lib.datagen.BaseBlockStateProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;

import static net.minecraftforge.client.model.generators.ModelProvider.BLOCK_FOLDER;

public class DataGenHelper {

    public static ModelFile screenModel(BaseBlockStateProvider provider, String modelName, ResourceLocation texture, int offset) {
        BlockModelBuilder model = provider.models().getBuilder(BLOCK_FOLDER + "/" + modelName)
                .parent(provider.models().getExistingFile(provider.mcLoc("block")));
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
