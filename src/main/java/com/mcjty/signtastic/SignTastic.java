package com.mcjty.signtastic;

import com.mcjty.signtastic.modules.signs.SignsModule;
import com.mcjty.signtastic.setup.Config;
import com.mcjty.signtastic.setup.ModSetup;
import com.mcjty.signtastic.setup.Registration;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.modules.Modules;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.api.distmarker.Dist;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.eventbus.api.IEventBus;
import net.neoforged.neoforge.fml.common.Mod;
import net.neoforged.neoforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.fml.loading.FMLEnvironment;

import java.util.function.Supplier;

@Mod(SignTastic.MODID)
public class SignTastic {

    public static final String MODID = "signtastic";

    @SuppressWarnings("PublicField")
    public static ModSetup setup = new ModSetup();
    private Modules modules = new Modules();
    public static SignTastic instance;

    public SignTastic() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        Dist dist = FMLEnvironment.dist;

        instance = this;
        Config.register();
        setupModules(bus, dist);
        Registration.register(bus);

        bus.addListener(setup::init);
        bus.addListener(modules::init);
        bus.addListener(this::onDataGen);

        if (dist.isClient()) {
            bus.addListener(modules::initClient);
        }
    }

    public static <T extends Item> Supplier<T> tab(Supplier<T> supplier) {
        return instance.setup.tab(supplier);
    }

    private void onDataGen(GatherDataEvent event) {
        DataGen datagen = new DataGen(MODID, event);
        modules.datagen(datagen);
        datagen.generate();
    }

    private void setupModules(IEventBus bus, Dist dist) {
        modules.register(new SignsModule(bus, dist));
    }
}
