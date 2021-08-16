package com.mcjty.signtastic.datagen;

import com.mcjty.signtastic.SignTastic;
import mcjty.lib.datagen.BaseItemModelProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

public class Items extends BaseItemModelProvider {

    public Items(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, SignTastic.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
//        parentedBlock(CrafterModule.CRAFTER1.get(), "block/crafter1");
    }

    @Override
    public String getName() {
        return "SignTastic Item Models";
    }
}
