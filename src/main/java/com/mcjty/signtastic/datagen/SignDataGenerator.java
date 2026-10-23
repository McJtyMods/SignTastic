package com.mcjty.signtastic.datagen;
import com.mcjty.signtastic.SignTastic;
import mcjty.lib.datagen.DataGen;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
public final class SignDataGenerator implements DataGeneratorEntrypoint {
    @Override public void onInitializeDataGenerator(FabricDataGenerator generator) {
        generator.createPack().addProvider((output, registries) -> {
            DataGen data = new DataGen(output, registries);
            SignTastic.modules.datagen(data, registries);
            data.generate();
            return data;
        });
    }
}
