package com.mcjty.signtastic.datagen;

import mcjty.lib.datagen.BaseLootTableProvider;
import net.minecraft.data.DataGenerator;

public class LootTables extends BaseLootTableProvider {

    public LootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void addTables() {
//        addSimpleTable(LogicBlockModule.DIGIT.get());
    }

    @Override
    public String getName() {
        return "SignTastic LootTables";
    }
}
