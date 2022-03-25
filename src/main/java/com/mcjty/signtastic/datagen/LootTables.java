package com.mcjty.signtastic.datagen;

import com.mcjty.signtastic.modules.signs.SignsModule;
import mcjty.lib.datagen.BaseLootTableProvider;
import net.minecraft.data.DataGenerator;

public class LootTables extends BaseLootTableProvider {

    public LootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void addTables() {
        addStandardTable(SignsModule.SQUARE_SIGN.get(), SignsModule.TYPE_SQUARE_SIGN.get());
        addStandardTable(SignsModule.BLOCK_SIGN.get(), SignsModule.TYPE_BLOCK_SIGN.get());
        addStandardTable(SignsModule.SLAB_SIGN.get(), SignsModule.TYPE_SLAB_SIGN.get());
    }

    @Override
    public String getName() {
        return "SignTastic LootTables";
    }
}
