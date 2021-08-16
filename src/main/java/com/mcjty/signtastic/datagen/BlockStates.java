package com.mcjty.signtastic.datagen;

import com.mcjty.signtastic.SignTastic;
import mcjty.lib.datagen.BaseBlockStateProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockStates extends BaseBlockStateProvider {

    public BlockStates(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, SignTastic.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
//        orientedBlock(SpawnerModule.SPAWNER.get(), frontBasedModel("spawner", modLoc("block/machinespawner")));
    }
}
