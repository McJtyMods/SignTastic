package com.mcjty.signtastic.datagen;

import com.mcjty.signtastic.modules.squares.SquaresModule;
import mcjty.lib.datagen.BaseLootTableProvider;
import net.minecraft.data.DataGenerator;

public class LootTables extends BaseLootTableProvider {

    public LootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void addTables() {
        addStandardTable(SquaresModule.SQUARE_SIGN.get());
    }

    @Override
    public String getName() {
        return "SignTastic LootTables";
    }
}
