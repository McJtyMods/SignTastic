package com.mcjty.signtastic;

import com.mcjty.signtastic.modules.signs.SignsModule;
import com.mcjty.signtastic.setup.Config;
import com.mcjty.signtastic.setup.Messages;
import com.mcjty.signtastic.setup.ModSetup;
import com.mcjty.signtastic.setup.Registration;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.modules.Modules;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@Mod(SignTastic.MODID)
public class SignTastic {

    public static final String MODID = "signtastic";

    @SuppressWarnings("PublicField")
    public static ModSetup setup = new ModSetup();
    private Modules modules = new Modules();
    public static SignTastic instance;

    public SignTastic(ModContainer container, IEventBus bus, Dist dist) {
        instance = this;
        Config.register(container);
        setupModules(bus, dist);
        Registration.register(bus);

        bus.addListener(setup::init);
        bus.addListener(modules::init);
        bus.addListener(this::onDataGen);
        bus.addListener(Messages::registerMessages);
        bus.addListener(setup.getBlockCapabilityRegistrar(Registration.RBLOCKS));

        if (dist.isClient()) {
            bus.addListener(modules::initClient);
        }
    }

    private void onDataGen(GatherDataEvent event) {
        DataGen datagen = new DataGen(MODID, event);
        modules.datagen(datagen, event.getLookupProvider());
        datagen.generate();
    }

    private void setupModules(IEventBus bus, Dist dist) {
        modules.register(new SignsModule(bus, dist));
    }
}
